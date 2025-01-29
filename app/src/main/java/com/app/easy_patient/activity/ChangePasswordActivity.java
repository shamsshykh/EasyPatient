package com.app.easy_patient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.easy_patient.R;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {
    EditText etNewPassword, etConfirmPassword;
    Button btnConfirm;
    String newPassword, confirmPassword;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.change_password_str));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnConfirm.setOnClickListener(v -> onConfirmClick());

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_str));
    }

    private void onConfirmClick() {
        newPassword = String.valueOf(etNewPassword.getText());
        confirmPassword = String.valueOf(etConfirmPassword.getText());
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError(getString(R.string.enter_email_str));
            if (!etNewPassword.requestFocus()) etNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.enter_confirm_pass_str));
            if (!etConfirmPassword.requestFocus()) etConfirmPassword.requestFocus();
        } else if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.pass_match_error_str));
            if (!etConfirmPassword.requestFocus()) etConfirmPassword.requestFocus();
        } else {
            changePassword(newPassword);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword(String newPassword) {
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        progressDialog.show();
        Call<StatusModel> loadUserDetailResponse = api.changePassword(newPassword);
        loadUserDetailResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        finish();
                    }
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Log.e("change_password_Error", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

}
