package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class VocabularyOptionsAdapter extends ArrayAdapter {
    private Context context;
    private List<VocabularyDB> objects;

    public VocabularyOptionsAdapter(Context context, int resource, int textViewResourceId, List<VocabularyDB> objects){
        super(context, resource, textViewResourceId, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.list_option_layout,parent,false);
        TextView fromLanguage = view.findViewById(R.id.from_language_option);
        TextView toLanguage = view.findViewById(R.id.to_language_option);
        fromLanguage.setText(objects.get(position).getFromLanguage());
        toLanguage.setText(objects.get(position).getToLanguage());

        view.setOnClickListener(view1 ->{
            VocabularyDB vocabularyDB = objects.get(position);
            if(Utils.user.getVocabularyByKey(vocabularyDB.getKey()).getDataBase().size() != 0){
                VocabularyFragment.displayVocabularyWords(context, vocabularyDB);
            } else
                FirebaseDBManager.readWordsFromVocabulary(context, vocabularyDB);
        });
        return view;
    }
}
