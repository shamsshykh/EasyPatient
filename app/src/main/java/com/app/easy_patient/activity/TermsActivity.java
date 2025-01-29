package com.app.easy_patient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.easy_patient.R;
import com.app.easy_patient.model.TermsModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsActivity extends BaseActivity {
    TextView tvTerms, txtTitle, txtDesc;
    Button btnAgree, tvCancel;
    Toolbar toolbar;
    ConstraintLayout constraintLayout;
    WebView webView;

    boolean fromSettings = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        fromSettings = getIntent().getBooleanExtra("FromSettings", false);

        tvCancel=findViewById(R.id.tv_cancel);
        btnAgree=findViewById(R.id.btn_agree);
        toolbar=findViewById(R.id.toolbar);
        txtTitle=findViewById(R.id.title);
        txtDesc=findViewById(R.id.desc);
        constraintLayout=findViewById(R.id.layoutButton);
        webView = findViewById(R.id.tv_terms);

        updateUi();

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FLAG_AGREE", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FLAG_AGREE", false);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        getTerms();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    private void updateUi() {
        if (fromSettings) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.terms_conditions));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            txtTitle.setVisibility(View.GONE);
            txtDesc.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
        }
    }

    private void getTerms() {
        progressDialog.show();
        GetDataService api= RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        Call<TermsModel> forgotPasswordResponse = api.terms();
        forgotPasswordResponse.enqueue(new Callback<TermsModel>() {
            @Override
            public void onResponse(Call<TermsModel> call, Response<TermsModel> response) {
                if (response.isSuccessful()) {
                    webView.loadData(response.body().getMessage().getText(),"text/html", "UTF-8");
                } else {
                    Toast.makeText(TermsActivity.this, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }

                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<TermsModel> call, Throwable t) {
                Log.e("Terms_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }
}