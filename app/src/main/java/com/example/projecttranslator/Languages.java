package com.example.projecttranslator;

import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.Locale;


public class Languages {

    private String fromLanguage, toLanguage;
    private static String[] Languages = new String[]{"English", "Hebrew", "Arabic"};
    private static String[] toLanguages = new String[]{"To", "English", "Hebrew", "Arabic"};
    private static String[] fromLanguages = new String[]{"From", "English", "Hebrew", "Arabic"};

    public Languages(String fromLanguage, String toLanguage){
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }

    public Languages(){}

    //Getters
    public static String[] getToLanguagesArray() { return toLanguages; }
    public static String[] getFromLanguagesArray() { return fromLanguages; }
    public static String[] getLanguages() { return Languages; }
    public String getFromLanguage() { return fromLanguage; }
    public String getToLanguage() { return toLanguage; }
    public String getFromLanguageCode(){ return getLanguageCode(fromLanguage); }
    public String getToLanguageCode(){ return getLanguageCode(toLanguage); }
    public Locale getFromLocaleLanguage(){ return getLocalLanguage(fromLanguage); }

    //Setter
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

    public static Locale getLocalLanguage(String languageCode) {
        switch (languageCode){
            case TranslateLanguage.ENGLISH:
                return Locale.ENGLISH;
        }
        return null;
    }
}
