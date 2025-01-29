package com.app.easy_patient.di.module

import androidx.lifecycle.ViewModel
import com.app.easy_patient.activity.appointment_detail.AppointmentDetailViewModel
import com.app.easy_patient.activity.audio_detail.AudioDetailViewModel
import com.app.easy_patient.activity.change_profile.ChangeProfileViewModel
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.activity.onboarding.OnBoardingViewModel
import com.app.easy_patient.ui.audio_archive.ArchiveAudioViewModel
import com.app.easy_patient.ui.appointment.AppointmentViewModel
import com.app.easy_patient.ui.delete_account.DeleteAccountViewModel
import com.app.easy_patient.ui.medicine.MedicineViewModel
import com.app.easy_patient.ui.sidemenu.SideMenuViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

/**
 * The Dagger Module for creating ViewModels based on the key-value Dagger bindings.
 * @author Hamza Khan
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangeProfileViewModel::class)
    abstract fun bindChangeProfileViewModel(viewModel: ChangeProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnBoardingViewModel::class)
    abstract fun bindOnBoardingViewModel(viewModel: OnBoardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppointmentViewModel::class)
    abstract fun bindAppointmentViewModel(viewModel: AppointmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppointmentDetailViewModel::class)
    abstract fun bindAppointmentDetailViewModel(viewModel: AppointmentDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AudioDetailViewModel::class)
    abstract fun bindAudioDetailViewModel(viewModel: AudioDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SideMenuViewModel::class)
    abstract fun bindSideMenuViewModel(viewModel: SideMenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MedicineViewModel::class)
    abstract fun bindMedicineViewModel(viewModel: MedicineViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArchiveAudioViewModel::class)
    abstract fun bindArchiveAudioViewModel(viewModel: ArchiveAudioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeleteAccountViewModel::class)
    abstract fun bindDeleteAccountViewModel(viewModel: DeleteAccountViewModel): ViewModel

}