package com.app.easy_patient.di.module

import com.app.easy_patient.data.interceptor.RequestInterceptor
import com.app.easy_patient.data.remote.EasyPatientApiService
import com.app.easy_patient.util.AppConstants
import com.app.easy_patient.util.Preferences
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun generateOkHttp(
        prefs: Preferences
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor(prefs))
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun generateRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun generateEasyPatientApiService(retrofit: Retrofit): EasyPatientApiService {
        return retrofit.create(EasyPatientApiService::class.java)
    }
}