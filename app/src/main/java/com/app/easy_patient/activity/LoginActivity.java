package com.app.easy_patient.activity;

import androidx.annotation.Nullable;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.util.NetworkChecker;
import com.app.easy_patient.R;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.SharedPrefs;
import com.app.easy_patient.model.LoginResponseModel;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.app.easy_patient.util.AppConstants.REQUEST_CODE_KEYS.START_TERMS_ACTIVITY;

public class LoginActivity extends BaseActivity {
    EditText etUserName, etPassword;
    Button btnLogin, btnLoginDisabled;
    Context mContext;
    NetworkChecker networkChecker;
    String userName;
    GetDataService api;
    SharedPrefs sharedPrefs;
    TextView tvSignUp, tvRegistered, tvLabelSignUp, tvForgotPassword, tvTermsConditionLink;
    boolean forgetPasswordFlag = false;
    boolean isEmailVerified = false;
    private static final int MY_PERMISSION_CODE = 101;
    CheckBox cbAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBar(R.color.statusBarColor);
        mContext = this;
        sharedPrefs = new SharedPrefs(mContext);
        networkChecker = new NetworkChecker(mContext);
        if (sharedPrefs.getLoginStatus()) {
            finish();
            startActivity(new Intent(mContext, com.app.easy_patient.activity.dashboard.DashboardActivity.class));
        } else {
            setContentView(R.layout.activity_login_new);
            api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
            etUserName = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            btnLoginDisabled = findViewById(R.id.btn_login_disabled);
            tvSignUp = findViewById(R.id.tv_sign_up);
            tvRegistered = findViewById(R.id.tv_already_registration);
            tvLabelSignUp = findViewById(R.id.tvLogin);
            tvForgotPassword = findViewById(R.id.tv_forgot_password);
            tvTermsConditionLink = findViewById(R.id.tv_terms_condition_link);
            cbAgree = findViewById(R.id.cb_agree);

            cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateLoginBtn(isChecked);
                    if (isChecked) {
                        tvTermsConditionLink.setTextColor(getColor(R.color.colorPrimary));
                        cbAgree.setTextColor(getColor(R.color.colorBlack));
                    } else {
                        cbAgree.setTextColor(getColor(R.color.colorText));
                        tvTermsConditionLink.setTextColor(getColor(R.color.colorText));
                    }
                }
            });

            moveToPlainLogin();

            tvForgotPassword.setOnClickListener(v -> {
                moveToForgotPassword();
            });
            tvSignUp.setOnClickListener(v -> {
                moveToSignUp();
            });

            tvRegistered.setOnClickListener(v -> {
                if (isEmailVerified)
                    moveToLoginWithPassword();
                else
                    moveToPlainLogin();
            });
            //implementation of click listener on Login button
            btnLogin.setOnClickListener(v -> {
                onLoginButtonClick();
            });

            tvTermsConditionLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(mContext, TermsActivity.class), START_TERMS_ACTIVITY);
                }
            });
        }
    }

    private void onLoginButtonClick() {
        userName = etUserName.getText().toString();
        if (forgetPasswordFlag) {
            if (TextUtils.isEmpty(userName)) {
                showError(etUserName, getString(R.string.enter_please_email_str));
            } else if (networkChecker.isConnectingNetwork()) {
                // isRegistered(userName);
                getForgotPassword(userName);
            } else {
                Toast.makeText(mContext, getString(R.string.check_internet_str), Toast.LENGTH_SHORT).show();
            }
        } else if (tvRegistered.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(userName)) {
                showError(etUserName, getString(R.string.enter_please_email_str));
            } else {
                if (cbAgree.isChecked()) {
                    Intent intent = new Intent(mContext, RegistrationActivity.class);
                    intent.putExtra("email", userName);
                    startActivity(intent);
                } else
                    Toast.makeText(mContext, getString(R.string.please_accept_terms_str), Toast.LENGTH_SHORT).show();
            }

        } else {
            if (etPassword.getVisibility() != View.VISIBLE) {
                if (TextUtils.isEmpty(userName)) {
                    showError(etUserName, getString(R.string.enter_please_email_str));
                } else if (networkChecker.isConnectingNetwork()) {
                    isRegistered(userName);
                } else {
                    Toast.makeText(mContext, getString(R.string.check_internet_str), Toast.LENGTH_SHORT).show();
                }
            } else if (etPassword.getVisibility() == View.VISIBLE) {
                String grantType = "password";
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    showError(etUserName, getString(R.string.enter_please_email_str));
                    if (!etUserName.requestFocus()) etUserName.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    showError(etPassword, getString(R.string.enter_please_password));
                    if (!etPassword.requestFocus()) etPassword.requestFocus();
                } else if (networkChecker.isConnectingNetwork()) {
                    getLoginDetails(grantType, userName, password);
                } else {
                    Toast.makeText(mContext, getString(R.string.check_internet_str), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Getting Registration status of user from API
    private void getForgotPassword(String userName) {
        progressDialog.show();
        Call<StatusModel> forgotPasswordResponse = api.forgotPassword(userName);
        forgotPasswordResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSent()) {
                        Intent intent = new Intent(mContext, OTPActivity.class);
                        intent.putExtra("FLAG", "forgot_password");
                        intent.putExtra("E_MAIL", userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, getString(R.string.forgot_password_error_str), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.forgot_password_error_str), Toast.LENGTH_SHORT).show();
                }

                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }

    //Getting Registration status of user from API
    private void isRegistered(String userName) {
        progressDialog.show();
        Call<LoginResponseModel> loginResponse = api.getRegistered(userName);
        loginResponse.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    String registered = response.body().getRegistered();
                    if (registered != null && registered.equalsIgnoreCase("true")) {
                        isEmailVerified = true;
                        moveToLoginWithPassword();
                    } else
                        Toast.makeText(mContext, getString(R.string.invalid_user_pass_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.invalid_user_pass_str), Toast.LENGTH_SHORT).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }

    //Getting token from API
    private void getLoginDetails(String grantType, String userName, String password) {
        progressDialog.show();
        Call<LoginResponseModel> loginResponse = api.getTokenAccess(grantType, userName, password);
        loginResponse.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    String loginResponse = response.body().getAccess_token();
                    if (loginResponse != null) {
                        if (response.body().getUser().getIsActive() != null) {
                            //saving token to shared preference for future use
                            sharedPrefs.setToken(response.body().getAccess_token());
                            sharedPrefs.setRefreshToken(response.body().getRefresh_token());
                            sharedPrefs.setLoginStatus(true);
                            sharedPrefs.setImagePath(response.body().getUser().getProfilePic());
                            sharedPrefs.setName(response.body().getUser().getFullName());
                            sharedPrefs.setEmail(response.body().getUser().getUsername());
                            sharedPrefs.setGender(response.body().getUser().getGender());
                            startActivity(new Intent(mContext, com.app.easy_patient.activity.dashboard.DashboardActivity.class));
                            finish();
                        } else {
                            Intent intent = new Intent(mContext, OTPActivity.class);
                            intent.putExtra("FLAG", "registration");
                            intent.putExtra("E_MAIL", userName);
                            intent.putExtra("PASSWORD", password);
                            startActivity(intent);
                        }
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.login_error_str), Toast.LENGTH_SHORT).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == START_TERMS_ACTIVITY) {
                if (data.getBooleanExtra("FLAG_AGREE", false))
                    cbAgree.setChecked(true);
                else
                    cbAgree.setChecked(false);
            }
        }
    }


    private void moveToPlainLogin() {
        forgetPasswordFlag = false;
        tvLabelSignUp.setText(getString(R.string.login_str));
        cbAgree.setVisibility(View.GONE);
        tvTermsConditionLink.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        btnLogin.setText(getString(R.string.forward_str));
        tvForgotPassword.setVisibility(View.VISIBLE);
        tvSignUp.setVisibility(View.VISIBLE);
        tvRegistered.setVisibility(View.GONE);
        updateLoginBtn(true);
    }

    private void moveToLoginWithPassword() {
        forgetPasswordFlag = false;
        tvLabelSignUp.setText(getString(R.string.login_str));
        cbAgree.setVisibility(View.GONE);
        tvTermsConditionLink.setVisibility(View.GONE);
        etPassword.setVisibility(View.VISIBLE);
        btnLogin.setText(getString(R.string.enter_str));
        tvForgotPassword.setVisibility(View.VISIBLE);
        tvSignUp.setVisibility(View.VISIBLE);
        tvRegistered.setVisibility(View.GONE);
        updateLoginBtn(true);
    }

    private void moveToForgotPassword() {
        forgetPasswordFlag = true;
        tvLabelSignUp.setText(getString(R.string.forgot_password_str));
        cbAgree.setVisibility(View.GONE);
        tvTermsConditionLink.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        btnLogin.setText(getString(R.string.forward_str));
        tvForgotPassword.setVisibility(View.GONE);
        tvSignUp.setVisibility(View.VISIBLE);
        tvRegistered.setVisibility(View.VISIBLE);
        updateLoginBtn(true);
    }

    private void moveToSignUp() {
        forgetPasswordFlag = false;
        tvLabelSignUp.setText(getString(R.string.sign_up));
        cbAgree.setVisibility(View.VISIBLE);
        tvTermsConditionLink.setVisibility(View.VISIBLE);
        etPassword.setVisibility(View.GONE);
        btnLogin.setText(getString(R.string.forward_str));
        tvForgotPassword.setVisibility(View.GONE);
        tvSignUp.setVisibility(View.GONE);
        tvRegistered.setVisibility(View.VISIBLE);
        updateLoginBtn(cbAgree.isChecked());
    }

    private void updateLoginBtn(boolean isSelected) {
        if (isSelected) {
            btnLogin.setEnabled(true);
            btnLogin.setClickable(true);
            btnLogin.setVisibility(View.VISIBLE);
            btnLoginDisabled.setVisibility(View.GONE);
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setClickable(false);
            btnLogin.setVisibility(View.INVISIBLE);
            btnLoginDisabled.setVisibility(View.VISIBLE);
        }
    }
}
