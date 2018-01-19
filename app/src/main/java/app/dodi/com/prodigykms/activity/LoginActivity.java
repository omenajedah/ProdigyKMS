package app.dodi.com.prodigykms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SessionHelper;
import okhttp3.Response;

/**
 * Created by User on 01/01/2018.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = LoginActivity.class.getSimpleName();

    private TextInputEditText idUser, pass;
    private SessionHelper helper;
    private ProgressDialog dialog;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new SessionHelper(this);

        if (helper.isLogin()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);
        idUser = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(textView.getWindowToken(),0);

                loginButton.performClick();
                return true;
            }
        });
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        if (helper.isLogin()) {
            goToMain();
            finish();
        }
    }

    private void goToMain() {
        finish();
        startActivity(new Intent(this, SplashScreen.class));
    }


    @Override
    public void onClick(View view) {
        String idUser = this.idUser.getText().toString();
        String pass = this.pass.getText().toString();
        if (TextUtils.isEmpty(idUser)) {
            this.idUser.setError("Username Cannot Be Empty!");
            this.idUser.findFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            this.pass.setError("Username Cannot Be Empty!");
            this.pass.findFocus();
            return;
        }

        doLogin(idUser, pass);
    }

    private void doLogin(final String idUser, final String pass) {
        dialog.show();
        Map<String,String> param = new HashMap<>();
        param.put("id", idUser);
        param.put("pass", pass);
        RequestorHelper.get(this).addRequest(RequestorHelper.LOGINURL, param, TAG,  new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                Log.i(TAG, response.toString());
                dialog.dismiss();
                try {
                    if (response.has("success")&&response.getBoolean("success")) {
                        JSONObject datauser = response.getJSONObject("DataUser");
                        String status = datauser.getString("c_status");
                        String nama = datauser.getString("v_namauser");

                        helper.login(idUser,status, nama, pass);
                        goToMain();
                    } else {
                        Snackbar.make(loginButton, "Username atau Password salah!", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Snackbar.make(loginButton, "Error response.", Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onError(ANError anError) {
                dialog.dismiss();
                Log.e(TAG, anError.toString());
                Snackbar.make(loginButton, "Error connection.", Snackbar.LENGTH_SHORT);
            }
        });
    }
}
