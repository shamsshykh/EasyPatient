package com.app.easy_patient.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.app.easy_patient.R;
import com.app.easy_patient.database.EasyPatientDatabase;
import com.app.easy_patient.database.MedicineDetailDao;
import com.app.easy_patient.database.MedicineReminderDao;
import com.app.easy_patient.fragment.BottomSheetDOW;
import com.app.easy_patient.fragment.BottomSheetFrequency;
import com.app.easy_patient.fragment.DurationBottomSheetDialog;
import com.app.easy_patient.ktx.BindingsKt;
import com.app.easy_patient.ktx.TheKtxKt;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.model.kotlin.Medicine;
import com.app.easy_patient.model.kotlin.MedicineReminder;
import com.app.easy_patient.model.kotlin.MedicineStatus;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.AlarmCallback;
import com.app.easy_patient.util.GenericCallback;
import com.app.easy_patient.util.InstantAutoComplete;
import com.app.easy_patient.util.MyDialogCloseListener;
import com.app.easy_patient.util.SharedPrefs;
import com.app.easy_patient.util.Utility;
import com.app.easy_patient.worker.CreateMedicineReminderWorker;
import com.app.easy_patient.worker.UpdateMedicineReminderWorker;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMedicineActivity extends BaseActivity implements MyDialogCloseListener {
    Toolbar toolbar;
    EditText etSupplementName, etDosage, etStartTime, etNoDays, etFrequency;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button nowBtn, btnDelete;
    BottomSheetDialog dialog;
    public static HashMap<String, Boolean> selectedDays = new HashMap<>();
    // StringBuilder selectedDayNameString = new StringBuilder();
    StringBuilder selectedDayCodeString = new StringBuilder();
    private MedicineDetailDao mMedicineDetailDao;
    private MedicineReminderDao mMedicineReminderDao;
    private EasyPatientDatabase db;
    int notification = 0, critical = 0;
    Switch switchNotification, switchCritical;
    String flag = "new", imageLink, localImageLink, defaultIcon = "0";
    int id;
    Medicine medicineDetail;
    ImageView imgMedicine, imgMedicineCustom;
    Context mContext;
    String selectedTime;
    SharedPrefs sharedPrefs;
    TextInputLayout ILDosages, ILNoDays, ILName, ILStartTime, ILDuration, ILFrequency;
    LinearLayout llCustomImage;
    final static int RQS_1 = 1;
    TimePickerDialog timePickerDialog;
    InstantAutoComplete durationAutoComplete;
    int frequencyValue = -1, frequencyType = -1, durationType = 0;
    boolean isUpdateSameTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medicine);
        mContext = this;
        db = EasyPatientDatabase.getDatabase(this);
        mMedicineDetailDao = db.medicineDetailDao();
        mMedicineReminderDao = db.medicineReminderDao();
        sharedPrefs = new SharedPrefs(this);
        etSupplementName = findViewById(R.id.et_medicine_name);
        nowBtn = findViewById(R.id.nowBtn);
        nowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog(false);
            }
        });
        ILName = findViewById(R.id.Input_layout_medicine_name);
        etDosage = findViewById(R.id.et_dosage);
        ILDosages = findViewById(R.id.input_layout_dosages);
        etStartTime = findViewById(R.id.et_start_time);
        etNoDays = findViewById(R.id.et_no_days);
        ILNoDays = findViewById(R.id.input_layout_no_days);
        ILStartTime = findViewById(R.id.input_layout_start_time);
        ILDuration = findViewById(R.id.inputDuration);
        ILFrequency = findViewById(R.id.input_layout_frequency);
        // etFrequency = findViewById(R.id.et_frequency);
        // etDaysWeek = findViewById(R.id.et_days_week);
        toolbar = findViewById(R.id.toolbar);
        switchNotification = findViewById(R.id.switch_notification);
        switchCritical = findViewById(R.id.switch_critical);
        imgMedicine = findViewById(R.id.medicine_image);
        imgMedicineCustom = findViewById(R.id.medicine_image_custom);
        llCustomImage = findViewById(R.id.ll_custom_image);
        btnDelete = findViewById(R.id.btn_delete);
        etFrequency = findViewById(R.id.spinner_frequency);
        durationAutoComplete = findViewById(R.id.spinner_duration);

        setEditTextView(etSupplementName, ILName, getString(R.string.med_sup_str), getString(R.string.type_name_med_str));
        setEditTextView(etDosage, ILDosages, getString(R.string.dosage_str), getString(R.string.dosage_desc_str));
        setEditTextView(etNoDays, ILNoDays, getString(R.string.no_of_days_str), getString(R.string.days_desc_str));
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.new_med_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            flag = extras.getString("flag");
        if (flag.equalsIgnoreCase("edit")) {
            medicineDetail = (Medicine) getIntent().getParcelableExtra("MEDICINE_DETAIL");

            id = medicineDetail.getId();
            initFields();
//            id = extras.getInt("ID");
//            new AsyncInitFields().execute();
        }

        updateAutoComplete(medicineDetail != null);
        initWeekDays();

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notification = isChecked ? 1 : 0;
        });

        switchCritical.setOnCheckedChangeListener((buttonView, isChecked) -> {
            critical = isChecked ? 1 : 0;
            if (isChecked) {
                switchNotification.setChecked(true);
                notification = 1;
            }
        });

        imgMedicine.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MedicinePictureActivity.class);
            if (flag.equalsIgnoreCase("edit")) {
                intent.putExtra("DEFAULT_ICON", defaultIcon);
                intent.putExtra("IMAGE_LINK", imageLink);
            }
            startActivityForResult(intent, 1);
        });
        imgMedicineCustom.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MedicinePictureActivity.class);
            if (flag.equalsIgnoreCase("edit")) {
                intent.putExtra("DEFAULT_ICON", defaultIcon);
                intent.putExtra("IMAGE_LINK", imageLink);
            }
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
        if (resultCode == RESULT_OK) {
            llCustomImage.setVisibility(View.VISIBLE);
            String medicineImageLocalPath;
//                medicineImageLocalPath = data.getData().toString();
            defaultIcon = data.getStringExtra("DEFAULT_ICON");
            localImageLink = sharedPrefs.getMedicinePicturePath();
            if (defaultIcon != null) {
                if (!(defaultIcon.equals("0"))) {
                    llCustomImage.setVisibility(View.GONE);
                    imgMedicine.setImageResource(TheKtxKt.medicineIcon(defaultIcon));
                } else if (localImageLink != null && localImageLink != "") {
                    Bitmap thumbnail = (BitmapFactory.decodeFile(localImageLink));
                    imgMedicineCustom.setImageBitmap(thumbnail);
                } else if (imageLink != null && imageLink != "") {
                    Glide.with(mContext)
                            .load(imageLink)
                            .into(imgMedicineCustom);
                }
            }
        }
