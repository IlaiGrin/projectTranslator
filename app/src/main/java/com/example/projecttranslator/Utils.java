package com.example.projecttranslator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Utils {
    public static  User user;
    public static SharedPreferences sp;
    public static File file;

    public static void putStringInSP(Context context, String key, String value){
        sp = context.getApplicationContext().getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    public static String getStringFromSP(Context context, String key){
        sp = context.getApplicationContext().getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static int dpToPx(int dps, Context context) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public static Size getScreenSize(Context context) {
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

    public static void nativeLanguageDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.native_dialog);
        dialog.setCancelable(true);
        dialog.show();
    }
}
