package com.mattpflance.hearthlist.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

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
                // stuff
                break;
            case JSON_ERR:
                // stuff
                break;
            case SUCCESS:
                // Store a boolean (for now) so we do not make a second API call after download starts
                SharedPreferences prefs = context.getSharedPreferences(null, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.card_download_key), ""); // TODO temp
                editor.apply();
                break;
            default:
                // Unknown status
                break;
        }
    }

}
