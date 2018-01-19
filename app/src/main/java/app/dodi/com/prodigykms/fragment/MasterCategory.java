package app.dodi.com.prodigykms.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.CategoryObject;
import app.dodi.com.prodigykms.object.User;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SQLite;
import okhttp3.Response;

/**
 * Created by User on 18/01/2018.
 */

public class MasterCategory extends ListFragment {

    private static final int DELETE_CATEGORY = 0;
    private List<CategoryObject> categoryObjectList = new ArrayList<>();
    private List<String> namaUser = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SQLite sqLite;
    private ProgressDialog progressDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLite = ((MyApplication)getActivity().getApplication()).sqLite;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mohon Tunggu");
        progressDialog.setCancelable(false);

        categoryObjectList.addAll(sqLite.getCategory());

        for (CategoryObject categoryObject : categoryObjectList) {
            namaUser.add(categoryObject.getNm_div());
        }

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, namaUser);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDialogUser(categoryObjectList.get(position));
    }

    private void showDialogUser(CategoryObject categoryObject) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_category, null);
        final TextInputEditText nama_kategori = v.findViewById(R.id.nama_category);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addCategory(nama_kategori.getText().toString());
            }
        });
        builder.setNegativeButton("Batal", null);

        builder.setView(v);
        builder.setTitle("Tambah Kategori");

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        CategoryObject categoryObject = categoryObjectList.get(info.position);
        String title = categoryObject.getNm_div();
        menu.setHeaderTitle(title);

        menu.add(Menu.NONE, DELETE_CATEGORY, Menu.NONE, "Delete Category");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == DELETE_CATEGORY) {
            deleteCategory(String.valueOf(categoryObjectList.get(info.position).getId_div()));
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_category, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tambah_kategori) {
            showDialogUser(null);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addCategory(final String nama) {
        progressDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("nm_div", nama);
        RequestorHelper.get(getActivity()).addRequest(RequestorHelper.ADDDIVISION, param, "", new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                progressDialog.dismiss();
                try {
                    sqLite.saveDivision(response.getJSONArray("DataRow"));
                    categoryObjectList.clear();
                    categoryObjectList.addAll(sqLite.getCategory());
                    namaUser.clear();
                    for (CategoryObject categoryObject : categoryObjectList) {
                        namaUser.add(categoryObject.getNm_div());
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getActivity(), "Gagal menambahkan kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(String id_div) {
        progressDialog.show();
        Map<String, String> param = new HashMap<>();
        param.put("id_div", id_div);
        RequestorHelper.get(getActivity()).addRequest(RequestorHelper.DELETEDIVISION, param, "", new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {
                progressDialog.dismiss();
                try {
                    sqLite.saveDivision(response.getJSONArray("DataRow"));
                    categoryObjectList.clear();
                    categoryObjectList.addAll(sqLite.getCategory());
                    namaUser.clear();
                    for (CategoryObject categoryObject : categoryObjectList) {
                        namaUser.add(categoryObject.getNm_div());
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getActivity(), "Gagal menghapus kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
