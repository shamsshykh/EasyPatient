package com.app.easy_patient.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.easy_patient.R;
import com.app.easy_patient.activity.dashboard.DashboardActivity;

public class CompleteRegistrationActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compete_registration);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        switch (view.getId()){
            case R.id.btn_no:
                startActivity(intent);
                break;
            case R.id.btn_yes:
                intent.putExtra("TYPE", "new_user");
                startActivity(intent);
                break;
        }
    }
}
