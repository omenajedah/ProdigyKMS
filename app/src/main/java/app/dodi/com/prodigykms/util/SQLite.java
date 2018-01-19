package app.dodi.com.prodigykms.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.dodi.com.prodigykms.activity.MainActivity;
import app.dodi.com.prodigykms.object.CommentObject;
import app.dodi.com.prodigykms.object.CategoryObject;
import app.dodi.com.prodigykms.object.NotificationObject;
import app.dodi.com.prodigykms.object.PostObject;
import app.dodi.com.prodigykms.object.User;

/**
 * Created by User on 06/01/2018.
 */

public class SQLite extends SQLiteOpenHelper {

    public static final String TACIT_POST_TABLE = "TACIT_POST_TABLE";
    public static final String EXPLICIT_POST_TABLE = "EXPLICIT_POST_TABLE";
    public static final String TACIT_COMMENT_TABLE = "TACIT_COMMENT_TABLE";
    public static final String EXPLICIT_COMMENT_TABLE = "EXPLICIT_COMMENT_TABLE";
    public static final String DIVISION_TABLE = "DIVISION_TABLE";
    public static final String USER_TABLE = "USER_TABLE";
    public static final String NOTIFICATION_TABLE = "NOTIFICATION_TABLE";
    public static final String IMG_POST_TABLE = "IMG_POST_TABLE";

    private static final String TAG = SQLite.class.getSimpleName();


    public SQLite(Context context) {
        super(context, "PRODIGY_KMS", null, 1);
    }

    //Function untuk membuat table sqlite
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL;
        SQL = "CREATE TABLE " + TACIT_POST_TABLE + "(" +
                "    id_post integer NOT NULL PRIMARY KEY," +
                "    id_user varchar(10)," +
                "    id_div char(2)," +
                "    d_created varchar(2) NOT NULL," +
                "    b_closed smallint DEFAULT '0'," +
                "    b_hapus smallint DEFAULT '0'," +
                "    c_title varchar(255)," +
                "    t_content text," +
                "    t_img text" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + EXPLICIT_POST_TABLE + "(" +
                "    id_post integer NOT NULL PRIMARY KEY," +
                "    id_user varchar(10)," +
                "    id_div char(2)," +
                "    d_created varchar(2) NOT NULL," +
                "    b_closed smallint DEFAULT '0'," +
                "    b_hapus smallint DEFAULT '0'," +
                "    c_title varchar(255)," +
                "    t_content text," +
                "    t_img text" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + TACIT_COMMENT_TABLE + " (" +
                "    id_comment integer NOT NULL PRIMARY KEY," +
                "    id_post varchar(11)," +
                "    id_user varchar(10)," +
                "    b_hapus smallint DEFAULT '0'," +
                "    d_commented varchar(2) NOT NULL," +
                "    t_comment text," +
                "    t_img text" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + EXPLICIT_COMMENT_TABLE + " (" +
                "    id_comment integer NOT NULL PRIMARY KEY," +
                "    id_post varchar(11)," +
                "    id_user varchar(10)," +
                "    b_hapus smallint DEFAULT '0'," +
                "    d_commented varchar(2) NOT NULL," +
                "    t_comment text," +
                "    t_img text" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + DIVISION_TABLE + " (" +
                "   id_div varchar(2) NOT NULL PRIMARY KEY, " +
                "   nm_div varchar(55)" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + USER_TABLE + " (" +
                "    id_user varchar(10) NOT NULL PRIMARY KEY," +
                "    c_status varchar(20)," +
                "    v_namauser text," +
                "    d_tgllahir date" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + NOTIFICATION_TABLE + " (" +
                "    id_type_post smallint NOT NULL," +
                "    id_div varchar(2)," +
                "    id_post varchar(11)," +
                "    id_comment varchar(11)," +
                "    id_user varchar(10)," +
                "    t_msg text," +
                "    t_img text," +
                "    d_receive varchar(2)," +
                "    id_type_notif smallint NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(SQL);

