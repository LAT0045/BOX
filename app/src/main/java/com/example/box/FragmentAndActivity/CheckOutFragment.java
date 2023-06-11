package com.example.box.FragmentAndActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.Category;
import com.example.box.Entity.CategoryAdapter;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.LocationPicker;
import com.example.box.Entity.Product;
import com.example.box.Entity.ProductAdapter;
import com.example.box.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckOutFragment extends Fragment {
    private View view;

    private TextView nameTextView;
    private AppCompatEditText phoneEditText;
    private AppCompatEditText addressEditText;
    private AppCompatEditText appointmentEditText;

    private RecyclerView categoryRcv;
    private List<Product> checkOutProducts;

    private TextView totalPriceTextView;

    private RadioGroup radioGroup;
    private RadioButton cashButton;
    private RadioButton momoButton;

    private ImageView backButton;
    private TextView editButton;
    private RelativeLayout checkOutButton;

    private boolean isAllowed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_check_out, container, false);

        // Initialize UI
        initializeUI();

        // Get the check out products
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            checkOutProducts = bundle.getParcelableArrayList("checkOutList");

            // Set total price
            totalPriceTextView.setText(Integer.toString(getTotalPrice()) + "Đ");
        }

        showUserInfo();

        setUpRecyclerView();

        clickEdit();

        selectAddress();

        clickCheckOut();

        clickBack();

        return view;
    }

    private void initializeUI() {
        nameTextView = view.findViewById(R.id.name_text_view);
        phoneEditText = view.findViewById(R.id.phone_edit_text);
        addressEditText = view.findViewById(R.id.address_edit_text);
        appointmentEditText = view.findViewById(R.id.appointment_edit_text);

        categoryRcv = view.findViewById(R.id.category_rcv);

        totalPriceTextView = view.findViewById(R.id.total_price_text_view);

        radioGroup = view.findViewById(R.id.radio_group);
        cashButton = view.findViewById(R.id.cash_button);
        momoButton = view.findViewById(R.id.momo_button);

        backButton = view.findViewById(R.id.back_button);
        editButton = view.findViewById(R.id.edit_button);
        checkOutButton = view.findViewById(R.id.check_out_button);
    }

    private void showUserInfo() {
        String urlStr = "/box/getUser.php";

        // Get current user id
        String userId = FirebaseAuth.getInstance().getUid();

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("NO"))
                {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        // Iterate over the JSON array
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String name = jsonObject.getString("tenkhachhang");
                            String phoneNumber = jsonObject.getString("sodienthoai");
                            String address = jsonObject.getString("diachi");

                            nameTextView.setText(name);

                            if (!phoneNumber.isEmpty())
                            {
                                phoneEditText.setText(phoneNumber);
                            }

                            else
                            {
                                phoneEditText.setText("Chưa cập nhật số điện thoại");
                            }

                            if (!address.isEmpty())
                            {
                                addressEditText.setText(address);
                            }

                            else
                            {
                                addressEditText.setText("Chưa cập nhật địa chỉ");
                            }
                        }
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        dataHandler.execute(DataHandler.TYPE_GET_USER_INFO, urlStr, userId);
    }

    private void setUpRecyclerView() {
        categoryRcv.setNestedScrollingEnabled(false);

        // Initialize category and product list
        List<Category<?>> categoryList = new ArrayList<Category<?>>();
        Category<Product> products = new Category<Product>("", checkOutProducts);
        categoryList.add(products);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1,
                GridLayoutManager.VERTICAL, false );
        categoryRcv.setLayoutManager(gridLayoutManager);

        // Set category to recycler view
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categoryList);
        categoryAdapter.setType(ProductAdapter.CHECKOUT);
        categoryRcv.setAdapter(categoryAdapter);
    }

    private void clickEdit() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowed)
                {
                    ViewCompat.setBackgroundTintList(phoneEditText,
                            ColorStateList.valueOf(ContextCompat
                                    .getColor(requireActivity(), R.color.dark_gray)));

                    phoneEditText.setClickable(true);
                    phoneEditText.setCursorVisible(true);
                    phoneEditText.setFocusable(true);
                    phoneEditText.setFocusableInTouchMode(true);

                    ViewCompat.setBackgroundTintList(addressEditText,
                            ColorStateList.valueOf(ContextCompat
                                    .getColor(requireActivity(), R.color.dark_gray)));

                    editButton.setText("Xong");
                    isAllowed = true;
                }

                else
                {
                    ViewCompat.setBackgroundTintList(phoneEditText,
                            ColorStateList.valueOf(ContextCompat
                                    .getColor(requireActivity(), android.R.color.transparent)));

                    phoneEditText.setClickable(false);
                    phoneEditText.setCursorVisible(false);
                    phoneEditText.setFocusable(false);
                    phoneEditText.setFocusableInTouchMode(false);

                    ViewCompat.setBackgroundTintList(addressEditText,
                            ColorStateList.valueOf(ContextCompat
                                    .getColor(requireActivity(), android.R.color.transparent)));

                    editButton.setText("Sửa");
                    isAllowed = false;
                }
            }
        });
    }

    private void selectAddress() {
        if (isAllowed)
        {
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
    }

    private int getTotalPrice() {
        int total = 0;
        for(Product product : checkOutProducts)
        {
            int toppingPrice = getToppingPrice(product.getToppingList());
            total += (product.getProductPrice() * product.getCurQuantity());
            total += toppingPrice;
        }
        return total;
    }

    private int getToppingPrice(List<Product> toppings) {
        int total = 0;
        for(Product product : toppings)
        {
            total += (product.getProductPrice() * product.getCurQuantity());
        }
        return total;
    }

    private void clickCheckOut() {
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String appointment = appointmentEditText.getText().toString().trim();

                if (!isEmptyEditText(phoneNumber, address) &&
                isValidPhoneNumber(phoneNumber))
                {
                    Log.d("IN CHECK OUT", "HERE");
                    // Check the payment method

                    if (cashButton.isChecked())
                    {
                        Log.d("IN CASH", "PAY WITH CASH");
                        payWithCash(phoneNumber, address, appointment);
                    }

                    else
                    {
                        
                    }
                }
            }
        });
    }

    private void payWithCash(String phoneNumber, String address, String appointment) {
        String purchaseOrderId = UUID.randomUUID().toString();
        purchaseOrderId = purchaseOrderId.replace("-", "");


        String urlStr = "/box/insertPurchase.php";

        // Get current user id
        String userId = FirebaseAuth.getInstance().getUid();
        Log.d("RANDOM userId", userId);

        // Convert list to json string
        Gson gson = new Gson();
        String checkOutListStr = gson.toJson(checkOutProducts);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(currentDate);

        Log.d("PRINT CHECK OUT LIST", checkOutListStr);

        String finalPurchaseOrderId = purchaseOrderId;
        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("TEST CHECK OUT CASH", output);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                CheckOutDoneFragment checkOutDone = new CheckOutDoneFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, checkOutDone)
                        .addToBackStack(null)
                        .commit();
                //insertDetail(finalPurchaseOrderId);
            }
        });
        dataHandler.execute(DataHandler.TYPE_PURCHASE, urlStr, purchaseOrderId, appointment,
                phoneNumber, currentDateStr, address, userId, checkOutListStr);
    }

    private void insertDetail(String purchaseId) {

        //for (Product product : checkOutProducts)
        //{
            //insertAnItem(product, purchaseId);
        //}

        insertAnItem(checkOutProducts.get(0), purchaseId);
    }

    private void insertAnItem(Product product, String purchaseId) {
        String urlStr = "/box/insertPurchaseDetail.php";
        String toppings = "";
        if (product.getToppingList().size() > 0)
        {
            Gson gson = new Gson();
            toppings = gson.toJson(product.getToppingList());
        }

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("DETAILLLLL", output);
                //resumeLoop();
            }
        });
        dataHandler.execute(DataHandler.TYPE_INSERT_PURCHASE_DETAIL, urlStr, purchaseId,
                product.getProductId(), toppings, product.getCustomerNote());

        //pauseLoop();
    }

    private boolean loopPaused = false;

    private synchronized void pauseLoop() {
        loopPaused = true;
        while (loopPaused) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void resumeLoop() {
        loopPaused = false;
        notifyAll();
    }

    private boolean isEmptyEditText(String phone, String address) {
        if (phone.equals("Chưa cập nhật số điện thoại"))
        {
            phoneEditText.setError("Cần có số diện thoại để đặt hàng");
            return true;
        }

        else if (address.equals("Chưa cập nhật địa chỉ"))
        {
            addressEditText.setError("Cần có địa chỉ để đặt hàng");
            return true;
        }

        return false;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regexStr = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phoneNumber);
        boolean isValid = matcher.find();

        if (!isValid)
        {
            phoneEditText.setError("Số điện thoại không hợp lệ");
            return false;
        }

        return true;
    }

    private void clickBack() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                OrderDetailFragment orderDetailFragment = (OrderDetailFragment) fragmentManager
                        .findFragmentByTag("Order Detail");

                fragmentManager.beginTransaction()
                        .replace(R.id.container, orderDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}