package com.example.projecttranslator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class NetworkChangeReceiver extends BroadcastReceiver {

    ImageView micBtn;
    Button save1, save2;
    Spinner nativeSpinner;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!isOnline(context)) {
                if(micBtn == null)
                    nativeSpinner.setEnabled(false);
                else {
                    micBtn.setBackground(context.getDrawable(R.drawable.red_btn));
                    micBtn.setTag(R.drawable.red_btn);
                    save1.setBackgroundColor(context.getColor(R.color.red));
                    save1.setTag(R.color.red);
                    save2.setBackgroundColor(context.getColor(R.color.red));
                }
            }
            else {
                if(micBtn == null)
                    nativeSpinner.setEnabled(true);
                else {
                    micBtn.setBackground(context.getDrawable(R.drawable.round_background));
                    micBtn.setTag(R.drawable.round_background);
                    save1.setBackgroundColor(context.getColor(R.color.teal_200));
                    save1.setTag("");
                    save2.setBackgroundColor(context.getColor(R.color.teal_200));
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public NetworkChangeReceiver(ImageView micBtn, Button save1, Button save2){
        this.micBtn = micBtn;
        this.save1 = save1;
        this.save2 = save2;
    }

    public NetworkChangeReceiver(Spinner nativeSpinner){
        this.nativeSpinner = nativeSpinner;
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
