package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Size;
import android.widget.EditText;

import java.util.Random;

public class Utils {
    public static  User user = new User();
    public static Random rnd = new Random();
    public static int dpToPx(int dps, Context context) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    public static Size getScreenSize(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }
    public static boolean isDetailsEmpty(EditText email, EditText password){
        if(email.getText().toString().equals("")){
            email.setError("Required");
            email.requestFocus();
            return true;
        } else if(!email.getText().toString().contains("@gmail.com")){
            email.setError("Incorrect format");
            email.requestFocus();
            return true;
        } else if(password.getText().toString().equals("")){
            password.setError("Required");
            password.requestFocus();
            return true;
        }
        return false;
    }
}
