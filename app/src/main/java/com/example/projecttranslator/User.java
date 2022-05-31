package com.example.projecttranslator;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    //Members
    private String email, username, nativeLanguage;
    private ArrayList<VocabularyDB> dictionary;
    private FirebaseUser firebaseUser;
    private Context context;

    //Getters
    public String getEmail() { return email; }
    public String getNativeLanguage() { return nativeLanguage; }
    public String getUsername(){ return username; }
    public ArrayList<VocabularyDB> getDictionary() { return dictionary; }

    public User(Context context){
        this.context = context;
        this.dictionary = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            email = firebaseUser.getEmail();
            username = firebaseUser.getDisplayName();
        }
    }

    public void addVocabularyDB(VocabularyDB vocabularyDB){
        dictionary.add(vocabularyDB);
    }

    public void updateUsername(String username){
        this.username = username;
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build());
    }

    public void setNativeLanguage(String nativeLanguage){
        this.nativeLanguage = nativeLanguage;
        FirebaseDBManager.saveNativeLanguage(context, nativeLanguage);
    }

    public VocabularyDB getVocabularyByLanguages(Languages languages){   //returns the vocabulary that has the same languages
        VocabularyDB vocabularyDB = null;
        Languages newLanguages = new Languages(languages.getFromLanguage(), languages.getToLanguage()); //to not change the current one
        //arrange: from language -> to native language
        if(!languages.getToLanguage().equals(nativeLanguage)){
            String temp = newLanguages.getFromLanguage();
            newLanguages.setFromLanguage(languages.getToLanguage());
            newLanguages.setToLanguage(temp);
        }
        for (VocabularyDB vocabulary : dictionary) {
            if(vocabulary.getToLanguage().equals(newLanguages.getToLanguage())
                    && vocabulary.getFromLanguage().equals(newLanguages.getFromLanguage()))
                vocabularyDB = vocabulary;
        }
        //if doesn't exist, creating a new one
        if (vocabularyDB == null){
            vocabularyDB = new VocabularyDB(newLanguages, context);
            FirebaseDBManager.addVocabularyDB(context, vocabularyDB);   //save in firebase
            dictionary.add(vocabularyDB);
        }
        return vocabularyDB;
    }

    public VocabularyDB getVocabularyByKey(String key){   //returns the vocabulary that has the same key
        VocabularyDB vocabularyDB = null;
        for (VocabularyDB vocabulary : dictionary) {
            if(vocabulary.getKey().equals(key))
                return vocabulary;
        }
        return vocabularyDB;
    }

    public void removeWord(String vocabularyKey, String word){
        VocabularyDB vocabulary = getVocabularyByKey(vocabularyKey);
        if(vocabulary != null){
            vocabulary.removeWord(word);
            if(vocabulary.getDataBase().isEmpty()) {
                dictionary.remove(vocabulary);
                FirebaseDBManager.deleteVocabularyOption(context, vocabularyKey);
            }
        }
    }

    public boolean isLoggedIn(){
        return firebaseUser != null;
    }
}
