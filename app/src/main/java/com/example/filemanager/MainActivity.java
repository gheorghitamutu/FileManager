package com.example.filemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.example.filemanager.ui.home.HFragment;
import com.example.filemanager.ui.storage.external.ESFragment;
import com.example.filemanager.ui.storage.internal.ISFragment;
import com.example.filemanager.ui.storage.options.ODialog;
import com.example.filemanager.ui.storage.options.OModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ODialog.OnListFragmentInteractionListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private static Fragment currentNavigationFragment;

    private static final int REQUEST_CODE_WRITE_STORAGE = 102;
    private static final int REQUEST_CODE_READ_STORAGE = 101;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isStoragePermissionGranted()) {
            this.finish();
            System.exit(0);
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(mDrawerToggle);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static Fragment getCurrentNavigationFragment() {
        return currentNavigationFragment;
    }

    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        } else {
            Log.v(TAG, "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
            return false;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        } else {
            Log.v(TAG, "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                // resume tasks needing this permission
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentContainerView containerView = findViewById(R.id.nav_host_fragment);
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                currentNavigationFragment = new HFragment();
                break;
            case R.id.nav_internal_storage:
                currentNavigationFragment = new ISFragment();
                break;
            case R.id.nav_external_storage:
                currentNavigationFragment = new ESFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }

        item.setChecked(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, currentNavigationFragment).commit();

        containerView.post(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        toolbar.setTitle(item.getTitle());

        return true;
    }

    @Override
    public void onListFragmentInteraction(OModel.Item item) {
        if (currentNavigationFragment instanceof ISFragment) {
            ISFragment isf = (ISFragment) currentNavigationFragment;
            isf.processActionOnItem(item);
        }
    }

    @Override
    public void onBackPressed() {
        boolean showExitAppDialog = false;

        if (currentNavigationFragment instanceof ISFragment) {
            if (Manager.removePathLevel()) {
                Manager.refreshFragment(this, currentNavigationFragment);
            } else {
                showExitAppDialog = true;
            }
        }
        // TODO: should add the rest of the fragments
        else {
            showExitAppDialog = true;
        }

        if (showExitAppDialog) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing App")
                    .setMessage("Are you sure you want to close this app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}
