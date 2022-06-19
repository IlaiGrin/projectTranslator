package com.example.projecttranslator;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translator;

public class WordTranslator {
    private Translator translator;
    private TranslatorOptions options;
    private Context context;
    private EditText source;
    private TextView display;
    private ProgressBar downloadModelBar;
    private LinearLayout layout;

    public WordTranslator(Context context, EditText source, TextView display, ProgressBar downloadModelBar, LinearLayout layout){
        this.context = context;
        this.display = display;
        this.source = source;
        this.downloadModelBar = downloadModelBar;
        this.layout = layout;
    }
    public void translate(String fromLanguageCode, String toLanguageCode){
        Utils.setProgressBar(layout, downloadModelBar, true);
        // creating firebase translate option
        options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode).build();
        translator = Translation.getClient(options);
        downloadModel(String.valueOf(source.getText()));
    }

    private void downloadModel(String input) {
        //download the modal which we will require to translate
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid ->{
            Toast.makeText(context, "Language modal is downloaded", Toast.LENGTH_SHORT).show();
            // calling method to translate text
            translator.translate(input).addOnSuccessListener(translation-> {
                String noVowelsTranslation = "";
                for(int j=0; j<translation.length() ; j++) {
                    char c = translation.charAt(j);
                    if(c<1425 || c>1479)
                        noVowelsTranslation = noVowelsTranslation + translation.charAt(j);
                }
                display.setText(noVowelsTranslation);
            });
        }).addOnFailureListener(exception -> Toast.makeText(context, "Fail to download modal", Toast.LENGTH_SHORT).show())
        .addOnCompleteListener(complete ->Utils.setProgressBar(layout, downloadModelBar, false));
    }
}
