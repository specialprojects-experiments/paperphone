package com.withgoogle.experiments.unplugged.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.withgoogle.experiments.unplugged.PaperPhoneApp
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.AccountSelectionActivity
import com.withgoogle.experiments.unplugged.ui.HomeActivity
import com.withgoogle.experiments.unplugged.ui.NameSettingActivity
import com.withgoogle.experiments.unplugged.util.bindView

private const val NAME_REQUEST = 0x2
private const val ACCOUNT_REQUEST = 0x3

class OnBoardingActivity: AppCompatActivity() {

    private val mPager by bindView<ViewPager>(R.id.view_pager)

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    private val fragments = listOf(
        WelcomeFragment(),
        FoldingFragment(),
        SelectingFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        findViewById<Button>(R.id.next).setOnClickListener {
            if (mPager.currentItem == fragments.size - 1) {
                startActivityForResult(Intent(this@OnBoardingActivity, NameSettingActivity::class.java), NAME_REQUEST)
            } else {
                mPager.currentItem = mPager.currentItem + 1
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NAME_REQUEST && resultCode == Activity.RESULT_OK) {
            startActivityForResult(Intent(this, AccountSelectionActivity::class.java), ACCOUNT_REQUEST)
        } else if (requestCode == ACCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            PaperPhoneApp.obtain(this).onboardingPreference.set(true)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int): Fragment = fragments[position]
    }
}
