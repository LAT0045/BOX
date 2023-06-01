package com.example.box.FragmentAndActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.box.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        //Initialize UI
        initializeUI();

        //Set up view pager
        //setUpViewPager();

        //Select icon in bottom navigation
        selectOption();
    }

    private void initializeUI() {
        frameLayout = findViewById(R.id.frame_layout);
        bottomNav = findViewById(R.id.bottom_nav);
    }

    private void selectOption() {
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.home:
                    changeFragment(new HomeFragment());
                    break;
                case R.id.favorite:
                    changeFragment(new FavoriteFragment());
                    break;
                case R.id.order:
                    changeFragment(new OrderFragment());
                    break;
                case R.id.notification:
                    changeFragment(new NotificationFragment());
                    break;
                case R.id.account:
                    changeFragment(new AccountFragment());
                    break;
            }
            return true;
        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}