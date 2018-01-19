package app.dodi.com.prodigykms.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import app.dodi.com.prodigykms.R;
import app.dodi.com.prodigykms.fragment.AboutFragment;
import app.dodi.com.prodigykms.fragment.CategoryFragment;
import app.dodi.com.prodigykms.fragment.HomeFragment;
import app.dodi.com.prodigykms.fragment.MasterCategory;
import app.dodi.com.prodigykms.fragment.MasterUser;
import app.dodi.com.prodigykms.fragment.NotificationFragment;
import app.dodi.com.prodigykms.object.CategoryObject;
import app.dodi.com.prodigykms.object.NotificationObject;
import app.dodi.com.prodigykms.util.RequestorHelper;
import app.dodi.com.prodigykms.util.SessionHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SessionHelper helper;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Fragment root;

    public static final int TACIT = 0;
    public static final int EXPLICIT = 1;

    private boolean notif_source = false;
    private boolean notif_handled = false;


    private NotificationObject notificationObject;

    private int id_div = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("global");

        helper = new SessionHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (helper.getDataUser().getC_status().equals("Admin")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_admin_drawer);
        }

        navigationView.getMenu().getItem(0).setTitle(String.format(getString(R.string.text_category),""));
        navigationView.setCheckedItem(R.id.nav_category);
        getSupportFragmentManager().beginTransaction().replace(R.id.root, new CategoryFragment()).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Fragment root = getSupportFragmentManager().findFragmentById(R.id.root);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (root instanceof HomeFragment) {
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.root, new CategoryFragment()).commit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Keluar");
            builder.setMessage("Apa anda yakin ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //tutup drawer
        drawerLayout.closeDrawer(GravityCompat.START);


        return processSelected(item.getItemId());
    }

    // Handle user selected return true if selected, false if not
    private boolean processSelected(@IdRes int menu) {
        switch (menu) {
            case R.id.nav_category:
                if (id_div!=-1)
                    getSupportFragmentManager().beginTransaction().replace(R.id.root, new HomeFragment()).commit();
                else
                    getSupportFragmentManager().beginTransaction().replace(R.id.root, new CategoryFragment()).commit();
                break;

            case R.id.nav_kelola_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.root, new AboutFragment()).commit();
                break;
            case R.id.nav_kelola_division:
                getSupportFragmentManager().beginTransaction().replace(R.id.root, new MasterCategory()).commit();
                break;
            case R.id.nav_kelola_user:
                getSupportFragmentManager().beginTransaction().replace(R.id.root, new MasterUser()).commit();
                break;
            case R.id.nav_notification:
                getSupportFragmentManager().beginTransaction().replace(R.id.root, new NotificationFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.root, new AboutFragment()).commit();
                break;
            case R.id.nav_logout:
                helper.logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return false;
        }
        return true;
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_title_about);
        builder.setMessage("Prodigy KMS v1");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("REGISTERED")) {
                // gcm successfully registered
                // now subscribe to `global` topic to receive app wide notifications
                FirebaseMessaging.getInstance().subscribeToTopic("global");
            } else if (intent.getAction().equals("NEWNOTIFICATION")) {
                navigationView.getMenu().getItem(1).setTitle(String.format(getString(R.string.text_notification)+" (%d)", helper.get("JUMLAHNOTIF", 0)+1));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (helper.getString("TOKEN", null) == null) {

            helper.putString("TOKEN", FirebaseInstanceId.getInstance().getToken());
        }
        if (!helper.get("ISSENDTOKEN", false) && helper.getString("TOKEN", null) != null) {
            RequestorHelper.get(this).sendToken(helper.getString("TOKEN", null));
        }
        Log.i(TAG, "TOKEN "+helper.getString("TOKEN", null));
        root = getSupportFragmentManager().findFragmentById(R.id.root);

        if (root instanceof HomeFragment) {
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.root, new HomeFragment()).commit();
        }

        setJumlahNotification(helper.get("JUMLAHNOTIF", 0));

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("REGISTERED"));

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("NEWNOTIFICATION"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);


    }

    //set category for context post
    public void setCategory(CategoryObject category) {

        this.id_div=category.getId_div();
        helper.setCategoryPosition(category.getId_div());

        navigationView.getMenu().getItem(0).setTitle(String.format(getString(R.string.text_category),category.getNm_div()));
        navigationView.invalidate();

        getSupportFragmentManager().beginTransaction().replace(R.id.root, new HomeFragment()).commitAllowingStateLoss();
    }

    public void setJumlahNotification(int jml) {
        navigationView.getMenu().getItem(1).setTitle(String.format(getString(R.string.text_notification)+" (%d)", jml));
        navigationView.invalidate();
    }

    public void openPost(NotificationObject object) {
        this.notificationObject = object;
        notif_source = true;
        navigationView.setCheckedItem(R.id.nav_category);
        getSupportFragmentManager().beginTransaction().replace(R.id.root, new CategoryFragment()).commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof CategoryFragment && notif_source) {
            ((CategoryFragment)fragment).click(notificationObject.getId_div());
            notif_source = false;
            notif_handled = true;
        }

        if (fragment instanceof HomeFragment && notif_handled) {
            ((HomeFragment)fragment).expandPost(notificationObject.getId_type_post(), notificationObject.getId_post());
            notif_handled = false;
        }
    }
}
