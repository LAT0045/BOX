package com.example.box;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignUpOTPFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private RelativeLayout confirmBtn;
    private ImageView backBtn;

    private TextView instructTextView;
    private String phoneNumber = "dummy_phone_number";

    private AppCompatEditText inputNumber_1;
    private AppCompatEditText inputNumber_2;
    private AppCompatEditText inputNumber_3;
    private AppCompatEditText inputNumber_4;
    private AppCompatEditText inputNumber_5;
    private AppCompatEditText inputNumber_6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up_o_t_p, container, false);

        // Initialize UI
        initializeUI();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Show instruction text
        showInstructionText();

        // Click back button
        clickBack();

        // Click confirm button
        clickConfirm();

        return view;
    }

    private void initializeUI() {
        instructTextView = (TextView) view.findViewById(R.id.instruction);

        inputNumber_1 = (AppCompatEditText) view.findViewById(R.id.input_number_1);
        inputNumber_2 = (AppCompatEditText) view.findViewById(R.id.input_number_2);
        inputNumber_3 = (AppCompatEditText) view.findViewById(R.id.input_number_3);
        inputNumber_4 = (AppCompatEditText) view.findViewById(R.id.input_number_4);
        inputNumber_5 = (AppCompatEditText) view.findViewById(R.id.input_number_5);
        inputNumber_6 = (AppCompatEditText) view.findViewById(R.id.input_number_6);

        confirmBtn = (RelativeLayout) view.findViewById(R.id.btn_confirm);
        backBtn = (ImageView) view.findViewById(R.id.btn_back);
    }

    private void showInstructionText() {
        String instructText_1 = "Mã OTP đã được gửi đến số điện thoại ";
        String instructText_2 = ". Vui lòng hãy nhập mã vào đây.";
        String instructText = instructText_1 + phoneNumber + instructText_2;

        // Highlight phone number
        Spannable spannable = new SpannableString(instructText);
        spannable.setSpan(new ForegroundColorSpan(getResources()
                        .getColor(R.color.blue, null)), instructText_1.length(),
                (instructText_1 + phoneNumber).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Show instruction text
        instructTextView.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    private void clickConfirm() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get SignUpUpdateInfoFragment from back stack with tag
                UpdateFragment updateFragment = (UpdateFragment) fragmentManager
                        .findFragmentByTag("updateFragment");

                // If fragment not exists then create new onw
                if (updateFragment == null)
                {
                    updateFragment = new UpdateFragment();

                    // Replace current fragment with update fragment and add to back stack
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, updateFragment, "updateFragment")
                            .addToBackStack("updateFragment")
                            .commit();
                }

                // If fragment exists then
                else
                {
                    // Replace current fragment with update fragment and not add to back stack
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, updateFragment, "updateFragment")
                            .commit();
                }
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
}