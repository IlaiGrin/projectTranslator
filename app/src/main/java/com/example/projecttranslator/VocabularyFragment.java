package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class VocabularyFragment extends Fragment {

    public VocabularyFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vocabulary_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!Utils.user.getDictionary().isEmpty())        //already read vocabularies
            displayVocabularyOptions(getContext(), Utils.user.getDictionary());
        else
            FirebaseDBManager.readOnlyVocabularyOptions(getContext());

        Utils.putStringInSP(getContext(),"previous_fragment", "vocabulary");
    }

    public static void displayVocabularyOptions(Context context, List<VocabularyDB> vocabularyList){
        ListView listView = ((Activity)context).findViewById(R.id.list_view);
        TextView title = ((Activity)context).findViewById(R.id.vocabulary_fragment_title);
        if(title != null && listView != null) {
            title.setText("Options");
            listView.setAdapter(new VocabularyOptionsAdapter(context, 0, 0, vocabularyList, listView, title));
        }
    }
}
