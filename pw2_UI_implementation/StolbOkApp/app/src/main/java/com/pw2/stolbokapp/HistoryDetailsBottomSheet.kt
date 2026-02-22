package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HistoryDetailsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем тему: прозрачный фон под окном, чтобы работали скруглённые углы
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_history_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Нажатие на крестик — закрыть окно
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            dismiss()
        }
    }
}