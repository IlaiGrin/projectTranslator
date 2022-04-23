package com.example.projecttranslator;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Size;

import java.util.Random;

public class Functions {
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
}
