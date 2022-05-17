package com.example.projecttranslator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.internal.zzt;

public class ProfileFragment extends Fragment {

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    EditText usernameEditText;
    TextView usernameDisplay;
    TextInputLayout usernameLayout;
    Spinner spinner;
    ArrayAdapter adaptor;
    Context context;
    boolean isSpinnerBuilt;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        usernameEditText = getView().findViewById(R.id.username_edit_txt);
        usernameLayout = getView().findViewById(R.id.username_edit_txt_layout);
        usernameDisplay = getView().findViewById(R.id.username_display);
        spinner = getView().findViewById(R.id.select_native_language_spinner);
        FirebaseDBManager.setUserEmail(Utils.user.getEmail());
        isSpinnerBuilt = false;
        initializeSpinner();

        usernameDisplay.setText("Username: "+Utils.user.getUsername());
        usernameLayout.setStartIconOnClickListener(view1 -> {
            usernameEditText.setVisibility(View.VISIBLE);
            usernameLayout.setEndIconDrawable(R.drawable.ic_done);
            usernameLayout.setHint("username");
        });
        usernameLayout.setEndIconOnClickListener(view1 -> {
            if(!usernameEditText.getText().toString().equals(""))
                Utils.user.updateUsername(usernameEditText.getText().toString());
            usernameDisplay.setText("Username: "+Utils.user.getUsername());
            usernameEditText.setVisibility(View.GONE);
            usernameLayout.setEndIconDrawable(0);
            usernameLayout.setHint("");
        });

        getView().findViewById(R.id.log_out_btn).setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LogInActivity.class));
        });
    }

    private void initializeSpinner(){
        //initialize the native language spinner
        spinner = getView().findViewById(R.id.select_native_language_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(isSpinnerBuilt)  //to avoid calling on first time the spinner is built
                    Utils.user.setNativeLanguage(Languages.getLanguages()[i]);
                isSpinnerBuilt = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        adaptor = new ArrayAdapter(context, R.layout.spinner_item, Languages.getLanguages());
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adaptor);
        spinner.setVisibility(View.INVISIBLE);
        if(Utils.user.getNativeLanguage() == null)
            FirebaseDBManager.getNativeLanguageFormDB(context, spinner, adaptor);   //set the position of the spinner to native language
        else {  //for efficiency - not reading from firebase every time
            spinner.setSelection(adaptor.getPosition(Utils.user.getNativeLanguage()));
            spinner.setVisibility(View.VISIBLE);
        }
    }
}
