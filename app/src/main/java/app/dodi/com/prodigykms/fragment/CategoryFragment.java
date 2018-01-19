package app.dodi.com.prodigykms.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dodi.com.prodigykms.activity.MainActivity;
import app.dodi.com.prodigykms.adapter.CategoryAdapter;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.CategoryObject;
import app.dodi.com.prodigykms.object.DownloadObject;
import app.dodi.com.prodigykms.util.DownloaderHelper;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;

/**
 * Created by User on 02/01/2018.
 */

public class CategoryFragment extends ListFragment implements DownloaderHelper.DownloadListener {

    private final String TAG = CategoryFragment.class.getSimpleName();

    private List<CategoryObject> categoryArray;
    private DownloaderHelper downloaderHelper;
    private List<DownloadObject> downloaderHelperList = new ArrayList<>();
    private CategoryObject categoryObject;
    private SQLite sqLite;
    private ProgressDialog progressDialog;
    private SessionHelper helper;
    private String id_div;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLite = ((MyApplication) getActivity().getApplication()).sqLite;
        progressDialog = new ProgressDialog(getActivity());
        helper = new SessionHelper(getActivity());
        progressDialog.setMessage("Mohon Tunggu");
        progressDialog.setCancelable(false);
        categoryArray = sqLite.getCategory();
        downloaderHelper = new DownloaderHelper(getActivity());
        downloaderHelper.setDownloadListener(this);
        CategoryAdapter adp = new CategoryAdapter(getActivity(), categoryArray);
        setListAdapter(adp);

        if (id_div != null)
            getListView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    getListView().removeOnLayoutChangeListener(this);

                    ListView listView = getListView();
                    try {
                        int div = Integer.valueOf(id_div);
                        for (int j = 0; j < categoryArray.size(); j++) {
                            if (categoryArray.get(j).getId_div() == div) {
                                listView.performItemClick(listView, j, categoryArray.get(j).hashCode());
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        categoryObject = categoryArray.get(position);
        Map<String, String> param = new HashMap<>();

        param.put("id_div", String.valueOf(categoryObject.getId_div()));
        DownloadObject object = new DownloadObject(param, "POST_DIV", RequestorHelper.GETALLPOSTFROMDIV);
        downloaderHelperList.add(object);

        if (!helper.get("POST_DIV_DOWNLOADED_" + categoryObject.getId_div(), false) || id_div != null) {
            progressDialog.show();
            downloaderHelper.start(downloaderHelperList);
        } else {
            ((MainActivity) getActivity()).setCategory(categoryObject);
        }

    }

    @Override
    public void onDownloadProgress(int pos) {

    }

    @Override
    public void onDownloadSuccess(int pos, Handler mainThread) {
        DownloadObject object = downloaderHelperList.get(pos);
        try {
            sqLite.savePostDiv(object.getResponse().getJSONArray("DataRow"), String.valueOf(categoryObject.getId_div()));
            helper.put("POST_DIV_DOWNLOADED_" + categoryObject.getId_div(), true);
        } catch (JSONException e) {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Gagal mengunduh data diskusi", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadError(int pos, ANError error) {
        Log.e(TAG, "ERRPR " + error.getErrorBody());
        Toast.makeText(getActivity(), "Gagal mengunduh data diskusi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadsFinished() {
        if (!isAdded())
            return;
        ((MainActivity) getActivity()).setCategory(categoryObject);
        progressDialog.dismiss();

    }

    public void click(String id_div) {
        this.id_div = id_div;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}

