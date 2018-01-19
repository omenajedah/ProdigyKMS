package app.dodi.com.prodigykms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.DownloadObject;
import app.dodi.com.prodigykms.util.DownloaderHelper;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;

/**
 * Created by User on 06/01/2018.
 */

public class SplashScreen extends AppCompatActivity implements DownloaderHelper.DownloadListener {

    private List<DownloadObject> downloadObjectList = new ArrayList<>();
    private SessionHelper sessionHelper;
    private DownloaderHelper downloaderHelper;
    private SQLite sqlite;
    private final String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionHelper = new SessionHelper(this);
        if (!sessionHelper.isLogin()) goToLogin();
        setContentView(R.layout.activity_splashscreen);


        downloaderHelper = new DownloaderHelper(this);
        sqlite = ((MyApplication) getApplicationContext()).sqLite;

        downloaderHelper.setDownloadListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDownload();
    }

    private void setDownload() {
        if (sessionHelper.isLogin()) {
            DownloadObject postdata = new DownloadObject(null, "POSTDATA", RequestorHelper.GETALLPOST);
            DownloadObject commentdata = new DownloadObject(null, "COMMENTDATA", RequestorHelper.GETALLCOMMENT);
            DownloadObject userdata = new DownloadObject(null, "USERDATA", RequestorHelper.GETALLUSER);
            DownloadObject divisiondata = new DownloadObject(null, "DIVISIONDATA", RequestorHelper.GETALLDIVISION);
            DownloadObject about = new DownloadObject(null, "ABOUTAPPS", RequestorHelper.GETABOUTAPPS);
            downloadObjectList.add(divisiondata);
            downloadObjectList.add(userdata);
            downloadObjectList.add(commentdata);
            downloadObjectList.add(about);
            //downloadObjectList.add(postdata);

        } else {
            DownloadObject divisiondata = new DownloadObject(null, "DIVISIONDATA", RequestorHelper.GETALLDIVISION);
            downloadObjectList.add(divisiondata);
        }
        downloaderHelper.start(downloadObjectList);
    }

    @Override
    public void onDownloadProgress(int pos) {

    }

    @Override
    public void onDownloadSuccess(int pos, Handler mainThread) {
        DownloadObject current = downloadObjectList.get(pos);
        JSONObject result = current.getResponse();
        Log.i(TAG, String.format("TAG %s, Response %s", current.getTag(), current.getResponse()));
        try {
            switch (current.getTag()) {
                case "POSTDATA":
                    if (result.getInt("RecordCount") == 0) {
                        return;
                    }
                    sqlite.savePost(result.getJSONArray("DataRow"));
                    break;
                case "COMMENTDATA":
                    if (result.getInt("RecordCount") == 0) {
                        return;
                    }
                    sqlite.saveCommentPost(result.getJSONArray("DataRow"));
                    break;
                case "DIVISIONDATA":
                    if (result.getInt("RecordCount") == 0) {
                        return;
                    }
                    sqlite.saveDivision(result.getJSONArray("DataRow"));
                    break;
                case "USERDATA":
                    if (result.getInt("RecordCount") == 0) {
                        return;
                    }
                    sqlite.saveUser(result.getJSONArray("DataRow"));
                    break;
                case "ABOUTAPPS":
                    sessionHelper.putString("title_about", result.getString("title"));
                    sessionHelper.putString("content_about", result.getString("konten"));
                    break;
            }
            Log.i(TAG, "SUCCESS TAG " + current.getTag());

        } catch (JSONException e) {
            Log.e(TAG, "JsonException download pos " + pos);
            Log.e(TAG, "JsonException download url " + current.getUrl());
            Log.e(TAG, "JsonException download tag " + current.getTag());
            Log.e(TAG, "JsonException download error " + e.toString());
        }


    }

    @Override
    public void onDownloadError(int pos, ANError error) {
        DownloadObject current = downloadObjectList.get(pos);
        Log.e(TAG, "Error download pos " + pos);
        Log.e(TAG, "Error download url " + current.getUrl());
        Log.e(TAG, "Error download tag " + current.getTag());
        Log.e(TAG, "Error download error " + error.getErrorBody());
    }

    @Override
    public void onDownloadsFinished() {
        goToLogin();
    }

    public void goToLogin() {
        Log.i(TAG,"CALLED FINISH");
        Intent intent = new Intent(this, LoginActivity.class);
        if (sessionHelper.isLogin()) {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
