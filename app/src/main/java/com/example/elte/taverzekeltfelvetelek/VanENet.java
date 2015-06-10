package com.example.elte.taverzekeltfelvetelek;

/**
 * Created by Zoltan on 2015.06.09..
 */

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class VanENet {

    public boolean ServerCheck(String url) {
        boolean siker = false;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.d("Van-e net", url);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();
            String codec = String.valueOf(code);
            Log.d("Statusz kod: ", codec);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                siker = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return siker;
    }

    public boolean NetCheck(Context context) {
        boolean isConnected;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnected();
        } else {
            isConnected = false;
        }
        return isConnected;
    }

}
