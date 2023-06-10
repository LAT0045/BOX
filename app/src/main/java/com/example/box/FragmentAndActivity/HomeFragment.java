package com.example.box.FragmentAndActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private LinearLayout allButton;
    private LinearLayout fastFoodButton;
    private LinearLayout drinkButton;
    private LinearLayout vietnameseButton;
    private LinearLayout koreanButton;
    private LinearLayout japaneseButton;

    private View allView;
    private View fastFoodView;
    private View drinkView;
    private View vietnameseView;
    private View koreanView;
    private View japaneseView;

    private List<Product> productList;
    private List<Product> fastFoodList;
    private List<Product> drinkList;
    private List<Product> vietnameseList;
    private List<Product> koreanList;
    private List<Product> japaneseList;
    private List<Product> otherList;

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

        // Show banner
        bannerSlider();

        return view;
    }

    private void initializeUI() {
        locationText = view.findViewById(R.id.location_text);
        locationMoreButton = view.findViewById(R.id.location_more_button);
        categoryRCV = view.findViewById(R.id.category_rcv);
        imageSlider = view.findViewById(R.id.imgBanner);

        allButton = view.findViewById(R.id.all_button);
        fastFoodButton = view.findViewById(R.id.fast_food_button);
        drinkButton = view.findViewById(R.id.drink_button);
        vietnameseButton = view.findViewById(R.id.vietnamese_button);
        koreanButton = view.findViewById(R.id.korean_button);
        japaneseButton = view.findViewById(R.id.japanese_button);

        allView = view.findViewById(R.id.all_view);
        fastFoodView = view.findViewById(R.id.fast_food_view);
        drinkView = view.findViewById(R.id.drink_view);
        vietnameseView = view.findViewById(R.id.vietnamese_view);
        koreanView = view.findViewById(R.id.korean_view);
        japaneseView = view.findViewById(R.id.japanese_view);
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
        fastFoodList = new ArrayList<>();
        drinkList = new ArrayList<>();
        vietnameseList = new ArrayList<>();
        koreanList = new ArrayList<>();
        japaneseList = new ArrayList<>();
        otherList = new ArrayList<>();

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray jsonArray = null;

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
                        String section = jsonObject.getString("phanloai");
                        int productTypeId  = jsonObject.getInt("loaisanpham_id");

                        Product product = new Product(productImg, productName, productPrice, section);

                        // Check food type
                        // If food type is different than 6 types in Product
                        // Then no displaying in homepage
                        switch (productTypeId)
                        {
                            case Product.FAST_FOOD_TYPE:
                                fastFoodList.add(product);
                                productList.add(product);
                                break;
                            case Product.DRINK_TYPE:
                                drinkList.add(product);
                                productList.add(product);
                                break;
                            case Product.VIETNAMESE_TYPE:
                                vietnameseList.add(product);
                                productList.add(product);
                                break;
                            case Product.KOREAN_TYPE:
                                koreanList.add(product);
                                productList.add(product);
                                break;
                            case Product.JAPANESE_TYPE:
                                japaneseList.add(product);
                                productList.add(product);
                                break;
                            case Product.OTHER_TYPE:
                                otherList.add(product);
                                productList.add(product);
                                break;
                            default:
                                break;
                        }
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1,
                GridLayoutManager.VERTICAL, false );
        categoryRCV.setLayoutManager(gridLayoutManager);

        // Set category to recyclerview
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), requireContext(), categoryList);
        categoryAdapter.setType(ProductAdapter.PRODUCT_IN_HOME_TYPE);
        categoryRCV.setAdapter(categoryAdapter);

        chooseCategory(categoryAdapter);
    }

    private void chooseCategory(CategoryAdapter categoryAdapter) {
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", productList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.VISIBLE);
                fastFoodView.setVisibility(View.GONE);
                drinkView.setVisibility(View.GONE);
                vietnameseView.setVisibility(View.GONE);
                koreanView.setVisibility(View.GONE);
                japaneseView.setVisibility(View.GONE);
            }
        });

        fastFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", fastFoodList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.GONE);
                fastFoodView.setVisibility(View.VISIBLE);
                drinkView.setVisibility(View.GONE);
                vietnameseView.setVisibility(View.GONE);
                koreanView.setVisibility(View.GONE);
                japaneseView.setVisibility(View.GONE);
            }
        });

        drinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", drinkList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.GONE);
                fastFoodView.setVisibility(View.GONE);
                drinkView.setVisibility(View.VISIBLE);
                vietnameseView.setVisibility(View.GONE);
                koreanView.setVisibility(View.GONE);
                japaneseView.setVisibility(View.GONE);
            }
        });

        vietnameseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", vietnameseList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.GONE);
                fastFoodView.setVisibility(View.GONE);
                drinkView.setVisibility(View.GONE);
                vietnameseView.setVisibility(View.VISIBLE);
                koreanView.setVisibility(View.GONE);
                japaneseView.setVisibility(View.GONE);
            }
        });

        koreanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", koreanList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.GONE);
                fastFoodView.setVisibility(View.GONE);
                drinkView.setVisibility(View.GONE);
                vietnameseView.setVisibility(View.GONE);
                koreanView.setVisibility(View.VISIBLE);
                japaneseView.setVisibility(View.GONE);
            }
        });

        japaneseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category<Product> products = new Category<Product>("", japaneseList);
                categoryList.remove(0);
                categoryList.add(0, products);
                categoryAdapter.setData(categoryList);

                allView.setVisibility(View.GONE);
                fastFoodView.setVisibility(View.GONE);
                drinkView.setVisibility(View.GONE);
                vietnameseView.setVisibility(View.GONE);
                koreanView.setVisibility(View.GONE);
                japaneseView.setVisibility(View.VISIBLE);
            }
        });
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