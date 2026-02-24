package com.pw2.stolbokapp

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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfileFragment : Fragment() {

    // Отслеживаем активную вкладку
    private var isHistoryTabActive = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка выхода
        view.findViewById<ImageButton>(R.id.exitBtn).setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Открытие деталей истории
        view.findViewById<View>(R.id.historyItem).setOnClickListener {
            val sheet = HistoryDetailsBottomSheet()
            sheet.show(parentFragmentManager, "history_details")
        }

        // Элементы вкладок
        val tabHistory = view.findViewById<LinearLayout>(R.id.tabHistory)
        val tabPlans = view.findViewById<LinearLayout>(R.id.tabPlans)
        val tabHistoryText = tabHistory.getChildAt(0) as TextView
        val tabHistoryIndicator = tabHistory.getChildAt(1) as View
        val tabPlansText = tabPlans.getChildAt(0) as TextView
        val tabPlansIndicator = tabPlans.getChildAt(1) as View

        // Контент вкладок
        val historyContent = view.findViewById<LinearLayout>(R.id.historyContent)
        val plansContent = view.findViewById<LinearLayout>(R.id.plansContent)

        // FAB
        val fab = view.findViewById<FloatingActionButton>(R.id.addHistoryBtn)

        // Функция переключения вкладок
        fun switchTab(toHistory: Boolean) {
            isHistoryTabActive = toHistory
            val activeColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_primary).let {
                // 50% прозрачность
                android.graphics.Color.argb(128,
                    android.graphics.Color.red(it),
                    android.graphics.Color.green(it),
                    android.graphics.Color.blue(it))
            }

            if (toHistory) {
                // Активируем вкладку История
                tabHistoryText.setTextColor(activeColor)
                tabHistoryIndicator.setBackgroundColor(activeColor)
                tabPlansText.setTextColor(inactiveColor)
                tabPlansIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                historyContent.visibility = View.VISIBLE
                plansContent.visibility = View.GONE
            } else {
                // Активируем вкладку Запланировано
                tabPlansText.setTextColor(activeColor)
                tabPlansIndicator.setBackgroundColor(activeColor)
                tabHistoryText.setTextColor(inactiveColor)
                tabHistoryIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                historyContent.visibility = View.GONE
                plansContent.visibility = View.VISIBLE
            }
        }

        // Обработчики нажатий на вкладки
        tabHistory.setOnClickListener { switchTab(true) }
        tabPlans.setOnClickListener { switchTab(false) }

        // FAB — открывает нужное окно в зависимости от активной вкладки
        fab.setOnClickListener {
            if (isHistoryTabActive) {
                HistoryAddBottomSheet().show(parentFragmentManager, "history_add")
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CalendarFragment())
                    .addToBackStack(null)
                    .commit()
                // Подсветить пункт меню "Календарь" в MainActivity
                (requireActivity() as? MainActivity)?.updateMenuUI(R.id.nav_calendar)
            }
        }

        // Установить начальное состояние (История активна)
        switchTab(true)
    }
}
