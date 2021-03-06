package com.example.projecttranslator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class VocabularyWordsAdapter extends ArrayAdapter {

    private Context context;
    private HashMap<String, ArrayList<String>> dataBase;
    private ArrayList<String> sourceWords;
    private String vocabularyKey;
    private TextView title;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public VocabularyWordsAdapter(Context context, int resource, int textViewResourceId, HashMap<String, ArrayList<String>> dataBase, String vocabularyKey, TextView title) {
        super(context, resource, textViewResourceId, dataBase.keySet().toArray());      //enter source words as an array

        Object[] words = dataBase.keySet().toArray();

        //initialize speaker
        Utils.speak(context, "");

        //converting to list
        this.sourceWords = new ArrayList<>();
        for (Object word:words)
            this.sourceWords.add(word.toString());
        this.context = context;
        this.dataBase = dataBase;
        this.vocabularyKey = vocabularyKey;
        this.title = title;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = null;
        if(position < sourceWords.size()) { //when deleting a word for some reason, the position is equal to the size
            view = layoutInflater.inflate(R.layout.list_word_layout, parent, false);
            TextView sourceWord = view.findViewById(R.id.source_word);
            TextView numOfTranslations = view.findViewById(R.id.number_of_translations);

            sourceWord.setText(sourceWords.get(position));
            sourceWord.setOnClickListener(view1 -> Utils.speak(context, sourceWords.get(position)));
            numOfTranslations.setText(dataBase.get(sourceWords.get(position)).size() + "");
            initializeTranslationsSpinner(view.findViewById(R.id.translations_spinner), dataBase.get(sourceWords.get(position)));

            view.findViewById(R.id.delete_word_img).setOnClickListener(view1 ->{
                if(NetworkChangeReceiver.isOnline(context))
                    alertDialog(position);
                else
                    Toast.makeText(context,"No Internet connection",Toast.LENGTH_SHORT).show();
            });
            return view;
        }
        convertView.setVisibility(View.GONE);
        return convertView;
    }

    private void alertDialog(int position){
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Delete word")
                .setMessage("Are you sure you want to delete the word:\n"+sourceWords.get(position))
                .setCancelable(true)
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Yes, delete", (dialogInterface, i)->{
                    title.setText(Integer.parseInt(title.getText().toString().split(" ", 2)[0]) - 1 +" Words");    //get the number from the previous title, -1, and update
                    //remove from user's dictionary
                    Utils.user.removeWord(vocabularyKey, sourceWords.get(position));
                    //remove from listView
                    dataBase.remove(sourceWords.get(position));
                    sourceWords.remove(position);
                    notifyDataSetChanged();
                    if(Utils.user.getVocabularyByKey(vocabularyKey) == null)    //if the vocabulary is empty
                        VocabularyFragment.displayVocabularyOptions(context, Utils.user.getDictionary());
                    dialogInterface.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initializeTranslationsSpinner(Spinner spinner, ArrayList<String> translations){
        //initialize the translations spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter adaptor = new ArrayAdapter(context, R.layout.spinner_item, translations);
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adaptor);
    }

}
