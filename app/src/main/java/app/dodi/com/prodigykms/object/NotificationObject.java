package app.dodi.com.prodigykms.object;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by User on 07/01/2018.
 */

public class NotificationObject {

    private final int id_type_post, id_type_notif;
    private final String id_div,id_post,id_comment,id_user, t_msg;
    private Bitmap t_img;
    private final Date d_receive;

    public NotificationObject(int id_type_post, int id_type_notif, String id_div,
                              String id_post, String id_comment, String id_user, String t_msg, Bitmap t_img, Date d_receive) {
        this.id_type_post = id_type_post;
        this.id_type_notif = id_type_notif;
        this.id_div = id_div;
        this.id_post = id_post;
        this.id_comment = id_comment;
        this.id_user = id_user;
        this.t_msg = t_msg;
        this.t_img = t_img;
        this.d_receive = d_receive;
    }

    public int getId_type_post() {
        return id_type_post;
    }

    public int getId_type_notif() {
        return id_type_notif;
    }

    public String getId_div() {
        return id_div;
    }

    public String getId_post() {
        return id_post;
    }

    public String getId_comment() {
        return id_comment;
    }

    public String getId_user() {
        return id_user;
    }

    public String getT_msg() {
        return t_msg;
    }

    public Bitmap getT_img() {
        return t_img;
    }

    public Date getD_receive() {
        return d_receive;
    }
}
