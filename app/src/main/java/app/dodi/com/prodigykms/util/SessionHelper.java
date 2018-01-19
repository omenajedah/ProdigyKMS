package app.dodi.com.prodigykms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Set;

import okhttp3.internal.Util;

/**
 * Created by User on 02/01/2018.
 */

public class SessionHelper {

    private Context c;
    private SharedPreferences preferences;
    private final String ISLOGIN = "ISLOGIN";
    private final String DATAUSER = "DATAUSER";


    private final String CATEGORY_POSITION = "CATEGORY_POSITION";


    public SessionHelper(Context c) {
        this.c = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(c);

        checkForReset();
    }

    private void checkForReset() {
        String last_date = getString("LAST_DATE", "");
        String now = Utils.formatDate(Calendar.getInstance().getTime(), "dd-MM-yyyy");
        if (!last_date.equals(now)) {
            putString("LAST_DATE", now);
            for (int i=0;i<12;i++) {
                put("POST_DIV_DOWNLOADED_"+i, false);
            }
        }
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void put(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public void put(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key,value);
        editor.apply();
    }

    public void put(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public void put(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public void put(String key, Set<String> value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key,value);
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void removeAll() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getString(String key, String def) {
        return preferences.getString(key, def);
    }

    public int get(String key, int def) {
        return preferences.getInt(key, def);
    }

    public float get(String key, float def) {
        return preferences.getFloat(key, def);
    }

    public long get(String key, long def) {
        return preferences.getLong(key, def);
    }

    public boolean get(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    public Set<String> get(String key, Set<String> def) {
        return preferences.getStringSet(key, def);
    }

    public void login(String id, String status, String nama, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("id",id);
            object.put("status",status);
            object.put("nama",nama);
            object.put("password",password);
            putString(DATAUSER, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCategoryPosition() {
        return get(CATEGORY_POSITION, -1);
    }

    public void setCategoryPosition(int pos) {
        put(CATEGORY_POSITION, pos);
    }

    public boolean isLogin() {
        return getString(DATAUSER, null) != null;
    }

    public void logout() {
        removeAll();
    }

    public User getDataUser() {
        try {
            JSONObject object = new JSONObject(getString(DATAUSER, null));
            User user = new User();
            user.setId_user(object.getString("id"));
            user.setC_status(object.getString("status"));
            user.setV_namauser(object.getString("nama"));
            user.setPass(object.getString("password"));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public class User {
        private String id_user,c_status, v_namauser,pass;

        public String getId_user() {
            return id_user;
        }
        private void setId_user(String id_user) {
            this.id_user = id_user;
        }
        public String getC_status() {
            return c_status;
        }
        private void setC_status(String c_status) {
            this.c_status = c_status;
        }
        public String getV_namauser() {
            return v_namauser;
        }
        private void setV_namauser(String v_namauser) {
            this.v_namauser = v_namauser;
        }
        public String getPass() {
            return pass;
        }
        private void setPass(String pass) {
            this.pass = pass;
        }
    }

}
