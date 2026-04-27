package com.pw2.stolbokapp.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.local.HikeWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri

class ProfileHistoryAdapter(
    private val onItemClick: (HikeWithDetails) -> Unit
) : RecyclerView.Adapter<ProfileHistoryAdapter.HistoryViewHolder>() {

    private val items = mutableListOf<HikeWithDetails>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<HikeWithDetails>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class HistoryViewHolder(
        view: View,
        private val onItemClick: (HikeWithDetails) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val dateText = view.findViewById<TextView>(R.id.tvHistoryDate)
        private val imagePreview = view.findViewById<ImageView>(R.id.imgHistoryPreview)

        fun bind(hikeWithDetails: HikeWithDetails) {
            val hike = hikeWithDetails.hike
            val photos = hikeWithDetails.photos

            // Format Date
            val sdf = SimpleDateFormat("EEE, d MMMM, yyyy", Locale.forLanguageTag("ru"))
            val dateStr = sdf.format(Date(hike.date))
            // Capitalize first letter
            dateText.text = dateStr.replaceFirstChar { it.uppercase() }

            // Load first image if available
            if (photos.isNotEmpty()) {
                val uriStr = photos[0].uri
                try {
                    // Check if it's a resource URI (starts with android.resource://)
                    // or content/file URI. parse() handles both generally if structure is correct.
                    if (uriStr.startsWith("android.resource")) {
                         imagePreview.setImageURI(uriStr.toUri())
                    } else {
                         imagePreview.setImageURI(uriStr.toUri())
                    }
                } catch (_: Exception) {
                    imagePreview.setImageResource(android.R.color.darker_gray)
                }
            } else {
                imagePreview.setImageResource(android.R.color.transparent)
            }

            itemView.setOnClickListener { onItemClick(hikeWithDetails) }
        }
    }
}

