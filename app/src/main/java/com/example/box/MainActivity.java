package com.example.box;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final String IPStr = "192.168.1.6";

    private TextView signUpBtn;
    private LinearLayout facebookBtn;
    private LinearLayout googleBtn;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_REQUEST_CODE = 100;
    private String id = "";
    private String name = "";
    private String currentAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //User is already logged in
            loadHomePage();
        }

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn
                            .getSignedInAccountFromIntent(result.getData());

                    try {
                        GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                        handleGoogleAccessToken(googleSignInAccount);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        // Initialize UI
        initializeUI();

        // Click sign up button
        clickSignUpBtn();

        // Click facebook button
        clickFacebookBtn();

        // Click google button
        clickGoogleBtn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            // Permission accepted
            saveUserInfo();

        }
    }

    private void initializeUI() {
        signUpBtn = (TextView) findViewById(R.id.sign_up_btn);
        facebookBtn = (LinearLayout) findViewById(R.id.facebook_btn);
        googleBtn = (LinearLayout) findViewById(R.id.google_btn);
    }

    private void clickFacebookBtn() {
        mCallbackManager = CallbackManager.Factory.create();

        facebookBtn.setOnClickListener(view -> {
            FacebookSdk.setApplicationId("1418588802292677");
            FacebookSdk.setClientToken("ea744160e94a86857cead716506d0c86");

            FacebookSdk.sdkInitialize(getApplicationContext());

            LoginManager.getInstance()
                    .logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));

            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(MainActivity.this, "Log in successfully", Toast.LENGTH_SHORT)
                            .show();

                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    //Do nothing
                }

                @Override
                public void onError(@NonNull FacebookException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //FirebaseUser user = mAuth.getCurrentUser();
                        loadHomePage();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void clickGoogleBtn() {
        googleBtn.setOnClickListener(v -> configureGoogleLogIn());
    }

    private void configureGoogleLogIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    private void handleGoogleAccessToken(GoogleSignInAccount googleSignInAccount) {
        // Got an ID token from Google
        String idToken = googleSignInAccount.getIdToken();

        // Use it to authenticate with Firebase
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Get userID, name
                        id = user.getUid();
                        name = googleSignInAccount.getDisplayName();

                        // Save the user information to MySQL
                        saveUserInfo();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Fail to log in", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void saveUserInfo() {
        // Get current location
        // Check if user grant permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            CancellationTokenSource tokenSource = new CancellationTokenSource();
            CancellationToken token = tokenSource.getToken();

            // Permission granted
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentAddress = getAddress(location);

                            // Save information to MySQL
                            String urlStr = "http://" + IPStr + "/box/signUp.php";

                            UserHandler userHandler = new UserHandler(output -> {
                                // Do whatever with output here
                            });


                            userHandler.execute(UserHandler.TYPE_SIGN_UP, urlStr, id, currentAddress, name);

                            // Load the home page
                            loadHomePage();
                        }
                    });
        } else {
            // Permission not granted

            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_REQUEST_CODE);
    }

    private String getAddress(Location location) {
        String res = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

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

    private void loadHomePage() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        intent.putExtra("Address", currentAddress);
        startActivity(intent);
    }

    private void clickSignUpBtn() {
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });
    }
}
