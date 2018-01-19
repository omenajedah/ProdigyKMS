package app.dodi.com.prodigykms.object;

/**
 * Created by User on 18/01/2018.
 */

public class User {

    private String id_user;
    private String nama_user;
    private String status_user;

    public User(String id_user, String nama_user, String status_user) {
        this.id_user = id_user;
        this.nama_user = nama_user;
        this.status_user = status_user;
    }

    public String getId_user() {
        return id_user;
    }

    public String getNama_user() {
        return nama_user;
    }

    public String getStatus_user() {
        return status_user;
    }
}
