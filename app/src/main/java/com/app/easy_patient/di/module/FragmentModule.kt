package com.app.easy_patient.di.module

import com.app.easy_patient.fragment.*
import com.app.easy_patient.ui.audio.AudioFragment
import com.app.easy_patient.ui.audio_archive.ArchiveAudioFragment
import com.app.easy_patient.ui.appointment.AppointmentFragment
import com.app.easy_patient.ui.delete_account.DeleteAccountFragment
import com.app.easy_patient.ui.home.HomeFragment
import com.app.easy_patient.ui.home.dialog.MedicineDialogFragment
import com.app.easy_patient.ui.medicine.MedicineFragment
import com.app.easy_patient.ui.sidemenu.SideMenuFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun bindHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun bindMedicineFragment(): MedicineFragment

    @ContributesAndroidInjector
    abstract fun bindMealFragment(): MealPlanFragment

    @ContributesAndroidInjector
    abstract fun bindPrescriptionsFragment(): PrescriptionsFragment

    @ContributesAndroidInjector
    abstract fun bindSideMenuFragment(): SideMenuFragment

    @ContributesAndroidInjector
    abstract fun bindAppointmentFragment(): AppointmentFragment

    @ContributesAndroidInjector
    abstract fun bindOrientationsFragment(): OrientationsFragment


    @ContributesAndroidInjector
    abstract fun bindDocumentFragment(): AudioFragment

    @ContributesAndroidInjector
    abstract fun bindArchiveAudioFragment(): ArchiveAudioFragment

    @ContributesAndroidInjector
    abstract fun bindMedicineDialogFragment(): MedicineDialogFragment


    @ContributesAndroidInjector
    abstract fun binMealPlanFragment(): ArchivedMealPlanFragment

    @ContributesAndroidInjector
    abstract fun binOrientationFragment(): ArchivedOrientationFragment

    @ContributesAndroidInjector
    abstract fun binPrescriptionsFragment(): ArchivePrescriptionsFragment

    @ContributesAndroidInjector
    abstract fun binSettingsFragment(): SettingsFragment


    @ContributesAndroidInjector
    abstract fun binProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun bindDeleteAccountFragment(): DeleteAccountFragment
}