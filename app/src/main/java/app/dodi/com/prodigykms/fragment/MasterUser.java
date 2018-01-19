package app.dodi.com.prodigykms.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ListFragment;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.object.User;
import app.dodi.com.prodigykms.util.SQLite;

/**
 * Created by User on 18/01/2018.
 */

public class MasterUser extends ListFragment {

    private static final int DELETE_USER = 0;
    private List<User> userList = new ArrayList<>();
    private List<String> namaUser = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SQLite sqLite = ((MyApplication)getActivity().getApplication()).sqLite;

        userList.addAll(sqLite.getAllUser());

        for (User user : userList) {
            namaUser.add(user.getNama_user());
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, namaUser);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDialogUser(userList.get(position));
    }

    private void showDialogUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user, null);
        TextInputEditText username = v.findViewById(R.id.username);
        TextInputEditText password = v.findViewById(R.id.password);
        Spinner statususer = v.findViewById(R.id.status_user);
        String[] category = getResources().getStringArray(R.array.status_user);

        builder.setView(v);
        builder.setTitle("Tambah User");

        if (user != null) {
            builder.setTitle("Edit User");
            username.setText(user.getId_user());
            statususer.setSelection(category[0].equals(user.getStatus_user()) ? 0 : 1);
        }

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        User user = userList.get(info.position);
        String title = user.getNama_user();
        menu.setHeaderTitle(title);

        menu.add(Menu.NONE, DELETE_USER, Menu.NONE, "Delete User");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == DELETE_USER) {

        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tambah_user) {
            showDialogUser(null);
        }
        return super.onOptionsItemSelected(item);
    }
}
