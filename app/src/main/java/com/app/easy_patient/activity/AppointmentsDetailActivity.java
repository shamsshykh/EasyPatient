package com.app.easy_patient.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.easy_patient.R;
import com.app.easy_patient.model.AppointmentsListModel;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.receiver.AppointmentReceiver;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.AlarmManager.RTC_WAKEUP;

public class AppointmentsDetailActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Context mContext;
    Toolbar toolbar;
    TextView tvMonth, tvDate, tvDay, tvType, tvSpecialist, tvTime, tvAddress, tvWhatsapp,
            tvPhone, tvEmail, tvConfirm, tvCancel, labelConfirmStatus, tvClinicName;
    LinearLayout llConfirm;
    AppointmentsListModel appointmentDetail;
    LinearLayout llCardInfo, contactLayout, ll_whatsapp, ll_phone, ll_email;
    //    ImageView imgConfirm,imgCancel;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Calendar cal;
    Double lat = 0.0, lng = 0.0;
    java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = null, time = null;
    ImageView imgClinic;
    private static final int REQUEST_CONTACTS = 1;
    private static final int CALL_PHONE = 2;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_detail);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);

        ll_email = findViewById(R.id.ll_email);
        ll_phone = findViewById(R.id.ll_phone);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);
        llCardInfo = findViewById(R.id.ll_card_info);
        contactLayout = findViewById(R.id.contactLayout);
        tvMonth = findViewById(R.id.tv_month);
        tvDate = findViewById(R.id.tv_date);
        tvDay = findViewById(R.id.tv_day);
        tvType = findViewById(R.id.tv_type);
        tvSpecialist = findViewById(R.id.tv_specialist);
        tvTime = findViewById(R.id.tv_time);
        tvAddress = findViewById(R.id.tv_address);
        tvWhatsapp = findViewById(R.id.tv_whatsapp);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        imgClinic = findViewById(R.id.clinic_image);
//        imgConfirm = findViewById(R.id.img_confirm);
//        imgCancel = findViewById(R.id.img_cancel);
//        tvConfirm = findViewById(R.id.tv_confirm);
//        llConfirm = findViewById(R.id.ll_confirm);
//        llReschedule = findViewById(R.id.ll_reschedule);
//        tvRescheduleInfo = findViewById(R.id.tv_reschedule_info);
//        tvCancel = findViewById(R.id.tv_cancel);
//        labelConfirmStatus = findViewById(R.id.label_confirm_status);
        tvClinicName = findViewById(R.id.tv_clinic_name);
//        tvReschedule = findViewById(R.id.tv_reschedule);
        cal = new GregorianCalendar();

        setSupportActionBar(toolbar);
        setTitle(getString(R.string.appointments));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appointmentDetail = (AppointmentsListModel) getIntent().getSerializableExtra("appointment_detail");
        if (appointmentDetail.getPhone() == null || appointmentDetail.getPhone().isEmpty()) {
            ll_phone.setVisibility(View.GONE);
            ll_whatsapp.setVisibility(View.GONE);
        }
        if (appointmentDetail.getEmail() == null || appointmentDetail.getEmail().isEmpty()) {
            ll_email.setVisibility(View.GONE);
        }
        if (appointmentDetail.getPhone() == null && appointmentDetail.getEmail() == null) {
            contactLayout.setVisibility(View.GONE);
        }
        if (appointmentDetail.getPhone() != null && appointmentDetail.getEmail() != null) {
            if (appointmentDetail.getPhone().isEmpty() && appointmentDetail.getEmail().isEmpty()) {
                contactLayout.setVisibility(View.GONE);
            }
        }

        initAppointmentDetail();

