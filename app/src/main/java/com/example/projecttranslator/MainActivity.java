package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
Dialog logInDialog;
ImageView plusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        plusBtn = findViewById(R.id.plus_btn);
        Intent addIntent = new Intent(this, AddWordActivity.class);
        plusBtn.setOnClickListener(view -> startActivity(addIntent));

        findViewById(R.id.log_in_btn).setOnClickListener(view -> {
            signInDialog();
        });
    }

    private void signInDialog(){
        logInDialog = new Dialog(this);
        logInDialog.setContentView(R.layout.login_layout);
        logInDialog.getWindow().setLayout(Functions.getScreenSize(this).getWidth(), Functions.getScreenSize(this).getWidth() + Functions.dpToPx(80, this));
        logInDialog.setCancelable(true);

        logInDialog.show();
    }
}