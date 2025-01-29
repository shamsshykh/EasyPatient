package com.app.easy_patient.network;

import com.app.easy_patient.database.MealPlanModel;
import com.app.easy_patient.database.OrientationListModel;
import com.app.easy_patient.database.PrescriptionListModel;
import com.app.easy_patient.model.AppointmentsListModel;
import com.app.easy_patient.model.LoginResponseModel;
import com.app.easy_patient.model.MedicinesResponseModel;
import com.app.easy_patient.model.ImageUploadModel;
import com.app.easy_patient.model.ResendCodeModel;
import com.app.easy_patient.model.ResetPasswordModel;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.model.TermsModel;
import com.app.easy_patient.model.UserDetailModel;
import com.app.easy_patient.model.UserRegistrationModel;
import com.app.easy_patient.model.ValidationModel;
import com.app.easy_patient.model.kotlin.Medicine;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GetDataService {
    @POST("o/token/")
    @FormUrlEncoded
    Call<LoginResponseModel> getTokenAccess(@Field("grant_type") String grant_type, @Field("username") String username, @Field("password") String password);

    @POST("o/token/")
    @FormUrlEncoded
    Call<LoginResponseModel> getRefreshToken(@Field("grant_type") String grant_type, @Field("refresh_token") String refresh_token);

    @POST("patient/activate/")
    @FormUrlEncoded
    Call<ValidationModel> activateAccount(@Field("email") String email, @Field("code") String code);

    @GET("patient/{patient}")
    Call<LoginResponseModel> getRegistered(@Path("patient") String patient);

    @PUT("orientations/archive/{id}")
    Call<StatusModel> postOrientationsArchive(@Path("id") int id);

    @PUT("prescriptions/archive/{id}")
    Call<StatusModel> postPrescriptionsArchive(@Path("id") int id);

    @PUT("orientations/available/{id}")
    Call<StatusModel> putOrientationsAvailable(@Path("id") int id);

    @PUT("prescriptions/available/{id}")
    Call<StatusModel> putPrescriptionsAvailable(@Path("id") int id);

    @FormUrlEncoded
    @POST("patient/")
    Call<UserRegistrationModel> postRegisterPatient(@Field("email") String email, @Field("fullname") String fullname, @Field("date_of_birth") String date_of_birth, @Field("gender") String gender, @Field("password") String password, @Field("device") String device);

    @GET("orientations/")
    Call<ArrayList<OrientationListModel>> getOrientations();

    @GET("orientations/archive/")
    Call<ArrayList<OrientationListModel>> getOrientationsArchive();

    @GET("prescriptions/archive/")
    Call<ArrayList<PrescriptionListModel>> getPrescriptionsArchive();

    @GET("medicines/")
    Call<ArrayList<Medicine>> getMedicines();

    @GET("prescriptions/")
    Call<ArrayList<PrescriptionListModel>> getPrescriptions();

    @GET("appointments/")
    Call<ArrayList<AppointmentsListModel>> getAppointments();

    @PUT("appointments/confirm/{id}")
    Call<StatusModel> confirmAppointment(@Path("id") String id);

    @PUT("appointments/reschedule/{id}")
    @FormUrlEncoded
    Call<StatusModel> rescheduleAppointment(@Path("id") int id, @Field("date") String date,
                                            @Field("time") String time);

    @PUT("appointments/cancel/{id}")
    Call<StatusModel> cancelAppointment(@Path("id") String id);

    @GET("patient/")
    Call<UserDetailModel> getUserDetail();

    @PUT("patient/")
    @FormUrlEncoded
    Call<StatusModel> updateUserDetail(@Field("fullname") String fullname,
                                       @Field("email") String email,
                                       @Field("date_of_birth") String date_of_birth,
                                       @Field("gender") String gender);

    @POST("medicines/")
    @Multipart
    Call<Medicine> postMedicines(@Part("name") RequestBody name,
                                               @Part("dosage") RequestBody dosage,
                                               @Part("start_time") RequestBody start_time,
                                               @Part("number_of_days") RequestBody number_of_days,
                                               @Part("frequency") RequestBody frequency,
                                               @Part("days_of_the_week") RequestBody days_of_the_week,
                                               @Part("st_notification") RequestBody st_notification,
                                               @Part("st_critical") RequestBody st_critical,
                                               @Part MultipartBody.Part file,
                                               @Part("default_icon") RequestBody default_icon);

    @PUT("medicines/{id}")
    @Multipart
    Call<StatusModel> updateMedicines(@Path("id") int id,
                                      @Part("name") RequestBody name,
                                      @Part("dosage") RequestBody dosage,
                                      @Part("start_time") RequestBody start_time,
                                      @Part("number_of_days") RequestBody number_of_days,
                                      @Part("frequency") RequestBody frequency,
                                      @Part("days_of_the_week") RequestBody days_of_the_week,
                                      @Part("st_notification") RequestBody st_notification,
                                      @Part("st_critical") RequestBody st_critical,
                                      @Part MultipartBody.Part file,
                                      @Part("default_icon") RequestBody default_icon,
                                      @Part("current_picture_path") RequestBody current_picture_path,
                                      @Part("current_picture_link") RequestBody current_picture_link);

    @DELETE("medicines/{id}")
    Call<StatusModel> deleteMedicine(@Path("id") int id);

    @Multipart
    @POST("patient/upload")
    Call<ImageUploadModel> uploadProfilePicture(@Part MultipartBody.Part file);

    @DELETE("patient/picture/")
    Call<StatusModel> deleteProfilePicture();

    @PUT("o/change-password/")
    @FormUrlEncoded
    Call<StatusModel> changePassword(@Field("new_password") String new_password);

    @GET("o/forgot-password/{username}")
    Call<StatusModel> forgotPassword(@Path("username") String username);

    @POST("o/reset-password/")
    @FormUrlEncoded
    Call<ResetPasswordModel> postResetPassword(@Field("username") String username,
                                               @Field("change_key") String change_key,
                                               @Field("new_password") String new_password);

//    @Multipart
//    @POST("medicines/upload/{id}")
//    Call<ImageUploadModel> uploadMedicinePicture(@Path("id") int id, @Part MultipartBody.Part file);

    @DELETE("medicines/picture/{id}")
    Call<StatusModel> deleteMedicinePicture(@Path("id") int id);

    @GET("terms")
    Call<TermsModel> terms();

    @GET("meal-plan/")
    Call<ArrayList<MealPlanModel>> getMealPlan();

    @GET("meal-plan/archive")
    Call<ArrayList<MealPlanModel>> getMealPlanArchive();

    @PUT("meal-plan/archive/{id}")
    Call<StatusModel> postMealPlanArchive(@Path("id") int id);

    @PUT("meal-plan/available/{id}")
    Call<StatusModel> putMealPlanAvailable(@Path("id") int id);

    @POST("patient/resend-code/")
    @FormUrlEncoded
    Call<ResendCodeModel> resendCode(@Field("email") String email);

    @POST("patient/verify-otp/")
    @FormUrlEncoded
    Call<ValidationModel> verifyOtp(@Field("email") String email, @Field("code") String code);
}
