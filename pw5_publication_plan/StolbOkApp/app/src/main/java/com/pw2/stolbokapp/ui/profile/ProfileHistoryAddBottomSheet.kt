package com.pw2.stolbokapp.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.local.AppDatabase
import com.pw2.stolbokapp.data.local.HikeEntity
import com.pw2.stolbokapp.data.local.PeakEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri

class ProfileHistoryAddBottomSheet : BottomSheetDialogFragment() {

    private lateinit var db: AppDatabase
    private var selectedDateMillis: Long = MaterialDatePicker.todayInUtcMilliseconds()
    private val selectedPeakIds = mutableSetOf<Int>()
    private val selectedPhotoUris = mutableListOf<String>()
    private var hikeId: Long = -1L

    companion object {
        private const val ARG_HIKE_ID = "hike_id"
        fun newInstance(hikeId: Long = -1L): ProfileHistoryAddBottomSheet {
            val fragment = ProfileHistoryAddBottomSheet()
            if (hikeId != -1L) {
                val args = Bundle()
                args.putLong(ARG_HIKE_ID, hikeId)
                fragment.arguments = args
            }
            return fragment
        }
    }

    // Pick image launcher
    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                val savedUri = saveImageToInternalStorage(uri)
                if (savedUri != null) {
                    selectedPhotoUris.add(savedUri.toString())
                }
            }
            updatePhotoList()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val contentResolver = requireContext().contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "hike_photo_${System.currentTimeMillis()}_${(0..1000).random()}.jpg"
            val file = java.io.File(requireContext().filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private lateinit var photosContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        // Initialize DB
        db = AppDatabase.getDatabase(requireContext())

        // Get hike ID from arguments if available
        arguments?.getLong(ARG_HIKE_ID)?.let { id ->
            hikeId = id
        }
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

        // Close button
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            dismiss()
        }

        // Display field for the selected date
        val dateInput = view.findViewById<TextView>(R.id.dateInput)
        updateDateLabel(dateInput, selectedDateMillis)

        // Date Picker Container Button - Opens the DatePicker on click
        view.findViewById<ConstraintLayout>(R.id.datePickerContainer).setOnClickListener {
            showDatePicker(dateInput)
        }

        // --- Populate Peaks ---
        val peaksListContainer = view.findViewById<LinearLayout>(R.id.peaksList)
        val searchInput = view.findViewById<EditText>(R.id.peaksSearchInput)

        // Clear static children from XML except maybe dividers if needed, but let's clear all
        peaksListContainer.removeAllViews()

        loadPeaks(peaksListContainer)

        // Search listener
        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPeaks(peaksListContainer, s.toString())
            }
        })

        // --- Photos ---
        photosContainer = view.findViewById(R.id.photosContainer)
        updatePhotoList()

        // --- Buttons Setup ---
        val btnAdd = view.findViewById<View>(R.id.sign_in_button)
        val editContainer = view.findViewById<LinearLayout>(R.id.editButtonsContainer)
        val btnSave = view.findViewById<View>(R.id.btnSave)
        val btnCancel = view.findViewById<View>(R.id.btnCancel)

        if (hikeId != -1L) {
            // Edit Mode
            view.findViewById<TextView>(R.id.textView).text = "Редактировать поход"
            btnAdd.visibility = View.GONE
            editContainer.visibility = View.VISIBLE

            loadHikeDataForEdit(view)

            btnSave.setOnClickListener { saveHike() }
            btnCancel.setOnClickListener { dismiss() }
        } else {
            // Add Mode
            btnAdd.visibility = View.VISIBLE
            editContainer.visibility = View.GONE
            btnAdd.setOnClickListener { saveHike() }
        }
    }

    private var allPeaksForList: List<PeakEntity> = emptyList()

    private fun loadPeaks(container: LinearLayout) {
        lifecycleScope.launch {
            allPeaksForList = withContext(Dispatchers.IO) {
                db.peakDao().getAllPeaksList()
            }
            populatePeaksList(container, allPeaksForList)
        }
    }

    private fun populatePeaksList(container: LinearLayout, peaks: List<PeakEntity>) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(context)

        if (peaks.isEmpty()) {
            val emptyView = TextView(context).apply {
                text = "Нет столбов в базе"
                setPadding(30, 30, 30, 30)
            }
            container.addView(emptyView)
            return
        }

        peaks.forEach { peak ->
            val itemView = inflater.inflate(R.layout.item_peak_select, container, false)
            val nameView = itemView.findViewById<TextView>(R.id.peakName)
            val checkBox = itemView.findViewById<CheckBox>(R.id.peakCheckbox)

            nameView.text = peak.name
            checkBox.isChecked = selectedPeakIds.contains(peak.peakId)

            // Toggle on whole item click
            itemView.setOnClickListener {
                if (selectedPeakIds.contains(peak.peakId)) {
                    selectedPeakIds.remove(peak.peakId)
                    checkBox.isChecked = false
                } else {
                    selectedPeakIds.add(peak.peakId)
                    checkBox.isChecked = true
                }
            }

            // Also listener on checkbox just in case
            checkBox.setOnClickListener {
                 if (checkBox.isChecked) selectedPeakIds.add(peak.peakId)
                 else selectedPeakIds.remove(peak.peakId)
            }

            container.addView(itemView)

            // Add divider
            val divider = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
                setBackgroundColor(resources.getColor(R.color.stroke_dark, null))
            }
            container.addView(divider)
        }
    }

    private fun filterPeaks(container: LinearLayout, query: String) {
        val filtered = if (query.isEmpty()) allPeaksForList
                       else allPeaksForList.filter { it.name.contains(query, ignoreCase = true) }
        populatePeaksList(container, filtered)
    }

    private fun updatePhotoList() {
        if (!::photosContainer.isInitialized) {
             val container = view?.findViewById<LinearLayout>(R.id.photosContainer)
             if (container != null) {
                 photosContainer = container
             } else {
                 return
             }
        }

        photosContainer.removeAllViews()

        // Add "Add Photo" button
        val addBtnView = createAddPhotoItem()
        photosContainer.addView(addBtnView)

        // Add selected photos
        selectedPhotoUris.forEach { uriString ->
             val photoView = createPhotoItem(uriString)
             photosContainer.addView(photoView)
        }
    }

    private fun createAddPhotoItem(): View {
         val card = LayoutInflater.from(context).inflate(R.layout.item_photo_card, photosContainer, false) as CardView
         val imageView = card.findViewById<ImageView>(R.id.photoImage)
         imageView.setImageResource(R.drawable.img_add_photo) // ensure correct drawable

         card.setOnClickListener {
             pickImagesLauncher.launch("image/*")
         }
         return card
    }

    private fun createPhotoItem(uriString: String): View {
        val card = LayoutInflater.from(context).inflate(R.layout.item_photo_card, photosContainer, false) as CardView
        val imageView = card.findViewById<ImageView>(R.id.photoImage)

        try {
            imageView.setImageURI(uriString.toUri())
        } catch (_: Exception) {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        card.setOnClickListener {
             selectedPhotoUris.remove(uriString)
             updatePhotoList()
        }
        return card
    }

    private fun showDatePicker(dateInput: TextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату похода")
            .setSelection(selectedDateMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedMillis ->
            selectedDateMillis = selectedMillis
            updateDateLabel(dateInput, selectedMillis)
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun updateDateLabel(dateInput: TextView, millis: Long) {
        val sdf = SimpleDateFormat("EEE, d MMMM yyyy", Locale.forLanguageTag("ru"))
        val formatted = sdf.format(Date(millis))
        dateInput.text = formatted.replaceFirstChar { it.uppercase() }
    }

    private fun loadHikeDataForEdit(view: View) {
        lifecycleScope.launch {
            val hikeDetails = withContext(Dispatchers.IO) {
                db.hikeDao().getHikeById(hikeId)
            }

            if (hikeDetails != null) {
                // Date
                selectedDateMillis = hikeDetails.hike.date
                updateDateLabel(view.findViewById(R.id.dateInput), selectedDateMillis)

                // Comment
                view.findViewById<EditText>(R.id.commentInput).setText(hikeDetails.hike.comment)

                // Peaks
                selectedPeakIds.clear()
                hikeDetails.peaks.forEach { selectedPeakIds.add(it.peakId) }
                // Re-populate peaks list to show selection
                populatePeaksList(view.findViewById(R.id.peaksList), allPeaksForList)

                // Photos
                selectedPhotoUris.clear()
                hikeDetails.photos.forEach { selectedPhotoUris.add(it.uri) }
                updatePhotoList()
            }
        }
    }

    private fun saveHike() {
        val commentInput = this.view?.findViewById<EditText>(R.id.commentInput)
        val comment = commentInput?.text?.toString() ?: ""

        if (selectedPeakIds.isEmpty()) {
            Toast.makeText(context, "Выберите хотя бы один столб", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val hike = HikeEntity(
                hikeId = if (hikeId != -1L) hikeId else 0,
                date = selectedDateMillis,
                comment = comment
            )

            withContext(Dispatchers.IO) {
                if (hikeId != -1L) {
                    db.hikeDao().updateHike(hike, selectedPeakIds.toList(), selectedPhotoUris)
                } else {
                    db.hikeDao().addHike(hike, selectedPeakIds.toList(), selectedPhotoUris)
                }
            }

            withContext(Dispatchers.Main) {
                val msg = if (hikeId != -1L) "Поход обновлен!" else "Поход добавлен!"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
}
