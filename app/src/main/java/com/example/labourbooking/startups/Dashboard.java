package com.example.labourbooking.startups;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.labourbooking.Fragments.More;
import com.example.labourbooking.Fragments.MyTasks;
import com.example.labourbooking.Fragments.PostTask;
import com.example.labourbooking.Fragments.SearchFragment;
import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Controlers;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navigationView;
    SearchFragment searchFragment;
    long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fg_cnotainer, new SearchFragment()).commit();
        }
        initGuiI();
    }

    void initGuiI() {
        searchFragment = new SearchFragment();
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.earn_money:
                Controlers.removeStackFragments(getSupportFragmentManager());
                getSupportFragmentManager().beginTransaction().replace(R.id.fg_cnotainer, new SearchFragment(), "earn").commit();
                break;

            case R.id.my_tasks:
                Controlers.removeStackFragments(getSupportFragmentManager());
                getSupportFragmentManager().beginTransaction().replace(R.id.fg_cnotainer, new MyTasks(), "mytasks").commit();
                break;

            case R.id.more:
                Controlers.removeStackFragments(getSupportFragmentManager());
                getSupportFragmentManager().beginTransaction().replace(R.id.fg_cnotainer, new More(), "more").commit();
                break;

            case R.id.post:
                Controlers.removeStackFragments(getSupportFragmentManager());
                getSupportFragmentManager().beginTransaction().replace(R.id.fg_cnotainer, new PostTask(), "post").commit();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Controlers.topToast(Dashboard.this, "Press once again to exit!");
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag("post");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
