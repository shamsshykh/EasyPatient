package com.app.easy_patient.activity.onboarding.entity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.easy_patient.R

enum class OnBoardingPage(
    @StringRes val titleResource: Int,
    @StringRes val descriptionResource: Int,
    @DrawableRes val logoResource: Int
) {

    ONE(R.string.onboarding_slide1_title, R.string.onboarding_slide1_desc, R.drawable.ic_onboarding_1),
    TWO(R.string.onboarding_slide2_title, R.string.onboarding_slide2_desc, R.drawable.ic_onboarding_2),
    THREE(R.string.onboarding_slide3_title, R.string.onboarding_slide3_desc, R.drawable.ic_onboarding_3),
    FOUR(R.string.onboarding_slide4_title, R.string.onboarding_slide4_desc, R.drawable.ic_onboarding_4)

}