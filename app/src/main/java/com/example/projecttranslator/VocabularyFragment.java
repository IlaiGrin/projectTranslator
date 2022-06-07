package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VocabularyFragment extends Fragment {

    public VocabularyFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vocabulary_fragment, container, false);
    }

    private EditText searchEditText;
    private TextInputLayout searchLayout;
    public static String vocabularyKey;
    private ListView listView;
    private TextView title;
    private Context context;
    static String[] sortingFormats = new String[]{"New→Old","Old→New","A→B→C"};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!Utils.user.getDictionary().isEmpty())        //already read vocabularies
            displayVocabularyOptions(getContext(), Utils.user.getDictionary());
        else
            FirebaseDBManager.readOnlyVocabularyOptions(getContext());

        context = getContext();
        searchLayout = getView().findViewById(R.id.search_edit_txt_layout);
        searchEditText = getView().findViewById(R.id.search_edit_txt);
        listView = ((Activity)context).findViewById(R.id.list_view);
        title = ((Activity)context).findViewById(R.id.vocabulary_fragment_title);

        searchLayout.setStartIconOnClickListener(view1 -> {
            if(searchEditText.hasFocus()){
                HashMap newDatabase = Utils.user.getVocabularyByKey(vocabularyKey).orderVocabularyByString(searchEditText.getText().toString());
                if (newDatabase.isEmpty())
                    Toast.makeText(context,"Not found",Toast.LENGTH_SHORT).show();
                else
                    listView.setAdapter(new VocabularyWordsAdapter(context, 0,0, newDatabase, vocabularyKey, title));
                searchEditText.clearFocus();
                //keyboard disappear
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
        });
    }

    public static void displayVocabularyOptions(Context context, List<VocabularyDB> vocabularyList){
        Spinner sortingSpinner = ((Activity)context).findViewById(R.id.select_word_sorting_spinner);
        sortingSpinner.setVisibility(View.GONE);
        TextInputLayout searchLayout = ((Activity)context).findViewById(R.id.search_edit_txt_layout);
        searchLayout.setVisibility(View.GONE);

        ListView listView = ((Activity)context).findViewById(R.id.list_view);
        TextView title = ((Activity)context).findViewById(R.id.vocabulary_fragment_title);
        if(title != null && listView != null) {
            title.setText("Options");
            listView.setAdapter(new VocabularyOptionsAdapter(context, 0, 0, vocabularyList));
            if(vocabularyList.isEmpty())
                Toast.makeText(context,"You have no vocabulary saved",Toast.LENGTH_SHORT).show();
        }
    }

    public static void displayVocabularyWords(Context context, VocabularyDB vocabularyDB){
        ListView listView = ((Activity)context).findViewById(R.id.list_view);
        TextView title = ((Activity)context).findViewById(R.id.vocabulary_fragment_title);
        TextInputLayout searchLayout = ((Activity)context).findViewById(R.id.search_edit_txt_layout);
        searchLayout.setVisibility(View.VISIBLE);
        vocabularyKey = vocabularyDB.getKey();
        title.setText(vocabularyDB.getDataBase().size()+" Words");
        HashMap sortedInsertionTime = Utils.user.getVocabularyByKey(vocabularyKey).orderVocabularyByInsertionTime(true);
        listView.setAdapter(new VocabularyWordsAdapter(context, 0,0, sortedInsertionTime, vocabularyKey, title));

        initializeSortingSpinner(context);
    }

    public static void initializeSortingSpinner(Context context){
        //initialize the sorting spinner
        Spinner sortingSpinner = ((Activity)context).findViewById(R.id.select_word_sorting_spinner);
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortWords(context, sortingFormats[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter adaptor = new ArrayAdapter(context, R.layout.spinner_item, sortingFormats);
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(adaptor);
        sortingSpinner.setVisibility(View.VISIBLE);
    }

    public static void sortWords(Context context, String format){
        ListView listView = ((Activity)context).findViewById(R.id.list_view);
        TextView title = ((Activity)context).findViewById(R.id.vocabulary_fragment_title);
        if(format.equals(sortingFormats[0])){       //new to old
            HashMap sortedInsertionTime = Utils.user.getVocabularyByKey(vocabularyKey).orderVocabularyByInsertionTime(true);
            listView.setAdapter(new VocabularyWordsAdapter(context, 0,0, sortedInsertionTime, vocabularyKey, title));
        }
        if(format.equals(sortingFormats[1])){       //old to new
            HashMap sortedInsertionTime = Utils.user.getVocabularyByKey(vocabularyKey).orderVocabularyByInsertionTime(false);
            listView.setAdapter(new VocabularyWordsAdapter(context, 0,0, sortedInsertionTime, vocabularyKey, title));
        }
        if(format.equals(sortingFormats[2])){       //ABC
            HashMap sortedABC = Utils.user.getVocabularyByKey(vocabularyKey).orderVocabularyByACB();
            listView.setAdapter(new VocabularyWordsAdapter(context, 0,0, sortedABC, vocabularyKey, title));
        }
    }
}
