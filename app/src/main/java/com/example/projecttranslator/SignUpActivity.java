package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
EditText email, password;
FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.password_edit_txt);
        email = findViewById(R.id.email_edit_txt);

        findViewById(R.id.sign_up_btn).setOnClickListener(view -> {
            if(!Functions.isDetailsEmpty(email, password)){
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_LONG).show())
                        .addOnSuccessListener(authResult1 -> {
                            Toast.makeText(this, "user was created", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_LONG).show())
                                    .addOnSuccessListener(authResult2 -> startActivity(new Intent(this, MainActivity.class)));
                        });
            }
        });

        findViewById(R.id.backward_img).setOnClickListener(view -> startActivity(new Intent(this, LogInActivity.class)));
    }
}