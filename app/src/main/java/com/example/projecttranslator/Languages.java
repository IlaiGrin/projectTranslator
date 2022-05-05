package com.example.projecttranslator;

import android.icu.lang.UProperty;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.HashMap;

public class Languages {

    private String fromLanguage, toLanguage;
    private String[] toLanguages, fromLanguages;
    private FirebaseLanguageIdentification identifier;

    public Languages(String fromLanguage, String toLanguage){
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
        toLanguages = new String[]{"To", "English", "Hebrew", "Arabic"};
        fromLanguages = new String[]{"From", "English", "Hebrew", "Arabic"};
        identifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
    }

    public String[] getToLanguagesArray() { return toLanguages; }
    public String[] getFromLanguagesArray() { return fromLanguages; }

    public String getFromLanguage() { return fromLanguage; }
    public String getToLanguage() { return toLanguage; }

    public void setFromLanguage(String fromLanguage) { this.fromLanguage = fromLanguage; }
    public void setToLanguage(String toLanguage) { this.toLanguage = toLanguage; }

    public String getLanguageCode(String language) {
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

    public String getFromLanguageCode(){ return getLanguageCode(fromLanguage); }
    public String getToLanguageCode(){ return getLanguageCode(toLanguage); }

    public String identifyLanguageCode(String input){
        final String[] languageCode = {""};
        identifier.identifyLanguage(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@Nullable String code) {
                if (code != "und")
                    languageCode[0] = code;
            }
        });
        return languageCode[0];
    }
}
