package com.app.easy_patient.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.app.easy_patient.R


class DrawShadowFrameLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context!!, attrs, defStyleAttr) {
    private var mShadowDrawable: Drawable? = null
    private val mShadowElevation = 8
    private var mWidth = 0
    private var mHeight = 0
    private val mShadowVisible = true

    constructor(context: Context?) : this(context, null, 0) {}
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    private fun init() {
        mShadowDrawable = ContextCompat.getDrawable(context, R.drawable.shadow)
        if (mShadowDrawable != null) {
            mShadowDrawable!!.callback = this
        }
        setWillNotDraw(!mShadowVisible)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        updateShadowBounds()
    }

    private fun updateShadowBounds() {
        if (mShadowDrawable != null) {
            mShadowDrawable!!.setBounds(0, 0, mWidth, mShadowElevation)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (mShadowDrawable != null && mShadowVisible) {
            background.setBounds(0, mShadowDrawable!!.bounds.bottom, mWidth, mHeight)
            mShadowDrawable!!.draw(canvas)
        }
    }

    fun setShadowVisible(shadowVisible: Boolean) {
        setWillNotDraw(!mShadowVisible)
        updateShadowBounds()
    }

    val shadowElevation: Int
        get() = if (mShadowVisible) mShadowElevation else 0

    init {
        init()
    }
}