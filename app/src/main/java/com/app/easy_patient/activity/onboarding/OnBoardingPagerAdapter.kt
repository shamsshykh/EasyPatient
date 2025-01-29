package com.app.easy_patient.activity.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.R
import com.app.easy_patient.activity.onboarding.entity.OnBoardingPage

class OnBoardingPagerAdapter(private val onBoardingPageList:Array<OnBoardingPage> = OnBoardingPage.values())
    : RecyclerView.Adapter<PagerViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PagerViewHolder {
        return LayoutInflater.from(parent.context).inflate(
            PagerViewHolder.LAYOUT, parent, false
        ).let { PagerViewHolder(it) }
    }

    override fun getItemCount() = onBoardingPageList.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(onBoardingPageList[position])
    }
}

class PagerViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    fun bind(onBoardingPage: OnBoardingPage) {
       val res = root.context.resources
        root.findViewById<TextView>(R.id.titleTv).text = res.getString(onBoardingPage.titleResource)
        root.findViewById<TextView>(R.id.descTV).text = res.getString(onBoardingPage.descriptionResource)
        root.findViewById<ImageView>(R.id.img).setImageResource(onBoardingPage.logoResource)
    }

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.onboarding_page_item
    }
}