package com.app.easy_patient.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.easy_patient.model.LoginResponseModel;
import com.app.easy_patient.util.SharedPrefs;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class CustomInterceptor implements Interceptor {
    private SharedPrefs sharedPrefs;
    private String credentials;
    Context mContext;

    public CustomInterceptor(Context context, String user, String password) {
        sharedPrefs = new SharedPrefs(context);
        this.credentials = Credentials.basic(user, password);
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder;
        // Request customization: add request headers
        if (!sharedPrefs.getLoginStatus()) {
            requestBuilder = original.newBuilder().addHeader("Authorization", credentials);
//            requestBuilder = original.newBuilder().addHeader("Content-Type", "application/x-www-form-urlencoded");
        } else
            requestBuilder = original.newBuilder().addHeader("Authorization", "Bearer " + sharedPrefs.getToken());

        Request request = requestBuilder.build();
        Response response = chain.proceed(request);

        while (response.code() == 401) { //if unauthorized
            sharedPrefs.setLoginStatus(false);
            refreshToken(); //refresh token

            if (sharedPrefs.getToken() != null) { //retry requires new auth token,
                response.close();
                requestBuilder = original.newBuilder().addHeader("Authorization", "Bearer " + sharedPrefs.getToken());
                request = requestBuilder.build();
                response = chain.proceed(request); //repeat request with new token
            }
        }
        return response;
    }

    private void refreshToken() {
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        Call<LoginResponseModel> loginResponse = api.getRefreshToken("refresh_token", sharedPrefs.getRefreshToken());
        loginResponse.enqueue(new Callback<LoginResponseModel>() {

            @Override
            public void onResponse(Call<LoginResponseModel> call, retrofit2.Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    String loginResponse = response.body().getAccess_token();
                    if (loginResponse != null) {
                        //saving token to shared  preference for future use
                        sharedPrefs.setToken(loginResponse);
                        sharedPrefs.setRefreshToken(response.body().getRefresh_token());
                        sharedPrefs.setLoginStatus(true);
                    }

                } else {
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());

            }
        });
    }
}
