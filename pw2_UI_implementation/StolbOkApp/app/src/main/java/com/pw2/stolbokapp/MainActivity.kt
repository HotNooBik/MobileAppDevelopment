package com.pw2.stolbokapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // TODO: change to Calendar
        // Load default fragment (e.g., Profile)
        if (savedInstanceState == null) {
            loadFragment(ProfileFragment())
            updateMenuUI(R.id.nav_profile)
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navCalendar = findViewById<LinearLayout>(R.id.nav_calendar)
        val navPeaks = findViewById<LinearLayout>(R.id.nav_peaks)
        val navGuide = findViewById<LinearLayout>(R.id.nav_guide)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        navCalendar.setOnClickListener {
            // loadFragment(CalendarFragment()) // Create this fragment later
            updateMenuUI(R.id.nav_calendar)
        }

        navPeaks.setOnClickListener {
            // loadFragment(PeaksFragment()) // Create this fragment later
            updateMenuUI(R.id.nav_peaks)
        }

        navGuide.setOnClickListener {
            // loadFragment(GuideFragment()) // Create this fragment later
            updateMenuUI(R.id.nav_guide)
        }

        navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
            updateMenuUI(R.id.nav_profile)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateMenuUI(selectedId: Int) {
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