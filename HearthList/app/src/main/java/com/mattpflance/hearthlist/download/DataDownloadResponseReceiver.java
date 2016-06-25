package com.mattpflance.hearthlist.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.mattpflance.hearthlist.HearthListApplication;
import com.mattpflance.hearthlist.R;

public class DataDownloadResponseReceiver extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "com.mattpflance.hearthlist.BROADCAST";
    public static final String STATUS = "com.mattpflance.hearthlist.WORK_STATUS";

    public static final int REQUEST_ERR = 0;
    public static final int JSON_ERR = 1;
    public static final int SUCCESS = 2;

    public DataDownloadResponseReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(STATUS, 0);
        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        switch (status) {
            case REQUEST_ERR:
                // TODO handle error
                break;
            case JSON_ERR:
                // TODO handle error
                break;
            case SUCCESS:
                SharedPreferences prefs = context.getSharedPreferences(null, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                String cardSet = ((HearthListApplication) context.getApplicationContext()).getTMCardSet();
                editor.putString(context.getString(R.string.card_download_key), cardSet);
                editor.apply();
                break;
            default:
                // TODO handle unknown status
                break;
        }
    }

}
