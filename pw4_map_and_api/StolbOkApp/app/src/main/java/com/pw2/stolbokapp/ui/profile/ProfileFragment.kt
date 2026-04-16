package com.pw2.stolbokapp.ui.profile

import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.local.AppDatabase
import com.pw2.stolbokapp.data.local.HikeEntity
import com.pw2.stolbokapp.data.peaks.PeaksRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pw2.stolbokapp.ui.auth.AuthActivity
import com.pw2.stolbokapp.ui.calendar.CalendarFragment
import com.pw2.stolbokapp.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ProfileFragment : Fragment() {

    // Tracking the active tab
    private var isHistoryTabActive = true
    private lateinit var db: AppDatabase
    private lateinit var profileHistoryAdapter: ProfileHistoryAdapter
    private lateinit var profilePlansAdapter: ProfilePlansAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())
        profileHistoryAdapter = ProfileHistoryAdapter { hike ->
             ProfileHistoryDetailsBottomSheet.newInstance(hike.hike.hikeId)
                 .show(childFragmentManager, "history_details")
        }

        val rvHistory = view.findViewById<RecyclerView>(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = profileHistoryAdapter

        // --- Plans Setup ---
        profilePlansAdapter = ProfilePlansAdapter { plan, _ ->
            // Navigate to Calendar focused on this day
            val fragment = CalendarFragment()
            val args = Bundle()
            args.putInt("targetDay", plan.dayNumber)
            args.putInt("targetMonth", plan.month)
            args.putInt("targetYear", plan.year)
            fragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

            (requireActivity() as? MainActivity)?.updateMenuUI(R.id.nav_calendar)
        }

        val rvPlans = view.findViewById<RecyclerView>(R.id.rvPlans)
        rvPlans.layoutManager = LinearLayoutManager(requireContext())
        rvPlans.adapter = profilePlansAdapter

        // Seed Database
        seedDatabase()

        // Observe Data
        lifecycleScope.launch {
             db.hikeDao().getAllHikes().collect { hikes ->
                 profileHistoryAdapter.submitList(hikes)
             }
        }

        lifecycleScope.launch {
             db.planDao().getAllPlans().collect { plans ->
                 profilePlansAdapter.submitList(plans)
             }
        }

        // Exit button
        view.findViewById<ImageButton>(R.id.exitBtn).setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Tab elements
        val tabHistory = view.findViewById<LinearLayout>(R.id.tabHistory)
        val tabPlans = view.findViewById<LinearLayout>(R.id.tabPlans)
        val tabHistoryText = tabHistory.getChildAt(0) as TextView
        val tabHistoryIndicator = tabHistory.getChildAt(1) as View
        val tabPlansText = tabPlans.getChildAt(0) as TextView
        val tabPlansIndicator = tabPlans.getChildAt(1) as View

        // Tabs Content
        val historyContent = view.findViewById<LinearLayout>(R.id.historyContent)
        val plansContent = view.findViewById<LinearLayout>(R.id.plansContent)

        // FAB
        val fab = view.findViewById<FloatingActionButton>(R.id.addHistoryBtn)

        // Tab switching feature
        fun switchTab(toHistory: Boolean) {
            isHistoryTabActive = toHistory
            val activeColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_primary).let {
                android.graphics.Color.argb(128,
                    android.graphics.Color.red(it),
                    android.graphics.Color.green(it),
                    android.graphics.Color.blue(it))
            }

            if (toHistory) {
                // Activate the History tab
                tabHistoryText.setTextColor(activeColor)
                tabHistoryIndicator.setBackgroundColor(activeColor)
                tabPlansText.setTextColor(inactiveColor)
                tabPlansIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                historyContent.visibility = View.VISIBLE
                plansContent.visibility = View.GONE
            } else {
                // Activate the Scheduled tab
                tabPlansText.setTextColor(activeColor)
                tabPlansIndicator.setBackgroundColor(activeColor)
                tabHistoryText.setTextColor(inactiveColor)
                tabHistoryIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                historyContent.visibility = View.GONE
                plansContent.visibility = View.VISIBLE
            }
        }

        // Tab click handlers
        tabHistory.setOnClickListener { switchTab(true) }
        tabPlans.setOnClickListener { switchTab(false) }

        // FAB — opens the required window depending on the active tab
        fab.setOnClickListener {
            if (isHistoryTabActive) {
                ProfileHistoryAddBottomSheet().show(parentFragmentManager, "history_add")
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CalendarFragment())
                    .addToBackStack(null)
                    .commit()
                // Highlight the "Calendar" menu item in MainActivity
                (requireActivity() as? MainActivity)?.updateMenuUI(R.id.nav_calendar)
            }
        }

        // Set initial state (History active)
        switchTab(true)
    }

    private fun seedDatabase() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // 1. Seed Peaks if needed (less than expected count)
                if (db.peakDao().getCount() < 5) {
                    db.peakDao().deleteAll()

                    val peaksToSeed = PeaksRepository.getPeaksForSeeding()
                    db.peakDao().insertAll(peaksToSeed)
                }

                // 2. Seed History if empty
                if (db.hikeDao().getHikesCount() == 0) {
                    val allPeaks = db.peakDao().getAllPeaksList()
                    val targetNames = listOf("Второй Столб", "Четвертый Столб", "Львиные ворота", "Первый Столб")
                    val peakIds = allPeaks.filter { it.name in targetNames }.map { it.peakId }

                    val calendar = Calendar.getInstance()
                    calendar.set(2025, Calendar.APRIL, 4, 12, 0)

                    val hike = HikeEntity(
                        date = calendar.timeInMillis,
                        comment = "Это было весело. Наш первый поход на Столбы весной."
                    )

                    val packName = requireContext().packageName
                    val photos = listOf(
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packName + "/" + R.drawable.img_history_1,
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packName + "/" + R.drawable.img_history_2,
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packName + "/" + R.drawable.img_history_3
                    )

                    db.hikeDao().addHike(hike, peakIds, photos)
                }
            }
        }
    }
}
