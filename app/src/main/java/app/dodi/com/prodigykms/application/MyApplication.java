package app.dodi.com.prodigykms.application;

import android.app.Application;

import app.dodi.com.prodigykms.util.SQLite;

/**
 * Created by User on 07/01/2018.
 */

public class MyApplication extends Application {

    public SQLite sqLite;

    @Override
    public void onCreate() {
        super.onCreate();
        sqLite = new SQLite(this);

    }
}
