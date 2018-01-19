package app.dodi.com.prodigykms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.activity.DetailPostActivity;
import app.dodi.com.prodigykms.activity.ExplicitActivity;
import app.dodi.com.prodigykms.activity.TacitActivity;
import app.dodi.com.prodigykms.adapter.PostListAdapter;
import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.SessionHelper;

import static app.dodi.com.prodigykms.activity.MainActivity.EXPLICIT;
import static app.dodi.com.prodigykms.activity.MainActivity.TACIT;

/**
 * Created by User on 03/01/2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String TAG = HomeFragment.class.getSimpleName();
    private SQLite sqLite;
    private SessionHelper sessionHelper;

    private List<PostObject> tacitList = new ArrayList<>();
    private List<PostObject> explicitList = new ArrayList<>();

    private PostListAdapter tacitAdapter, explicitAdapter;

    private TextView expandTacit, expandExplicit;

    private String id_post;
    private int id_type = -1;

    private void popupData() {
        this.tacitList.clear();
        this.explicitList.clear();

        List<PostObject> tacitList = sqLite.getTacitPost(sessionHelper.getCategoryPosition(), 6, null);
        List<PostObject> explicitList = sqLite.getExplicitPost(sessionHelper.getCategoryPosition(), 6, null);

        if (tacitList != null) {
            Log.i(TAG, "Tacit size " + tacitList.size());
            this.tacitList.addAll(tacitList);
        }

        if (explicitList != null) {
            Log.i(TAG, "Explicit size " + explicitList.size());
            this.explicitList.addAll(explicitList);
        }

        tacitAdapter.notifyDataSetChanged();
        explicitAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_home, container, false);

        sqLite = ((MyApplication) getActivity().getApplication()).sqLite;
        sessionHelper = new SessionHelper(getActivity());

        ListView explicitListView = v.findViewById(R.id.explicit_list);
        ListView tacitListView = v.findViewById(R.id.tacit_list);

        expandTacit = v.findViewById(R.id.expand_tacit);
        expandExplicit = v.findViewById(R.id.expand_explicit);

        tacitAdapter = new PostListAdapter(getActivity(), tacitList);
        explicitAdapter = new PostListAdapter(getActivity(), explicitList);

        tacitListView.setAdapter(tacitAdapter);
        explicitListView.setAdapter(explicitAdapter);

        tacitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DetailPostActivity.class);
                intent.putExtra("id_post", tacitList.get(i).getId_post());
                intent.putExtra("Type", TACIT);
                startActivity(intent);
            }
        });
        explicitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DetailPostActivity.class);
                intent.putExtra("id_post", explicitList.get(i).getId_post());
                intent.putExtra("Type", EXPLICIT);
                startActivity(intent);
            }
        });

        expandTacit.setOnClickListener(this);
        expandExplicit.setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupData();

        if (id_type == TACIT) {
            id_type = -1;
            expandTacit.performClick();
        } else if (id_type == EXPLICIT){
            expandExplicit.performClick();
            id_type = -1;
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.expand_tacit:
                intent = new Intent(getActivity(), TacitActivity.class);
                break;
            case R.id.expand_explicit:
                intent = new Intent(getActivity(), ExplicitActivity.class);

                break;
        }

        if (id_post != null && !TextUtils.isEmpty(id_post)) {
            intent.putExtra("id_post", id_post);
        }
        startActivity(intent);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PostObject object;
        Intent intent = new Intent(getContext(), DetailPostActivity.class);

        switch (view.getId()) {
            case R.id.tacit_list:
                object = tacitList.get(i);
                intent.putExtra("id_post", object.getId_post());
                intent.putExtra("Type", TACIT);
                break;
            case R.id.explicit_list:
                object = explicitList.get(i);
                intent.putExtra("id_post", object.getId_post());
                intent.putExtra("Type", EXPLICIT);
                break;
        }
        startActivity(intent);
    }

    public void expandPost(int id_type, String id_post) {
        this.id_post = id_post;
        this.id_type = id_type;
    }
}
