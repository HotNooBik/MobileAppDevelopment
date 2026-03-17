package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GuideDetailsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE_RES = "imageRes"

        fun newInstance(title: String, description: String, imageRes: Int): GuideDetailsBottomSheet {
            return GuideDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_IMAGE_RES, imageRes)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_guide_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE) ?: ""
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        val imageRes = arguments?.getInt(ARG_IMAGE_RES) ?: 0

        view.findViewById<TextView>(R.id.guideSheetTitle).text = title
        view.findViewById<TextView>(R.id.guideSheetDescription).text = description
        if (imageRes != 0) {
            view.findViewById<ImageView>(R.id.guideSheetImage).setImageResource(imageRes)
        }

        view.findViewById<ImageButton>(R.id.guideSheetClose).setOnClickListener {
            dismiss()
        }
    }
}

