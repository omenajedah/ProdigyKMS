package app.dodi.com.prodigykms.util;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.core.ANExecutor;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by User on 02/01/2018.
 */

public class RequestorHelper {

    //declare public url
    public static final String LOGINURL = "https://dodia-app.herokuapp.com/login";

    public static final String GETALLPOSTFROMDIV = "https://dodia-app.herokuapp.com/control/getAllPostFromDiv";


    public static final String GETALLPOST = "https://dodia-app.herokuapp.com/control/getAllPost";
    public static final String GETALLCOMMENT = "https://dodia-app.herokuapp.com/control/getAllComment";
    public static final String GETALLUSER = "https://dodia-app.herokuapp.com/control/getAllUser";

    public static final String SENDTOKEN = "https://dodia-app.herokuapp.com/control/putFirebaseId";

    public static final String SETABOUTAPPS = "https://dodia-app.herokuapp.com/control/setAboutApps";
    public static final String GETABOUTAPPS = "https://dodia-app.herokuapp.com/control/getAboutApps";

    public static final String GETALLDIVISION = "https://dodia-app.herokuapp.com/control/getAllDivision";
    public static final String ADDDIVISION = "https://dodia-app.herokuapp.com/control/addDivision";
    public static final String DELETEDIVISION = "https://dodia-app.herokuapp.com/control/deleteDivision";

    public static final String GETPOST = "https://dodia-app.herokuapp.com/%s/getPost";
    public static final String ADDPOST = "https://dodia-app.herokuapp.com/%s/addPost";
    public static final String EDITPOST = "https://dodia-app.herokuapp.com/%s/editPost";
    public static final String GETCOMMENT = "https://dodia-app.herokuapp.com/%s/getCommentPost";
    public static final String ADDCOMMENT = "https://dodia-app.herokuapp.com/%s/addComment";
    public static final String DELETEPOST = "https://dodia-app.herokuapp.com/%s/deletePost";
    public static final String RESTOREPOST = "https://dodia-app.herokuapp.com/%s/restorePost";
    public static final String DELETECOMMENT = "https://dodia-app.herokuapp.com/%s/deleteCommentPost";
    public static final String RESTORECOMMENT = "https://dodia-app.herokuapp.com/%s/restoreCommentPost";
    public static final String DISABLECOMMENT = "https://dodia-app.herokuapp.com/%s/disableCommentPost";
    public static final String ENABLECOMMENT = "https://dodia-app.herokuapp.com/%s/enableCommentPost";


    private final Context c;
    private final SessionHelper sessionHelper;
    private final Map<String, String> header = new HashMap<>();

    //instance class
    private static RequestorHelper ins;

    public static RequestorHelper get(Context c) {
        if (ins == null) ins = new RequestorHelper(c);

        return ins;
    }

    RequestorHelper(Context c) {

        this.c = c;
        sessionHelper = new SessionHelper(c);

        OkHttpClient client =  new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(c, client);
    }

    public void cancel(Object tag) {
        AndroidNetworking.forceCancel(tag);
    }

    public void addRequest(String url, Map<String, String> param, Object tag, final OkHttpResponseAndJSONObjectRequestListener lst) {
        if (sessionHelper.isLogin()) {
            SessionHelper.User user = sessionHelper.getDataUser();
            header.put("php-session",user.getId_user()+","+user.getPass());
        } else
            header.remove("php-session");

        ANRequest request = AndroidNetworking.post(url)
                .addHeaders(header)
                .addBodyParameter(param)
                .setTag(tag)
                .build();
        request.getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                lst.onResponse(okHttpResponse, response);
            }

            @Override
            public void onError(ANError anError) {
                lst.onError(anError);
            }
        });
    }

    @WorkerThread
    public ANResponse<JSONObject> addRequest(String url, Map<String, String> param) {
        if (sessionHelper.isLogin()) {
            SessionHelper.User user = sessionHelper.getDataUser();
            header.put("php-session",user.getId_user()+","+user.getPass());
        } else
            header.remove("php-session");

        OkHttpClient client =  new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        ANRequest request = AndroidNetworking.post(url)
                .addHeaders(header)
                .addBodyParameter(param)
                .setPriority(Priority.MEDIUM)
                .getResponseOnlyFromNetwork()
                .build();

        return request.executeForJSONObject();
    }



    public void sendToken(final String token) {
        Map<String, String> param = new HashMap<>();
        param.put("id_user", sessionHelper.getDataUser().getId_user());
        param.put("token", token);

        RequestorHelper.get(c).addRequest(SENDTOKEN, param, "SENDTOKEN", new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                try {
                    Log.i("TOKEN", "TOKEN HAS SENT , token "+token+" RESPONSE "+response.toString());
                    if (response.getJSONObject("DataRow").getBoolean("success")) {
                        sessionHelper.put("ISSENDTOKEN", true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

                Log.e("TOKEN", "ERROR SEND TOKEN "+anError.getErrorDetail());
                sessionHelper.put("ISSENDTOKEN", false);
            }
        });
    }
}