//        cardView.setOnClickListener(v -> startActivity(new Intent(this,MapsActivity.class)));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (appointmentDetail.getLatitude() != null && appointmentDetail.getLongitude() != null &&
                appointmentDetail.getLatitude() != "") {
            lat = Double.parseDouble(appointmentDetail.getLatitude());
            lng = Double.parseDouble(appointmentDetail.getLongitude());
        }
        // Add a marker and move the camera
        LatLng ll = new LatLng(lat, lng);

        if (lat == 0.0 && lng == 0.0) {
            mapFragment.getView().setVisibility(View.GONE);
        } else {
            mapFragment.getView().setVisibility(View.VISIBLE);
        }
        mMap.addMarker(new MarkerOptions().position(ll)
                .title(appointmentDetail.getClinic()));

        CameraPosition position = new CameraPosition.Builder().
                target(ll).zoom(17f).bearing(19).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private void initAppointmentDetail() {
        Glide.with(mContext)
                .load(appointmentDetail.getClinic_logo())
                .apply(imagePlaceHolder(R.drawable.ic_appointmrnts_placeholder))
                .into(imgClinic);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(appointmentDetail.getDate());
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        if (date != null) {
            tvMonth.setText(DateFormat.format("MMMM", date).toString().toUpperCase());
            tvDate.setText(DateFormat.format("dd", date));
            tvDay.setText(DateFormat.format("EE", date).toString().toUpperCase());
            tvTime.setText(DateFormat.format("HH", date) + "h" + DateFormat.format("mm", date));
        }
        tvType.setText(appointmentDetail.getType());
        tvSpecialist.setText(appointmentDetail.getSpecialist());
        tvClinicName.setText(appointmentDetail.getClinic());

//        if(appointmentDetail.getSchedule_status_id()==5){
//            llConfirm.setVisibility(View.GONE);
////            llReschedule.setVisibility(View.GONE);
//            imgCancel.setImageResource(R.drawable.img_cancel_confirmed);
//            tvCancel.setText("Canceled");
//            tvCancel.setTextColor(getResources().getColor(R.color.colorCancel));
//            labelConfirmStatus.setText("The date of this appointment expired.");
//        }else if(appointmentDetail.getSchedule_status_id()==2){
//            imgConfirm.setImageResource(R.drawable.img_appointment_confirmed);
//            tvConfirm.setText("Confirmed");
//            tvConfirm.setTextColor(getResources().getColor(R.color.colorConfirm));
//            labelConfirmStatus.setText("You have confirmed your presense in this appointment");
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvWhatsapp.setText(appointmentDetail.getWhatsapp_value());
        tvPhone.setText(appointmentDetail.getPhone());
        tvEmail.setText(appointmentDetail.getEmail());
        tvAddress.setText(appointmentDetail.getAddress());
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_whatsapp:

                String url = "https://api.whatsapp.com/send?phone=" + appointmentDetail.getWhatsapp_value();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);


//                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                whatsappIntent.setType("text/plain");
//                whatsappIntent.setPackage("com.whatsapp");
//                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "text");
//
//                try {
//                    startActivity(whatsappIntent);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(mContext, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//                }


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED||
//                            ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
//
//                    } else {
//                        whatsappCall();
//                    }
//                    }
                break;

            case R.id.ll_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + appointmentDetail.getPhone()));
                startActivity(intent);
                break;

            case R.id.ll_email:
//                Intent email = new Intent(Intent.ACTION_SEND);
                String[] recipients = {appointmentDetail.getEmail()};
//                email.putExtra(Intent.EXTRA_EMAIL,recipients);
////                email.putExtra(Intent.EXTRA_SUBJECT, tvTitle.getText());
////                email.putExtra(Intent.EXTRA_TEXT, String.valueOf(tvContent.getText()));
//
//                //need this to prompts email client only
//                email.setType("message/rfc822");
//                if (email.resolveActivity(getPackageManager()) != null) {
//                    startActivity(Intent.createChooser(email, null));
//                }
                shareToGMail(recipients, null, null);
                break;

