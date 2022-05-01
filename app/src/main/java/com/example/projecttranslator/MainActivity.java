package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
Dialog logInDialog;
ImageView plusBtn;
Button logInBtn;
FirebaseAuth firebaseAuth;
FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logInBtn = findViewById(R.id.log_in_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(user != null)
            logInBtn.setText("Log out");

        findViewById(R.id.plus_btn).setOnClickListener(view -> startActivity(new Intent(this, AddWordActivity.class)));

        logInBtn.setOnClickListener(view -> {
            if(user != null) {
                firebaseAuth.signOut();
                user = null;
                logInBtn.setText("Log in");
            } else
                startActivity(new Intent(this, LogInActivity.class));
        });
    }

    /*private void signInDialog(){
        logInDialog = new Dialog(this);
        logInDialog.setContentView(R.layout.activity_login);
        logInDialog.getWindow().setLayout(Functions.getScreenSize(this).getWidth(), Functions.getScreenSize(this).getWidth() + Functions.dpToPx(65, this));
        logInDialog.setCancelable(true);

        logInDialog.show();
    }*/
}