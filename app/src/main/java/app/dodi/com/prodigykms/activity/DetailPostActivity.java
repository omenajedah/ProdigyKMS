package app.dodi.com.prodigykms.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.CommentObject;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;
import app.dodi.com.prodigykms.util.Utils;
import okhttp3.Response;

import static app.dodi.com.prodigykms.activity.MainActivity.EXPLICIT;
import static app.dodi.com.prodigykms.activity.MainActivity.TACIT;

/**
 * Created by User on 04/01/2018.
 */

public class DetailPostActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = DetailPostActivity.class.getSimpleName();

    private int type = -1;
    private TextView nama_user, status_user, waktu_post, judul_post, content_post;
    private LinearLayout img_container, comment_container;
    private EditText quick_reply;
    private ImageView advance_comment, add_post;
    private SQLite sqLite;
    private SessionHelper sessionHelper;

    private PostObject object;

    private String id_post;

    private boolean deleteInput = false, sendInput = false;
    private ProgressDialog progressDialog;

    private boolean refresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        setToolbar();
        setView();
        sqLite = ((MyApplication) getApplication()).sqLite;
        sessionHelper = new SessionHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu");
        progressDialog.setCancelable(false);

        type = getIntent().getIntExtra("Type", -1);
        id_post = getIntent().getStringExtra("id_post");
        refresh = getIntent().hasExtra("refresh");

        getSupportActionBar().setTitle(type == EXPLICIT ? getString(R.string.text_data_explicit) : getString(R.string.text_data_tacit));

        object = sqLite.getPost(sessionHelper.getCategoryPosition(), type, id_post);


        Log.i(TAG, "TYPE " + type);
    }

    private void updateUI() {
        if (object.isB_closed()) {
            quick_reply.setEnabled(false);
            advance_comment.setEnabled(false);
            Toast.makeText(this, "Diskusi ini telah ditutup, anda tidak dapat berkomentar.", Toast.LENGTH_LONG).show();
        }

        nama_user.setText(object.getUser_name());
        status_user.setText(object.getV_status_user());
        waktu_post.setText(Utils.formatDate(object.getCreated(), "MMM dd yyyy hh:mm a"));
        judul_post.setText(object.getTitle());
        content_post.setText(object.getContent());

        img_container.removeAllViews();
        List<Bitmap> imgBitmap = sqLite.getImgPost(id_post, type);
        if (imgBitmap != null) {
            Log.i(TAG, "Image size " + imgBitmap);

            for (int i = 0; i < imgBitmap.size(); i++) {
                View v = getLayoutInflater().inflate(R.layout.list_img_post, null);
                ImageView imageView = v.findViewById(R.id.img_post);
                imageView.setImageBitmap(imgBitmap.get(i));
                img_container.addView(v, i);
            }
        }

    }

    private void setView() {
        nama_user = findViewById(R.id.nama_user);
        status_user = findViewById(R.id.status_user);
        waktu_post = findViewById(R.id.waktu_post);
        judul_post = findViewById(R.id.judul_post);
        content_post = findViewById(R.id.content_post);

        img_container = findViewById(R.id.img_container);
        comment_container = findViewById(R.id.comment_container);

        quick_reply = findViewById(R.id.quick_reply);

        advance_comment = findViewById(R.id.advance_comment);
        add_post = findViewById(R.id.add_post);

        quick_reply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(quick_reply.getText().toString().trim())) {
                    advance_comment.setImageResource(R.drawable.ic_edit);
                    add_post.setImageResource(R.drawable.ic_add_post);
                    deleteInput = sendInput = false;
                } else {
                    advance_comment.setImageResource(android.R.drawable.ic_menu_delete);
                    add_post.setImageResource(R.drawable.ic_menu_send);
                    deleteInput = sendInput = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        advance_comment.setOnClickListener(this);
        add_post.setOnClickListener(this);
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
    protected void onResume() {
        super.onResume();

        Log.i(TAG, type == TACIT ? "TACIT" : "EXPLICIT");
        updateUI();
        if (refresh)
            getComment();
        else
            updateComments();
    }

    private void updateComments() {
        final List<CommentObject> commentObjects;

        Log.i(TAG, "id_post "+ object.getId_post());

        if (type == TACIT) {
            commentObjects = sqLite.getTacitComment(id_post);

        } else {
            commentObjects = sqLite.getExplicitComment(id_post);
        }
        comment_container.removeAllViews();

        if (commentObjects != null) {
            for (int i = 0; i < commentObjects.size(); i++) {
                final View v = getLayoutInflater().inflate(R.layout.list_comment, null);


                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final int pos = comment_container.indexOfChild(v);
                        Log.i(TAG, "POS COMMENT "+pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPostActivity.this);
                        CharSequence[] item = new CharSequence[]{"Hapus Komentar"};
                        builder.setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();
                                CommentObject commentObject = commentObjects.get(pos);
                                String url = String.format(RequestorHelper.DELETECOMMENT, type == EXPLICIT ? "explicit" : "tacit");
                                Map<String, String> param = new HashMap<>();
                                param.put("id_comment", commentObject.getId_comment());
                                param.put("id_post", id_post);

                                RequestorHelper.get(DetailPostActivity.this).addRequest(url, param, "DELETECOMMENT", new OkHttpResponseAndJSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(Response okHttpResponse, JSONObject response) {
                                        progressDialog.dismiss();
                                        String table = type == EXPLICIT ? SQLite.EXPLICIT_COMMENT_TABLE : SQLite.TACIT_COMMENT_TABLE;
                                        try {
                                            sqLite.saveCommentPost(response.getJSONArray("DataRow"), id_post, table);
                                            Toast.makeText(DetailPostActivity.this, "Sukses menghapus komentar", Toast.LENGTH_SHORT).show();
                                            updateComments();
                                        } catch (JSONException e) {
                                            Toast.makeText(DetailPostActivity.this, "Gagal menghapus komentar", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        Toast.makeText(DetailPostActivity.this, "Gagal menghapus komentar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        builder.show();

                        return false;
                    }
                });

                CommentObject current = commentObjects.get(i);

                if (current.isB_hapus()) {
                    v.findViewById(R.id.root).setBackgroundResource(android.R.color.darker_gray);
                }

                TextView nama_user = v.findViewById(R.id.nama_user);
                TextView waktu_post = v.findViewById(R.id.waktu_post);
                TextView content_post = v.findViewById(R.id.content_post);
                ImageView img_user = v.findViewById(R.id.img_user);
                ImageView img_comment = v.findViewById(R.id.img_comment);

                nama_user.setText(current.getUser_name());
                waktu_post.setText(Utils.formatDate(current.getCreated(), "MMM dd yyyy hh:mm a"));
                content_post.setText(current.getComment());
                img_user.setImageBitmap(Utils.createBitmap(current.getUser_name().substring(0, 1), 60, 60, Color.WHITE, Color.BLACK, 58));
                if (current.getImg() != null) {
                    Log.i(TAG, "IMG COMMENT NOT NULL" );
                    img_comment.setImageBitmap(current.getImg());
                    img_comment.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "IMG COMMENT IS NULL" );
                }
                comment_container.addView(v, i);
                Log.i(TAG, String.format("id_post %s, id_comment %s, comment %s", current.getId_post(), current.getId_comment(), current.getComment()));
            }
        } else Log.i(TAG, type == TACIT ? "TACIT IS NULL COMMENT" : "EXPLICIT IS NULL COMMENT");
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
        id_post = savedInstanceState.getString("id_post");

        object = sqLite.getPost(sessionHelper.getCategoryPosition(), type, id_post);

        updateUI();
        updateComments();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.advance_comment:
                if (this.deleteInput) {
                    quick_reply.getText().clear();
                    return;
                }
                intent = new Intent(this, AddCommentActivity.class);
                intent.putExtra("id_post", id_post);
                intent.putExtra("Type", type);
                startActivity(intent);
                break;
            case R.id.add_post:
                if (this.sendInput) {
                    sendComment();
                    return;
                }
                intent = new Intent(this, AddPostActivity.class);
                intent.putExtra("Type", type);
                startActivity(intent);
                break;
        }
    }



    private void sendComment() {
        progressDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("id_user", sessionHelper.getDataUser().getId_user());
        param.put("id_post", id_post);
        param.put("t_comment", quick_reply.getText().toString());

        String link;

        if (type == EXPLICIT) {
            link = String.format(RequestorHelper.ADDCOMMENT, "explicit");
        } else {
            link = String.format(RequestorHelper.ADDCOMMENT, "tacit");
        }

        RequestorHelper.get(this).addRequest(link, param, TAG, new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                Log.i(TAG, "Response " + response.toString());
                try {
                    if (type == EXPLICIT)
                        sqLite.saveExplicitComment(response.getJSONArray("DataRow"));
                    else
                        sqLite.saveTacitComment(response.getJSONArray("DataRow"));
                    quick_reply.getText().clear();
                    Toast.makeText(DetailPostActivity.this, "Sukses menambahkan komentar", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailPostActivity.this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
                updateComments();
            }

            @Override
            public void onError(ANError anError) {
                progressDialog.dismiss();
                Toast.makeText(DetailPostActivity.this, "Gagal menambahkan komentar", Toast.LENGTH_SHORT).show();
                if (anError.getErrorCode() != -1) {
                    Log.e(TAG, "Response error code " + anError.getErrorCode() + " Body " + anError.getErrorBody());
                } else Log.e(TAG, "Response error " + anError.toString());

            }
        });
    }


    private void getComment() {
        progressDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("id_post", id_post);

        String link;

        if (type == EXPLICIT) {
            link = String.format(RequestorHelper.GETCOMMENT, "explicit");
        } else {
            link = String.format(RequestorHelper.GETCOMMENT, "tacit");
        }

        RequestorHelper.get(this).addRequest(link, param, TAG, new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                Log.i(TAG, "Response " + response.toString());
                try {
                    if (type == EXPLICIT)
                        sqLite.saveExplicitComment(response.getJSONArray("DataRow"));
                    else
                        sqLite.saveTacitComment(response.getJSONArray("DataRow"));
                    quick_reply.getText().clear();
                    Toast.makeText(DetailPostActivity.this, "Sukses memeriksa komentar", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailPostActivity.this, "Gagal memeriksa komentar", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
                updateComments();
            }

            @Override
            public void onError(ANError anError) {
                progressDialog.dismiss();
                Toast.makeText(DetailPostActivity.this, "Gagal memeriksa komentar", Toast.LENGTH_SHORT).show();
                if (anError.getErrorCode() != -1) {
                    Log.e(TAG, "Response error code " + anError.getErrorCode() + " Body " + anError.getErrorBody());
                } else Log.e(TAG, "Response error " + anError.toString());

            }
        });
    }
}
