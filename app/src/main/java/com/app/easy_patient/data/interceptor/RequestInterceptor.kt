package com.app.easy_patient.data.interceptor

import com.app.easy_patient.util.Preferences
import com.app.easy_patient.util.SharedPrefs
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RequestInterceptor (val sharedPrefs: Preferences): Interceptor  {
    private val credentials = Credentials.basic("effectivesales_web_client", "8w?dx^pUEqb&mJy?!jAf#C%kN9!R2BZU")
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val requestBuilder = if (sharedPrefs.isUserLogin()) {
            original.newBuilder().addHeader("Authorization", "Bearer " + sharedPrefs.token())
        } else {
            original.newBuilder().addHeader("Authorization", credentials)
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }


}