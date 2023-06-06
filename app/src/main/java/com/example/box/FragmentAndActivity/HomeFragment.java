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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.Category;
import com.example.box.Entity.CategoryAdapter;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.LoadingDialog;
import com.example.box.Entity.LocationPicker;
import com.example.box.Entity.Product;
import com.example.box.Entity.ProductAdapter;
import com.example.box.Entity.Store;
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

    private ImageSlider imageSlider;
    private RecyclerView categoryRCV;

    private List<Product> productList;
    private List<Store> storeList;

    private List<Category<?>> categoryList;

    private int totalTasks = 2;
    private int finishedTasks = 0;

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

        // Show all products
        getProductInfo();

        // Show all stores
        getStoreInfo();

        bannerSlider();

        return view;
    }

    private void initializeUI() {
        locationText = (TextView) view.findViewById(R.id.location_text);
        locationMoreButton = (ImageView) view.findViewById(R.id.location_more_button);
        categoryRCV = (RecyclerView) view.findViewById(R.id.category_rcv);
        imageSlider = (ImageSlider)  view.findViewById(R.id.imgBanner);
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
                Log.d("TEST RECYCLERVIEW BOTH", "PRODUCT");

                try
                {
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
                    }

                }

                catch (JSONException e)
                {
                    throw new RuntimeException(e);
                }

                // Mark task as done
                taskFinishedCallBack();
            }
        });

        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataHandler.TYPE_GET_PRODUCT_STORE, urlStr);
    }

    private void getStoreInfo() {
        String urlStr = "/box/getStore.php";
        storeList = new ArrayList<>();
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(output);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = null;

                        jsonObject = jsonArray.getJSONObject(i);

                        String storeId = jsonObject.getString("macuahang");
                        String storeName = jsonObject.getString("tencuahang");
                        String storeImage  = jsonObject.getString("hinhanh");
                        String storeAddress = jsonObject.getString("diachi");

                        Store store = new Store(storeId, storeName, storeImage, storeAddress);
                        storeList.add(store);
                    }

                }

                catch (JSONException e)
                {
                    throw new RuntimeException(e);
                }

                // Mark task as done
                taskFinishedCallBack();
            }
        });

        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataHandler.TYPE_GET_PRODUCT_STORE, urlStr);
    }

    private void taskFinishedCallBack() {
        finishedTasks++;

        if (finishedTasks == totalTasks)
        {
            // All tasks are done
            // Set up recycler view
            setupRecyclerView();
        }
    }

    private void setupRecyclerView()
    {
        // Make sure that the recyclerview can be swiped while in nested scroll view
        categoryRCV.setNestedScrollingEnabled(false);

        // Initialize category list
        categoryList = new ArrayList<Category<?>>();

        // Category show the product
        Category<Product> products = new Category<Product>("", productList);

        // Category show the store
        Category<Store> stores = new Category<Store>("Cửa hàng", storeList);

        // Add all categories
        categoryList.add(products);
        categoryList.add(stores);

        // Set up grid and layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false );
        categoryRCV.setLayoutManager(gridLayoutManager);

        // Set category to recyclerview
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categoryList);
        categoryAdapter.setType(ProductAdapter.PRODUCT_IN_HOME_TYPE);
        categoryRCV.setAdapter(categoryAdapter);
    }

    public void bannerSlider()
    {
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
    }
}