package com.example.projecttranslator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class User {

    String email, username, nativeLanguage;
    ArrayList<VocabularyDB> dictionary;
    FirebaseUser firebaseUser;

    public String getEmail() { return email; }
    public String getNativeLanguage() { return nativeLanguage; }
    public String getUsername(){ return username; }

    public User(){
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

    public boolean isLoggedIn(){
        return firebaseUser != null;
    }
}