//        }
    }

    private void initFields() {
        imageLink = medicineDetail.getPicture_link();
        defaultIcon = medicineDetail.getDefault_icon();
        BindingsKt.medicineImage(imgMedicine, medicineDetail);

        /*days = medicineDetail.getDays_of_the_week();
        selectedDayNameString = getSelectedDayString(medicineDetail.getDays_of_the_week());
        etDaysWeek.setText(selectedDayNameString);*/
        etSupplementName.setText(medicineDetail.getName());
        etDosage.setText(medicineDetail.getDosage());
        selectedTime = medicineDetail.getStart_time();
        etStartTime.setText(setDateString(selectedTime));
        etNoDays.setText(String.valueOf(medicineDetail.getNumber_of_days()));
        // etFrequency.setText(String.valueOf(medicineDetail.getFrequency()));
        if (TheKtxKt.notification(medicineDetail)) {
            switchNotification.setChecked(true);
            notification = 1;
        }
        if (TheKtxKt.critical(medicineDetail)) {
            switchCritical.setChecked(true);
            critical = 1;
        }
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void initWeekDays() {
        selectedDays = new HashMap<>();
        selectedDays.put("Monday", false);
        selectedDays.put("Tuesday", false);
        selectedDays.put("Wednesday", false);
        selectedDays.put("Thursday", false);
        selectedDays.put("Friday", false);
        selectedDays.put("Saturday", false);
        selectedDays.put("Sunday", false);
        if (flag.equalsIgnoreCase("edit")) {
            initSelectedDays(medicineDetail.getDays_of_the_week());
        }
    }

    private void initSelectedDays(String dayOfWeek) {
        String[] daysArray = dayOfWeek.split(",");
        for (int i = 0; i < daysArray.length; i++) {
            selectedDays.put((mapToDayString(daysArray[i])), true);
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.et_start_time) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DATE, dayOfMonth);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.YEAR, year);
                        calendar.add(Calendar.MINUTE, 10);
                        setTime(date, calendar);

                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(c.getTime().getTime());
            datePickerDialog.show();
        } else if (view.getId() == R.id.btn_confirm) {
            onClickConfirmButton();
        } else if (view.getId() == R.id.et_days_week) {
            if (selectedTime != null) {
                etNoDays.clearFocus();
                BottomSheetDOW fragment = new BottomSheetDOW(medicineDetail != null, selectedTime);
                fragment.show(getSupportFragmentManager(), "DOW");
            }
        } else if (view.getId() == R.id.btn_delete) {
            deleteConfirmation();
        }
    }

    private void deleteConfirmation() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.alert_str))
                .setMessage(getString(R.string.medicine_delete_str, medicineDetail.getName()))
                .setNegativeButton(getString(R.string.cancel_str), (dialoginterface, i) -> dialoginterface.cancel())
                .setPositiveButton("Ok", (dialoginterface, i) -> {
                    deleteMedicineItem();
                }).show();

    }

    private void deleteMedicineItem() {
        progressDialog.show();
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        Call<StatusModel> loadOrientationResponse = api.deleteMedicine(medicineDetail.getId());
        loadOrientationResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    StatusModel result = response.body();
                    if (result.getStatus()) {
                        cancelMedicineReminderNotifications(medicineDetail.getId());
                        deleteAllRemindersOfMedicine(medicineDetail.getId());
                        db.databaseWriteExecutor.execute(() -> {
                            mMedicineDetailDao.deleteMedicineEntry(medicineDetail);
                        });
                        setResult(RESULT_OK);
                        finish();
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Log.e("Medicines_delete_Error", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void editMedicineDetail(Medicine medicineDetail) {
        db.databaseWriteExecutor.execute(() -> {
            mMedicineDetailDao.deleteMedicineEntry(medicineDetail);
            mMedicineDetailDao.insertMedicineDetail(medicineDetail);
        });
    }

    private void createMedicine(Medicine medicine) {
        db.databaseWriteExecutor.execute(() -> {
            mMedicineDetailDao.insertMedicineDetail(medicine);
            createMedicineReminderWorker(medicine);
            closeProgressDialog();
            setResult(RESULT_OK);
            finish();
        });
    }

    void setTime(String date, Calendar calendar) {
        // Get Current Time
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                (view, hourOfDay, minute) -> {
                    selectedTime = date + " " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + "00";
                    etStartTime.setText(setDateString(selectedTime));
                    etNoDays.requestFocus();
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String setDateString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String month, dd, year, hour, min;
        Date date = new Date();
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        hour = checkDigit(cal.get(Calendar.HOUR_OF_DAY));
        min = checkDigit(cal.get(Calendar.MINUTE));

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd MMM yyyy", new Locale( "pt" , "BR" ));
        String formattedDate = formatterDate.format(date);
        String formattedTime = hour + "h" + min;
        return getString(R.string.medicine_date_str, formattedDate, formattedTime);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
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

    public void handleDOWDialogClose() {
        /*selectedDayCodeString.setLength(0);
        boolean firstFlag = true;
        selectedDayNameString.setLength(0);
        for (Map.Entry<String, Boolean> entry : selectedDays.entrySet()) {
            if (entry.getValue()) {
                if (firstFlag) {
                    selectedDayCodeString.append(mapDayToIntegerString(entry.getKey()));
                    selectedDayNameString.append(entry.getKey());
                    firstFlag = false;
                } else {
                    selectedDayCodeString.append(",").append(mapDayToIntegerString(entry.getKey()));
                    selectedDayNameString.append(",").append(entry.getKey());
                }

            }
        }
        days = selectedDayCodeString.toString();
//        etDaysWeek.setText(selectedDayNameString);
        if (days != null && !days.isEmpty()){
            etDaysWeek.setText(getSelectedDayString(days));
        }*/
    }

    @Override
    public void handleFrequencyDialogClose(int value, int type) {
        frequencyType = type;
        frequencyValue = value;
        switch (type) {
            case 0:
                if (value > 1) {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.hours_str)));
                } else {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.hour_str)));
                }
                break;
            case 1:
                if (value > 1) {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.days_str)));
                } else {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.day_str)));
                }
                break;
            case 2:
                if (value > 1) {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.weeks_str)));
                } else {
                    etFrequency.setText(getString(R.string.frequency_value_type_str, value, getString(R.string.week_str)));
                }
                break;
        }
    }

    private boolean isEmpty(String value, String message, TextInputLayout field) {
        if (TextUtils.isEmpty(value)) {
            field.setError(message);
            if (!field.requestFocus()) field.requestFocus();
            return true;
        } else
            return false;
    }

    private boolean validateCurrentTime(String startTime, String message, TextInputLayout field) {
//        if (isUpdateSameTime) {
//            return true;
//        }
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(TheKtxKt.stringToDate(startTime));
//
//        Calendar currentTimeCalendar = Calendar.getInstance();
//        currentTimeCalendar.add(Calendar.MINUTE, 5);
//        currentTimeCalendar.set(Calendar.SECOND, 0);
//        currentTimeCalendar.set(Calendar.MILLISECOND, 0);
//
//        if (calendar.getTimeInMillis() < currentTimeCalendar.getTimeInMillis()) {
//            field.setError(message);
//            field.requestFocus();
//            return false;
//        }
        return true;
    }

    private void postMedicines(String name, String dosage, String start_time, String number_of_days, String frequency, String days_of_the_week, String st_notification, String st_critical) {
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        String filePath;
        RequestBody mpName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody mpDosage = RequestBody.create(MediaType.parse("text/plain"), dosage);
        RequestBody mpStart_time = RequestBody.create(MediaType.parse("text/plain"), start_time);
        RequestBody mpNumber_of_days = RequestBody.create(MediaType.parse("text/plain"), number_of_days);
        RequestBody mpFrequency = RequestBody.create(MediaType.parse("text/plain"), frequency);
        RequestBody mpDays_of_the_week = RequestBody.create(MediaType.parse("text/plain"), days_of_the_week);
        RequestBody mpSt_notification = RequestBody.create(MediaType.parse("text/plain"), st_notification);
        RequestBody mpSt_critical = RequestBody.create(MediaType.parse("text/plain"), st_critical);
        Log.e("DEFAULT_ICON", defaultIcon);
        RequestBody mpDefault_icon = RequestBody.create(MediaType.parse("text/plain"), defaultIcon);
        MultipartBody.Part filePart;
        if (sharedPrefs.getMedicinePicturePath() != null && sharedPrefs.getMedicinePicturePath() != "") {
            filePath = sharedPrefs.getMedicinePicturePath();
            File imgFile = new File(filePath);
            filePart = MultipartBody.Part.createFormData("file", filePath, RequestBody.create(MediaType.parse("image/jpeg"), imgFile));
        } else {
            filePart = null;
        }
        Call<Medicine> postMedicineResponse = api.postMedicines(mpName, mpDosage, mpStart_time,
                mpNumber_of_days, mpFrequency, mpDays_of_the_week, mpSt_notification, mpSt_critical, filePart,
                mpDefault_icon);
        postMedicineResponse.enqueue(new Callback<Medicine>() {
            @Override
            public void onResponse(Call<Medicine> call, Response<Medicine> response) {
                if (response.isSuccessful()) {
                    createMedicine(response.body());
                } else {
                    Toast.makeText(mContext, "Server error Try again!!", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<Medicine> call, Throwable t) {
                Log.e("Post_Register_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }


    private void updateMedicines(int id,
                                 String name,
                                 String dosage,
                                 String start_time,
                                 String number_of_days,
                                 String frequency,
                                 String days_of_the_week,
                                 String st_notification,
                                 String st_critical,
                                 String picturePath,
                                 String pictureLink,
                                 GenericCallback callback) {
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        String filePath;
        RequestBody mpName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody mpDosage = RequestBody.create(MediaType.parse("text/plain"), dosage);
        RequestBody mpStart_time = RequestBody.create(MediaType.parse("text/plain"), start_time);
        RequestBody mpNumber_of_days = RequestBody.create(MediaType.parse("text/plain"), number_of_days);
        RequestBody mpFrequency = RequestBody.create(MediaType.parse("text/plain"), frequency);
        RequestBody mpDays_of_the_week = RequestBody.create(MediaType.parse("text/plain"), days_of_the_week);
        RequestBody mpSt_notification = RequestBody.create(MediaType.parse("text/plain"), st_notification);
        RequestBody mpSt_critical = RequestBody.create(MediaType.parse("text/plain"), st_critical);
        RequestBody mpDefault_icon = RequestBody.create(MediaType.parse("text/plain"), defaultIcon);
        String path = picturePath != null ? picturePath : "";
        RequestBody mpCurrent_picture_path = RequestBody.create(MediaType.parse("text/plain"), path);
        String link = pictureLink != null ? pictureLink : "";
        RequestBody mpCurrent_picture_link = RequestBody.create(MediaType.parse("text/plain"), link);
        MultipartBody.Part filePart;
        if (sharedPrefs.getMedicinePicturePath() != null && sharedPrefs.getMedicinePicturePath() != "") {
            filePath = sharedPrefs.getMedicinePicturePath();
            File imgFile = new File(filePath);
            filePart = MultipartBody.Part.createFormData("file", filePath, RequestBody.create(MediaType.parse("image/jpeg"), imgFile));
        } else {
            filePart = null;
        }
        Call<StatusModel> registrationResponse = api.updateMedicines(id, mpName, mpDosage, mpStart_time,
                mpNumber_of_days, mpFrequency, mpDays_of_the_week, mpSt_notification, mpSt_critical, filePart, mpDefault_icon, mpCurrent_picture_path, mpCurrent_picture_link);
        registrationResponse.enqueue(new Callback<StatusModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        callback.success();
                    } else{
                        closeProgressDialog();
                        Toast.makeText(mContext, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    closeProgressDialog();
                    Toast.makeText(mContext, "Server error Try again!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Toast.makeText(mContext, "Server error Try again!!", Toast.LENGTH_SHORT).show();
                Log.e("Post_Register_Response", t.getMessage());
                closeProgressDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPrefs.setMedicinePicturePath("");
    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(NewMedicineActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }
        }
    };

    private void createMedicineReminders(Medicine medicine) {
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(TheKtxKt.stringToDate(medicine.getStart_time()));

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(TheKtxKt.stringToDate(medicine.getStart_time()));

        int durationType = Integer.parseInt(medicine.getDays_of_the_week().split(",")[1]);
        switch (durationType) {
            case 0:
                endDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.getNumber_of_days());
                break;
            case 1:
                endDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.getNumber_of_days());
                break;
            case 2:
                endDateCalendar.add(Calendar.MONTH, medicine.getNumber_of_days());
                break;
        }
        insertNextPossibleReminders(medicine, startDateCalendar, endDateCalendar);
    }

    private void insertNextPossibleReminders(Medicine medicine, Calendar startDateCalendar, Calendar endDateCalendar) {
        if (startDateCalendar.getTimeInMillis() <= endDateCalendar.getTimeInMillis()) {
            MedicineReminder medicineReminder = TheKtxKt.toMedicineReminder(medicine, MedicineStatus.Future, startDateCalendar.getTimeInMillis());
            db.databaseWriteExecutor.execute(() -> {
                mMedicineReminderDao.insertMedicineReminder(medicineReminder);
            });
            int frequencyType = Integer.parseInt(medicine.getDays_of_the_week().split(",")[0]);
            switch (frequencyType) {
                case 0:
                    startDateCalendar.add(Calendar.HOUR_OF_DAY, medicine.getFrequency());
                    break;
                case 1:
                    startDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.getFrequency());
                    break;
                case 2:
                    startDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.getFrequency());
                    break;
            }
            insertNextPossibleReminders(medicine, startDateCalendar, endDateCalendar);
        }
    }

    private void deleteAllRemindersOfMedicine(int medicineId) {
        db.databaseWriteExecutor.execute(() -> {
            mMedicineReminderDao.deleteAllMedicineReminders(medicineId);
        });
    }

    private void cancelMedicineReminderNotifications(int medicineId) {
        List<MedicineReminder> medicineReminders = db.medicineReminderDao().medicineReminderList(medicineId);
        for (MedicineReminder medicineReminder: medicineReminders) {
            if (medicineReminder.reminderTime() > Calendar.getInstance().getTimeInMillis())
                Utility.cancelAlarm(getApplicationContext(), Utility.pendingIntent(getApplicationContext(), medicineReminder, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        }
    }

    private void updateAutoComplete(boolean isEdit) {
        /*ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(mContext, R.array.frequency_array, android.R.layout.simple_list_item_1);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        frequencyAutoComplete.setAdapter(frequencyAdapter);
        frequencyAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            updateFrequencyText();
            BottomSheetFrequency fragment = new BottomSheetFrequency(position);
            fragment.show(getSupportFragmentManager(), "Frequency");
        });*/

        etFrequency.setOnClickListener(v -> {
            DurationBottomSheetDialog fragment = new DurationBottomSheetDialog(type -> {
                updateFrequencyText();
                BottomSheetFrequency fragment1 = new BottomSheetFrequency(type);
                fragment1.show(getSupportFragmentManager(), "Frequency");
            });
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), "Frequency");
        });

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(mContext, R.array.duration_array, android.R.layout.simple_list_item_1);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        durationAutoComplete.setAdapter(durationAdapter);
        durationAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            updateFrequencyText();
            durationType = position;
        });

        if (isEdit) {
            durationType = Integer.parseInt(medicineDetail.getDays_of_the_week().split(",")[1]);
            frequencyValue = medicineDetail.getFrequency();
            frequencyType = Integer.parseInt(medicineDetail.getDays_of_the_week().split(",")[0]);
            durationAutoComplete.setText(durationAdapter.getItem(durationType).toString());
            handleFrequencyDialogClose(medicineDetail.getFrequency(), frequencyType);
        }
    }

    private void updateFrequencyText() {
        etFrequency.setText("");
        etFrequency.setHint(getString(R.string.frequency));
    }

    private void onClickConfirmButton() {
        if (!Utility.hasSchedulePermission(mContext.getApplicationContext())) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
            return;
        }

        String supplementName = etSupplementName.getText().toString();
        String dosage = etDosage.getText().toString();
        String noOfDays = etNoDays.getText().toString();
        String frequency = etFrequency.getText().toString();
        String durationFrequencyType = frequencyType + "," + durationType;


        updateSameTimeValue(noOfDays, frequencyValue, durationFrequencyType);

        if (!isEmpty(supplementName, getString(R.string.please_enter_med_name_str), ILName))
            if (!isEmpty(dosage, getString(R.string.please_enter_dosages_str), ILDosages))
                if (!isEmpty(selectedTime, getString(R.string.please_enter_start_time_str), ILStartTime))
                    if (validateCurrentTime(selectedTime, getString(R.string.please_enter_correct_time_str), ILStartTime))
                        if (!isEmpty(noOfDays, getString(R.string.please_enter_no_of_days_str), ILNoDays))
                            if (!isEmpty(frequency, getString(R.string.please_enter_frequency_str), ILFrequency)) {
                                progressDialog.show();
                                if (flag.equalsIgnoreCase("edit")){
                                    medicineDetail.setName(supplementName);
                                    medicineDetail.setDosage(dosage);
                                    medicineDetail.setNumber_of_days(Integer.parseInt(noOfDays));
                                    medicineDetail.setFrequency(frequencyValue);
                                    medicineDetail.setDays_of_the_week(durationFrequencyType);
                                    medicineDetail.setStart_time(selectedTime);
                                    medicineDetail.setSt_critical(critical);
                                    medicineDetail.setSt_notification(notification);
                                    medicineDetail.setDefault_icon(defaultIcon);
                                    updateMedicine(medicineDetail);
                                } else {
                                    postMedicines(supplementName, dosage, selectedTime, noOfDays, String.valueOf(frequencyValue), durationFrequencyType, String.valueOf(notification), String.valueOf(critical));
                                }
                            }
    }

    private void updateMedicine(Medicine medicine) {
        try {
            if (!isUpdateSameTime) {
                cancelMedicineReminderNotifications(medicine.getId());
                deleteAllRemindersOfMedicine(medicine.getId());
            }

            updateMedicines(
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getDosage(),
                    medicine.getStart_time(),
                    String.valueOf(medicine.getNumber_of_days()),
                    String.valueOf(medicine.getFrequency()),
                    medicine.getDays_of_the_week(),
                    String.valueOf(notification),
                    String.valueOf(critical),
                    medicine.getPicture_path(),
                    medicine.getPicture_link(), () -> {
                        editMedicineDetail(medicine);
                        if (!isUpdateSameTime) {
                            createMedicineReminderWorker(medicine);
                        } else {
                            updateMedicineReminderWorker(medicine);
                        }
                        closeProgressDialog();
                        setResult(RESULT_OK);
                        finish();
                    });
        } catch (Exception ex) {
            showToast(mContext, getString(R.string.error_str));
            closeProgressDialog();
            ex.printStackTrace();
        }
    }

    private void createMedicineReminderWorker(Medicine medicine) {
        Data data = new Data.Builder().putInt("medicineId", medicine.getId()).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(CreateMedicineReminderWorker.class).setInputData(data).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(CreateMedicineReminderWorker.TAG, ExistingWorkPolicy.APPEND_OR_REPLACE, work);
    }

    private void updateSameTimeValue(String noOfDays, int frequency, String durationFrequencyType) {
        if (flag.equalsIgnoreCase("edit") && !noOfDays.isEmpty()) {
            isUpdateSameTime =  medicineDetail.getStart_time().equalsIgnoreCase(selectedTime) &&
                    medicineDetail.getNumber_of_days() == Integer.parseInt(noOfDays) &&
                    medicineDetail.getDays_of_the_week().equalsIgnoreCase(durationFrequencyType) &&
                    medicineDetail.getFrequency() == frequency;
        }
    }

    private void updateMedicineReminderWorker(Medicine medicine) {
        Data data = new Data.Builder().putInt("medicineId", medicine.getId()).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(UpdateMedicineReminderWorker.class).setInputData(data).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(UpdateMedicineReminderWorker.TAG, ExistingWorkPolicy.APPEND_OR_REPLACE, work);
    }
}
