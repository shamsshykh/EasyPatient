package com.app.easy_patient.activity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.easy_patient.R;
import com.app.easy_patient.activity.dashboard.DashboardActivity;
import com.app.easy_patient.model.LoginResponseModel;
import com.app.easy_patient.model.ResendCodeModel;
import com.app.easy_patient.model.ValidationModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    Button btnOTPSubmit;
    GetDataService api;
    Context mContext;
    String email, password;
    ProgressDialog progressDialog;
    SharedPrefs sharedPrefs;
    String flag;
    TextView txtResendCode;

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBar(R.color.statusBarColor);
        setContentView(R.layout.activity_forgot_password_otp);
        mContext = this;
        sharedPrefs = new SharedPrefs(mContext);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flag = extras.getString("FLAG");
            if (flag.equalsIgnoreCase("registration")) {
                email = extras.getString("E_MAIL");
                password = extras.getString("PASSWORD");
            } else if (flag.equalsIgnoreCase("forgot_password")) {
                email = extras.getString("E_MAIL");
            }
        }
        init();
        setPINListeners();

        btnOTPSubmit.setOnClickListener(v -> {
            if (mPinHiddenEditText.getText().length() == 4) {
                if (flag.equalsIgnoreCase("forgot_password")) {
                    verifyOTP(mPinHiddenEditText.getText().toString(), email);
                } else {
                    postActivationAccount(mPinHiddenEditText.getText().toString(), email);
                }
            } else {
                Toast.makeText(this, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
            }
        });

        txtResendCode.setOnClickListener(v -> {
            if (email != null) {
                progressDialog.show();
                Call<ResendCodeModel> otpResend = api.resendCode(email);
                otpResend.enqueue(new Callback<ResendCodeModel>() {
                    @Override
                    public void onResponse(Call<ResendCodeModel> call, Response<ResendCodeModel> response) {
                        if (response.isSuccessful() && response.body().isCreated()) {
                            Toast.makeText(mContext, getString(R.string.opt_send_successful_str), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.opt_send_error), Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResendCodeModel> call, Throwable t) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, getString(R.string.opt_send_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Initialize EditText fields.
     */
    private void init() {
        progressDialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_str));
        api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.pin_hidden_edittext);
        btnOTPSubmit = findViewById(R.id.btn_otp_submit);
        txtResendCode = findViewById(R.id.txtResendCode);
    }

    private void postActivationAccount(String OTP, String eMail) {
        Call<ValidationModel> otpResponse = api.activateAccount(eMail, OTP);
        otpResponse.enqueue(new Callback<ValidationModel>() {
            @Override
            public void onResponse(Call<ValidationModel> call, Response<ValidationModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().isValid()) {
                        if (flag.equalsIgnoreCase("registration")){
                            getLoginDetails("password", eMail, password);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValidationModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Implementation for the call of login API
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

                        Intent intent = new Intent(mContext, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.invalid_user_pass_str), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setFocus(mPinHiddenEditText);
            showSoftKeyboard(mPinHiddenEditText);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0) {
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));
//                            Log.e("changedString", String.valueOf(mPinHiddenEditText.getText()));
                        }
                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            hideSoftKeyboard(mPinForthDigitEditText);
        }
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
//        mPinHiddenEditText.setOnKeyListener(this);
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * Custom LinearLayout with overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
    public class MainLayout extends LinearLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_registration, this);
        }

    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void verifyOTP(String OTP, String eMail) {
        Call<ValidationModel> otpResponse = api.verifyOtp(eMail, OTP);
        otpResponse.enqueue(new Callback<ValidationModel>() {
            @Override
            public void onResponse(Call<ValidationModel> call, Response<ValidationModel> response) {
                if (response.isSuccessful()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.body().isValid()) {
                        Intent intent = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("E_MAIL", email);
                        intent.putExtra("OTP", mPinHiddenEditText.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValidationModel> call, Throwable t) {
                Log.e("Login_Response", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(mContext, getString(R.string.opt_error), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
