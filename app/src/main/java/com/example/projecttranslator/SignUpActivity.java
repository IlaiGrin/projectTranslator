package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
EditText email, password, username;
String nativeLanguage;
Spinner spinner;
ArrayAdapter adaptor;
FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.password_edit_txt);
        email = findViewById(R.id.email_edit_txt);
        username = findViewById(R.id.username_edit_txt);
        initializeSpinner();

        findViewById(R.id.sign_up_btn).setOnClickListener(view -> {
            if(!Utils.isDetailsEmpty(email, password)){
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_LONG).show())
                        .addOnSuccessListener(authResult1 -> {
                            Toast.makeText(this, "User was created", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_LONG).show())
                                    .addOnSuccessListener(authResult2 ->{
                                        Utils.user = new User(getApplicationContext());
                                        FirebaseDBManager.setUserEmail(Utils.user.getEmail());
                                        Utils.user.updateUsername(username.getText().toString());   //add username
                                        Utils.user.setNativeLanguage(nativeLanguage);
                                        FirebaseDBManager.addUser(this);
                                        startActivity(new Intent(this, MainActivity.class));
                                    });
                        });
            }
        });

        findViewById(R.id.native_language_info_img).setOnClickListener(view1 -> Utils.nativeLanguageDialog(this));

        findViewById(R.id.backward_img).setOnClickListener(view -> startActivity(new Intent(this, LogInActivity.class)));
    }
    private void initializeSpinner(){
        //initialize the native language spinner
        spinner = findViewById(R.id.select_native_language_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nativeLanguage = Languages.getLanguages()[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        adaptor = new ArrayAdapter(this, R.layout.spinner_item, Languages.getLanguages());
        adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adaptor);
    }
}