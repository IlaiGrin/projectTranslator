package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VocabularyWordsAdaptor extends ArrayAdapter {

    private Context context;
    private HashMap<String, ArrayList<String>> dataBase;
    private Object[] sourceWords;

    public VocabularyWordsAdaptor(Context context, int resource, int textViewResourceId, HashMap<String, ArrayList<String>> dataBase) {
        super(context, resource, textViewResourceId, dataBase.keySet().toArray());      //enter source words as an array

        this.sourceWords = dataBase.keySet().toArray();
        this.context = context;
        this.dataBase = dataBase;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.list_word_layout, parent, false);
        TextView sourceWord = view.findViewById(R.id.source_word);

        sourceWord.setText(sourceWords[position].toString());
        initializeTranslationsSpinner(view.findViewById(R.id.translations_spinner), dataBase.get(sourceWords[position]));

        return view;
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
