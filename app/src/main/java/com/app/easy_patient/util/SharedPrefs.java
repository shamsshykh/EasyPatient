package com.app.easy_patient.util;
import android.content.Context;
import android.content.SharedPreferences;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.DB_NAME;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.DEVICE_CURRENT_VOLUME;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.LOGIN_STATUS;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.NOTIFY_MEDICINE_STATUS;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.SET_ALARM_LIST;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.NOTIFY_SCHEDULE_STATUS;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.TOKEN;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.REFRESH_TOKEN;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.IMAGE_PATH;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.MEDICINE_PICTURE_PATH;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.NAME;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.EMAIL;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.GENDER;
import static com.app.easy_patient.util.AppConstants.PREF_KEYS.FIRST_TIME_APP;


public class SharedPrefs {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private Context context;

    public SharedPrefs(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
    }

    public void setLoginStatus(boolean status) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(LOGIN_STATUS, status);
        spEditor.commit();
    }

    public boolean getLoginStatus() {
        return sharedPreferences.getBoolean(LOGIN_STATUS, false);
    }


    public void setNotifyMedicineStatus(boolean status) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(NOTIFY_MEDICINE_STATUS, status);
        spEditor.commit();
    }

    public boolean getNotifyMedicineStatus() {
        return sharedPreferences.getBoolean(NOTIFY_MEDICINE_STATUS, true);
    }

    public void setAlarmListDataList(String alarmListData) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SET_ALARM_LIST, alarmListData);
        spEditor.commit();
    }

    public String getAlarmListDataList() {
        return sharedPreferences.getString(SET_ALARM_LIST, "");
    }

    public void setNotifyScheduleStatus(boolean status) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(NOTIFY_SCHEDULE_STATUS, status);
        spEditor.commit();
    }

    public boolean getNotifyScheduleStatus() {
        return sharedPreferences.getBoolean(NOTIFY_SCHEDULE_STATUS, true);
    }

    public void setToken(String tok) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(TOKEN, tok);
        spEditor.commit();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, null);
    }

    public void setRefreshToken(String refreshTok) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(REFRESH_TOKEN, refreshTok);
        spEditor.commit();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, null);
    }

    public void setImagePath(String path) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(IMAGE_PATH, path);
        spEditor.commit();
    }

    public String getImagePath() {
        return sharedPreferences.getString(IMAGE_PATH, null);
    }

    public void setMedicinePicturePath(String path) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(MEDICINE_PICTURE_PATH, path);
        spEditor.commit();
    }

    public String getMedicinePicturePath() {
        return sharedPreferences.getString(MEDICINE_PICTURE_PATH, null);
    }

    public void setName(String username) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(NAME, username);
        spEditor.commit();
    }

    public String getName() {
        return sharedPreferences.getString(NAME, null);
    }

    public void setEmail(String email) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(EMAIL, email);
        spEditor.commit();
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, null);
    }

    public void setGender(String gen) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(GENDER, gen);
        spEditor.commit();
    }

    public String getGender() {
        return sharedPreferences.getString(GENDER, null);
    }

    public void setFirstTimeApp(String firstApp) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(FIRST_TIME_APP, firstApp);
        spEditor.commit();
    }

    public String getFirstTimeApp() {
        return sharedPreferences.getString(FIRST_TIME_APP, "");
    }

    public void setDeviceVolume(int volume) {
        spEditor = sharedPreferences.edit();
        spEditor.putInt(DEVICE_CURRENT_VOLUME, volume);
        spEditor.commit();
    }

    public int getDeviceVolume() {
        return sharedPreferences.getInt(DEVICE_CURRENT_VOLUME, 0);
    }

    public void deleteAll() {
        spEditor = sharedPreferences.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
