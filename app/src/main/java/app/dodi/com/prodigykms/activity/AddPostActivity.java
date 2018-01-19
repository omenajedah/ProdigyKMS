package app.dodi.com.prodigykms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;
import app.dodi.com.prodigykms.util.Utils;

import static app.dodi.com.prodigykms.activity.MainActivity.EXPLICIT;

/**
 * Created by User on 04/01/2018.
 */

public class AddPostActivity extends AppCompatActivity {

    private final String TAG = AddPostActivity.class.getSimpleName();
    private TextInputEditText add_title, add_post;
    private LinearLayout img_container;

    private final int ADD_CODE = 101;

    private List<Uri> imageUri = new ArrayList<>();

    private int type;
    private SessionHelper sessionHelper;
    private SQLite sqLite;
    private ProgressDialog progressDialog;

    private PostObject object;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_add_post);
        setToolbar();

        sqLite = ((MyApplication) getApplication()).sqLite;
        sessionHelper = new SessionHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu");
        progressDialog.setCancelable(false);

        TextView add_image = findViewById(R.id.add_img);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ADD_CODE
                );
            }
        });

        img_container = findViewById(R.id.img_container);
        add_title = findViewById(R.id.add_title);
        add_post = findViewById(R.id.add_post);

        type = getIntent().getIntExtra("Type", -1);
        if (getIntent().hasExtra("edit")) {
            object = sqLite.getPost(sessionHelper.getCategoryPosition(), type, getIntent().getStringExtra("edit"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (object != null) {
            add_title.setText(object.getTitle());
            add_post.setText(object.getContent());
            List<Bitmap> img = sqLite.getImgPost(object.getId_post(), type);
            for (int i = 0; i < img.size(); i++) {
                View v = getLayoutInflater().inflate(R.layout.list_img_post, null);
                ImageView imageView = v.findViewById(R.id.img_post);

                imageView.setImageBitmap(img.get(i));
                img_container.addView(v, i);
            }
        }
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
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        type = savedInstanceState.getInt("Type");
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
                if (object != null)
                    sendEditedPost();
                else
                    sendPost();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == ADD_CODE && resultCode == RESULT_OK && data != null) {

                imageUri.clear();

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        this.imageUri.add(imageUri);
                        Log.i(TAG, "IMAGE uri " + imageUri.toString());
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        currentItem = currentItem + 1;
                    }
                    addImage();
                } else if (data.getData() != null) {
                    Uri imagePath = data.getData();
                    imageUri.add(imagePath);
                    addImage();
                    Log.i(TAG, "IMAGE path " + imagePath.toString());
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void addImage() {
        img_container.removeAllViews();
        for (int i = 0; i < imageUri.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.list_img_post, null);
            ImageView imageView = v.findViewById(R.id.img_post);

            imageView.setImageURI(imageUri.get(i));
            img_container.addView(v, i);
        }
    }

    private void sendEditedPost() {
        progressDialog.show();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        final Context context = this;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Map<String, String> param = new HashMap<>();

                param.put("id_post", object.getId_post());
                param.put("id_user", sessionHelper.getDataUser().getId_user());
                param.put("id_div", "" + sessionHelper.getCategoryPosition());
                param.put("c_title", add_title.getText().toString());
                param.put("t_content", add_post.getText().toString());

                List<Bitmap> img = sqLite.getImgPost(object.getId_post(), type);
                if (img.size() > 0) {
                    StringBuilder b64 = new StringBuilder();
                    for (int i = 0; i < img.size(); i++) {
                        try {
                            if (i > 0) b64.append(",");
                            b64.append(Utils.encodeBitmspToBase64(img.get(i)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    param.put("t_img", b64.toString());

                }

                String link;

                if (type == EXPLICIT) {
                    link = String.format(RequestorHelper.EDITPOST, "explicit");
                } else {
                    link = String.format(RequestorHelper.EDITPOST, "tacit");
                }

                ANResponse<JSONObject> response = RequestorHelper.get(context).addRequest(link, param);
                final boolean isSuccess = response.isSuccess();
                if (isSuccess) {
                    JSONObject result = response.getResult();
                    Log.i(TAG, "Response " + result.toString());
                    try {
                        if (type == EXPLICIT)
                            sqLite.saveExplicitPost(result.getJSONArray("DataRow"), sessionHelper.getCategoryPosition());
                        else
                            sqLite.saveTacitPost(result.getJSONArray("DataRow"), sessionHelper.getCategoryPosition());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ANError error = response.getError();
                    if (error.getErrorCode() != -1) {
                        Log.i(TAG, "Response error code " + error.getErrorCode() + " body " + error.getErrorBody());

                    } else {
                        Log.i(TAG, "Response error " + error.toString());
                    }

                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (isSuccess) {
                            finish();
                        } else Toast.makeText(AddPostActivity.this, "Gagal mengubah diskusi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void sendPost() {
        progressDialog.show();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        final Context context = this;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Map<String, String> param = new HashMap<>();

                param.put("id_user", sessionHelper.getDataUser().getId_user());
                param.put("id_div", "" + sessionHelper.getCategoryPosition());
                param.put("c_title", add_title.getText().toString());
                param.put("t_content", add_post.getText().toString());

                if (imageUri.size() > 0) {
                    StringBuilder b64 = new StringBuilder();
                    for (int i = 0; i < imageUri.size(); i++) {
                        try {
                            if (i > 0) b64.append(",");
                            b64.append(Utils.encodeBitmspToBase64(MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri.get(i))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    param.put("t_img", b64.toString());
                }

                String link;

                if (type == EXPLICIT) {
                    link = String.format(RequestorHelper.ADDPOST, "explicit");
                } else {
                    link = String.format(RequestorHelper.ADDPOST, "tacit");
                }

                ANResponse<JSONObject> response = RequestorHelper.get(context).addRequest(link, param);
                final boolean isSuccess = response.isSuccess();
                if (isSuccess) {
                    JSONObject result = response.getResult();
                    Log.i(TAG, "Response " + result.toString());
                    try {
                        if (type == EXPLICIT)
                            sqLite.saveExplicitPost(result.getJSONArray("DataRow"), sessionHelper.getCategoryPosition());
                        else
                            sqLite.saveTacitPost(result.getJSONArray("DataRow"), sessionHelper.getCategoryPosition());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ANError error = response.getError();
                    if (error.getErrorCode() != -1) {
                        Log.i(TAG, "Response error code " + error.getErrorCode() + " body " + error.getErrorBody());

                    } else {
                        Log.i(TAG, "Response error " + error.toString());
                    }

                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (isSuccess) {
                            finish();
                        } else
                            Toast.makeText(AddPostActivity.this, "Gagal menambah diskusi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
