package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAddBottomSheet : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_history_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка закрытия
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            dismiss()
        }

        // Поле отображения выбранной даты
        val dateInput = view.findViewById<TextView>(R.id.dateInput)

        // Контейнер-кнопка выбора даты — открывает DatePicker по нажатию
        view.findViewById<ConstraintLayout>(R.id.datePickerContainer).setOnClickListener {
            showDatePicker(dateInput)
        }
    }

    private fun showDatePicker(dateInput: TextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату похода")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedMillis ->
            // Форматируем дату в удобочитаемый вид: "Сб, 4 Апреля 2025"
            val sdf = SimpleDateFormat("EEE, d MMMM yyyy", Locale.forLanguageTag("ru"))
            val formatted = sdf.format(Date(selectedMillis))
            // Первую букву делаем заглавной (на случай если локаль вернёт строчную)
            dateInput.text = formatted.replaceFirstChar { it.uppercase() }
        }

        datePicker.show(childFragmentManager, "date_picker")
    }
}


