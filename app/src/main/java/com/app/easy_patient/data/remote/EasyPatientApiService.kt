package com.app.easy_patient.data.remote

import com.app.easy_patient.database.MealPlanModel
import com.app.easy_patient.database.OrientationListModel
import com.app.easy_patient.database.PrescriptionListModel
import com.app.easy_patient.model.StatusModel
import com.app.easy_patient.model.kotlin.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface EasyPatientApiService {

    @Multipart
    @POST("patient/upload")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): UploadImage

    @DELETE("patient/picture/")
    suspend fun deleteProfilePicture(): Status

    @GET("appointments/")
    suspend fun getAppointments(): List<Appointment>

    @GET("audio/")
    suspend fun getAudios(): List<Audio>

    @GET("medicines/")
    suspend fun getMedicines(): List<Medicine>

    @GET("meal-plan/")
    suspend fun getMealPlan(): List<MealPlanModel>

    @GET("prescriptions/")
    suspend fun getPrescriptions(): List<PrescriptionListModel>

    @GET("orientations/")
    suspend fun getOrientations(): List<OrientationListModel>

    @GET("orientations/archive/")
    suspend fun getOrientationsArchive(): List<OrientationListModel>

    @GET("prescriptions/archive/")
    suspend fun getPrescriptionsArchive(): List<PrescriptionListModel>

    @GET("meal-plan/archive")
    suspend fun getMealPlanArchive(): List<MealPlanModel>

    @GET("audio/archive")
    suspend fun getArchiveAudios(): List<Audio>

    @PUT("audio/available/{id}")
    suspend fun unArchiveAudioDocument(@Path("id") id: Int): StatusModel

    @PUT("audio/archive/{id}")
    suspend fun archiveAudioDocument(@Path("id") id: Int): StatusModel

    @GET("views/")
    suspend fun getMenu(): MenuModel

    @PUT("{type}/view/{id}")
    suspend fun updateCount(@Path("type") type: String,@Path("id") id: Int): StatusModel

    @PUT("medicines/{id}")
    @Multipart
    suspend fun updateMedicines(
        @Path("id") id: Int,
        @Part("name") name: RequestBody?,
        @Part("dosage") dosage: RequestBody?,
        @Part("start_time") start_time: RequestBody?,
        @Part("number_of_days") number_of_days: RequestBody?,
        @Part("frequency") frequency: RequestBody?,
        @Part("days_of_the_week") days_of_the_week: RequestBody?,
        @Part("st_notification") st_notification: RequestBody?,
        @Part("st_critical") st_critical: RequestBody?,
        @Part file: MultipartBody.Part?,
        @Part("default_icon") default_icon: RequestBody?,
        @Part("current_picture_path") current_picture_path: RequestBody?,
        @Part("current_picture_link") current_picture_link: RequestBody?,
        @Part("medicine_schedules") medicine_schedule: RequestBody?
    ): StatusModel

    @POST("o/delete-account/")
    @FormUrlEncoded
    suspend fun deleteAccount(
        @Field("username") userName: String,
        @Field("password") password: String
    ): ResponseModel
}