package com.example.box.FragmentAndActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.box.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpInfoFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;

    private ImageView backButton;
    private RelativeLayout signUpButton;

    private AppCompatEditText emailEditText;
    private AppCompatEditText passwordEditText;
    private AppCompatEditText reEnterPasswordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up_info, container, false);

        // Initialize UI
        initializeUI();
        firebaseAuth = FirebaseAuth.getInstance();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Click sign up button
        clickSignUp();

        return view;
    }

    private void initializeUI() {
        backButton = (ImageView) view.findViewById(R.id.back_button);
        signUpButton = (RelativeLayout) view.findViewById(R.id.sign_up_button);

        emailEditText = (AppCompatEditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (AppCompatEditText) view.findViewById(R.id.password_edit_text);
        reEnterPasswordEditText = (AppCompatEditText) view.findViewById(R.id.re_enter_password_edit_text);
    }

    private void clickSignUp() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String reEnterPassword = reEnterPasswordEditText.getText().toString();
                if (!isEmptyInput(email, password, reEnterPassword) && isValidEmail(email) &&
                        isValidPassword(password) && isSamePassword(password, reEnterPassword))
                {
                    // Check if email has been registered or not
                    firebaseAuth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean isNewUser = task.getResult()
                                            .getSignInMethods()
                                            .isEmpty();

                                    if (isNewUser)
                                    {
                                        // User is not registered
                                        // Move to Update Info
                                        fragmentTransaction(email, password);
                                    }

                                    else
                                    {
                                        // User is already registered
                                        Toast.makeText(getContext(), "Email này đã được đăng ký", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean isEmptyInput(String email, String password, String reEnterPassword) {
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

        else if (reEnterPassword.isEmpty())
        {
            reEnterPasswordEditText.setError("Cần nhập lại mật khẩu");
            return true;
        }

        return false;
    }

    private boolean isValidEmail(String email) {
        String regexStr = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        boolean isValid = matcher.find();

        if (!isValid)
        {
            emailEditText.setError("Email không hợp lệ");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8)
        {
            passwordEditText.setError("Mật khẩu phải ít nhất 8 ký tự");
            return false;
        }

        else
        {
            return true;
        }
    }

    private boolean isSamePassword(String password, String reEnterPassword) {
        if (!password.equals(reEnterPassword))
        {
            reEnterPasswordEditText.setError("Mật khẩu nhập lại không đúng");
            return false;
        }

        else
        {
            return true;
        }
    }

    private void fragmentTransaction(String email, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);

        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setArguments(bundle);

        // Replace current fragment with update fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, updateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void clickBack() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement back button
            }
        });
    }
}