package com.example.box;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize UI
        initializeUI();

        //Set up view pager
        setUpViewPager();

        //Select icon in bottom navigation
        selectOption();
    }

    private void initializeUI() {
        viewPager2 = (ViewPager2) findViewById(R.id.view_pager);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        //Select icon in navigation when swipe to another layout
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position)
                {
                    case 0:
                        bottomNav.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.favorite).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.order).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.notification).setChecked(true);
                        break;
                    case 4:
                        bottomNav.getMenu().findItem(R.id.account).setChecked(true);
                        break;
                }
            }
        });
    }

    private void selectOption() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.favorite:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.order:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.notification:
                        viewPager2.setCurrentItem(3);
                        break;
                    case R.id.account:
                        viewPager2.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
}