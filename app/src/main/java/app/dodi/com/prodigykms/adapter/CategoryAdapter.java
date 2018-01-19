package app.dodi.com.prodigykms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

import app.dodi.com.prodigykms.object.CategoryObject;

/**
 * Created by User on 06/01/2018.
 */

public class CategoryAdapter extends ArrayAdapter<String> {

    private List<CategoryObject> dataList;

    public CategoryAdapter(@NonNull Context context, List<CategoryObject> dataList) {
        super(context, android.R.layout.simple_list_item_1);
        this.dataList=dataList;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return dataList.get(position).getNm_div();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    public List<CategoryObject> getDataList() {
        return dataList;
    }
}
