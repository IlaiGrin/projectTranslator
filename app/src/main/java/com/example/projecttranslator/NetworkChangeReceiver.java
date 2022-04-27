package com.example.projecttranslator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

public class NetworkChangeReceiver extends BroadcastReceiver {

    ImageView micBtn;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!isOnline(context)) {
                micBtn.setBackground(context.getDrawable(R.drawable.red_btn));
                micBtn.setTag(R.drawable.red_btn);
            }
            else {
                micBtn.setBackground(context.getDrawable(R.drawable.round_background));
                micBtn.setTag(R.drawable.round_background);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public NetworkChangeReceiver(ImageView micBtn){
        this.micBtn = micBtn;
    }

    private boolean isOnline(Context context) {
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
