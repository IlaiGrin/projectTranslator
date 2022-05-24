package com.example.projecttranslator;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
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
    public void setDataBase(HashMap<String, ArrayList<String>> dataBase) { this.dataBase = dataBase; }

    public VocabularyDB(Languages languages, Context context){
        this.languages = languages;
        this.context = context;
        dataBase = new HashMap();
    }

    public void addTranslation(String sourceWord, String translation){
        sourceWord = sourceWord.toLowerCase(Locale.ROOT);        //transform to lower case to remain consisted
        translation = translation.toLowerCase(Locale.ROOT);

        if(sourceWord.equals(translation))
            Toast.makeText(context,"Invalid",Toast.LENGTH_SHORT).show();
        else if(dataBase.get(sourceWord) == null) {      //if word doesn't exist in DB
            ArrayList<String> translations = new ArrayList<String>();
            translations.add(translation);
            dataBase.put(sourceWord, translations);
            FirebaseDBManager.updateDataBase(context,this);
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
        } else if(!dataBase.get(sourceWord).contains(translation)) {     //if translation doesn't exist in DB
            dataBase.get(sourceWord).add(translation);  //add the translation
            FirebaseDBManager.updateDataBase(context,this);
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context,"Translation already exists",Toast.LENGTH_SHORT).show();
    }
    public void removeWord(String word){
        dataBase.remove(word);
    }
}
