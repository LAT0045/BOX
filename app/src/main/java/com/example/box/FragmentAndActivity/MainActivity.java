package com.example.box.FragmentAndActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;

import com.example.box.Entity.LoadingDialog;
import com.example.box.R;
import com.example.box.Entity.UserHandler;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int LOCATION_REQUEST_CODE = 100;

    private TextView signUpBtn;
    private LinearLayout facebookBtn;
    private LinearLayout googleBtn;
    private RelativeLayout signInButton;

    private AppCompatEditText emailEditText;
    private AppCompatEditText passwordEditText;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LoadingDialog loadingDialog;

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

        // Activity launcher for google sign in
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_CANCELED && result.getData() != null)
                    {
                        Task<GoogleSignInAccount> task = GoogleSignIn
                                .getSignedInAccountFromIntent(result.getData());

                        try
                        {
                            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                            handleGoogleAccessToken(googleSignInAccount);
                        }

                        catch (ApiException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        // Initialize UI
        initializeUI();

        // Initialize loading dialog
        loadingDialog = new LoadingDialog(this);

        // Click sign up button
        clickSignUpBtn();

        // Click facebook button
        clickFacebookBtn();

        // Click google button
        clickGoogleBtn();

        // Click sign in button
        clickSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE)
        {
            // Permission accepted
            saveUserInfo();
        }

        else
        {
            Toast.makeText(this, "Cần quyền truy cập vị trí để tiếp tục", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initializeUI() {
        signUpBtn = (TextView) findViewById(R.id.sign_up_btn);
        facebookBtn = (LinearLayout) findViewById(R.id.facebook_btn);
        googleBtn = (LinearLayout) findViewById(R.id.google_btn);
        signInButton = (RelativeLayout) findViewById(R.id.sign_in_button);

        emailEditText = (AppCompatEditText) findViewById(R.id.email_edit_text);
        passwordEditText = (AppCompatEditText) findViewById(R.id.password_edit_text);
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
                        // TODO: Implement save user's information for facebook user
                        loadHomePage();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void clickGoogleBtn() {
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureGoogleLogIn();
            }
        });
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
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        // Get userID, name
                        id = firebaseUser.getUid();
                        name = googleSignInAccount.getDisplayName();

                        // Save the user information to MySQL
                        saveUserInfo();
                    }

                    else
                    {
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
            // Permission granted
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentAddress = getAddress(location);

                            // Save information to MySQL
                            String urlStr = "/box/signUp.php";

                            UserHandler userHandler = new UserHandler(output -> {
                                Log.d("ID HASHED IN MAIN ACTIVITY", output);
                                if (!output.equals("NO"))
                                {
                                    // Load the home page
                                    loadHomePage();
                                }

                                else
                                {
                                    Toast.makeText(MainActivity.this, "Lỗi đăng nhập", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });


                            userHandler.execute(UserHandler.TYPE_SIGN_UP_GOOGLE, urlStr, id, currentAddress, name);
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

    private void clickSignUpBtn() {
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });
    }

    private void clickSignIn() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                if (!isEmptyInput(email, password))
                {
                    // Start loading screen
                    loadingDialog.startLoadingAlertDialog();

                    // Check if email and password are correct
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // Done checking, stop loading screen
                                    loadingDialog.endLoadingAlertDialog();

                                    if (task.isSuccessful())
                                    {
                                        // Sign in successfully
                                        // Load home page
                                        loadHomePage();
                                    }

                                    else
                                    {
                                        // Fail to sign in
                                        Toast.makeText(MainActivity.this,
                                                "Email hoặc mật khẩu sai",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void loadHomePage() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
    }

    private boolean isEmptyInput(String email, String password) {
        if (email.isEmpty())
        {
            emailEditText.setError("Email không thể để trống");
            return true;
        }

        else if (password.isEmpty())
        {
            passwordEditText.setError("Mật khẩu không thể để trống");
            return true;
        }

        return false;
    }
}
