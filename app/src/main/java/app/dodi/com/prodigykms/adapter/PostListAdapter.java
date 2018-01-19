package app.dodi.com.prodigykms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 03/01/2018.
 */

public class PostListAdapter extends ArrayAdapter<PostObject> {

    private Context context;
    private List<PostObject> data;

    public PostListAdapter(@NonNull Context context, List<PostObject> dataList) {
        super(context, R.layout.list_post, dataList);
        this.context = context;
        this.data = dataList;
    }

    @Nullable
    @Override
    public PostObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getPosition(@Nullable PostObject item) {
        return data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_post, null);
            holder = new ViewHolder();
            holder.profil_image = convertView.findViewById(R.id.profile_image);
            holder.content_post = convertView.findViewById(R.id.content_post);
            holder.status_user = convertView.findViewById(R.id.status_user);
            holder.jumlah_comment = convertView.findViewById(R.id.jumlah_comment);
            holder.waktu_post = convertView.findViewById(R.id.waktu_post);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PostObject current = getItem(position);

        holder.content_post.setText(current.getContent());
        holder.status_user.setText(current.getV_status_user());
        holder.jumlah_comment.setText(String.valueOf(current.getJumlah_comment()));
        holder.waktu_post.setText(Utils.formatDate(current.getCreated(), "MMM dd yyyy hh:mm a"));

        Bitmap img;
        if (current.getImg() != null && current.getImg().size() > 0) {
            img = current.getImg().get(0);
        } else {
            String firstChar = current.getTitle().substring(0,1);
            img = Utils.createBitmap(firstChar, 60,60, Color.WHITE, Color.BLACK, 58);
        }

        holder.profil_image.setImageBitmap(img);


        if (current.isB_closed()) {
            convertView.setBackgroundResource(android.R.color.darker_gray);
        } else {
            convertView.setBackgroundResource(android.R.color.transparent);
        }

        return convertView;
    }



    class ViewHolder {
        CircleImageView profil_image;
        TextView content_post, status_user, jumlah_comment, waktu_post;
    }
}
