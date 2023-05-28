package com.example.box;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateFragment extends Fragment {
    private final String IPStr = "192.168.1.6";
    private final int IMG_REQUEST_CODE = 103;
    private View view;
    private FragmentManager fragmentManager;

    private String email;
    private String password;

    private AppCompatEditText nameEditText;
    private AppCompatEditText addressEditText;

    private ShapeableImageView avatar;
    private TextView selectAvatarButton;

    private ActivityResultLauncher activityResultLauncher;
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;

    private ImageView backBtn;
    private RelativeLayout updateBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update, container, false);

        // Initialize UI
        initializeUI();

        // Initialize firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        // Initialize firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Get email and password from previous fragment
        getEmailPassword();

        // Activity launcher for selecting image
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        //Get image uri
                        imageUri = result.getData().getData();

                        //Load image
                        Picasso.get()
                                .load(imageUri)
                                .fit()
                                .centerCrop()
                                .into(avatar);
                    }
                });

        // Select address
        selectAddress();

        // Select avatar
        selectAvatar();

        // Click back button
        clickBack();

        // Click update button
        clickUpdate();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMG_REQUEST_CODE)
        {
            // Permission accepted
            selectImage();
        }

        else
        {
            // Permission denied
            Toast.makeText(getActivity(), "Cần quyền truy cập hình ảnh để cập nhật ảnh đại diện",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initializeUI() {
        nameEditText = (AppCompatEditText) view.findViewById(R.id.name_edit_text);
        addressEditText = (AppCompatEditText) view.findViewById(R.id.address_edit_text);

        avatar = (ShapeableImageView) view.findViewById(R.id.avatar);
        selectAvatarButton = (TextView) view.findViewById(R.id.select_avatar);

        backBtn = (ImageView) view.findViewById(R.id.btn_back);
        updateBtn = (RelativeLayout) view.findViewById(R.id.btn_update);
    }

    private void getEmailPassword() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            email = bundle.getString("email");
            password = bundle.getString("password");
        }
    }

    private void selectAddress() {
        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPicker locationPicker = new LocationPicker(getActivity(),
                        new LocationPicker.LocationConfirmationListener() {
                    @Override
                    public void onLocationConfirmed(String address) {
                        addressEditText.setText(address);
                    }
                });
                locationPicker.loadMap();
            }
        });
    }

    private void selectAvatar() {
        selectAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            // Permission is granted
            selectImage();
        }

        else
        {
            // Permission not granted
            // Ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, IMG_REQUEST_CODE);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }

    private void saveInfo(String name, String address) {
        // Get storage reference
        StorageReference storageReference = firebaseStorage.getReference().child("Avatar")
                .child(System.currentTimeMillis()+"");

        // Save image to firebase storage first
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Get image url string
                        String imageStr = uri.toString();

                        // Get current user id
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userID = firebaseUser.getUid();

                        // Save information to MySQL
                        String urlStr = "http://" + IPStr + "/box/signUpEmail.php";

                        UserHandler userHandler = new UserHandler(output -> {
                            if (output.equals("YES"))
                            {
                                fragmentTransaction();
                            }

                            else if (output.equals("NO"))
                            {
                                Toast.makeText(getContext(), "Lỗi đăng ký", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });


                        userHandler.execute(UserHandler.TYPE_SIGN_UP_EMAIL, urlStr, userID, address,
                                name, imageStr);
                    }
                });
            }
        });
    }

    private void clickBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get SignUpInfoFragment from back stack using "signUpInfoFragment" tag
                SignUpInfoFragment signUpInfoFragment = (SignUpInfoFragment) fragmentManager
                        .findFragmentByTag("signUpInfoFragment");

                // Replace current fragment with SignUpInfoFragment and add to back stack
                fragmentManager.beginTransaction()
                        .replace(R.id.container, signUpInfoFragment, "signUpInfoFragment")
                        .commit();
            }
        });
    }

    private void clickUpdate() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();

                if (!isEmptyEditText(name, address))
                {
                    // Create new user
                    createNewUser(name, address);
                }
            }
        });
    }

    private void createNewUser(String name, String address) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Save information to MySQL and save image to firebase storage
                            saveInfo(name, address);
                        }

                        else
                        {
                            Toast.makeText(getContext(), "Đăng ký tài khoản không thành công", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void fragmentTransaction() {
        // Create SignUpCongrateFragment fragment
        CongratulationFragment congratulationFragment = new CongratulationFragment();

        // Replace current fragment with congratulation fragment and not add to back stack
        fragmentManager.beginTransaction()
                .replace(R.id.container, congratulationFragment)
                .commit();
    }

    private boolean isEmptyEditText(String name, String address) {
        if (name.isEmpty())
        {
            nameEditText.setError("Tên không được để trống");
            return true;
        }

        else if (address.isEmpty())
        {
            addressEditText.setError("Địa chỉ không được để trống");
            return true;
        }

        return false;
    }
}