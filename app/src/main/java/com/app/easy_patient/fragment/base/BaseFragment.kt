package com.app.easy_patient.fragment.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.util.LangContextWrapper.Companion.wrap
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment: Fragment() {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(wrap(context, "pt-rBR"))
    }
}