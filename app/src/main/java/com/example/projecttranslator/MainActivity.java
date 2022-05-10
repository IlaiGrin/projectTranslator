package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
Dialog logInDialog;
ImageView plusBtn;
Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();     //start with profile fragment
        BottomNavigationView navigationView = findViewById(R.id.bottomNavBar);
        navigationView.setOnNavigationItemSelectedListener(navListener);

        if(getIntent() != null && getIntent().getBooleanExtra("open_translator", false))    //when returning from camera
            navigationView.findViewById(R.id.translator).callOnClick();

        if(!Utils.user.isLoggedIn())
            startActivity(new Intent(this, LogInActivity.class));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()){
            case R.id.profile:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.translator:
                selectedFragment = new AddWordFragment();
                break;
            case R.id.vocabulary:
                selectedFragment = new VocabularyFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();  //replacing the fragment
        return true;
    };
}