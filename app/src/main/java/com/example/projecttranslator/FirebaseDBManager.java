package com.example.projecttranslator;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBManager {

    private static DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static String userEmail;

    public FirebaseDBManager(){}

    public static void setUserEmail(String email){ userEmail = email.replace(".",""); }//remove "." because firebase do not allow "."

    public static void getNativeLanguageFormDB(Context context, Spinner spinner, ArrayAdapter adapter){
        reference.child(context.getString(R.string.firebase_user_setting)).child(userEmail).child(context.getString(R.string.firebase_user_native_language)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nativeLan = snapshot.getValue(String.class);
                Utils.user.setNativeLanguage(nativeLan);
                spinner.setSelection(adapter.getPosition(nativeLan));
                spinner.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void addUser(Context context){
        //inserting user's settings
        reference.child(context.getString(R.string.firebase_user_setting)).child(userEmail).child(context.getString(R.string.firebase_user_native_language)).setValue(Utils.user.getNativeLanguage());
        for (VocabularyDB vocabulary : Utils.user.getDictionary()) {
            //inserting every vocabulary option in DB
            vocabulary.setKey(reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).push().getKey());    //each vocabulary has a unique key
            reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).child(vocabulary.getKey()).setValue(vocabulary.getLanguages());
            //inserting the words in vocabulary
            reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabulary.getKey()).setValue(vocabulary.getDataBase());
        }
    }

    //updates to words in DB
    public static void updateDataBase(Context context, VocabularyDB vocabulary){  reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabulary.getKey()).setValue(vocabulary.getDataBase());}

    public static void addVocabularyDB(Context context, VocabularyDB vocabulary){
        //inserting vocabulary's languages in DB
        vocabulary.setKey(reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).push().getKey());    //each vocabulary has a unique key
        reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).child(vocabulary.getKey()).setValue(vocabulary.getLanguages());
        //inserting the words in vocabulary
        reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabulary.getKey()).setValue(vocabulary.getDataBase());
    }
    public static void saveNativeLanguage(Context context, String nativeLan){
        reference.child(context.getString(R.string.firebase_user_setting)).child(userEmail).child(context.getString(R.string.firebase_user_native_language)).setValue(nativeLan);
    }
    /*
    usersSettings{
        "user's gmail address"{
            "native":"English"
            ...
        }
    }
    usersVocabularyOptions{
        "user's gmail address"{
            "key1 from push"{    //represent db
                languages object
             }
             "key2..."{...}
         }
    }
    usersWords{
        "user's gmail address"{
            "same key1 from push"{
                HashMap<String, ArrayList<String>> words
            }
            "key2..."{...}
    }
     */
}
