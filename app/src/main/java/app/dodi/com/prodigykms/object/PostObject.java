package app.dodi.com.prodigykms.object;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 01/01/2018.
 */
public class PostObject implements Serializable {

    private String id_post, id_div, id_user, v_status_user, title, content, user_name;
    private Date created;
    private boolean b_closed, b_hapus;
    private int jumlah_comment, type;

    public static PostObject from(PostObject object) {
        PostObject postObject = new PostObject();
        postObject.setContent(object.getContent());
        postObject.setImg(object.getImg());
        postObject.setJumlah_comment(object.getJumlah_comment());
        postObject.setB_hapus(object.isB_hapus());
        postObject.setB_closed(object.isB_closed());
        postObject.setCreated(object.getCreated());
        postObject.setUser_name(object.getUser_name());
        postObject.setTitle(object.getTitle());
        postObject.setV_status_user(object.getV_status_user());
        postObject.setId_div(object.getId_div());
        postObject.setId_user(object.getId_user());
        postObject.setId_post(object.getId_post());
        return postObject;
    }

    private List<Bitmap> img;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_div() {
        return id_div;
    }

    public void setId_div(String id_div) {
        this.id_div = id_div;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getV_status_user() {
        return v_status_user;
    }

    public void setV_status_user(String v_status_user) {
        this.v_status_user = v_status_user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isB_closed() {
        return b_closed;
    }

    public void setB_closed(boolean b_closed) {
        this.b_closed = b_closed;
    }

    public boolean isB_hapus() {
        return b_hapus;
    }

    public void setB_hapus(boolean b_hapus) {
        this.b_hapus = b_hapus;
    }

    public int getJumlah_comment() {
        return jumlah_comment;
    }

    public void setJumlah_comment(int jumlah_comment) {
        this.jumlah_comment = jumlah_comment;
    }

    public List<Bitmap> getImg() {
        return img;
    }

    public void setImg(List<Bitmap> img) {
        this.img = img;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}