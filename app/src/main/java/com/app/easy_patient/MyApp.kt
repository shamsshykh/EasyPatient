package com.app.easy_patient

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.app.easy_patient.di.component.DaggerAppComponent
import com.app.easy_patient.util.FlipperHelper
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.paperdb.Paper
import javax.inject.Inject

class
MyApp : Application(), HasAndroidInjector, Configuration.Provider {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var workerFactory: WorkerFactory

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)

        DaggerAppComponent.builder()
            .create(this)
            .build()
            .inject(this)

        initFlipper()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun getWorkManagerConfiguration(): Configuration =
        // provide custom configuration
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    private fun initFlipper() {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {

            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(SharedPreferencesFlipperPlugin(this))
            client.addPlugin(CrashReporterPlugin.getInstance())
            client.addPlugin(FlipperHelper.networkPlugin)
            client.start()
        }
    }
}