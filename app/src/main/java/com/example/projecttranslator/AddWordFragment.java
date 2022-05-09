package com.example.projecttranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AddWordFragment extends Fragment {

    private static final int MICROPHONE_CODE = 1;
    Languages languages;
    Spinner toSpinner, fromSpinner;
    ArrayAdapter toAdaptor, fromAdaptor;
    WordTranslator translator;
    EditText input, additionalTranslation;
    TextInputLayout additionalTranslationLayout;
    Button saveBtn1, saveBtn2;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    NetworkChangeReceiver networkReceiver;
    Context context;

    public AddWordFragment() {}// Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_word_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        sharedPref = context.getSharedPreferences(getResources().getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        languages = new Languages(sharedPref.getString("from_language","From"),sharedPref.getString("to_language","To"));
        //select languages spinners
        initializeSpinners();
        //initialize members
        input = getView().findViewById(R.id.source_editTxt);
        additionalTranslation = getView().findViewById(R.id.additional_translation_edit_txt);
        additionalTranslationLayout = getView().findViewById(R.id.additional_translation_layout);
        saveBtn2 = getView().findViewById(R.id.save_btn2);
        saveBtn1 = getView().findViewById(R.id.save_btn1);
        translator = new WordTranslator(context, input, getView().findViewById(R.id.display_txt),
                getView().findViewById(R.id.progress_bar), getView().findViewById(R.id.add_word_screen));
        //follow network state - mic is unavailable without connection
        networkReceiver = new NetworkChangeReceiver(getView().findViewById(R.id.mic_img));
        context.registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getView().findViewById(R.id.translate_btn).setOnClickListener(view1 -> {
            if(languages.getToLanguage().equals("To") || languages.getFromLanguage().equals("From"))
                Toast.makeText(context,"Select a language",Toast.LENGTH_SHORT).show();
            else if(languages.getToLanguage().equals(languages.getFromLanguage()))
                Toast.makeText(context,"Select a different language",Toast.LENGTH_SHORT).show();
            else if(input.getText().toString().equals("")) {
                input.setError("Enter a word");
                input.requestFocus();
            } else {
                additionalTranslationLayout.setVisibility(View.GONE);
                saveBtn2.setVisibility(View.GONE);
                translator.translate(languages.getFromLanguageCode(), languages.getToLanguageCode());
                saveBtn1.setVisibility(View.VISIBLE);
                //option of another translation
                ImageView plusBtn = requireView().findViewById(R.id.add_translation_btn);
                plusBtn.setVisibility(View.VISIBLE);
                plusBtn.setOnClickListener(btn ->{
                    btn.setVisibility(View.GONE);
                    additionalTranslationLayout.setVisibility(View.VISIBLE);
                    saveBtn2.setVisibility(View.VISIBLE);
                    saveBtn2.setOnClickListener(save2 ->{
                        if(additionalTranslation.getText().equals("")) {
                            additionalTranslation.setError("Enter a translation");
                            additionalTranslation.requestFocus();
                        }
                    });
                });
            }
        });

        getView().findViewById(R.id.mic_img).setOnClickListener(view1 -> {
            if(view1.getTag().equals(R.drawable.red_btn))
                Toast.makeText(context,"No Internet connection",Toast.LENGTH_SHORT).show();
            else {
                if(languages.getFromLanguage().equals("From"))
                    Toast.makeText(context,"Select from language",Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languages.getFromLanguageCode());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");
                    try {
                        startActivityForResult(intent, MICROPHONE_CODE);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        getView().findViewById(R.id.camera_img).setOnClickListener(view1 -> startActivity(new Intent(context, CameraActivity.class)));

        getView().findViewById(R.id.switch_languages_img).setOnClickListener(view1 -> {
            if(languages.getToLanguage().equals("To") || languages.getFromLanguage().equals("From"))
                Toast.makeText(context,"Select both languages",Toast.LENGTH_SHORT).show();
            else {
                //switching spinners
                toSpinner.setSelection(toAdaptor.getPosition(languages.getFromLanguage()));
                fromSpinner.setSelection(fromAdaptor.getPosition(languages.getToLanguage()));
                //switching variables
                String tempFromLanguage = languages.getFromLanguage();
                languages.setFromLanguage(languages.getToLanguage());
                languages.setToLanguage(tempFromLanguage);
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(networkReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROPHONE_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            input.setText(result.get(0).split(" ", 2)[0]);
        }
    }

    private void initializeSpinners(){
        //initialize the select to language spinner
        toSpinner = getView().findViewById(R.id.select_to_language_spinner);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languages.setToLanguage(languages.getToLanguagesArray()[i]);
                editor.putString("to_language", languages.getToLanguagesArray()[i]).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        toAdaptor = new ArrayAdapter(context, R.layout.spinner_item, languages.getToLanguagesArray());
        toAdaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdaptor);
        toSpinner.setSelection(toAdaptor.getPosition(languages.getToLanguage()));

        //initialize the select from language spinner
        fromSpinner = getView().findViewById(R.id.select_from_language_spinner);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                languages.setFromLanguage(languages.getFromLanguagesArray()[i]);
                editor.putString("from_language", languages.getFromLanguagesArray()[i]).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        fromAdaptor = new ArrayAdapter(context, R.layout.spinner_item,  languages.getFromLanguagesArray());
        fromAdaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdaptor);
        fromSpinner.setSelection(toAdaptor.getPosition(languages.getFromLanguage()));
    }
}