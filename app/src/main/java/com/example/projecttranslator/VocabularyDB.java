package com.example.projecttranslator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        dataBase = new LinkedHashMap();   // use LinkedHashMap to maintain sequence
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

    public HashMap<String, ArrayList<String>> orderVocabularyByString(String wordToOrderBy){
        HashMap<String, ArrayList<String>> orderedDB = new HashMap<>();
        for (Object word:dataBase.keySet().toArray()) {
            if(word.toString().contains(wordToOrderBy))
                orderedDB.put(word.toString(), dataBase.get(word.toString()));  //add word if contains the wanted string
        }

        return orderedDB;
    }

    @SuppressLint("NewApi")
    public HashMap<String, ArrayList<String>> orderVocabularyByACB(){
        return (HashMap<String, ArrayList<String>>) dataBase.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)); //<Class name>::<method name>
    }

    public void removeWord(String word){
        dataBase.remove(word);
    }
}
