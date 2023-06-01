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

public class UserHandler extends AsyncTask<String, Void, String> {
    public static final String TYPE_SIGN_UP_GOOGLE = "Sign Up Google";
    public static final String TYPE_SIGN_UP_EMAIL = "Sign Up Email";
    public static final String TYPE_GET_USER_INFO = "Get User Info";
    //public final String IP = "http://192.168.1.10";
    public final String IP = "http://192.168.1.6";

    private AsyncResponse asyncResponse;

    public UserHandler(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
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
