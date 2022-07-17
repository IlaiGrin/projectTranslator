package com.example.projecttranslator;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FirebaseDBManager {

    private static DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static String userEmail;

    public FirebaseDBManager(){}

    public static void setUserEmail(String email){  //remove "." because firebase do not allow "."
        if(email != null)
            userEmail = email.replace(".","");
    }

    public static void deleteVocabularyOption(Context context, String vocabularyKey){
        if(NetworkChangeReceiver.isOnline(context))
            reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).child(vocabularyKey).removeValue();
    }

    //writers
    public static void addUser(Context context){
        if(NetworkChangeReceiver.isOnline(context)){
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
    }

    //updates words in DB
    public static void updateDataBase(Context context, VocabularyDB vocabulary){    //save btns are disable so user can't call this without internet
        reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabulary.getKey()).child(context.getString(R.string.words_translations)).setValue(vocabulary.getDataBase());
        reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabulary.getKey()).child(context.getString(R.string.words_order)).setValue(vocabulary.getDataBaseOrder());
    }

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

    //readers
    public static void readVocabularyDBs(Context context, LinearLayout layout, ProgressBar progressBar){
        if(NetworkChangeReceiver.isOnline(context)){
            Utils.setProgressBar(layout, progressBar, true);
            //read options
            reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        VocabularyDB vocabulary = new VocabularyDB(childDataSnapshot.getValue(Languages.class), context);
                        vocabulary.setKey(childDataSnapshot.getKey());
                        if(Utils.user.getVocabularyByKey(vocabulary.getKey()) == null)  //if the DB isn't saved already in user
                            Utils.user.addVocabularyDB(vocabulary);
                    }
                    readWords(context, layout, progressBar);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    public static void readWords(Context context, LinearLayout layout, ProgressBar progressBar){
        if(NetworkChangeReceiver.isOnline(context)){
            //read words
            reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        Utils.user.getVocabularyByKey(childDataSnapshot.getKey()).setDataBase((HashMap<String, ArrayList<String>>) childDataSnapshot.child(context.getString(R.string.words_translations)).getValue());
                        Utils.user.getVocabularyByKey(childDataSnapshot.getKey()).setDataBaseOrder((HashMap<String, Long>) childDataSnapshot.child(context.getString(R.string.words_order)).getValue());
                    }
                    Utils.setProgressBar(layout, progressBar, false);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    public static void readOnlyVocabularyOptions(Context context){
        if(NetworkChangeReceiver.isOnline(context)){
            //read options
            reference.child(context.getString(R.string.firebase_user_vocabulary_options)).child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        VocabularyDB vocabulary = new VocabularyDB(childDataSnapshot.getValue(Languages.class), context);
                        vocabulary.setKey(childDataSnapshot.getKey());
                        if(Utils.user.getVocabularyByKey(vocabulary.getKey()) == null)  //if the DB isn't saved already in user
                            Utils.user.addVocabularyDB(vocabulary);
                    }
                    VocabularyFragment.displayVocabularyOptions(context, Utils.user.getDictionary());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    public static void readWordsFromVocabulary(Context context, VocabularyDB vocabularyDB){
        if(NetworkChangeReceiver.isOnline(context)){
            reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).child(vocabularyDB.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    vocabularyDB.setDataBase((HashMap<String, ArrayList<String>>) snapshot.child(context.getString(R.string.words_translations)).getValue());
                    vocabularyDB.setDataBaseOrder((HashMap<String, Long>) snapshot.child(context.getString(R.string.words_order)).getValue());
                    VocabularyFragment.displayVocabularyWords(context, vocabularyDB);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static void readRandomWords(Context context, int numOfWords, String email, ArrayList<String> words, ArrayList<String> translations, int appWidgetId){
        if(NetworkChangeReceiver.isOnline(context)){
            Random rnd = new Random();
            reference = FirebaseDatabase.getInstance().getReference();
            setUserEmail(email);
            int[] wordsArraySize = {numOfWords};
            int[] totalWordsCounter = {0};
            reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).orderByKey().keepSynced(true);
            reference.child(context.getString(R.string.firebase_user_words)).child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    while (words.size() < wordsArraySize[0]) {
                        totalWordsCounter[0] = 0;
                        for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                            HashMap<String, ArrayList<String>> database = (HashMap<String, ArrayList<String>>)childDataSnapshot.child(context.getString(R.string.words_translations)).getValue();
                            totalWordsCounter[0] = totalWordsCounter[0] + database.size();
                            if(rnd.nextBoolean() && words.size() < numOfWords) {    //random vocabulary
                                //get a random key = word
                                String randomWord = database.keySet().toArray()[rnd.nextInt(database.size())].toString();
                                if(!words.contains(randomWord)) {
                                    words.add(randomWord);
                                    translations.add(database.get(randomWord).toString());
                                }
                            }
                        }
                        if(totalWordsCounter[0] < wordsArraySize[0])    //to little words
                            wordsArraySize[0] = totalWordsCounter[0];
                    }
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.daily_words_stack_view);  //trigger onDataSetChanged in WidgetItemFactory
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    readRandomWords(context, numOfWords, email, words, translations, appWidgetId);  //retry
                }
            });
        }
    }

    public static void getNativeLanguageFormDB(Context context, Spinner spinner, ArrayAdapter adapter, RelativeLayout mainLayout){
        if(NetworkChangeReceiver.isOnline(context)){
            reference.child(context.getString(R.string.firebase_user_setting)).child(userEmail).child(context.getString(R.string.firebase_user_native_language)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nativeLan = snapshot.getValue(String.class);
                    Utils.user.setNativeLanguage(nativeLan);
                    spinner.setSelection(adapter.getPosition(nativeLan));
                    spinner.setVisibility(View.VISIBLE);
                    MainActivity.stopSplashScreen(context, mainLayout);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } else
            new Handler().postDelayed(()->MainActivity.stopSplashScreen(context, mainLayout), 1500);

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
