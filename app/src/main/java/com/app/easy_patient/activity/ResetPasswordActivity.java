package com.app.easy_patient.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.app.easy_patient.activity.dashboard.DashboardActivity;
import com.app.easy_patient.R;
import com.app.easy_patient.model.LoginResponseModel;
import com.app.easy_patient.model.ResetPasswordModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends BaseActivity {
    EditText etPassword, etConfirmPassword;
    String otp,userName;
    Button btnConfirm;
    GetDataService api;
    ProgressDialog progressDialog;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        sharedPrefs = new SharedPrefs(this);
        api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm);
        Bundle extras = getIntent().getExtras();
        userName = extras.getString("E_MAIL");
        otp = extras.getString("OTP");
        btnConfirm.setOnClickListener(v -> {
            String password=etPassword.getText().toString();
            postResetPassword(userName,otp,password);
        });
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    private void postResetPassword(String userName,String otp,String newPassword) {
        progressDialog.show();
        Call<ResetPasswordModel> loadUserDetailResponse = api.postResetPassword(userName,otp,newPassword);
        loadUserDetailResponse.enqueue(new Callback<ResetPasswordModel>() {
            @Override
            public void onResponse(Call<ResetPasswordModel> call, Response<ResetPasswordModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getChanged()) {
                        getLoginDetails("password", userName, newPassword);
                    }else{
                        Toast.makeText(ResetPasswordActivity.this,getString(R.string.enter_valid_otp_str),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ResetPasswordActivity.this,getString(R.string.enter_valid_otp_str),Toast.LENGTH_SHORT).show();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordModel> call, Throwable t) {
                Log.e("reset_password_Error", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
    private void getLoginDetails(String grantType, String userName, String password) {
        Call<LoginResponseModel> loginResponse = api.getTokenAccess(grantType, userName, password);
        loginResponse.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    String loginResponse = response.body().getAccess_token();
                    if (loginResponse != null) {
                        sharedPrefs.setToken(response.body().getAccess_token());
                        sharedPrefs.setRefreshToken(response.body().getRefresh_token());
                        sharedPrefs.setLoginStatus(true);
                        sharedPrefs.setImagePath(response.body().getUser().getProfilePic());
                        sharedPrefs.setName(response.body().getUser().getFullName());
                        sharedPrefs.setEmail(response.body().getUser().getUsername());
                        sharedPrefs.setGender(response.body().getUser().getGender());
                        Intent intent = new Intent(ResetPasswordActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.enter_valid_user_pass_str), Toast.LENGTH_SHORT).show();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

}
