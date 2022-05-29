package com.example.projecttranslator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
BottomNavigationView navigationView;
Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Utils.user = new User(this);
        context = this;
        Utils.putStringInSP(context, "current_fragment", "3");
        if(!Utils.user.isLoggedIn())
            startActivity(new Intent(this, LogInActivity.class));
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();     //start with profile fragment
        navigationView = findViewById(R.id.bottomNavBar);
        navigationView.setOnNavigationItemSelectedListener(bottomNavSelection);

        // Request permissions
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, 1);

        if(getIntent() != null && getIntent().getBooleanExtra("open_translator", false))    //when returning from camera
            navigationView.findViewById(R.id.translator).callOnClick();
        Utils.putStringInSP(context, "user_email", Utils.user.getEmail());
    }

    @Override
    public void onBackPressed() { } //disable back press

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavSelection = item -> {

        // Each fragment gets an index between 1-3, starting from the right side of the Nav bar
        // Profile - 3, Translator - 2, Vocabulary - 1

        int currentFragment = Integer.parseInt(Utils.getStringFromSP(context, "current_fragment"));

        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.profile:
                fragment = new ProfileFragment();
                Utils.putStringInSP(context, "current_fragment", "3");
                break;
            case R.id.translator:
                fragment = new AddWordFragment();
                Utils.putStringInSP(context, "current_fragment", "2");
                break;
            case R.id.vocabulary:
                fragment = new VocabularyFragment();
                Utils.putStringInSP(context, "current_fragment", "1");
                break;
        }

        // Parse the fragment to travel to
        int travelToFragment = Integer.parseInt(Utils.getStringFromSP(context, "current_fragment"));
        int in_anim , out_anim;

        // Following the algorithm stated above: assign values to the relative left fragment and the relative right one
        if (currentFragment < travelToFragment){
            in_anim = R.anim.slide_in_left;
            out_anim = R.anim.slide_out_right;
        }
        else if (currentFragment > travelToFragment){
            in_anim = R.anim.slide_in_right;
            out_anim = R.anim.slide_out_left;
        }
        else
            return true;

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(in_anim, out_anim)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        return true;
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                    requestPermissions(permissions, 1);
                }
            }
        }
    }
}