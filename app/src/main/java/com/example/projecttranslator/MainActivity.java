package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
Dialog logInDialog;
ImageView plusBtn;
Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViewById(R.id.plus_btn).setOnClickListener(view -> startActivity(new Intent(this, AddWordActivity.class)));

        logInBtn = findViewById(R.id.log_in_btn);
        logInBtn.setOnClickListener(view -> startActivity(new Intent(this, LogInActivity.class)));
    }

    /*private void signInDialog(){
        logInDialog = new Dialog(this);
        logInDialog.setContentView(R.layout.activity_login);
        logInDialog.getWindow().setLayout(Functions.getScreenSize(this).getWidth(), Functions.getScreenSize(this).getWidth() + Functions.dpToPx(65, this));
        logInDialog.setCancelable(true);

        logInDialog.show();
    }*/
}