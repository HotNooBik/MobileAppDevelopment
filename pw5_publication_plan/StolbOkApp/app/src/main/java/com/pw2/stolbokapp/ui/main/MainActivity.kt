package com.pw2.stolbokapp.ui.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.ui.calendar.CalendarFragment
import com.pw2.stolbokapp.ui.guide.GuideFragment
import com.pw2.stolbokapp.ui.peaks.PeaksFragment
import com.pw2.stolbokapp.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applyWindowInsets()

        if (savedInstanceState == null) {
            loadFragment(CalendarFragment())
            updateMenuUI(R.id.nav_calendar)
        }

        setupNavigation()
    }

    private fun applyWindowInsets() {
        val bottomNavigationBar = findViewById<android.view.View>(R.id.bottom_navigation_bar)
        val initialBottomMargin =
            (bottomNavigationBar.layoutParams as? android.view.ViewGroup.MarginLayoutParams)
                ?.bottomMargin ?: 0

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationBar) { view, windowInsets ->
            val navigationInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updateLayoutParams<androidx.constraintlayout.widget.ConstraintLayout.LayoutParams> {
                bottomMargin = initialBottomMargin + navigationInsets.bottom
            }

            windowInsets
        }

        ViewCompat.requestApplyInsets(bottomNavigationBar)
    }

    private fun dismissBottomSheet() {
        (supportFragmentManager.findFragmentByTag("history_details") as? BottomSheetDialogFragment)
            ?.dismiss()
    }

    private fun setupNavigation() {
        val navCalendar = findViewById<LinearLayout>(R.id.nav_calendar)
        val navPeaks = findViewById<LinearLayout>(R.id.nav_peaks)
        val navGuide = findViewById<LinearLayout>(R.id.nav_guide)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navCalendar.setOnClickListener {
            dismissBottomSheet()
            loadFragment(CalendarFragment())
            updateMenuUI(R.id.nav_calendar)
        }

        navPeaks.setOnClickListener {
            dismissBottomSheet()
            loadFragment(PeaksFragment())
            updateMenuUI(R.id.nav_peaks)
        }

        navGuide.setOnClickListener {
            dismissBottomSheet()
            loadFragment(GuideFragment())
            updateMenuUI(R.id.nav_guide)
        }

        navProfile.setOnClickListener {
            dismissBottomSheet()
            loadFragment(ProfileFragment())
            updateMenuUI(R.id.nav_profile)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun updateMenuUI(selectedId: Int) {
        // Reset all icons and text colors to default
        resetMenuItem(R.id.icon_calendar, R.id.text_calendar, R.drawable.ic_menu_calendar_off)
        resetMenuItem(R.id.icon_peaks, R.id.text_peaks, R.drawable.ic_menu_peaks_off)
        resetMenuItem(R.id.icon_guide, R.id.text_guide, R.drawable.ic_menu_guide_off)
        resetMenuItem(R.id.icon_profile, R.id.text_profile, R.drawable.ic_menu_profile_off)

        // Highlight selected
        when (selectedId) {
            R.id.nav_calendar -> highlightMenuItem(R.id.icon_calendar, R.id.text_calendar, R.drawable.ic_menu_calendar_on)
            R.id.nav_peaks -> highlightMenuItem(R.id.icon_peaks, R.id.text_peaks, R.drawable.ic_menu_peaks_on)
            R.id.nav_guide -> highlightMenuItem(R.id.icon_guide, R.id.text_guide, R.drawable.ic_menu_guide_on)
            R.id.nav_profile -> highlightMenuItem(R.id.icon_profile, R.id.text_profile, R.drawable.ic_menu_profile_on)
        }
    }

    private fun resetMenuItem(iconId: Int, textId: Int, drawableId: Int) {
        findViewById<ImageView>(iconId).setImageResource(drawableId)
        val color = ContextCompat.getColor(this, R.color.text_primary)
        findViewById<TextView>(textId).setTextColor(color)
    }

    private fun highlightMenuItem(iconId: Int, textId: Int, drawableId: Int) {
        findViewById<ImageView>(iconId).setImageResource(drawableId)
        val color = ContextCompat.getColor(this, R.color.brand_primary)
        findViewById<TextView>(textId).setTextColor(color)
    }
}
