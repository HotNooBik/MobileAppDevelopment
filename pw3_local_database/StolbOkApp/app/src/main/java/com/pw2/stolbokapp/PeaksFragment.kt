package com.pw2.stolbokapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeaksFragment : Fragment() {

    private lateinit var adapter: PeaksAdapter
    private val displayedPeaks = mutableListOf<PeakItem>()
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peaks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        // Setup adapter with empty list initially
        adapter = PeaksAdapter(displayedPeaks) { peak ->
            PeakDetailsBottomSheet.newInstance(peak)
                .show(childFragmentManager, "peak_details")
        }

        val recycler = view.findViewById<RecyclerView>(R.id.peaksRecyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Load data from DB
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Seed database if empty
                if (db.peakDao().getCount() == 0) {
                    val entities = PeaksRepository.getPeaksForSeeding()
                    db.peakDao().insertAll(entities)
                }
            }

            // Observe changes
            db.peakDao().getAllPeaks().collect { entities ->
                val items = entities.map { entity ->
                    PeakItem(
                        id = entity.peakId,
                        name = entity.name,
                        description = entity.description,
                        difficulty = entity.difficulty,
                        height = entity.height,
                        climbTime = entity.climbTime,
                        distanceFromPereval = entity.distanceFromPereval,
                        mapDistanceLabel = entity.mapDistanceLabel,
                        lat = entity.lat,
                        lng = entity.lng,
                        imageRes1 = entity.imageRes1,
                        imageRes2 = entity.imageRes2,
                        imageRes3 = entity.imageRes3,
                        isVisited = entity.isVisited
                    )
                }

                currentDbPeaks = items
                displayedPeaks.clear()
                displayedPeaks.addAll(items)
                adapter.notifyDataSetChanged()
            }
        }

        view.findViewById<EditText>(R.id.searchPeaks).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase()?.trim() ?: ""

                // Re-filter currently loaded list
                // Ideally this should query DB, but for small list, memory filter is fine
                updateListFromFilter(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private var currentDbPeaks: List<PeakItem> = emptyList()

    private fun updateListFromFilter(query: String) {
        val filtered = if (query.isEmpty()) currentDbPeaks
        else currentDbPeaks.filter { it.name.lowercase().contains(query) }

        displayedPeaks.clear()
        displayedPeaks.addAll(filtered)
        adapter.notifyDataSetChanged()
    }

    class PeaksAdapter(
        private val items: List<PeakItem>,
        private val onItemClick: (PeakItem) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_PEAK = 0
            private const val TYPE_PLACEHOLDER = 1
            private const val PLACEHOLDER_COUNT = 1
        }

        override fun getItemCount() = items.size + PLACEHOLDER_COUNT

        override fun getItemViewType(position: Int) =
            if (position < items.size) TYPE_PEAK else TYPE_PLACEHOLDER

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_PEAK) {
                PeakViewHolder(inflater.inflate(R.layout.item_peak_card, parent, false), onItemClick)
            } else {
                PlaceholderViewHolder(inflater.inflate(R.layout.item_peak_placeholder, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PeakViewHolder) holder.bind(items[position])
        }

        class PeakViewHolder(
            view: View,
            private val onItemClick: (PeakItem) -> Unit
        ) : RecyclerView.ViewHolder(view) {
            private val image = view.findViewById<ImageView>(R.id.peakImage)
            private val name = view.findViewById<TextView>(R.id.peakName)
            private val difficultyView = view.findViewById<TextView>(R.id.peakDifficulty)
            private val description = view.findViewById<TextView>(R.id.peakDescription)
            private val visited = view.findViewById<ImageView>(R.id.peakVisited)

            fun bind(peak: PeakItem) {
                name.text = peak.name
                description.text = peak.description
                if (peak.imageRes1 != 0) image.setImageResource(peak.imageRes1)

                val (text, bg) = when (peak.difficulty) {
                    Difficulty.EASY -> "Простой" to R.drawable.bg_badge_easy
                    Difficulty.MEDIUM -> "Средний" to R.drawable.bg_badge_medium
                    Difficulty.HARD -> "Сложный" to R.drawable.bg_badge_hard
                }
                difficultyView.text = text
                difficultyView.background = ContextCompat.getDrawable(itemView.context, bg)
                visited.visibility = if (peak.isVisited) View.VISIBLE else View.GONE
                itemView.setOnClickListener { onItemClick(peak) }
            }
        }

        class PlaceholderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
