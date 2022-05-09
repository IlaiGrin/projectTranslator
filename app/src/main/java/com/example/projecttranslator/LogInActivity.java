package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
EditText email, password;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.password_edit_txt);
        email = findViewById(R.id.email_edit_txt);

        findViewById(R.id.log_in_btn).setOnClickListener(view -> {
            if(!Functions.isDetailsEmpty(email, password)){
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_LONG).show())
                    .addOnSuccessListener(authResult -> startActivity(new Intent(this, MainActivity.class)));
            }
        });

        findViewById(R.id.sign_up_btn).setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));
    }
}