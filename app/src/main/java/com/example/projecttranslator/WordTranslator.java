package com.example.projecttranslator;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.speech.RecognizerIntent;
import android.speech.RecognizerResultsIntent;
import android.util.Log;
import android.widget.EditText;
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

    public WordTranslator(Context context, EditText source, TextView display){
        this.context = context;
        this.display = display;
        this.source = source;
    }
    public void translate(String toLanguageCode, String fromLanguageCode){
        // creating firebase translate option
        TranslatorOptions options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode).build();
        translator = Translation.getClient(options);
        downloadModal(String.valueOf(source.getText()));
    }

    private void downloadModal(String input) {
        //download the modal which we will require to translate
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid ->{
                Toast.makeText(context, "language modal is downloaded", Toast.LENGTH_SHORT).show();
                // calling method to translate our entered text
                translator.translate(input).addOnSuccessListener(translation-> display.setText(translation));
            }).addOnFailureListener(exception -> Toast.makeText(context, "Fail to download modal", Toast.LENGTH_SHORT).show());
    }
}
