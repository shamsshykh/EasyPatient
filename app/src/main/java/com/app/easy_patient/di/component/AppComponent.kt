package com.app.easy_patient.di.component

import android.app.Application
import com.app.easy_patient.MyApp
import com.app.easy_patient.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        PreferencesModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelsFactoryModule::class,
        ViewModelModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        DatabaseModule::class,
        WorkerFactoryModule::class,
        WorkerModule::class,
        BroadcastReceiverModule::class,
        ServiceModule::class,
        AnalyticsModule::class
    ]
)
interface AppComponent: AndroidInjector<MyApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(app: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(app: MyApp)
}