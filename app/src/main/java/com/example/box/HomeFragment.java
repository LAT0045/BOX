package com.example.box;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    private final String IPStr = "192.168.1.6";
    private View view;

    private TextView locationText;
    private ImageView locationMoreButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI
        initializeUI();

        // Set up information
        setUpInformation();

        // Click more button in location
        clickMore();

        return view;
    }

    private void initializeUI() {
        locationText = (TextView) view.findViewById(R.id.location_text);
        locationMoreButton = (ImageView) view.findViewById(R.id.location_more_button);
    }

    private void setUpInformation() {
        String urlStr = "http://" + IPStr + "/box/getUser.php";

        // Get current user id
        String userId = FirebaseAuth.getInstance().getUid();
        Log.d("IN HOME FRAGMET", userId);

        UserHandler userHandler = new UserHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("HASHED ID IN HOME FRAGMENT", output);
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
                            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                                    .get(SharedViewModel.class);
                            sharedViewModel.setUser(user);

                            // Set up this user's location
                            if (!address.isEmpty())
                            {
                                locationText.setText(address);
                            }

                            else
                            {
                                locationText.setText("Chưa cập nhật địa chỉ");
                            }
                        }
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        userHandler.execute(UserHandler.TYPE_GET_USER_INFO, urlStr, userId);
    }

    private void clickMore() {
        locationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPicker locationPicker = new LocationPicker(getActivity(),
                        new LocationPicker.LocationConfirmationListener() {
                            @Override
                            public void onLocationConfirmed(String address) {
                                locationText.setText(address);
                            }
                        });
                locationPicker.loadMap();
            }
        });
    }
}