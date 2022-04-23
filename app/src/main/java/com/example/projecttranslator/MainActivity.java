package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
Dialog addWordDialog;
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
    }

    private void createDialog(){
        addWordDialog = new Dialog(this);
        addWordDialog.setContentView(R.layout.add_word_layout);
        addWordDialog.getWindow().setLayout(Functions.getScreenSize(this).getWidth(), Functions.getScreenSize(this).getWidth());
        addWordDialog.setCancelable(true);

        addWordDialog.show();
    }
}