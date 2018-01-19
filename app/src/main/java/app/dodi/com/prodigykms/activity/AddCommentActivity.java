package app.dodi.com.prodigykms.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;
import app.dodi.com.prodigykms.util.Utils;
import okhttp3.Response;
import okhttp3.internal.Util;

import static app.dodi.com.prodigykms.activity.MainActivity.EXPLICIT;

/**
 * Created by User on 04/01/2018.
 */

public class AddCommentActivity extends AppCompatActivity {

    private final String TAG = AddCommentActivity.class.getSimpleName();
    private ImageView image;
    private TextInputEditText user_comment;
    private SessionHelper helper;

    private final int ADD_CODE = 101;
    private String id_post;

    private int type;

    private Uri imageUri;

    private ProgressDialog progressDialog;
    private SQLite sqLite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_add_comment);
        setToolbar();
        sqLite = ((MyApplication) getApplication()).sqLite;
        helper = new SessionHelper(this);
        TextView add_image = findViewById(R.id.add_img);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ADD_CODE
                );
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu");
        progressDialog.setCancelable(false);

        image = findViewById(R.id.image);
        user_comment = findViewById(R.id.add_comment);

        id_post = getIntent().getStringExtra("id_post");
        type = getIntent().getIntExtra("Type", -1);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDefaultDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("Type", type);
        savedInstanceState.putString("id_post", id_post);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        type = savedInstanceState.getInt("Type");
        id_post = getIntent().getStringExtra("id_post");
        Log.i(TAG, "id_post " + id_post);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == ADD_CODE && resultCode == RESULT_OK && data != null) {

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        //this.imageUri.add(imageUri);
                        Log.i(TAG, "IMAGE uri " + imageUri.toString());
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        currentItem = currentItem + 1;
                    }
                } else if (data.getData() != null) {
                    Uri imagePath = data.getData();
                    this.imageUri = imagePath;

                    image.setImageURI(imagePath);
                    image.setVisibility(View.VISIBLE);
                    Log.i(TAG, "IMAGE path " + imagePath.toString());
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }

            } else {
                this.imageUri = null;
                image.setVisibility(View.GONE);
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.imageUri = null;
            image.setVisibility(View.GONE);
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.simpan:
                sendComment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendComment() {
        progressDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("id_user", helper.getDataUser().getId_user());
        param.put("id_post", id_post);
        param.put("t_comment", user_comment.getText().toString());

        if (image.getVisibility() == View.VISIBLE) {
            try {
                String b64 = Utils.encodeBitmspToBase64(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
                param.put("t_img", b64);
                Log.i(TAG, "IMG64 "+b64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String link;

        if (type == EXPLICIT) {
            link = String.format(RequestorHelper.ADDCOMMENT, "explicit");
        } else {
            link = String.format(RequestorHelper.ADDCOMMENT, "tacit");
        }

        Log.i(TAG, "param " + param.toString());

        RequestorHelper.get(this).addRequest(link, param, TAG, new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                Log.i(TAG, "Response " + response.toString());
                try {
                    if (type == EXPLICIT)
                        sqLite.saveExplicitComment(response.getJSONArray("DataRow"));
                    else
                        sqLite.saveTacitComment(response.getJSONArray("DataRow"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onError(ANError anError) {
                progressDialog.dismiss();
                Toast.makeText(AddCommentActivity.this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
                if (anError.getErrorCode() != -1) {
                    Log.e(TAG, "Response error code " + anError.getErrorCode() + " Body " + anError.getErrorBody());
                } else Log.e(TAG, "Response error " + anError.toString());

            }
        });
    }
}
