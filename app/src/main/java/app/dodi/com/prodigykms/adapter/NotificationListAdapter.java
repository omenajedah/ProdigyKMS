package app.dodi.com.prodigykms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.object.NotificationObject;
import app.dodi.com.prodigykms.util.SQLite;
import app.dodi.com.prodigykms.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.dodi.com.prodigykms.activity.MainActivity.TACIT;

/**
 * Created by User on 03/01/2018.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationObject> {

    private final String TAG = NotificationListAdapter.class.getSimpleName();

    private Context context;
    private List<NotificationObject> data;
    private SQLite sqLite;

    public NotificationListAdapter(@NonNull Context context, List<NotificationObject> dataList, SQLite sqLite) {
        super(context, R.layout.list_notification, dataList);
        this.context = context;
        this.data = dataList;
        this.sqLite = sqLite;
    }

    @Nullable
    @Override
    public NotificationObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getPosition(@Nullable NotificationObject item) {
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
            convertView = inflater.inflate(R.layout.list_notification, null);
            holder = new ViewHolder();
            holder.profil_image = convertView.findViewById(R.id.profile_image);
            holder.t_msg = convertView.findViewById(R.id.t_msg);
            holder.d_receive = convertView.findViewById(R.id.d_receive);
            holder.id_user = convertView.findViewById(R.id.id_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NotificationObject current = getItem(position);

        holder.id_user.setText(current.getId_user());
        try {
            holder.d_receive.setText(Utils.formatDate(current.getD_receive(), "MMM dd yyyy hh:mm a"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String message = current.getT_msg();
        //0 post/diskusi baru, 1 komentar baru

        Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_img);
        if (current.getT_img() != null) {
            img = current.getT_img();
        } else {
            try {
                String firstChar = message.substring(0,1);
                img = Utils.createBitmap(firstChar, 60,60, Color.WHITE, Color.BLACK, 58);
            } catch (Exception e) {
                Log.e(TAG, "Failed create image, error "+e.toString());
            }

        }

        holder.profil_image.setImageBitmap(img);

        String type_post = current.getId_type_post() == TACIT ? "Tacit" : "Explicit";

        if (current.getId_type_notif() == 0) {
            message = String.format(context.getString(R.string.text_new_post), type_post, message, sqLite.getCategory(current.getId_div()).getNm_div());
        } else {
            message = String.format(context.getString(R.string.text_new_comment), type_post, message, sqLite.getCategory(current.getId_div()).getNm_div());
        }

        holder.t_msg.setText(message);
        return convertView;
    }



    class ViewHolder {
        CircleImageView profil_image;
        TextView t_msg, id_user, d_receive;
    }
}
