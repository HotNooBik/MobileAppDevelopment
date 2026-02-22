package com.pw2.stolbokapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exitBtn: ImageButton = view.findViewById(R.id.exitBtn)
        exitBtn.setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // При нажатии на карточку истории — открыть всплывающее окно
        view.findViewById<View>(R.id.historyItem).setOnClickListener {
            val sheet = HistoryDetailsBottomSheet()
            sheet.show(parentFragmentManager, "history_details")
        }
    }
}
