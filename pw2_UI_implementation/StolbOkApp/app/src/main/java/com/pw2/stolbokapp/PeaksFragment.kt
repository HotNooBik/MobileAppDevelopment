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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PeaksFragment : Fragment() {

    private val allPeaks = listOf(
        PeakItem(
            name = "Первый Столб",
            description = "Крупнейший и самый популярный скальный массив заповедника встречается первым — отсюда и его название.",
            difficulty = Difficulty.MEDIUM,
            height = "87 м.",
            climbTime = "20 мин.",
            distanceFromPereval = "700 м.",
            mapDistanceLabel = "От турбазы - 700 м",
            lat = 55.9010,
            lng = 92.7670,
            imageRes1 = R.drawable.img_peak_perviy,
            imageRes2 = R.drawable.img_peak_perviy_2,
            imageRes3 = R.drawable.img_peak_perviy_3,
            isVisited = true
        ),
        PeakItem(
            name = "Львиные ворота",
            description = "Величайшая иллюзия природы — два скальных останца образуют проход, напоминающий ворота.",
            difficulty = Difficulty.EASY,
            height = "40 м.",
            climbTime = "10 мин.",
            distanceFromPereval = "500 м.",
            mapDistanceLabel = "От турбазы - 400 м",
            lat = 55.8990,
            lng = 92.7640,
            imageRes1 = R.drawable.img_peak_lvinie_vorota,
            imageRes2 = R.drawable.img_peak_lvinie_vorota_2,
            imageRes3 = R.drawable.img_peak_lvinie_vorota_3,
            isVisited = true
        ),
        PeakItem(
            name = "Перья",
            description = "Необычная скала, одна из сложнейших для подъёма. Острые вертикальные грани требуют навыков скалолазания.",
            difficulty = Difficulty.HARD,
            height = "110 м.",
            climbTime = "45 мин.",
            distanceFromPereval = "1200 м.",
            mapDistanceLabel = "От турбазы - 1100 м",
            lat = 55.9050,
            lng = 92.7720,
            imageRes1 = R.drawable.img_peak_perya,
            imageRes2 = R.drawable.img_peak_perya_2,
            imageRes3 = R.drawable.img_peak_perya_3,
            isVisited = false
        )
    )

    private lateinit var adapter: PeaksAdapter
    private val displayedPeaks = mutableListOf<PeakItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peaks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayedPeaks.addAll(allPeaks)
        adapter = PeaksAdapter(displayedPeaks) { peak ->
            PeakDetailsBottomSheet.newInstance(peak)
                .show(childFragmentManager, "peak_details")
        }

        val recycler = view.findViewById<RecyclerView>(R.id.peaksRecyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<EditText>(R.id.searchPeaks).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase()?.trim() ?: ""
                displayedPeaks.clear()
                displayedPeaks.addAll(
                    if (query.isEmpty()) allPeaks
                    else allPeaks.filter { it.name.lowercase().contains(query) }
                )
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
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
