package com.example.box.Entity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.box.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationPicker implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private FragmentActivity fragmentActivity;
    private AlertDialog alertDialog;
    private SupportMapFragment supportMapFragment;
    private Marker currentMarker;
    private String curAddress;

    private View view;
    private GoogleMap myMap;
    private SearchView searchView;
    private RelativeLayout confirmButton;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_REQUEST_CODE = 102;

    public interface LocationConfirmationListener {
        void onLocationConfirmed(String address);
    }

    private LocationConfirmationListener confirmationListener;

    public LocationPicker(FragmentActivity fragmentActivity, LocationConfirmationListener confirmationListener) {
        this.fragmentActivity = fragmentActivity;
        this.curAddress = "";
        this.confirmationListener = confirmationListener;
        this.currentMarker = null;

        // Inflate view
        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
        view = inflater.inflate(R.layout.map_layout, null);

        // Initialize UI
        initializeUI();
    }

    public void loadMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragmentActivity);
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);

        // Set view
        builder.setView(view);
        builder.setCancelable(false);

        // Set up search view
        setUpSearchView();

        // Get map fragment
        supportMapFragment = (SupportMapFragment) fragmentActivity.getSupportFragmentManager().
                findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        // Create and show the dialog
        alertDialog = builder.create();
        alertDialog.show();

        // Confirm button
        confirmLocation();
    }

    private void initializeUI() {
        searchView = (SearchView) view.findViewById(R.id.search_view);
        confirmButton = (RelativeLayout) view.findViewById(R.id.confirm_button);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Set up the map and get the current location based on gps
        myMap = googleMap;
        getCurrentLocation();

        // Get the location when move the marker
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Remove the previous marker if exists
                if (currentMarker != null)
                {
                    currentMarker.remove();
                }

                // Create new marker and zoom camera to the location
                currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,  10));

                // Get the full address
                Double latitude = latLng.latitude;
                Double longitude = latLng.longitude;
                curAddress = "";
                curAddress = getAddress(longitude, latitude);

                // Display the full address in search view
                searchView.setQuery(curAddress, false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            // Permission accepted
            getCurrentLocation();
        }

        else
        {
            // Permission denied
            Toast.makeText(fragmentActivity, "Cần quyền truy cập vị trí để tiếp tục", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Press enter after typing
                // Get the full address from search view
                String locationStr = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (locationStr != null)
                {
                    // Use Geocoder to get the latitude and longitude from the full address
                    Geocoder geocoder = new Geocoder(fragmentActivity);

                    try
                    {
                        addressList = geocoder.getFromLocationName(locationStr, 1);
                    }

                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);

                    // Create new latlng from latitude and longitude
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Remove the previous marker if exists
                    if (currentMarker != null)
                    {
                        currentMarker.remove();
                    }

                    // Create new marker and zoom camera to the location
                    currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,  10));

                    // Get the current full address
                    curAddress = "";
                    getAddress(latLng.longitude, latLng.latitude);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: Implement Location Suggestion (get location list)
                return true;
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(fragmentActivity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(fragmentActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            // Get the current location based on gps
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();

                            LatLng currentLocation = new LatLng(latitude, longitude);

                            // Add the marker and move camera, zoom 10
                            currentMarker = myMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,  10));

                            // Get the current full address
                            curAddress = getAddress(longitude, latitude);

                            // Show the full address in the search view
                            searchView.setQuery(curAddress, false);
                        }
                    });
        }

        else
        {
            // Permission not granted
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(fragmentActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_REQUEST_CODE);
    }

    private String getAddress(double longitude, double latitude) {
        String res = "";
        Geocoder geocoder = new Geocoder(fragmentActivity, Locale.getDefault());

        try
        {
            List<Address> addressList =
                    geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null)
            {
                Address address = addressList.get(0);
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                {
                    stringBuilder.append(address.getAddressLine(i))
                            .append("\n");
                }

                res = stringBuilder.toString();
            }
        }

        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return res;
    }

    private void confirmLocation() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the confirmed address
                if (confirmationListener != null)
                {
                    confirmationListener.onLocationConfirmed(curAddress);
                }

                // Destroy map manager
                if (supportMapFragment != null) {
                    fragmentActivity.getSupportFragmentManager().beginTransaction().remove(supportMapFragment).commit();
                }

                // Dismiss the dialog
                alertDialog.dismiss();
            }
        });
    }
}
