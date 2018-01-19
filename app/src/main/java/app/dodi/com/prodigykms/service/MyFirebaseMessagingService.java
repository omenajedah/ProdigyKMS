package app.dodi.com.prodigykms.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.activity.MainActivity;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;
import app.dodi.com.prodigykms.util.Utils;

/**
 * Created by User on 07/01/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> dataMessage = remoteMessage.getData();
        if (dataMessage.size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            saveNotification(dataMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private void saveNotification(Map<String, String> dataMessage) {
        if (!Utils.isAppInBackground(this)) {
            Intent pushNotification = new Intent("NEWNOTIFICATION");
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }

        MyApplication myApplication = (MyApplication) getApplication();
        SessionHelper sessionHelper = new SessionHelper(getApplicationContext());

        sessionHelper.put("INCOMINGNOTIFICATION", true);
        sessionHelper.put("JUMLAHNOTIF", sessionHelper.get("JUMLAHNOTIF", 0)+1);

        SQLite sqLite = null;

        if (myApplication != null) {
            sqLite = myApplication.sqLite;
        } else
            sqLite = new SQLite(getApplicationContext());

        if (sqLite != null) {

            ContentValues values = new ContentValues();
            for (String key : dataMessage.keySet()) {
                values.put(key, dataMessage.get(key));
            }
            SQLiteDatabase db = sqLite.openDB();
            db.insert(SQLite.NOTIFICATION_TABLE, null, values);
            sqLite.closeDB();
            sqLite.close();
        }
    }
}