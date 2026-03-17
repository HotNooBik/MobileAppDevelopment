package com.pw2.stolbokapp

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileHistoryAddBottomSheet : BottomSheetDialogFragment() {

    private lateinit var db: AppDatabase
    private var selectedDateMillis: Long = MaterialDatePicker.todayInUtcMilliseconds()
    private val selectedPeakIds = mutableSetOf<Int>()
    private val selectedPhotoUris = mutableListOf<String>()

    // Pick image launcher
    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            // Take persistable permission if possible (not strictly needed for transient intent but good practice)
            // But with GetMultipleContents, we get temporary access usually.
            // Saving URI string to DB means we expect it to persist.
            // For real apps, copying the file to app storage is better.
            // For this assignment, we use the URI directly.
            uris.forEach { uri ->
                try {
                   requireContext().contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {
                    // Ignore if not supported
                }
                selectedPhotoUris.add(uri.toString())
            }
            updatePhotoList()
        }
    }

    private lateinit var photosContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        // Initialize DB
        db = AppDatabase.getDatabase(requireContext())
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
        updateDateLabel(dateInput, selectedDateMillis)

        // Контейнер-кнопка выбора даты — открывает DatePicker по нажатию
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

        // --- Save Button ---
        view.findViewById<View>(R.id.sign_in_button).setOnClickListener {
            saveHike(view)
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

        imageView.setImageURI(Uri.parse(uriString))

        // Optional: click to remove?
        card.setOnClickListener {
             selectedPhotoUris.remove(uriString)
             updatePhotoList()
        }
        return card
    }

    // ...

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

    private fun saveHike(view: View) {
        val commentInput = view.getRootView().findViewById<EditText>(R.id.commentInput) // Adjust finding ID
        // view is the button. getRootView or find from fragment view.
        // Actually 'view' in onViewCreated is the fragment view.
        // Here 'view' is button.

        val comment = (this.view?.findViewById<EditText>(R.id.commentInput))?.text?.toString() ?: ""

        if (selectedPeakIds.isEmpty()) {
            Toast.makeText(context, "Выберите хотя бы один столб", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val hike = HikeEntity(
                date = selectedDateMillis,
                comment = comment
            )

            withContext(Dispatchers.IO) {
                db.hikeDao().addHike(
                    hike,
                    selectedPeakIds.toList(),
                    selectedPhotoUris
                )
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Поход добавлен!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
}
