package com.example.projecttranslator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
BottomNavigationView navigationView;
Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Utils.user = new User(getApplicationContext());
        context = this;
        Utils.putStringInSP(context, "current_fragment", "3");
        if(!Utils.user.isLoggedIn())
            startActivity(new Intent(this, LogInActivity.class));
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();     //start with profile fragment
        navigationView = findViewById(R.id.bottomNavBar);
        navigationView.setOnNavigationItemSelectedListener(bottomNavSelection);

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
}