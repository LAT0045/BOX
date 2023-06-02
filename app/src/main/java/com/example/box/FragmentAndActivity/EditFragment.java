package com.example.box.FragmentAndActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.LoadingDialog;
import com.example.box.Entity.LocationPicker;
import com.example.box.Entity.User;
import com.example.box.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditFragment extends Fragment {
    private final int IMG_REQUEST_CODE = 103;
    private View view;
    private String userId;
    private String urlStr = "/box/updateUser.php";

    private ImageView backButton;
    private TextView saveButton;
    private RelativeLayout selectButton;

    private ShapeableImageView avatar;
    private AppCompatEditText nameEditText;
    private AppCompatEditText phoneEditText;
    private AppCompatEditText addressEditText;

    private ActivityResultLauncher activityResultLauncher;
    private Uri imageUri;
    private boolean isChangedAvatar = false;

    private User curUser;
    private User newUser;

    private int totalTasks = 0;
    private int finishedTasks = 0;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit, container, false);

        // Initialize UI
        initializeUI();

        // Set up information
        setUpInformation();

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

                        isChangedAvatar = true;
                    }
                });

        // Initialize loading dialog
        loadingDialog = new LoadingDialog(requireActivity());

        // Select new avatar
        selectAvatar();

        // Select new address
        selectAddress();

        // Click save button
        clickSave();

        // Click back button
        clickBack();

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
        backButton = (ImageView) view.findViewById(R.id.back_button);
        saveButton = (TextView) view.findViewById(R.id.save_button);
        selectButton = (RelativeLayout) view.findViewById(R.id.select_button);

        avatar = (ShapeableImageView) view.findViewById(R.id.avatar);
        nameEditText = (AppCompatEditText) view.findViewById(R.id.name_edit_text);
        phoneEditText = (AppCompatEditText) view.findViewById(R.id.phone_edit_text);
        addressEditText = (AppCompatEditText) view.findViewById(R.id.address_edit_text);
    }

    private void setUpInformation() {
        // Get user id
        userId = FirebaseAuth.getInstance().getUid();

        // Get user object from shared view model
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Get information
                curUser = user;
                newUser = curUser;

                String curName = user.getName();
                String curPhoneNumber = user.getPhoneNumber();
                String curAddress = user.getAddress();
                String curAvatarUrl = user.getAvatar();

                // Set up information
                nameEditText.setText(curName);

                if (!curPhoneNumber.isEmpty())
                {
                    phoneEditText.setText(curPhoneNumber);
                }

                if (!curAddress.isEmpty())
                {
                    addressEditText.setText(curAddress);
                }

                if (!curAvatarUrl.isEmpty())
                {
                    // User already updated avatar
                    Picasso.get()
                            .load(curAvatarUrl)
                            .fit()
                            .centerCrop()
                            .into(avatar);
                }

                else
                {
                    // User has not updated avatar
                    // Set default avatar
                    avatar.setImageResource(R.drawable.default_avatar);
                }
            }
        });
    }

    private void selectAvatar() {
        selectButton.setOnClickListener(new View.OnClickListener() {
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

    private void selectAddress() {
        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPicker locationPicker = new LocationPicker(requireActivity(),
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

    private void clickSave() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHECK SAVE FUNCTION", "BEFORE DOING ANYTHING");
                String newName = nameEditText.getText().toString().trim();
                String newPhoneNumber = phoneEditText.getText().toString();
                String newAddress = addressEditText.getText().toString();

                // Start loading screen
                loadingDialog.startLoadingAlertDialog();

                if (isChangedAvatar)
                {
                    totalTasks++;
                    changeAvatar();
                }

                if (!newName.equals(curUser.getName()))
                {
                    totalTasks++;
                    changeName(newName);
                }

                if (!newPhoneNumber.equals(curUser.getPhoneNumber()))
                {
                    totalTasks++;
                    changePhoneNumber(newPhoneNumber);
                }

                if (!newAddress.equals(curUser.getAddress()))
                {
                    totalTasks++;
                    changeAddress(newAddress);
                }

                if (totalTasks == 0)
                {
                    // User doesn't change anything
                    // Do nothing
                    // End loading screen
                    loadingDialog.endLoadingAlertDialog();
                }
            }
        });
    }

    private void changeAvatar() {
        // Get storage reference
        StorageReference storageReference = FirebaseStorage
                .getInstance().getReference().child("Avatar")
                .child(System.currentTimeMillis()+"");

        // Check if user has changed avatar before
        if (!curUser.getAvatar().isEmpty())
        {
            // User has changed avatar before
            // Remove old avatar from firebase storage
            StorageReference oldAvatarReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(curUser.getAvatar());
            oldAvatarReference.delete();
        }

        // Save image to firebase storage
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Get image url string
                        String imageStr = uri.toString();

                        // Save avatar to MySQL

                        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                if (output.equals("YES"))
                                {
                                    newUser.setAvatar(imageStr);
                                }

                                else
                                {
                                    Toast.makeText(requireContext(),
                                            "Lỗi cập nhật ảnh đại diện",
                                            Toast.LENGTH_SHORT).show();
                                }

                                taskFinishedCallBack();
                            }
                        });

                        // API > 11
                        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                DataHandler.TYPE_UPDATE_USER_INFO, urlStr, userId,
                                DataHandler.TYPE_CHANGE_USER_AVATAR, imageStr);
                    }
                });
            }
        });
    }

    private void changeName(String name) {
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.equals("YES"))
                {
                    newUser.setName(name);
                }

                else
                {
                    Toast.makeText(requireContext(), "Lỗi cập nhật tên", Toast.LENGTH_SHORT)
                            .show();
                }

                taskFinishedCallBack();
            }
        });

        // API > 11
        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                DataHandler.TYPE_UPDATE_USER_INFO, urlStr, userId,
                DataHandler.TYPE_CHANGE_USER_NAME, name);
    }

    private void changePhoneNumber(String phoneNumber) {
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.equals("YES"))
                {
                    newUser.setPhoneNumber(phoneNumber);
                }

                else
                {
                    Toast.makeText(requireContext(), "Lỗi cập nhật số điện thoại",
                            Toast.LENGTH_SHORT).show();
                }

                taskFinishedCallBack();
            }
        });

        // API > 11
        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                DataHandler.TYPE_UPDATE_USER_INFO, urlStr, userId,
                DataHandler.TYPE_CHANGE_USER_PHONE_NUMBER, phoneNumber);
    }

    private void changeAddress(String address) {
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.equals("YES"))
                {
                    newUser.setAddress(address);
                }

                else
                {
                    Toast.makeText(requireContext(), "Lỗi cập nhật địa chỉ",
                            Toast.LENGTH_SHORT).show();
                }

                taskFinishedCallBack();
            }
        });

        // API > 11
        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                DataHandler.TYPE_UPDATE_USER_INFO, urlStr, userId,
                DataHandler.TYPE_CHANGE_USER_ADDRESS, address);
    }

    private void taskFinishedCallBack() {
        finishedTasks++;

        if (finishedTasks == totalTasks)
        {
            // All tasks are done
            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT)
                    .show();

            // End loading screen
            loadingDialog.endLoadingAlertDialog();
        }
    }

    private void clickBack() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalTasks != 0)
                {
                    // User change their information
                    // Update the user object in SharedViewModel
                    SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                            .get(SharedViewModel.class);
                    sharedViewModel.setUser(newUser);
                }

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
    }
}