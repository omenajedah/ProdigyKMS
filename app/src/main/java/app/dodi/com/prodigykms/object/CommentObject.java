package app.dodi.com.prodigykms.object;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by User on 01/01/2018.
 */
public class CommentObject {

    private String id_comment, id_post, id_user, comment, user_name, v_status_user;
    private boolean b_hapus;
    private Date created;
    private Bitmap img;

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getV_status_user() {
        return v_status_user;
    }

    public void setV_status_user(String v_status_user) {
        this.v_status_user = v_status_user;
    }

    public boolean isB_hapus() {
        return b_hapus;
    }

    public void setB_hapus(boolean b_hapus) {
        this.b_hapus = b_hapus;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}