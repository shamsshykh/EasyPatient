package com.app.easy_patient.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkChecker {
    Context context;
    boolean networkFlag=false;

    public NetworkChecker(Context context) {

        this.context = context;

    }

    public boolean isConnectingNetwork() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info[] = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                        new CheckInternetConnection().execute();
//                        return  networkFlag;
                        return true;
                    }
                }
            }

        }
        return false;


    }

    class CheckInternetConnection extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(Void... urls) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (Exception e) {
                this.exception = e;
                return false;
            }
        }

        protected void onPostExecute(Boolean flag) {
           networkFlag=flag;
        }
    }

}
