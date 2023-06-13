package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.box.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckOutDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckOutDoneFragment extends Fragment {

    private View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RelativeLayout btnGoHome;
    private RelativeLayout btnGoToOrder;

    public CheckOutDoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckOutDone.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckOutDoneFragment newInstance(String param1, String param2) {
        CheckOutDoneFragment fragment = new CheckOutDoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check_out_done, container, false);
        initializeUI();

        goHomeClick();

        goToOrder();
        // Inflate the layout for this fragment
        return view;
    }

    private void initializeUI()
    {
        btnGoHome = (RelativeLayout) view.findViewById(R.id.btn_go_home);
        btnGoToOrder = (RelativeLayout) view.findViewById(R.id.btn_go_to_order);
    }

    private void goHomeClick()
    {
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), Home.class);
                startActivity(intent);
            }
        });
    }

    private void goToOrder()
    {
        btnGoToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), Home.class);
                intent.putExtra("Order", "CheckOutToOrder");
                startActivity(intent);
            }
        });
    }

}