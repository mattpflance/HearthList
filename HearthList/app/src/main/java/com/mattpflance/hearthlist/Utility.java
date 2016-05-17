package com.mattpflance.hearthlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Class with common methods
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

//    // convert from ArrayList<String> to String
//    public static String listToString(ArrayList<String> list) {
//        // Using the Gson library makes this really easy
//        return new Gson().toJson(list);
//    }
//
//    // convert from String to ArrayList<String>
//    public static ArrayList<String> stringToList(String str) {
//        // str was generated using Gson. Convert back to ArrayList
//        Type type = new TypeToken<ArrayList<String>>() {}.getType();
//        return new Gson().fromJson(str, type);
//    }

    // Check the current state of internet connection
    public static boolean isConnectedToInternet(Context context) {

        // Query active network
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
