package com.veed.oduchantingapp.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.veed.oduchantingapp.DatabaseHandler.DatabaseHandler;
import com.veed.oduchantingapp.HomeActivity;
import com.veed.oduchantingapp.MyApplication;
import com.veed.oduchantingapp.R;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Saboor Salaam on 10/19/2016.
 */

public class Utils {


    public final static String STAGGERED_GRID_MODE = "1";
    public final static String LARGE_CARD_MODE = "2";
    public static final int NOTIFICATION_REQUEST_CODE = 0;
    public static final int NOTIFICATION_ID = 777777;
    public static final int INTERVAL_THREE_DAYS = 345600000;
    public static final int INTERVAL_ONE_AND_HALF_DAYS = 129600000;
    public static final int INTERVAL_EIGHT_HOURS = 28800000;


    public static int [] patterns = {R.drawable.pattern1,
            R.drawable.pattern2,
            R.drawable.pattern3,
            R.drawable.pattern4,
            R.drawable.pattern5,
            R.drawable.pattern6,
            R.drawable.pattern7,
            R.drawable.pattern8,
            R.drawable.pattern9,
            R.drawable.pattern10,
            R.drawable.pattern11,
            R.drawable.pattern12
    };

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static void notifyUserOfNewContent(final Context context, int type){
        final NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.cancel(NOTIFICATION_ID);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        MyApplication.all_chants = DatabaseHandler.getInstance(context).getAllChants();

        String content = "";

        for(int i = 0; i < MyApplication.all_chants.size() && i <4; i++){

            content += MyApplication.all_chants.get(i).name + ", ";
        }

        if(MyApplication.all_chants.size() > 0 ) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            //.setLargeIcon(R.drawable.ic_launcher)
                            .setColor(context.getResources().getColor(R.color.light_primary))
                            .setContentTitle(getRandom(new String[]{"Practice Makes Perfect!", "Keep up the good work!", "When you have time"}))
                            .setContentText("Just a reminder to practice " + content)
                            .setLights(Color.BLUE, 1000, 1000)
                            .setAutoCancel(true)
                            .setContentIntent(resultPendingIntent);

            // Builds the notification and issues it.
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

        }


        }



    public static void setNotificationAlarm(Context context){
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, intent, 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //      AlarmManager.INTERVAL_DAY, alarmIntent);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                INTERVAL_ONE_AND_HALF_DAYS, alarmIntent);
        Log.d("Alarmmanager", "Alarm manager set");
    }


    public static List<String> extractWordsFromQuery(String query){
        List<String> words = new ArrayList<>();
        if(query !=null) {
            BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
            bi.setText(query);
            //
            // Iterates the boundary / breaks
            //
            int lastIndex = bi.first();
            while (lastIndex != BreakIterator.DONE) {
                int firstIndex = lastIndex;
                lastIndex = bi.next();

                if (lastIndex != BreakIterator.DONE
                        && Character.isLetterOrDigit(
                        query.charAt(firstIndex))) {
                    String word = query.substring(firstIndex, lastIndex);
                    if (!Utils.isArticle(word)) {
                        words.add(word);
                        System.out.println("'" + word + "' found at (" +
                                firstIndex + ", " + lastIndex + ")");
                    }

                }
            }
        }
        return words;
    }


    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static boolean isArticle(String word) {

        if (word.equalsIgnoreCase("the")) {
            return true;
        }
        if(word.equalsIgnoreCase("is")){
            return true;
        }
        if(word.equalsIgnoreCase("and")){
            return true;
        }
        if(word.equalsIgnoreCase("an")){
            return true;
        }
        if(word.equalsIgnoreCase("a")){
            return true;
        }
        if(word.equalsIgnoreCase("was")){
            return true;
        }
        if(word.equalsIgnoreCase("for")){
            return true;
        }
        return false;
    }


    public static void launchGooglePlay(Activity context, int YOUR_REQUEST_CODE ){
        try {
            context.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.veed.oduchantingapp")), YOUR_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.veed.oduchantingapp")), YOUR_REQUEST_CODE);
        }
    }



}
