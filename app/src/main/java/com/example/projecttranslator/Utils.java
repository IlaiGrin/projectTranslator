package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Random;

public class Utils {
    public static  User user;
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

    public static void setViewsEnable(LinearLayout layout, boolean isEnable){
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if(child instanceof LinearLayout) {     //if it is a linear layout
                for (int j = 0; j < ((LinearLayout)child).getChildCount(); j++)
                    ((LinearLayout)child).getChildAt(j).setEnabled(isEnable);
            }
            if(child instanceof RelativeLayout) {     //if it is a relative layout
                for (int j = 0; j < ((RelativeLayout)child).getChildCount(); j++)
                    ((RelativeLayout)child).getChildAt(j).setEnabled(isEnable);
            }
            child.setEnabled(isEnable);
        }
    }

    public static void setProgressBar(LinearLayout layout, ProgressBar progressBar, boolean inProgress) {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            setViewsEnable(layout, false);
        }else {
            progressBar.setVisibility(View.GONE);
            setViewsEnable(layout, true);
        }
    }
}
