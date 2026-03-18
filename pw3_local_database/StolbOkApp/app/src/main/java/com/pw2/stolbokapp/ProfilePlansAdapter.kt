package com.pw2.stolbokapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ProfilePlansAdapter(
    private val onItemClick: (PlanEntity, CalendarDay?) -> Unit
) : RecyclerView.Adapter<ProfilePlansAdapter.PlanViewHolder>() {

    private val items = mutableListOf<PlanEntity>()

    fun submitList(newItems: List<PlanEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class PlanViewHolder(
        view: View,
        private val onItemClick: (PlanEntity, CalendarDay?) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val dateText = view.findViewById<TextView>(R.id.tvPlanDate)
        private val statusText = view.findViewById<TextView>(R.id.tvPlanStatus)

        fun bind(plan: PlanEntity) {
            // Try to find in repo
            val dayData = CalendarRepository.getDay(plan.month, plan.dayNumber)

            if (dayData != null) {
                // Found in hardcoded data
                dateText.text = "${dayData.dayOfWeek.replaceFirstChar {it.uppercase()}}, ${plan.dayNumber} ${dayData.month} ${plan.year}"

                val (text, bg) = when (dayData.status) {
                    DayStatus.AWESOME -> "Идеальный день" to R.drawable.bg_badge_awesome
                    DayStatus.GOOD -> "Хороший день" to R.drawable.bg_badge_good
                    DayStatus.NOT_GOOD -> "Плохой день" to R.drawable.bg_badge_not_good
                    DayStatus.BAD -> "Ужасный день" to R.drawable.bg_badge_bad
                }
                statusText.text = text
                statusText.background = ContextCompat.getDrawable(itemView.context, bg)
                statusText.visibility = View.VISIBLE
            } else {
                // Fallback custom formatting if not in repo (e.g. different month)
                val months = arrayOf("", "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря")
                val mName = if (plan.month in 1..12) months[plan.month] else plan.month.toString()
                dateText.text = "${plan.dayNumber} $mName ${plan.year}"

                statusText.text = "Статус неизвестен"
                statusText.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_badge_not_good) // Greyish fallback?
                statusText.visibility = View.GONE // Hide status if unknown
            }

            itemView.setOnClickListener { onItemClick(plan, dayData) }
        }
    }
}

