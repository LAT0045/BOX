package com.example.box.FragmentAndActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.PurchaseOrder;
import com.example.box.Entity.PurchaseOrderAdapter;
import com.example.box.Entity.Store;
import com.example.box.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private View view;
    private RecyclerView orderRCV;
    List<PurchaseOrder> purchaseOrderList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        view = inflater.inflate(R.layout.fragment_order, container, false);
        // Inflate the layout for this fragment

        initializeUI();

        getOrder();

        return view;
    }

    private void initializeUI()
    {
        orderRCV = (RecyclerView) view.findViewById(R.id.present_order_rcv);
    }

    private void getOrder(){
        String urlStr = "/box/presentOrder.php";
        purchaseOrderList = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getUid();
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray jsonArray = null;
                Log.d("TestO", output);

                try {
                    jsonArray = new JSONArray(output);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = null;

                        jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("madondathang");
                        String status  = jsonObject.getString("tinhtrang");
                        String date  = jsonObject.getString("ngaydat");

                        PurchaseOrder purchaseOrder = new PurchaseOrder(status, date);
                        purchaseOrderList.add(purchaseOrder);
                    }

                    setupRCV();

                }

                catch (JSONException e)
                {
                    throw new RuntimeException(e);
                }

                // Mark task as done
            }
        });

        dataHandler.execute(dataHandler.TYPE_PRESENT_ORDER, urlStr, userId);
    }

    private void setupRCV()
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1);
        orderRCV.setLayoutManager(gridLayoutManager);

        PurchaseOrderAdapter purchaseOrderAdapter = new PurchaseOrderAdapter(purchaseOrderList);
        orderRCV.setAdapter(purchaseOrderAdapter);
    }

}