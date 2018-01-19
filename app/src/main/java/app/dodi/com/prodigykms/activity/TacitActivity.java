package app.dodi.com.prodigykms.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.adapter.PostListAdapter;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;

import static app.dodi.com.prodigykms.activity.MainActivity.EXPLICIT;
import static app.dodi.com.prodigykms.activity.MainActivity.TACIT;

/**
 * Created by User on 04/01/2018.
 */

public class TacitActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final String TAG = TacitActivity.class.getSimpleName();
    private SessionHelper sessionHelper;
    private ListView listView;
    private TextView tambahPost;
    private PostListAdapter adapter;

    private List<PostObject> dataPost = new ArrayList<>();
    private SQLite sqLite;

    private String query = null;

    private ProgressDialog progressDialog;

    private final int DELETE_POST = 1;
    private final int CLOSE_POST = 2;
    private final int ENABLE_POST = 3;
    private final int EDIT_POST = 4;

    private String id_post;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tacit);
        setToolbar();
        progressDialog = new ProgressDialog(this);
        listView = findViewById(R.id.tacit_list);
        tambahPost = findViewById(R.id.add_post);

        sqLite = ((MyApplication) getApplication()).sqLite;
        sessionHelper = new SessionHelper(this);
        adapter = new PostListAdapter(this, dataPost);
        listView.setAdapter(adapter);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mohon Tunggu");

        listView.setOnItemClickListener(this);

        tambahPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TacitActivity.this, AddPostActivity.class);
                intent.putExtra("Type", TACIT);
                startActivity(intent);
            }
        });

        if (sessionHelper.getDataUser().getC_status().equals("Admin"))
        registerForContextMenu(listView);

        if (getIntent().hasExtra("id_post")) {
            id_post = getIntent().getStringExtra("id_post");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        PostObject object = adapter.getItem(info.position);
        String title = object.getContent();
        menu.setHeaderTitle(title);

        if (object.isB_closed()) {
           // menu.add(Menu.NONE, DELETE_POST, Menu.NONE, "Hapus Post");
            menu.add(Menu.NONE, ENABLE_POST, Menu.NONE, "Buka Post");
        } else {
           // menu.add(Menu.NONE, DELETE_POST, Menu.NONE, "Hapus Post");
            menu.add(Menu.NONE, CLOSE_POST, Menu.NONE, "Tutup Post");
        }

        menu.add(Menu.NONE, EDIT_POST, Menu.NONE, "Edit Diskusi");

    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        progressDialog.show();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case DELETE_POST:
                        Log.i(TAG, "DELETED POST pos " + info.position);
                        deletePost(info.position);
                        break;
                    case CLOSE_POST:
                        Log.i(TAG, "CLOSED POST pos " + info.position);
                        closePost(info.position);
                        break;
                    case ENABLE_POST:
                        Log.i(TAG, "ENABLED POST pos " + info.position);
                        openPost(info.position);
                        break;
                    case EDIT_POST:
                        Log.i(TAG, "EDIT POST pos " + info.position);
                        editPost(info.position);
                        break;
                }
            }
        });
        return true;
    }

    @WorkerThread
    private void deletePost(int position) {
        Map<String, String> param = new HashMap<>();
        param.put("id_div", String.valueOf(sessionHelper.getCategoryPosition()));
        param.put("id_post", dataPost.get(position).getId_post());
        String url = String.format(RequestorHelper.DELETEPOST, "tacit");

        ANResponse<JSONObject> response = RequestorHelper.get(this).addRequest(url, param);

        if (response.isSuccess()) {
            JSONObject result = response.getResult();
            try {
                sqLite.savePost(result.getJSONArray("v"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ANError error = response.getError();
        }

    }

    @WorkerThread
    private void editPost(int position) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
        PostObject current = dataPost.get(position);
        Intent intent = new Intent(TacitActivity.this, AddPostActivity.class);
        intent.putExtra("Type", TACIT);
        intent.putExtra("edit", current.getId_post());
        startActivity(intent);
    }

    @WorkerThread
    private void openPost(int position) {
        Map<String, String> param = new HashMap<>();
        param.put("id_div", String.valueOf(sessionHelper.getCategoryPosition()));
        param.put("id_post", dataPost.get(position).getId_post());
        String url = String.format(RequestorHelper.ENABLECOMMENT, "tacit");

        ANResponse<JSONObject> response = RequestorHelper.get(this).addRequest(url, param);

        if (response.isSuccess()) {
            JSONObject result = response.getResult();
            Log.i(TAG, "RESPONSE open "+result.toString());
            try {
                sqLite.savePostDiv(result.getJSONArray("DataRow"), String.valueOf(sessionHelper.getCategoryPosition()));
                callFinish(true, null);

            } catch (JSONException e) {
                e.printStackTrace();
                callFinish(false, "Gagal Membuka Diskusi");

            }
        } else {
            ANError error = response.getError();
            Log.e(TAG, "error open post "+error.toString());
            callFinish(false, "Gagal Membuka Diskusi");
        }
    }

    @WorkerThread
    private void closePost(int position) {
        Map<String, String> param = new HashMap<>();
        param.put("id_div", String.valueOf(sessionHelper.getCategoryPosition()));
        param.put("id_post", dataPost.get(position).getId_post());
        String url = String.format(RequestorHelper.DISABLECOMMENT, "tacit");


        ANResponse<JSONObject> response = RequestorHelper.get(this).addRequest(url, param);

        if (response.isSuccess()) {
            JSONObject result = response.getResult();
            Log.i(TAG, "RESPONSE close "+result.toString());

            try {
                sqLite.savePostDiv(result.getJSONArray("DataRow"), String.valueOf(sessionHelper.getCategoryPosition()));
                callFinish(true, null);
            } catch (JSONException e) {
                e.printStackTrace();
                callFinish(false, "Gagal Menutup Diskusi");
            }
        } else {
            ANError error = response.getError();
            Log.e(TAG, "error close post "+error.toString());
            callFinish(false, "Gagal Menutup Diskusi");
        }


    }

    private void callFinish(final boolean success, final String reason) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(TacitActivity.this, reason, Toast.LENGTH_LONG).show();
                    return;
                }
                popupData();
            }
        });
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
        popupData();
    }

    private void popupData() {
        dataPost.clear();

        List<PostObject> tacitList = sqLite.getTacitPost(sessionHelper.getCategoryPosition(), 1000, query);

        if (tacitList != null) {
            Log.i(TAG, "Tacit size " + tacitList.size());
            dataPost.addAll(tacitList);
        }

        if (id_post!=null)
            listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    listView.removeOnLayoutChangeListener(this);
                    for (int i=0;i<dataPost.size();i++) {
                        String post = dataPost.get(i).getId_post();
                        if (post.equals(id_post)) {
                            listView.performItemClick(listView, i, dataPost.get(i).hashCode());
                            id_post = null;

                            break;
                        }
                    }
                }
            });

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, DetailPostActivity.class);
        intent.putExtra("id_post", dataPost.get(i).getId_post());
        intent.putExtra("Type", TACIT);
        if (id_post!=null)
            intent.putExtra("refresh", true);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_post, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                query = newText;
                if (TextUtils.isEmpty(newText)) query = null;
                popupData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                if (TextUtils.isEmpty(newText)) query = null;
                popupData();
                return true;
            }

        };
        searchView.setOnQueryTextListener(onQueryTextListener);
        return super.onCreateOptionsMenu(menu);
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
}