        SQL = "CREATE TABLE " + IMG_POST_TABLE + " (" +
                "    id_post varchar(11) NOT NULL," +
                "    type char(1) NOT NULL," +
                "    t_img text" +
                ")";
        sqLiteDatabase.execSQL(SQL);

    }

    //Function untuk update ulang table jika versi berbeda dari sebelumnya
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TACIT_POST_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPLICIT_POST_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TACIT_COMMENT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPLICIT_COMMENT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DIVISION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IMG_POST_TABLE);

        onCreate(sqLiteDatabase);
    }

    private SQLiteDatabase db;

    public final SQLiteDatabase openDB() {
        db = getWritableDatabase();
        db.beginTransaction();
        return db;
    }

    public void closeDB() {
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public PostObject getPost(int divisi, int type, String id_post) {
        openDB();

        String limit = " LIMIT 0, 1";
        String where = " WHERE a.id_div = '" + divisi + "' AND a.id_post = '" + id_post + "'";

        String sql = "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                "(SELECT COUNT(*) FROM " + TACIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                "a.t_img FROM " + TACIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user=b.id_user " +
                where + limit;

        if (type == MainActivity.EXPLICIT) {
            sql = "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                    "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                    "(SELECT COUNT(*) FROM " + EXPLICIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                    "a.t_img FROM " + EXPLICIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user=b.id_user " +
                    where + limit;
        }

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return new PostObject();
        }

        cursor.moveToFirst();

        PostObject postObject = new PostObject();
        postObject.setId_post(cursor.getString(0));
        postObject.setId_user(cursor.getString(1));
        postObject.setId_div(cursor.getString(2));
        postObject.setV_status_user(cursor.getString(3));
        postObject.setTitle(cursor.getString(4));
        postObject.setContent(cursor.getString(5));
        postObject.setUser_name(cursor.getString(6));
        try {
            postObject.setCreated(Utils.parseDate(cursor.getString(7), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        postObject.setB_closed(cursor.getInt(8) == 1);
        postObject.setB_hapus(cursor.getInt(9) == 1);
        postObject.setJumlah_comment(cursor.getInt(10));
        String b64 = cursor.getString(11);
        //Log.i(TAG, b64);
        if (!b64.equals("null")) {
            List<Bitmap> img = new ArrayList<>();
            try {
                img.add(Utils.decodeBase64ToBitmap(b64));
                postObject.setImg(img);
            } catch (Exception e) {
                postObject.setImg(null);
                Log.e(TAG + ". error b64", e.toString());
            }

        }

        cursor.close();
        closeDB();

        return postObject;
    }


    public List<PostObject> getTacitPost(int divisi, int jumlah, String query) {
        openDB();

        List<PostObject> postObjectList = new ArrayList<>();

        String limit = " LIMIT 0, " + jumlah;
        String where = " WHERE a.id_div = '" + divisi + "' ";

        if (query != null)
            where += " AND a.c_title LIKE '%" + query + "%' OR a.t_content LIKE '%" + query + "%' ";

        String sql = "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                "(SELECT COUNT(*) FROM " + TACIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                "a.t_img FROM " + TACIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user=b.id_user " +
                where + limit;

        Cursor cursor = db.rawQuery(sql, null);

        Log.i(TAG, "Cursor count " + cursor.getCount());

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            if (postObjectList.size() > 0) return postObjectList;

            return null;
        }

        cursor.moveToFirst();

        do {
            PostObject postObject = new PostObject();
            postObject.setId_post(cursor.getString(0));
            postObject.setId_user(cursor.getString(1));
            postObject.setId_div(cursor.getString(2));
            postObject.setV_status_user(cursor.getString(3));
            postObject.setTitle(cursor.getString(4));
            postObject.setContent(cursor.getString(5));
            postObject.setUser_name(cursor.getString(6));
            try {
                postObject.setCreated(Utils.parseDate(cursor.getString(7), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            postObject.setB_closed(cursor.getInt(8) == 1);
            postObject.setB_hapus(cursor.getInt(9) == 1);
            postObject.setJumlah_comment(cursor.getInt(10));
            String b64 = cursor.getString(11);
            //Log.i(TAG, b64);
            if (!b64.equals("null")) {
                List<Bitmap> img = new ArrayList<>();
                try {
                    img.add(Utils.decodeBase64ToBitmap(b64));
                    postObject.setImg(img);
                } catch (Exception e) {
                    postObject.setImg(null);
                    //Log.e(TAG + ". error b64", e.toString());
                }

            }
            postObjectList.add(postObject);
        } while (cursor.moveToNext());

        cursor.close();

        closeDB();

        return postObjectList;
    }

    public List<PostObject> getExplicitPost(int divisi, int jumlah, String query) {
        openDB();

        String limit = " LIMIT 0, " + jumlah;
        String where = " WHERE a.id_div = '" + divisi + "' ";

        if (query != null)
            where += " AND a.c_title LIKE '%" + query + "%' OR a.t_content LIKE '%" + query + "%' ";

        String sql = "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                "(SELECT COUNT(*) FROM " + EXPLICIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                "a.t_img FROM " + EXPLICIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user=b.id_user " +
                where + limit;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<PostObject> postObjectList = new ArrayList<>();

        do {
            PostObject postObject = new PostObject();
            postObject.setId_post(cursor.getString(0));
            postObject.setId_user(cursor.getString(1));
            postObject.setId_div(cursor.getString(2));
            postObject.setV_status_user(cursor.getString(3));
            postObject.setTitle(cursor.getString(4));
            postObject.setContent(cursor.getString(5));
            postObject.setUser_name(cursor.getString(6));
            try {
                postObject.setCreated(Utils.parseDate(cursor.getString(7), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            postObject.setB_closed(cursor.getInt(8) == 1);
            postObject.setB_hapus(cursor.getInt(9) == 1);
            postObject.setJumlah_comment(cursor.getInt(10));
            String b64 = cursor.getString(11);
            //Log.i(TAG, b64);
            if (!b64.equals("null")) {
                List<Bitmap> img = new ArrayList<>();
                try {
                    img.add(Utils.decodeBase64ToBitmap(b64));
                    postObject.setImg(img);
                } catch (Exception e) {
                    postObject.setImg(null);
                    //Log.e(TAG + ". error b64", e.toString());
                }

            }
            postObjectList.add(postObject);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return postObjectList;
    }

    public List<CommentObject> getTacitComment(String id_post) {
        openDB();

        String sql = "SELECT a.id_comment, a.id_post, a.id_user, a.b_hapus, a.d_commented, a.t_comment," +
                "b.c_status, b.v_namauser, a.t_img FROM " + TACIT_COMMENT_TABLE + " a JOIN " + USER_TABLE + " b " +
                " ON a.id_user = b.id_user AND a.id_post = '" + id_post + "' ORDER BY a.d_commented ";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<CommentObject> commentObjectList = new ArrayList<>();

        do {
            CommentObject commentObject = new CommentObject();
            commentObject.setId_comment(cursor.getString(0));
            commentObject.setId_post(cursor.getString(1));
            commentObject.setId_user(cursor.getString(2));
            commentObject.setB_hapus(cursor.getInt(3) == 1);

            try {
                commentObject.setCreated(Utils.parseDate(cursor.getString(4), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            commentObject.setComment(cursor.getString(5));
            commentObject.setV_status_user(cursor.getString(6));
            commentObject.setUser_name(cursor.getString(7));

            String b64 = cursor.getString(8);
            if (!b64.equals("null")) {
                try {
                    commentObject.setImg(Utils.decodeBase64ToBitmap(b64));
                } catch (Exception e) {
                    commentObject.setImg(null);
                }
            }
            commentObjectList.add(commentObject);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return commentObjectList;
    }

    public List<CommentObject> getExplicitComment(String id_post) {
        openDB();
        String sql = "SELECT a.id_comment, a.id_post, a.id_user, a.b_hapus, a.d_commented, a.t_comment," +
                "b.c_status, b.v_namauser, a.t_img FROM " + EXPLICIT_COMMENT_TABLE + " a JOIN " + USER_TABLE + " b " +
                " ON a.id_user = b.id_user AND a.id_post = '" + id_post + "' ORDER BY a.d_commented ";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<CommentObject> commentObjectList = new ArrayList<>();

        do {
            CommentObject commentObject = new CommentObject();
            commentObject.setId_comment(cursor.getString(0));
            commentObject.setId_post(cursor.getString(1));
            commentObject.setId_user(cursor.getString(2));
            commentObject.setB_hapus(cursor.getInt(3) == 1);

            try {
                commentObject.setCreated(Utils.parseDate(cursor.getString(4), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            commentObject.setComment(cursor.getString(5));
            commentObject.setV_status_user(cursor.getString(6));
            commentObject.setUser_name(cursor.getString(7));

            String b64 = cursor.getString(8);
            if (!b64.equals("null")) {
                try {
                    commentObject.setImg(Utils.decodeBase64ToBitmap(b64));
                } catch (Exception e) {
                    commentObject.setImg(null);
                }

            }
            commentObjectList.add(commentObject);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return commentObjectList;
    }

    public List<CategoryObject> getCategory() {
        openDB();

        String sql = "SELECT * FROM " + DIVISION_TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<CategoryObject> categoryObjectList = new ArrayList<>();

        do {
            CategoryObject categoryObject = new CategoryObject();
            categoryObject.setId_div(cursor.getInt(0));
            categoryObject.setNm_div(cursor.getString(1));
            categoryObjectList.add(categoryObject);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return categoryObjectList;
    }


    public CategoryObject getCategory(String id_div) {
        openDB();

        String sql = "SELECT * FROM " + DIVISION_TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();


        CategoryObject categoryObject = new CategoryObject();
        categoryObject.setId_div(cursor.getInt(0));
        categoryObject.setNm_div(cursor.getString(1));


        cursor.close();
        closeDB();

        return categoryObject;
    }

    @WorkerThread
    public void savePost(JSONArray array) {
        openDB();

        db.delete(TACIT_POST_TABLE, null, null);
        db.delete(EXPLICIT_POST_TABLE, null, null);
        db.delete(IMG_POST_TABLE, null, null);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("id_div", current.getString("id_div"));
                values.put("d_created", current.getString("d_created"));
                values.put("b_closed", current.getString("b_closed"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("c_title", current.getString("c_title"));
                values.put("t_content", current.getString("t_content"));
                values.put("t_img", "null");

                int type = current.getInt("type_post");

                String image = current.optString("t_img");

                try {
                    if (image != null && !TextUtils.isEmpty(image)) {
                        if (image.startsWith("{"))
                            image = image.replace("{", "");
                        if (image.endsWith("}"))
                            image = image.replace("}", "");
                        if (image.contains("\""))
                            image = image.replace("\"", "");
                        if (image.contains(" "))
                            image = image.replace(" ", "+");

                        if (image.contains(",")) {
                            String[] images = image.split(",");
                            if (images.length > 0)
                                values.put("t_img", images[0]);

                            for (int j = 0; j < images.length; j++) {
                                ContentValues val = new ContentValues();
                                val.put("id_post", current.getString("id_post"));
                                val.put("t_img", images[j]);
                                val.put("type", type);
                                db.insert(IMG_POST_TABLE, null, val);
                            }
                        } else {
                            values.put("t_img", image);

                            ContentValues val = new ContentValues();
                            val.put("id_post", current.getString("id_post"));
                            val.put("t_img", image);
                            val.put("type", type);
                            db.insert(IMG_POST_TABLE, null, val);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG + "insert img", " id_post " +
                            current.getString("id_post") + "cause " + e.toString());
                }

                String table = TACIT_POST_TABLE;
                if (type == MainActivity.EXPLICIT) {
                    table = EXPLICIT_POST_TABLE;
                }

                db.insert(table, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    private String getIdPost(String table, String div) {
        Cursor cursor = db.rawQuery("SELECT id_post FROM " + table + " WHERE id_div=?", new String[]{div});

        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        StringBuilder id_post = new StringBuilder();

        int i = 0;
        do {
            if (i > 0) id_post.append(",");
            id_post.append("'").append(cursor.getString(0)).append("'");
            i++;
        } while (cursor.moveToNext());

        cursor.close();

        return id_post.toString();
    }

    @WorkerThread
    public void savePostDiv(JSONArray array, String div) {
        openDB();

        String tacitPost = getIdPost(TACIT_POST_TABLE, div);
        String explicitPost = getIdPost(EXPLICIT_POST_TABLE, div);

        String delete = "DELETE FROM IMG_POST_TABLE WHERE type = '%s' AND id_post in (%s)";

        if (tacitPost != null) {
            Log.i(TAG, "TACITPOST ID " + tacitPost);
            db.execSQL(String.format(delete, "0", tacitPost));
//            int i = db.delete(IMG_POST_TABLE, "type=? AND id_post in " + makePlaceholders(tacitPost.length()), new String[]{"0", tacitPost});

        }

        if (explicitPost != null) {
            Log.i(TAG, "EXPLICITPOST ID " + explicitPost);
            db.execSQL(String.format(delete, "1", explicitPost));
//            int i = db.delete(IMG_POST_TABLE, "type=? AND id_post in " + makePlaceholders(explicitPost.length()), new String[]{"1", explicitPost});
        }

        db.delete(TACIT_POST_TABLE, "id_div=?", new String[]{div});
        db.delete(EXPLICIT_POST_TABLE, "id_div=?", new String[]{div});
        //db.delete(IMG_POST_TABLE, null, null);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("id_div", current.getString("id_div"));
                values.put("d_created", current.getString("d_created"));
                values.put("b_closed", current.getString("b_closed"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("c_title", current.getString("c_title"));
                values.put("t_content", current.getString("t_content"));
                values.put("t_img", "null");

                int type = current.getInt("type_post");

                String image = current.optString("t_img");

                try {
                    if (image != null && !TextUtils.isEmpty(image)) {
                        if (image.startsWith("{"))
                            image = image.replace("{", "");
                        if (image.endsWith("}"))
                            image = image.replace("}", "");
                        if (image.contains("\""))
                            image = image.replace("\"", "");
                        if (image.contains(" "))
                            image = image.replace(" ", "+");

                        if (image.contains(",")) {
                            String[] images = image.split(",");
                            if (images.length > 0)
                                values.put("t_img", images[0]);

                            for (int j = 0; j < images.length; j++) {
                                ContentValues val = new ContentValues();
                                val.put("id_post", current.getString("id_post"));
                                val.put("t_img", images[j]);
                                val.put("type", type);
                                db.insert(IMG_POST_TABLE, null, val);
                            }
                        } else {
                            values.put("t_img", image);

                            ContentValues val = new ContentValues();
                            val.put("id_post", current.getString("id_post"));
                            val.put("t_img", image);
                            val.put("type", type);
                            db.insert(IMG_POST_TABLE, null, val);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG + "insert img", " id_post " +
                            current.getString("id_post") + "cause " + e.toString());
                }

                String table = TACIT_POST_TABLE;
                if (type == MainActivity.EXPLICIT) {
                    table = EXPLICIT_POST_TABLE;
                }

                db.insert(table, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    private String makePlaceholders(int len) {

        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("(?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();

    }

    @WorkerThread
    public void saveDivision(JSONArray array) {
        openDB();

        db.delete(DIVISION_TABLE, null, null);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_div", current.getString("id_div"));
                values.put("nm_div", current.getString("nm_div"));
                db.insert(DIVISION_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    @WorkerThread
    public void saveCommentPost(JSONArray array) {
        openDB();

        db.delete(TACIT_COMMENT_TABLE, null, null);
        db.delete(EXPLICIT_COMMENT_TABLE, null, null);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_comment", current.getString("id_comment"));
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("d_commented", current.getString("d_commented"));
                values.put("t_comment", current.getString("t_comment"));
                values.put("t_img", current.getString("t_img"));

                String table = TACIT_COMMENT_TABLE;
                if (current.getInt("type_post") == 1) {
                    table = EXPLICIT_COMMENT_TABLE;
                }

                db.insert(table, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    @WorkerThread
    public void saveUser(JSONArray array) {
        openDB();

        db.delete(USER_TABLE, null, null);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_user", current.getString("id_user"));
                values.put("c_status", current.getString("c_status"));
                values.put("v_namauser", current.getString("v_namauser"));
                values.put("d_tgllahir", current.getString("d_tgllahir"));
                db.insert(USER_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }


    @MainThread
    public void saveCommentPost(JSONArray array, String id_post, String table) {
        openDB();

        db.delete(table, "id_post=?", new String[]{id_post});

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_comment", current.getString("id_comment"));
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("d_commented", current.getString("d_commented"));
                values.put("t_comment", current.getString("t_comment"));
                values.put("t_img", current.getString("t_img"));

                db.insert(table, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    public List<NotificationObject> getNotification() {
        openDB();

        String sql = "SELECT * FROM " + NOTIFICATION_TABLE;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<NotificationObject> notificationObjects = new ArrayList<>();

        do {
            int id_type_post = cursor.getInt(0);
            String id_div = cursor.getString(1);
            String id_post = cursor.getString(2);
            String id_comment = cursor.getString(3);
            String id_user = cursor.getString(4);
            String t_msg = cursor.getString(5);

            Bitmap img = null;
            Date d_receive = null;
            try {
                String b64 = cursor.getString(6);
                if (!b64.equals("null")) {
                    img = Utils.decodeBase64ToBitmap(b64);
                }
            } catch (Exception e) {
                Log.e(TAG + ". error b64", e.toString());
            }

            try {
                String date = cursor.getString(7);
                Log.i(TAG, "NOTIF DATE " + date);
                d_receive = Utils.parseDate(date, "yyyy-MM-dd HH:mm:ss.SSSSSSZ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            int id_type_notif = cursor.getInt(8);

            NotificationObject object =
                    new NotificationObject(id_type_post, id_type_notif, id_div, id_post, id_comment, id_user, t_msg, img, d_receive);
            notificationObjects.add(object);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return notificationObjects;
    }

    public List<Bitmap> getImgPost(String id_post, int type) {
        openDB();

        String sql = "SELECT * FROM " + IMG_POST_TABLE + " WHERE id_post = '" + id_post + "' AND type = '" + type + "'";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            return null;
        }

        cursor.moveToFirst();

        List<Bitmap> imgBitmap = new ArrayList<>();

        do {
            Log.i(TAG + ".img", "id_post " + cursor.getString(0));
            try {
                String b64 = cursor.getString(2);
                if (!b64.equals("null")) {
                    Bitmap img = Utils.decodeBase64ToBitmap(b64);
                    imgBitmap.add(img);
                }
            } catch (Exception e) {
                Log.e(TAG + ". error b64", e.toString());
            }

        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return imgBitmap;
    }

    @WorkerThread
    public void saveTacitPost(JSONArray array, int div) {
        openDB();

        db.delete(TACIT_POST_TABLE, "id_div=?", new String[]{String.valueOf(div)});


        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("id_div", div);
                values.put("d_created", current.getString("d_created"));
                values.put("b_closed", current.getString("b_closed"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("c_title", current.getString("c_title"));
                values.put("t_content", current.getString("t_content"));
                values.put("t_img", "null");

                String image = current.optString("t_img");

                db.delete(IMG_POST_TABLE, "id_post=?", new String[]{current.getString("id_post")});

                try {
                    if (image != null && !TextUtils.isEmpty(image)) {
                        if (image.startsWith("{"))
                            image = image.replace("{", "");
                        if (image.endsWith("}"))
                            image = image.replace("}", "");
                        if (image.contains("\""))
                            image = image.replace("\"", "");
                        if (image.contains(" "))
                            image = image.replace(" ", "+");

                        if (image.contains(",")) {
                            String[] images = image.split(",");
                            if (images.length > 0)
                                values.put("t_img", images[0]);

                            for (int j = 0; j < images.length; j++) {
                                ContentValues val = new ContentValues();
                                val.put("id_post", current.getString("id_post"));
                                val.put("t_img", images[j]);
                                val.put("type", MainActivity.TACIT);
                                db.insert(IMG_POST_TABLE, null, val);
                            }
                        } else {
                            values.put("t_img", image);

                            ContentValues val = new ContentValues();
                            val.put("id_post", current.getString("id_post"));
                            val.put("t_img", image);
                            val.put("type", MainActivity.TACIT);
                            db.insert(IMG_POST_TABLE, null, val);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG + "insert img", " id_post " +
                            current.getString("id_post") + "cause " + e.toString());
                }

                db.insert(TACIT_POST_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    @WorkerThread
    public void saveExplicitPost(JSONArray array, int div) {
        openDB();

        db.delete(EXPLICIT_POST_TABLE, "id_div=?", new String[]{String.valueOf(div)});

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("id_div", div);
                values.put("d_created", current.getString("d_created"));
                values.put("b_closed", current.getString("b_closed"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("c_title", current.getString("c_title"));
                values.put("t_content", current.getString("t_content"));
                values.put("t_img", "null");

                String image = current.optString("t_img");

                db.delete(IMG_POST_TABLE, "id_post=?", new String[]{current.getString("id_post")});

                try {
                    if (image != null && !TextUtils.isEmpty(image)) {
                        if (image.startsWith("{"))
                            image = image.replace("{", "");
                        if (image.endsWith("}"))
                            image = image.replace("}", "");
                        if (image.contains("\""))
                            image = image.replace("\"", "");
                        if (image.contains(" "))
                            image = image.replace(" ", "+");

                        if (image.contains(",")) {
                            String[] images = image.split(",");
                            if (images.length > 0)
                                values.put("t_img", images[0]);

                            for (int j = 0; j < images.length; j++) {
                                ContentValues val = new ContentValues();
                                val.put("id_post", current.getString("id_post"));
                                val.put("t_img", images[j]);
                                val.put("type", MainActivity.EXPLICIT);
                                db.insert(IMG_POST_TABLE, null, val);
                            }
                        } else {
                            values.put("t_img", image);

                            ContentValues val = new ContentValues();
                            val.put("id_post", current.getString("id_post"));
                            val.put("t_img", image);
                            val.put("type", MainActivity.EXPLICIT);
                            db.insert(IMG_POST_TABLE, null, val);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG + "insert img", " id_post " +
                            current.getString("id_post") + "cause " + e.toString());
                }

                db.insert(EXPLICIT_POST_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }


    @WorkerThread
    public void saveExplicitComment(JSONArray array) {
        openDB();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_comment", current.getString("id_comment"));
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("d_commented", current.getString("d_commented"));
                values.put("t_comment", current.getString("t_comment"));
                values.put("t_img", current.getString("t_img"));

                db.delete(EXPLICIT_COMMENT_TABLE, "id_comment=?", new String[]{current.getString("id_comment")});

                db.insert(EXPLICIT_COMMENT_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    @WorkerThread
    public void saveTacitComment(JSONArray array) {
        openDB();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id_comment", current.getString("id_comment"));
                values.put("id_post", current.getString("id_post"));
                values.put("id_user", current.getString("id_user"));
                values.put("b_hapus", current.getString("b_hapus"));
                values.put("d_commented", current.getString("d_commented"));
                values.put("t_comment", current.getString("t_comment"));
                values.put("t_img", current.getString("t_img"));

                db.delete(TACIT_COMMENT_TABLE, "id_comment=?", new String[]{current.getString("id_comment")});

                db.insert(TACIT_COMMENT_TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        closeDB();
    }

    public List<User> getAllUser() {
        openDB();

        String sql = "SELECT id_user,v_namauser,c_status FROM " + USER_TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount()<=0){
            cursor.close();
            closeDB();
            return null;
        }

        List<User> listUser = new ArrayList<>();
        cursor.moveToFirst();
        do {
            User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            listUser.add(user);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();
        return listUser;
    }


    public List<PostObject> getAllPost(String id_user) {
        openDB();

        List<PostObject> postObjectList = new ArrayList<>();

        String sql = "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                "(SELECT COUNT(*) FROM " + TACIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                "a.t_img,0 AS type  FROM " + TACIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user= '"+ id_user +"'"+
                "   UNION" +
                "SELECT a.id_post, a.id_user, a.id_div, b.c_status, a.c_title, a.t_content," +
                "b.v_namauser, a.d_created, a.b_closed, a.b_hapus," +
                "(SELECT COUNT(*) FROM " + EXPLICIT_COMMENT_TABLE + " c WHERE c.id_post = a.id_post)," +
                "a.t_img,0 AS type FROM " + EXPLICIT_POST_TABLE + " a JOIN " + USER_TABLE + " b ON a.id_user= '"+ id_user +"'";

        Cursor cursor = db.rawQuery(sql, null);

        Log.i(TAG, "Cursor count " + cursor.getCount());

        if (cursor.getCount() <= 0) {
            cursor.close();
            closeDB();
            if (postObjectList.size() > 0) return postObjectList;

            return null;
        }

        cursor.moveToFirst();

        do {
            PostObject postObject = new PostObject();
            postObject.setId_post(cursor.getString(0));
            postObject.setId_user(cursor.getString(1));
            postObject.setId_div(cursor.getString(2));
            postObject.setV_status_user(cursor.getString(3));
            postObject.setTitle(cursor.getString(4));
            postObject.setContent(cursor.getString(5));
            postObject.setUser_name(cursor.getString(6));
            try {
                postObject.setCreated(Utils.parseDate(cursor.getString(7), "yyyy-MM-dd HH:mm:ss.SSSSSSZ"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            postObject.setB_closed(cursor.getInt(8) == 1);
            postObject.setB_hapus(cursor.getInt(9) == 1);
            postObject.setJumlah_comment(cursor.getInt(10));
            String b64 = cursor.getString(11);
            //Log.i(TAG, b64);
            if (!b64.equals("null")) {
                List<Bitmap> img = new ArrayList<>();
                try {
                    img.add(Utils.decodeBase64ToBitmap(b64));
                    postObject.setImg(img);
                } catch (Exception e) {
                    postObject.setImg(null);
                    //Log.e(TAG + ". error b64", e.toString());
                }

            }

            postObject.setType(cursor.getInt(12));
            postObjectList.add(postObject);
        } while (cursor.moveToNext());

        cursor.close();
        closeDB();

        return postObjectList;
    }

}
