package com.example.projecttranslator;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameEditText = getView().findViewById(R.id.username_edit_txt);
        usernameLayout = getView().findViewById(R.id.username_edit_txt_layout);
        usernameDisplay = getView().findViewById(R.id.username_display);

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
}
