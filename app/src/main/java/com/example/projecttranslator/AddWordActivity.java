package com.example.projecttranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddWordActivity extends AppCompatActivity {

    private static final int MICROPHONE_CODE = 1;
    String selectedToLanguage = "", selectedFromLanguage = "";
    String[] toLanguages = {"To","English","Hebrew","Arabic"};
    String[] fromLanguages = {"From","English","Hebrew","Arabic"};
    Spinner toSpinner, fromSpinner;
    ArrayAdapter toAdaptor, fromAdaptor;
    WordTranslator translator;
    EditText input, additionalTranslation;
    Button saveBtn2;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word_layout);
        getSupportActionBar().hide();

        sharedPref = getSharedPreferences(getResources().getString(R.string.shared_preference_name),MODE_PRIVATE);
        editor = sharedPref.edit();
        initializeSpinners(sharedPref.getString("from_language","From"),sharedPref.getString("to_language","To"));       //select languages spinners
        input = findViewById(R.id.source_editTxt);
        additionalTranslation = findViewById(R.id.additional_translation_edit_txt);
        saveBtn2 = findViewById(R.id.save_btn2);
        translator = new WordTranslator(this, input, findViewById(R.id.display_txt));

        findViewById(R.id.backward_img).setOnClickListener(view -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        });

        findViewById(R.id.translate_btn).setOnClickListener(view -> {
            if(selectedToLanguage.equals("To1") || selectedFromLanguage.equals("From"))
                Toast.makeText(this,"Select a language",Toast.LENGTH_SHORT).show();
            else if(selectedToLanguage.equals(selectedFromLanguage))
                Toast.makeText(this,"Select a different language",Toast.LENGTH_SHORT).show();
            else if(input.getText().toString().equals(""))
                Toast.makeText(this, "enter a word", Toast.LENGTH_SHORT).show();
            else {
                additionalTranslation.setVisibility(View.GONE);
                saveBtn2.setVisibility(View.GONE);
                translator.translate(getLanguageCode(selectedToLanguage), getLanguageCode(selectedFromLanguage));
                //option of another translation
                ImageView plusBtn = findViewById(R.id.add_translation_btn);
                plusBtn.setVisibility(View.VISIBLE);
                plusBtn.setOnClickListener(btn ->{
                    btn.setVisibility(View.GONE);
                    additionalTranslation.setVisibility(View.VISIBLE);
                    saveBtn2.setVisibility(View.VISIBLE);
                    saveBtn2.setOnClickListener(save2 ->{
                        if(additionalTranslation.getText().equals(""))
                            Toast.makeText(this,"enter a translation",Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        findViewById(R.id.mic_img).setOnClickListener(view -> {
            if(selectedFromLanguage.equals(""))
                Toast.makeText(this,"Select from language",Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLanguageCode(selectedFromLanguage));
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");
                try {
                    startActivityForResult(intent, MICROPHONE_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(AddWordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.camera_img).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(this, CameraActivity.class);
            startActivity(cameraIntent);
        });

        findViewById(R.id.switch_languages_img).setOnClickListener(view -> {
            if(selectedToLanguage.equals("To") || selectedFromLanguage.equals("From"))
                Toast.makeText(this,"Select both languages",Toast.LENGTH_SHORT).show();
            else {
                //switching spinners
                toSpinner.setSelection(toAdaptor.getPosition(selectedFromLanguage));
                fromSpinner.setSelection(fromAdaptor.getPosition(selectedToLanguage));
                //switching variables
                String tempFromLanguage = selectedFromLanguage;
                selectedFromLanguage = selectedToLanguage;
                selectedToLanguage = tempFromLanguage;
            }
        });
    }

    private String getLanguageCode(String language) {
        switch (language){
            case "English":
                return TranslateLanguage.ENGLISH;
            case "Hebrew":
                return TranslateLanguage.HEBREW;
            case "Arabic":
                return TranslateLanguage.ARABIC;
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROPHONE_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            input.setText(result.get(0).split(" ", 2)[0]);
        }
    }

    private void initializeSpinners(String fromLanguage, String toLanguage){
        //initialize the select to language spinner
        toSpinner = findViewById(R.id.select_to_language_spinner);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedToLanguage = toLanguages[i];
                editor.putString("to_language", toLanguages[i]).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        toAdaptor = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
        toAdaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdaptor);
        toSpinner.setSelection(toAdaptor.getPosition(toLanguage));

        //initialize the select from language spinner
        fromSpinner = findViewById(R.id.select_from_language_spinner);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFromLanguage = fromLanguages[i];
                editor.putString("from_language", toLanguages[i]).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        fromAdaptor = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);
        fromAdaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdaptor);
        fromSpinner.setSelection(toAdaptor.getPosition(fromLanguage));
    }
    /*for (int i = 0; i<fromLanguages.length;i++) {
            TextView languageView = new TextView(this);
            fromAdaptor.getView(i,languageView,fromSpinner);
            languageView.setOnClickListener(view->selectedFromLanguage = ((TextView)view).getText().toString());
        }*/
}