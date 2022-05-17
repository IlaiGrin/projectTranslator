package com.example.projecttranslator;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class VocabularyDB {

    //Members
    private Context context;
    private Languages languages;
    private HashMap<String, ArrayList<String>> dataBase;
    private String key;         //used to save the route in Realtime DB

    //Getters
    public Languages getLanguages() { return languages; }
    public String getToLanguage(){ return languages.getToLanguage();}
    public String getFromLanguage(){ return languages.getFromLanguage();}
    public String getKey() { return key; }
    public HashMap<String, ArrayList<String>> getDataBase() { return dataBase; }

    //Setters
    public void setKey(String key) { this.key = key; }

    public VocabularyDB(Languages languages, Context context){
        this.languages = languages;
        this.context = context;
        dataBase = new HashMap();
    }

    public void addTranslation(String sourceWord, String translation){
        sourceWord.toLowerCase(Locale.ROOT);        //transform to lower case to remain consisted
        translation.toLowerCase(Locale.ROOT);

        if(languages.identifyLanguageCode(sourceWord).equals(languages.getFromLanguageCode()) &&
                languages.identifyLanguageCode(translation).equals(languages.getToLanguageCode())){     //the languages of the word and translation match the expected
            if(dataBase.get(sourceWord) == null) {      //if word doesn't exist in DB
                ArrayList<String> translations = new ArrayList<String>();
                translations.add(translation);
                dataBase.put(sourceWord, translations);
                FirebaseDBManager.updateDataBase(context,this);
            } else if(!dataBase.get(sourceWord).contains(translation)) {     //if translation doesn't exist in DB
                dataBase.get(sourceWord).add(translation);  //add the translation
                FirebaseDBManager.updateDataBase(context,this);
            } else
                Toast.makeText(context,"translation already exists",Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context,"words doesn't match the languages saved in the database",Toast.LENGTH_LONG).show();
    }
}
