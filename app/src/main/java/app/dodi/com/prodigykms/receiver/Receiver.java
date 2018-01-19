package app.dodi.com.prodigykms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 16/01/2018.
 */

public class Receiver extends BroadcastReceiver {

    private final String TAG = Receiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.toString());
        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {

        }
    }
}
