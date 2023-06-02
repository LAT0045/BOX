package com.example.box.FragmentAndActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.User;
import com.example.box.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        // Get current user
        getUserInformation();

        //Set home fragment when first load
        changeFragment(new HomeFragment());

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

    private void getUserInformation() {
        String urlStr = "/box/getUser.php";

        // Get current user id
        String userId = FirebaseAuth.getInstance().getUid();

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("NO"))
                {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        // Iterate over the JSON array
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // Map the values to your ClassA object
                            String address = jsonObject.getString("diachi");
                            String phoneNumber = jsonObject.getString("sodienthoai");
                            String name = jsonObject.getString("tenkhachhang");
                            String avatar = jsonObject.getString("anhdaidien");

                            User user = new User(address, phoneNumber, name, avatar);

                            // Put it in SharedViewModel in order to share it between fragments
                            SharedViewModel sharedViewModel = new ViewModelProvider(Home.this)
                                    .get(SharedViewModel.class);
                            sharedViewModel.setUser(user);
                        }
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        dataHandler.execute(DataHandler.TYPE_GET_USER_INFO, urlStr, userId);
    }
}