//            case R.id.ll_confirm:
//                if(appointmentDetail.getSchedule_status_id()!=2)
//                putConfirmAppointment();
//                break;
//            case R.id.ll_cancel:
//                if(appointmentDetail.getSchedule_status_id()==2)
//                putCancelAppointment();
//                break;
//            case R.id.ll_reschedule:
            // Get Current Date
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                mDay = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                date = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
//                                setTime();
//
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//                datePickerDialog.show();
//                break;
        }
    }

    private void
    whatsappCall() {
        String mimeString = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        //here you have to pass whatsApp contact  number  as  contact_number ..

        String name = getContactName(appointmentDetail.getWhatsapp(), mContext);
        int whatsappcall = getContactIdForWhatsAppCall(name, mContext);
        if (whatsappcall != 0) {
            intent.setDataAndType(Uri.parse("content://com.android.contacts/data/" + whatsappcall),
                    "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
            intent.setPackage("com.whatsapp");

            startActivityForResult(intent, 1);
        }
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


    public int getContactIdForWhatsAppCall(String name, Context context) {

        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data._ID},
                ContactsContract.Data.DISPLAY_NAME + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{name, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"},
                ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
            System.out.println("9999999999999999          name  " + name + "      id    " + phoneContactID);
            return phoneContactID;
        } else {
            System.out.println("8888888888888888888          ");
            return 0;
        }
    }

    private void putRescheduleAppointment() {
        progressDialog.show();
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        Call<StatusModel> rescheduleAppointmentResponse = api.rescheduleAppointment(appointmentDetail.getId(), date, time);
        rescheduleAppointmentResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().getStatus();
                    if (status) {
                        llConfirm.setVisibility(View.GONE);
//                        imgConfirm.setImageResource(R.drawable.img_appointment_confirmed);
//                        tvReschedule.setTextColor(getResources().getColor(R.color.colorWhite));
//                        tvRescheduleInfo.setText("Requested reschedule");
//                        tvRescheduleInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                        labelConfirmStatus.setText("Wait for the schedule confirmation");

//                        Calendar calAppointmentTime = Calendar.getInstance();
//                        try {
//                            calAppointmentTime.setTime(df.parse(appointmentDetail.getDate()));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        Intent intent = new Intent(mContext, AppointmentAlarmReciever.class);
//                        intent.putExtra("ID", appointmentDetail.getId());
//                        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, appointmentDetail.getId(), intent, 0);
//                        setAlarm(mContext, calAppointmentTime.getTimeInMillis(), pIntent);
                    } else
                        Toast.makeText(mContext,  getString(R.string.error_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    private void putConfirmAppointment() {
        progressDialog.show();
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        Call<StatusModel> confirmAppointmentResponse = api.confirmAppointment(String.valueOf(appointmentDetail.getId()));
        confirmAppointmentResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().getStatus();
                    if (status) {
//                        imgConfirm.setImageResource(R.drawable.img_appointment_confirmed);
//                        tvConfirm.setText("Confirmed");
//                        tvConfirm.setTextColor(getResources().getColor(R.color.colorConfirm));
//                        labelConfirmStatus.setText("You have confirmed your presense in this appointment");

                        Calendar calAppointmentTime = Calendar.getInstance();
                        try {
                            calAppointmentTime.setTime(df.parse(appointmentDetail.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(mContext, AppointmentReceiver.class);
                        intent.putExtra("ID", appointmentDetail.getId());
                        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, appointmentDetail.getId(), intent, 0);
                        setAlarm(mContext, calAppointmentTime.getTimeInMillis(), pIntent);
                    } else
                        Toast.makeText(mContext, getString(R.string.error_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    private void putCancelAppointment() {
        progressDialog.show();
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        Call<StatusModel> cancelAppointmentResponse = api.cancelAppointment(String.valueOf(appointmentDetail.getId()));
        cancelAppointmentResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().getStatus();
                    if (status) {
//                        llConfirm.setVisibility(View.GONE);
//                        imgCancel.setImageResource(R.drawable.img_cancel_confirmed);
//                        tvCancel.setText("Canceled");
//                        tvCancel.setTextColor(getResources().getColor(R.color.colorCancel));
//                        labelConfirmStatus.setText("You have canceled this appointment. For reeschedule, contact "+appointmentDetail.getClinic()+".");

                        cancelAlarm(mContext, appointmentDetail.getId());
                    } else
                        Toast.makeText(mContext, getString(R.string.error_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }

                closeProgressDialog();
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    void setTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        putRescheduleAppointment();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
//                        mMap.setMyLocationEnabled(true);

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Log.e("MapActivity", "permission denied");
                }
                break;
            case REQUEST_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    whatsappCall();
                } else {
                    Toast.makeText(this, getString(R.string.permission_warning_str), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private static void setAlarm(Context context, long time, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, time, pendingIntent);
        else
            alarmManager.setExact(RTC_WAKEUP, time, pendingIntent);
    }

    private static void cancelAlarm(Context context, int id) {
        Intent in = new Intent(context, AppointmentReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, in, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void shareToGMail(String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(emailIntent);
    }
}
