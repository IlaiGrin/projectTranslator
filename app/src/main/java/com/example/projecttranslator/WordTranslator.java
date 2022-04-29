package com.example.projecttranslator;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.speech.RecognizerIntent;
import android.speech.RecognizerResultsIntent;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseExceptionMapper;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translator;

public class WordTranslator {
    Translator translator;
    Context context;
    EditText source;
    TextView display;
    ProgressBar downloadModelBar;
    LinearLayout layout;

    public WordTranslator(Context context, EditText source, TextView display, ProgressBar downloadModelBar, LinearLayout layout){
        this.context = context;
        this.display = display;
        this.source = source;
        this.downloadModelBar = downloadModelBar;
        this.layout = layout;
    }
    public void translate(String toLanguageCode, String fromLanguageCode){
        setProgressBar(true);
        // creating firebase translate option
        TranslatorOptions options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode).build();
        translator = Translation.getClient(options);
        downloadModel(String.valueOf(source.getText()));
    }

    private void downloadModel(String input) {
        //download the modal which we will require to translate
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid ->{
            Toast.makeText(context, "language modal is downloaded", Toast.LENGTH_SHORT).show();
            // calling method to translate text
            translator.translate(input).addOnSuccessListener(translation-> display.setText(translation));
        }).addOnFailureListener(exception -> Toast.makeText(context, "Fail to download modal", Toast.LENGTH_SHORT).show())
        .addOnCompleteListener(complete ->setProgressBar(false));
    }

    private void setProgressBar(boolean inProgress) {
        if(inProgress){
            downloadModelBar.setVisibility(View.VISIBLE);
            for (int i = 0; i < layout.getChildCount(); i++) {  //disable all views
                View child = layout.getChildAt(i);
                if(child instanceof LinearLayout) {     //if it is a linear layout
                    for (int j = 0; j < ((LinearLayout)child).getChildCount(); j++)
                        ((LinearLayout)child).getChildAt(j).setEnabled(false);
                }
                if(child instanceof RelativeLayout) {     //if it is a relative layout
                    for (int j = 0; j < ((RelativeLayout)child).getChildCount(); j++)
                        ((RelativeLayout)child).getChildAt(j).setEnabled(false);
                }
                child.setEnabled(false);
            }
        }else {
            downloadModelBar.setVisibility(View.GONE);
            for (int i = 0; i < layout.getChildCount(); i++) {  //enable all views
                View child = layout.getChildAt(i);
                if(child instanceof LinearLayout) {     //if it is a linear layout
                    for (int j = 0; j < ((LinearLayout)child).getChildCount(); j++)
                        ((LinearLayout)child).getChildAt(j).setEnabled(true);
                }
                if(child instanceof RelativeLayout) {     //if it is a relative layout
                    for (int j = 0; j < ((RelativeLayout)child).getChildCount(); j++)
                        ((RelativeLayout)child).getChildAt(j).setEnabled(true);
                }
                child.setEnabled(true);
            }
        }
    }
}
