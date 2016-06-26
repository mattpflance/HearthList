package com.mattpflance.hearthlist.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.mattpflance.hearthlist.CardDetailsActivity;
import com.mattpflance.hearthlist.R;
import com.mattpflance.hearthlist.data.HearthListContract;
import com.mattpflance.hearthlist.models.Card;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class WidgetIntentService extends IntentService {

    private static final String TAG = WidgetIntentService.class.getSimpleName();

    public WidgetIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        Cursor data = getContentResolver().query(
                HearthListContract.CardEntry.CONTENT_URI,
                Card.CARD_COLUMNS,
                null,
                null,
                null);

        if (data == null) {
            return;
        }

        // Lets get some random number
        int max = data.getCount();
        Random random = new Random();
        int randCardIndex = random.nextInt(max);

        if (!data.move(randCardIndex)) {
            data.close();
            return;
        }

        // Make a Card!
        Card card = new Card(data);

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            // TODO Maybe add more widgets sizes?
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_regular);

            // Add the data to the RemoteViews
            // TODO get placeholder
            Bitmap bmp = null;
            try {
                bmp = Glide.with(this)
                        .load(card.getRegImageUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Load from internal
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            }  catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            views.setImageViewBitmap(R.id.widget_card_image, bmp);

            // Create an Intent to launch the card's Details
            Intent launchIntent = new Intent(this, CardDetailsActivity.class);
            launchIntent.putExtra(CardDetailsActivity.CARD_ARG_ID, card);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        // close cursor
        data.close();
    }

//    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
//        // Prior to Jelly Bean, widgets were always their default size
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
//        }
//        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
//        // retrieved from the newly added App Widget Options
//        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
//        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
//        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
//            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
//            // The width returned is in dp, but we'll convert it to pixels to match the other widths
//            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
//                    displayMetrics);
//        }
//        return  getResources().getDimensionPixelSize(R.dimen.widget_default_width);
//    }
}
