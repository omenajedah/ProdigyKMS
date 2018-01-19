package app.dodi.com.prodigykms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.dodi.com.prodigykms.activity.MainActivity;
import app.dodi.com.prodigykms.adapter.NotificationListAdapter;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.NotificationObject;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;

/**
 * Created by User on 02/01/2018.
 */

public class NotificationFragment extends ListFragment {

    private final String TAG = NotificationFragment.class.getSimpleName();

    private List<NotificationObject> notificationObjectList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SQLite sqLite = ((MyApplication)getActivity().getApplication()).sqLite;
        List<NotificationObject> newData = sqLite.getNotification();
        if (newData != null) notificationObjectList.addAll(newData);

        new SessionHelper(getActivity()).put("JUMLAHNOTIF", 0);

        setListAdapter(new NotificationListAdapter(getActivity(), notificationObjectList, sqLite));
        ((MainActivity)getActivity()).setJumlahNotification(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        NotificationObject object = notificationObjectList.get(position);
        Log.i(TAG, "id_div "+object.getId_div() + ", id_post "+object.getId_post()+" type "+object.getId_type_post());
        ((MainActivity)getActivity()).openPost(object);
    }
}

