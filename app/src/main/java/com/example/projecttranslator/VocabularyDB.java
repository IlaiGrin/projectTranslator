package com.example.projecttranslator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    private HashMap<String, Long> wordsOrder;     //save order by saving time
    private String key;         //used to save the route in Realtime DB

    //Getters
    public Languages getLanguages() { return languages; }
    public String getToLanguage(){ return languages.getToLanguage();}
    public String getFromLanguage(){ return languages.getFromLanguage();}
    public String getKey() { return key; }
    public HashMap<String, ArrayList<String>> getDataBase() { return dataBase; }
    public HashMap<String, Long> getDataBaseOrder() { return wordsOrder; }

    //Setters
    public void setKey(String key) { this.key = key; }
    public void setDataBase(HashMap<String, ArrayList<String>> dataBase) { this.dataBase = dataBase; }
    public void setDataBaseOrder(HashMap<String,Long> wordsOrder) { this.wordsOrder = wordsOrder; }

    public VocabularyDB(Languages languages, Context context){
        this.languages = languages;
        this.context = context;
        dataBase = new HashMap();
        wordsOrder = new HashMap();
    }

    public void addTranslation(String sourceWord, String translation){
        sourceWord = sourceWord.toLowerCase(Locale.ROOT);        //transform to lower case to remain consisted
        translation = translation.toLowerCase(Locale.ROOT);

        if(sourceWord.equals(translation))
            Toast.makeText(context,"Invalid",Toast.LENGTH_SHORT).show();
        else if(dataBase.get(sourceWord) == null) {      //if word doesn't exist in DB
            ArrayList<String> translations = new ArrayList<>();
            translations.add(translation);
            dataBase.put(sourceWord, translations);
            wordsOrder.put(sourceWord, (long) wordsOrder.size()+1);
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
        HashMap orderABC = new LinkedHashMap();  //to maintain order of insertion when returning
         dataBase.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> orderABC.put(x.getKey(), x.getValue()));
         return orderABC;
    }

    @SuppressLint("NewApi")
    public LinkedHashMap<String, ArrayList<String>> orderVocabularyByInsertionTime(boolean fromNewToOld){
        HashMap orderIndexes = new LinkedHashMap();
        if(fromNewToOld){
            wordsOrder.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> orderIndexes.put(x.getKey(), x.getValue()));
        } else {
            wordsOrder.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> orderIndexes.put(x.getKey(), x.getValue()));
        }
        LinkedHashMap<String, ArrayList<String>> orderedWords = new LinkedHashMap();    //to maintain order of insertion when returning
        for (Object word:orderIndexes.keySet())
            orderedWords.put(word.toString(), dataBase.get(word.toString()));
        return orderedWords;
    }

    @SuppressLint("NewApi")
    public void removeWord(String wordToDelete){
        dataBase.remove(wordToDelete);
        for (String word:wordsOrder.keySet()) {     //reordering the indexes
            long wordIndex = wordsOrder.get(word);
            long deleteIndex = wordsOrder.get(wordToDelete);
            if(wordIndex > deleteIndex)
                wordsOrder.replace(word, wordIndex, wordIndex-1);
        }
        wordsOrder.remove(wordToDelete);
        FirebaseDBManager.updateDataBase(context, this);        //update changes in firebase
    }
}
