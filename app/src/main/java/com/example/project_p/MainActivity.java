package com.example.project_p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ChecksListFragment checksListFragment = new ChecksListFragment();
    StatsFragment statsFragment = new StatsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    private Context context;
    public Mail MAILBOX;
    public Statistics STATS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new Mail().load(this)!=null){
            MAILBOX = new Mail().load(this);
            Log.i("prop","from_load");
        }

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.stats);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.flFragment, checksListFragment)
                .add(R.id.flFragment, statsFragment)
                .add(R.id.flFragment, settingsFragment)
                .hide(checksListFragment)
                .hide(settingsFragment).commit();


    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.checks:
                getSupportFragmentManager().beginTransaction()
                        .show(checksListFragment)
                        .hide(statsFragment)
                        .hide(settingsFragment).commit();
                return true;
            case R.id.stats:
                getSupportFragmentManager().beginTransaction()
                        .show(statsFragment)
                        .hide(checksListFragment)
                        .hide(settingsFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .hide(checksListFragment)
                        .hide(statsFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}