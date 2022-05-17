package com.example.projecttranslator;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            email = firebaseUser.getEmail();
            username = firebaseUser.getDisplayName();
        }
    }

    public void updateUsername(String username){
        this.username = username;
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build());
    }

    public void addVocabularyDB(VocabularyDB vocabulary){
        dictionary.add(vocabulary);
        FirebaseDBManager.addVocabularyDB(context, vocabulary);
    }

    public void setNativeLanguage(String nativeLanguage){
        this.nativeLanguage = nativeLanguage;
        FirebaseDBManager.saveNativeLanguage(context, nativeLanguage);
    }

    public VocabularyDB getVocabularyDB(Languages languages){   //returns the vocabulary that has the same languages
        for (VocabularyDB vocabulary : dictionary) {
            if(vocabulary.getToLanguage().equals(languages.getToLanguage())
                    && vocabulary.getFromLanguage().equals(languages.getFromLanguage()))
                return vocabulary;
        }
        return null;
    }

    public boolean isLoggedIn(){
        return firebaseUser != null;
    }
}
