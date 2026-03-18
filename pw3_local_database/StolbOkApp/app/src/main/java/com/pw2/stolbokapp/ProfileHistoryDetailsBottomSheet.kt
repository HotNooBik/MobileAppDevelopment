package com.pw2.stolbokapp

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileHistoryDetailsBottomSheet : BottomSheetDialogFragment() {

    private var hikeId: Long = -1L
    private lateinit var db: AppDatabase

    companion object {
        private const val ARG_HIKE_ID = "hike_id"

        fun newInstance(hikeId: Long): ProfileHistoryDetailsBottomSheet {
            val fragment = ProfileHistoryDetailsBottomSheet()
            val args = Bundle()
            args.putLong(ARG_HIKE_ID, hikeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the theme to transparent background under the window so that the rounded corners work
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        hikeId = arguments?.getLong(ARG_HIKE_ID) ?: -1L
        db = AppDatabase.getDatabase(requireContext())
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

        // Clicking on the cross closes the window
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            dismiss()
        }

        if (hikeId != -1L) {
            loadHikeDetails(view)
        }

        view.findViewById<android.widget.Button>(R.id.btnDelete).setOnClickListener {
            showDeleteConfirmation()
        }

        view.findViewById<android.widget.Button>(R.id.btnEdit).setOnClickListener {
             dismiss()
             ProfileHistoryAddBottomSheet.newInstance(hikeId)
                 .show(parentFragmentManager, "history_edit")
        }
    }

    private fun showDeleteConfirmation() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удаление похода")
            .setMessage("Вы уверены, что хотите удалить этот поход?")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.hikeDao().deleteHikeById(hikeId)
                    }
                    dismiss()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun loadHikeDetails(view: View) {
        lifecycleScope.launch {
            val hikeWithDetails = withContext(Dispatchers.IO) {
                db.hikeDao().getHikeById(hikeId)
            }
            if (hikeWithDetails != null) {
                populateUI(view, hikeWithDetails)
            }
        }
    }

    private fun populateUI(view: View, data: HikeWithDetails) {
        val dateView = view.findViewById<TextView>(R.id.textView)
        val commentView = view.findViewById<TextView>(R.id.comment)
        val photosContainer = view.findViewById<LinearLayout>(R.id.detailsPhotosContainer)
        val peaksContainer = view.findViewById<LinearLayout>(R.id.detailsPeaksContainer)

        // Date
        val sdf = SimpleDateFormat("EEE, d MMMM, yyyy", Locale.forLanguageTag("ru"))
        val dateStr = sdf.format(Date(data.hike.date))
        dateView.text = dateStr.replaceFirstChar { it.uppercase() }

        // Comment
        commentView.text = data.hike.comment

        // Photos
        photosContainer.removeAllViews()
        val displayMetrics = resources.displayMetrics
        data.photos.forEach { photo ->

            val card = CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (260 * displayMetrics.density).toInt(),
                    (180 * displayMetrics.density).toInt()
                ).apply {
                    marginEnd = (8 * displayMetrics.density).toInt()
                }
                radius = 28 * displayMetrics.density
                cardElevation = 2 * displayMetrics.density
            }

            val imageView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            try {
                 imageView.setImageURI(Uri.parse(photo.uri))
            } catch (e: Exception) {
                imageView.setImageResource(android.R.color.darker_gray)
            }

            card.addView(imageView)
            photosContainer.addView(card)
        }

        // Peaks
        peaksContainer.removeAllViews()
        data.peaks.forEach { peak ->
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(
                    (16 * resources.displayMetrics.density).toInt(),
                    (10 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (10 * resources.displayMetrics.density).toInt()
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val arrow = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (20 * resources.displayMetrics.density).toInt(),
                    (20 * resources.displayMetrics.density).toInt()
                ).apply {
                    marginEnd = (12 * resources.displayMetrics.density).toInt()
                }
                setImageResource(R.drawable.ic_arrow_right)
                imageTintList = ColorStateList.valueOf(resources.getColor(R.color.brand_primary, null))
            }

            val text = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textSize = 16f
                setTextColor(resources.getColor(R.color.text_primary, null))
                this.text = peak.name
            }

            itemLayout.addView(arrow)
            itemLayout.addView(text)

            peaksContainer.addView(itemLayout)
        }
    }
}