package com.app.easy_patient.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.app.easy_patient.model.UserRegistrationModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.R;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.InstantAutoComplete;
import com.app.easy_patient.util.SharedPrefs;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.GregorianCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends BaseActivity {
    Context mContext;
    Button btnRegister;
    TextInputEditText etEmail, etFullName, etBirthDate, etPassword, etConfirmPassword;
    TextView labelRegistration;
    InstantAutoComplete spinnerGender;
    String eMail, fullName, birthDate, password, confirmPassword, gender = "", device = android.os.Build.MODEL;
    GetDataService api;
    ProgressDialog progressDialog;
    private int mYear, mMonth, mDay;
    Calendar cal;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBar(R.color.statusBarColor);
        setContentView(R.layout.activity_registration_new);
        mContext = this;
        sharedPrefs = new SharedPrefs(mContext);
        cal = new GregorianCalendar();
        api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        btnRegister = findViewById(R.id.btn_register);
        etEmail = findViewById(R.id.et_email);
        etFullName = findViewById(R.id.et_full_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        spinnerGender = findViewById(R.id.spinner_gender);
        labelRegistration = findViewById(R.id.tv_sign_up);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    gender = "m";
                    break;
                case 1:
                    gender = "f";
                    break;
            }
        });

        eMail = getIntent().getStringExtra("email");
        etEmail.setText(eMail);

        labelRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            onRegisterButtonClick();
        });

        etFullName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    etFullName.clearFocus();
                    etBirthDate.requestFocus();
                    showDOBCalendar();
                    return true;
                }
                return false;
            }
        });

        spinnerGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etBirthDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    showDOBCalendar();
                    hideKeyboard(v);
                    // Do what you want
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void onRegisterButtonClick() {
        getData();
        if (TextUtils.isEmpty(eMail))
            showError(etEmail,getString(R.string.enter_please_email_str));
        else if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches())
            showError(etEmail,getString(R.string.is_valid_email_str));
        else if (TextUtils.isEmpty(fullName))
            showError(etFullName,getString(R.string.enter_please_fullname_str));
        else if (TextUtils.isEmpty(etBirthDate.getText().toString()))
            showError(etBirthDate,getString(R.string.enter_please_birthdate_str));
        else if (TextUtils.isEmpty(password))
            showError(etPassword,getString(R.string.enter_please_password));
        else if (TextUtils.isEmpty(confirmPassword))
            showError(etConfirmPassword,getString(R.string.enter_please_confirm_pass_str));
                            else if (!password.equals(confirmPassword)) {
                                etConfirmPassword.setError(getString(R.string.not_match_pass_str));
                                if (!etConfirmPassword.requestFocus())
                                    etConfirmPassword.requestFocus();
                            }else if (gender.equalsIgnoreCase("")) {
            Toast.makeText(mContext, getString(R.string.please_select_gender_str), Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loading_str));
            postRegister();
        }
    }

    private void showDOBCalendar() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    birthDate = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                    cal.set(Calendar.DATE, dayOfMonth);
                    cal.set(Calendar.MONTH, (monthOfYear));
                    cal.set(Calendar.YEAR, year);
                    etBirthDate.setText(String.format("%02d", dayOfMonth)+"/"+String.format("%02d", (monthOfYear + 1))+"/"+year);
                    spinnerGender.showDropDown();
                    spinnerGender.requestFocus();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //Calling post API to submit the data enter by the user
    private void postRegister() {
        progressDialog.show();
        Call<UserRegistrationModel> registrationResponse = api.postRegisterPatient(eMail, fullName, birthDate, gender, password, device);
        registrationResponse.enqueue(new Callback<UserRegistrationModel>() {
            @Override
            public void onResponse(Call<UserRegistrationModel> call, Response<UserRegistrationModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCreated()) {
                        Intent intent = new Intent(mContext, OTPActivity.class);
                        intent.putExtra("FLAG", "registration");
                        intent.putExtra("E_MAIL", eMail);
                        intent.putExtra("PASSWORD", password);
                        startActivity(intent);
                    } else if (response.body().getExists())
                        Toast.makeText(mContext, getString(R.string.account_already_exist_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserRegistrationModel> call, Throwable t) {
                Log.e("Post_Register_Response", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getData() {
        eMail = String.valueOf(etEmail.getText());
        fullName = String.valueOf(etFullName.getText());
//        birthDate = String.valueOf(etBirthDate.getText());
        password = String.valueOf(etPassword.getText());
        confirmPassword = String.valueOf(etConfirmPassword.getText());
    }
}
