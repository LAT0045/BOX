package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.box.R;
import com.example.box.Entity.User;
import com.facebook.login.LoginManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {
    private View view;

    private ShapeableImageView avatar;
    private TextView nameTextView;
    private TextView phoneTextView;

    private ImageView editButton;
    private RelativeLayout logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize UI
        initializeUI();

        // Set up user's information
        setUpInformation();

        // Log out
        logOut();

        // Edit user's information
        clickEdit();

        return view;
    }

    private void initializeUI() {
        avatar = (ShapeableImageView) view.findViewById(R.id.avatar);
        nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        phoneTextView = (TextView) view.findViewById(R.id.phone_text_view);

        editButton = (ImageView) view.findViewById(R.id.edit_button);
        logoutButton = (RelativeLayout) view.findViewById(R.id.logout_button);
    }

    private void setUpInformation() {
        Log.d("TEST SHOW INFO IN ACCOUNT", "IN GET INFORMATION");
        // Get user object from shared view model
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("TEST SHOW INFO IN ACCOUNT", "BEFORE GET USER NAME");
                Log.d("TEST SHOW INFO IN ACCOUNT", user.getName());
                String name = user.getName();
                String phoneNumber = user.getPhoneNumber();
                String avatarUrl = user.getAvatar();

                // Set up name
                nameTextView.setText(name);

                // Set up phone number
                if (!phoneNumber.isEmpty())
                {
                    phoneTextView.setText(phoneNumber);
                }

                else
                {
                    phoneTextView.setText("Chưa cập nhật số điện thoại");
                }

                // Set up avatar
                if (avatarUrl.isEmpty())
                {
                    // User has not update their avatar
                    // Load default avatar
                    avatar.setImageResource(R.drawable.default_avatar);
                }

                else
                {
                    // User has updated their avatar
                    // Load avatar from firebase storage using avatar url
                    Picasso.get()
                            .load(avatarUrl)
                            .fit()
                            .centerCrop()
                            .into(avatar);
                }
            }
        });
    }

    private void clickEdit() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFragment editFragment = new EditFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.view_pager, editFragment);
                fragmentTransaction.addToBackStack(null); // Does not allow user to navigate back
                fragmentTransaction.commit();
            }
        });
    }

    private void logOut() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                loadLogIn();
            }
        });
    }

    private void loadLogIn() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}