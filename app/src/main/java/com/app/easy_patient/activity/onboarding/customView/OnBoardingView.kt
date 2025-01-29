package com.app.easy_patient.activity.onboarding.customView

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.app.easy_patient.R
import com.app.easy_patient.activity.LoginActivity
import com.app.easy_patient.activity.onboarding.OnBoardingActivity
import com.app.easy_patient.activity.onboarding.OnBoardingPagerAdapter
import com.app.easy_patient.activity.onboarding.entity.OnBoardingPage
import com.app.easy_patient.util.AppConstants
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import setParallaxTransformation

class OnBoardingView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val numberOfPages by lazy { OnBoardingPage.values().size }
    lateinit var wormDotsIndicator: WormDotsIndicator
    lateinit var slider: ViewPager2
    lateinit var startBtn: Button
    lateinit var skipPermission: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.onboarding_view, this, true)
        setUpSlider(view)
        moveToNextPage()
    }

    private fun setUpSlider(view: View) {
        slider = view.findViewById(R.id.slider)
        startBtn = view.findViewById(R.id.startBtn)
        skipPermission = view.findViewById(R.id.skipPermission)

        with(slider) {
            adapter = OnBoardingPagerAdapter()
            setPageTransformer { page, position ->
                setParallaxTransformation(page, position)
            }

            addSlideChangeListener()

            wormDotsIndicator = view.findViewById<WormDotsIndicator>(R.id.page_indicator)
            wormDotsIndicator.setViewPager2(this)
        }

        skipPermission.setOnClickListener {
            moveToLogin(view.context)
        }
    }


    private fun addSlideChangeListener() {

        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position < 3) {
                    startBtn.text = context.getString(R.string.forward_str)
                    wormDotsIndicator.visibility = VISIBLE
                    skipPermission.visibility = GONE
                    moveToNextPage()
                } else {
                    startBtn.text = context.getString(R.string.permission_str)
                    wormDotsIndicator.visibility = GONE
                    skipPermission.visibility = VISIBLE
                    askPermissions()
                }
            }
        })
    }

    private fun moveToNextPage() {
        startBtn.setOnClickListener {
            navigateToNextSlide()
        }
    }

    private fun askPermissions() {
        startBtn.setOnClickListener {
            requestPermissions()
        }
    }

    private fun moveToLogin(context: Context) {
        val activity = context as Activity
        val intent = Intent(activity, LoginActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    private fun navigateToNextSlide() {
        val nextSlidePos: Int = slider?.currentItem?.plus(1) ?: 0
        slider?.setCurrentItem(nextSlidePos, true)
    }

    @AfterPermissionGranted(AppConstants.REQUEST_CODE_KEYS.REQUEST_LOCATION_PERMISSION)
    fun requestPermissions() {
        val activity = context as OnBoardingActivity
        EasyPermissions.requestPermissions(
            activity,
            context.getString(R.string.permission_request_str),
            PERMISSION_REQUEST_CODE,
            *activity.perms
        )
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 10001
    }
}