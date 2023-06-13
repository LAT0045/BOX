package com.example.box.Entity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class DataHandler extends AsyncTask<String, Void, String> {
    // Task Type
    public static final String TYPE_SIGN_UP_GOOGLE = "Sign Up Google";
    public static final String TYPE_SIGN_UP_EMAIL = "Sign Up Email";
    public static final String TYPE_GET_USER_INFO = "Get User Info";
    public static final String TYPE_UPDATE_USER_INFO = "Update User Info";
    public static final String TYPE_GET_PRODUCT_STORE =  "Get Product Store";
    public static final String TYPE_GET_BY_STORE_ID = "Get By Store Id";
    public static final String TYPE_PURCHASE = "Purchase";
    public static final String TYPE_PRESENT_ORDER = "Get id";
    public static final String TYPE_ORDER_ID = "Get id";

    // Update User Type
    public static final String TYPE_CHANGE_USER_NAME = "name";
    public static final String TYPE_CHANGE_USER_ADDRESS = "address";
    public static final String TYPE_CHANGE_USER_PHONE_NUMBER = "phoneNumber";
    public static final String TYPE_CHANGE_USER_AVATAR = "avatar";


    public final String IP = "http://192.168.1.9";
    //public final String IP = "http://172.16.3.42";
//    public final String IP = "http://192.168.1.6";

    private AsyncResponse asyncResponse;

    public DataHandler(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    public DataHandler() {

    }

    @Override
    protected String doInBackground(String... strings) {
        String type = strings[0];
        String urlStr = IP + strings[1];

        try
        {
            //Define URL where to send data
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //Send POST data request
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoOutput(true);

            // Open connection for sending data
            OutputStream outputStream =  httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));

            //Send data
            String data = "";

            if (type.equals(TYPE_SIGN_UP_GOOGLE))
            {
                String id = strings[2];
                String address = strings[3];
                String name = strings[4];

                data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&"
                        + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&"
                        + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            }

            else if (type.equals(TYPE_SIGN_UP_EMAIL))
            {
                String id = strings[2];
                String address = strings[3];
                String name = strings[4];
                String avatar = strings[5];

                data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&"
                        + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&"
                        + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&"
                        + URLEncoder.encode("avatar", "UTF-8") + "=" + URLEncoder.encode(avatar, "UTF-8");
            }

            else if (type.equals(TYPE_GET_USER_INFO))
            {
                String id = strings[2];

                data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            }

            else if (type.equals(TYPE_UPDATE_USER_INFO))
            {
                String id = strings[2];
                String changeType = strings[3];
                String changeValue = strings[4];

                data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&"
                        + URLEncoder.encode("changeType", "UTF-8") + "=" + URLEncoder.encode(changeType, "UTF-8")
                        + "&"
                        + URLEncoder.encode("changeValue", "UTF-8") + "=" + URLEncoder.encode(changeValue, "UTF-8");
            }

            else if (type.equals(TYPE_GET_BY_STORE_ID))
            {
                String storeId = strings[2];
                data = URLEncoder.encode("storeId", "UTF-8") + "=" + URLEncoder.encode(storeId, "UTF-8");
            }

            else if (type.equals(TYPE_PURCHASE))
            {
                String purchaseId = strings[2];
                String timeAppointment = strings[3];
                String phoneNumber = strings[4];
                String currentDate = strings[5];
                String address = strings[6];
                String userId = strings[7];
                String products = strings[8];
                String note = strings[9];

                data = URLEncoder.encode("purchaseId", "UTF-8") + "=" + URLEncoder.encode(purchaseId, "UTF-8")
                        + "&"
                        + URLEncoder.encode("timeAppointment", "UTF-8") + "=" + URLEncoder.encode(timeAppointment, "UTF-8")
                        + "&"
                        + URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8")
                        + "&"
                        + URLEncoder.encode("currentDate", "UTF-8") + "=" + URLEncoder.encode(currentDate, "UTF-8")
                        + "&"
                        + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&"
                        + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8")
                        + "&"
                        + URLEncoder.encode("products", "UTF-8") + "=" + URLEncoder.encode(products, "UTF-8")
                        + "&"
                        + URLEncoder.encode("note", "UTF-8") + "=" + URLEncoder.encode(note, "UTF-8");
            }
            else if(type.equals(TYPE_PRESENT_ORDER))
            {
                String userId = strings[2];
                data = URLEncoder.encode("makhachhang", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            }
            else if(type.equals(TYPE_ORDER_ID))
            {
                String billId = strings[2];
                String createDate = strings[3];
                String total = strings[4];
                String purchaseId = strings[5];
                data = URLEncoder.encode("billId", "UTF-8") + "=" + URLEncoder.encode(billId, "UTF-8")
                        + "&"
                        + URLEncoder.encode("createDate", "UTF-8") + "=" + URLEncoder.encode(createDate, "UTF-8")
                        + "&"
                        + URLEncoder.encode("total", "UTF-8") + "=" + URLEncoder.encode(total, "UTF-8")
                        + "&"
                        + URLEncoder.encode("purchaseId", "UTF-8") + "=" + URLEncoder.encode(purchaseId, "UTF-8");

            }

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            //Open connection for reading response
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "iso-8859-1"));

            //Read response from server
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
                break;
            }

            String result = stringBuilder.toString();

            bufferedReader.close();
            inputStream.close();

            return result;
        }

        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null)
        {
            asyncResponse.processFinish(s);
            Log.d("RESULTTTT", s);
        }

        else
        {
            Log.d("RESULTTTT", "Cant connect");
        }

        super.onPostExecute(s);
    }


}
