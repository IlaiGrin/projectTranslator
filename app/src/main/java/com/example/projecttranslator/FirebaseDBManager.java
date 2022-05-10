package com.example.projecttranslator;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBManager {

    FirebaseDatabase database;

    public FirebaseDBManager(){
        database = FirebaseDatabase.getInstance();
    }
    public void addUser(User user){
        DatabaseReference reference = database.getReference();

        String key = reference.push().getKey();
        reference.child("").child(user.getEmail()).addValueEventListener(new ValueEventListener() {     //קורה ברישום וכל פעם שהשתנה לשנות לקריאה חד פעמית
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                HashMap<String, ArrayList<String>> dataBase
            }
            "key2..."{...}
    }
     */
}
