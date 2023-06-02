package com.example.box.FragmentAndActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.Category;
import com.example.box.Entity.CategoryAdapter;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.LoadingDialog;
import com.example.box.Entity.LocationPicker;
import com.example.box.Entity.Product;
import com.example.box.Entity.User;
import com.example.box.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private TextView locationText;
    private ImageView locationMoreButton;
    private RecyclerView categoryRCV;
    private CategoryAdapter categoryAdapter;

    private List<Product> productList;
    private List<Category> categoryList;

    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        // Initialize UI
        initializeUI();

        // Initialize loading dialog
        loadingDialog = new LoadingDialog(requireActivity());

        // Set up information
        setUpInformation();

        // Click more button in location
        clickMore();

        //categorySetData();

        getProductInfo();

        return view;
    }

    private void initializeUI() {
        locationText = (TextView) view.findViewById(R.id.location_text);
        locationMoreButton = (ImageView) view.findViewById(R.id.location_more_button);
        categoryRCV = (RecyclerView) view.findViewById(R.id.category_rcv);
    }

    private void setUpInformation() {
        // Get user object from shared view model
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Set up this user's location
                if (!user.getAddress().isEmpty())
                {
                    locationText.setText(user.getAddress());
                }

                else
                {
                    locationText.setText("Chưa cập nhật địa chỉ");
                }
            }
        });
    }

    private void clickMore() {
        locationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPicker locationPicker = new LocationPicker(getActivity(),
                        new LocationPicker.LocationConfirmationListener() {
                            @Override
                            public void onLocationConfirmed(String address) {
                                // Start loading screen
                                loadingDialog.startLoadingAlertDialog();

                                // Update the address in MySQL
                                updateAddress(address);
                            }
                        });
                locationPicker.loadMap();
            }
        });
    }

    private void updateAddress(String address) {
        String urlStr = "/box/updateUser.php";
        String userId = FirebaseAuth.getInstance().getUid();

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.equals("YES"))
                {
                    locationText.setText(address);
                }

                else
                {
                    Toast.makeText(requireContext(), "Lỗi cập nhật địa chỉ", Toast.LENGTH_SHORT)
                            .show();
                }

                // Stop loading screen
                loadingDialog.endLoadingAlertDialog();
            }
        });

        dataHandler.execute(DataHandler.TYPE_UPDATE_USER_INFO, urlStr, userId,
                DataHandler.TYPE_CHANGE_USER_ADDRESS, address);
    }

    private void getProductInfo() {
        String urlStr = "/box/getProduct.php";
        productList = new ArrayList<>();
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray jsonArray = null;
                Log.d("test", output);
                try {
                    jsonArray = new JSONArray(output);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = null;

                        jsonObject = jsonArray.getJSONObject(i);

                        String productName = jsonObject.getString("tensanpham");
                        double productPrice  = jsonObject.getDouble("gia");
                        String productImg = jsonObject.getString("anhsanpham");

                        Product product = new Product(productImg, productName, productPrice);
                        productList.add(product);
                        setupRecyclerView();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataHandler.TYPE_GET_PRODUCT_INFO, urlStr);
    }

    private void setupRecyclerView()
    {
        categoryRCV.setNestedScrollingEnabled(false);
        categoryList = new ArrayList<>();
        Category categoryTest = new Category("Category1", productList);
        categoryList.add(categoryTest);
        categoryList.add(categoryTest);
        CategoryAdapter categoryAdapter1 = new CategoryAdapter(requireContext(), productList, categoryList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false );
        categoryRCV.setLayoutManager(gridLayoutManager);
        categoryRCV.setAdapter(categoryAdapter1);
    }

    private void test()
    {
        categoryRCV.setNestedScrollingEnabled(false);
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        Product product1 = new Product("https://firebasestorage.googleapis.com/v0/b/pbox-b4a17.appspot.com/o/Product_image%2FPMVQDX.jpg?alt=media&token=4512b551-884e-42dd-8667-df41d54bdb51&_gl=1*1wbqngr*_ga*MTk2MTAxOTE4OC4xNjgzMTgzMzAy*_ga_CW55HF8NVT*MTY4NTQzNzg0MC4xOC4xLjE2ODU0MzgyNTguMC4wLjA.", "Phô Mai Việt Quất Đá Xay", 10.99);
        productList.add(product1);
        productList.add(product1);
        productList.add(product1);
        productList.add(product1);
        productList.add(product1);
        Category categoryTest = new Category("Category1", productList);
        categoryList.add(categoryTest);
        categoryList.add(categoryTest);
        CategoryAdapter categoryAdapter1 = new CategoryAdapter(requireContext(), productList, categoryList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false );
        categoryRCV.setLayoutManager(gridLayoutManager);
        categoryRCV.setAdapter(categoryAdapter1);
    }

//    private void categorySetData()
//    {
//        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//
//        categoryAdapter.setData(getListCategory());
//        categoryRCV.setAdapter(categoryAdapter);
//    }

//    private List<Category> getListCategory()
//    {
//        List<Category> list = new ArrayList<>();
//
//        ProductAdapter productAdapter = new ProductAdapter();
//        List<Product> productList = productAdapter.getProductList();
//
//        Category category = new Category("Category", productList);
//
//        list.add(category);
//
//        return list;
//    }
}