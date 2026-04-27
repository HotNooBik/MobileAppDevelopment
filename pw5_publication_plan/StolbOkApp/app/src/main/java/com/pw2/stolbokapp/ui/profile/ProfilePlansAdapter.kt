package com.pw2.stolbokapp.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.calendar.CalendarDay
import com.pw2.stolbokapp.data.calendar.CalendarRepository
import com.pw2.stolbokapp.data.calendar.CalendarTextFormatter
import com.pw2.stolbokapp.data.calendar.DayStatus
import com.pw2.stolbokapp.data.local.PlanEntity

class ProfilePlansAdapter(
    private val onItemClick: (PlanEntity, CalendarDay?) -> Unit
) : RecyclerView.Adapter<ProfilePlansAdapter.PlanViewHolder>() {

    private val items = mutableListOf<PlanEntity>()

    @SuppressLint("NotifyDataSetChanged")
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

        @SuppressLint("SetTextI18n")
        fun bind(plan: PlanEntity) {
            val dayData = CalendarRepository.getDay(plan.month, plan.dayNumber, plan.year)

            if (dayData != null) {
                dateText.text = "${dayData.dayOfWeek}, ${plan.dayNumber} ${dayData.month} ${plan.year}"

                val (text, bg) = when (dayData.status) {
                    DayStatus.AWESOME -> CalendarTextFormatter.dayStatusText(DayStatus.AWESOME) to R.drawable.bg_badge_awesome
                    DayStatus.GOOD -> CalendarTextFormatter.dayStatusText(DayStatus.GOOD) to R.drawable.bg_badge_good
                    DayStatus.NOT_GOOD -> CalendarTextFormatter.dayStatusText(DayStatus.NOT_GOOD) to R.drawable.bg_badge_not_good
                    DayStatus.BAD -> CalendarTextFormatter.dayStatusText(DayStatus.BAD) to R.drawable.bg_badge_bad
                }
                statusText.text = text
                statusText.background = ContextCompat.getDrawable(itemView.context, bg)
                statusText.visibility = View.VISIBLE
            } else {
                val monthName = CalendarTextFormatter.monthNameGenitive(plan.month)
                dateText.text = "${plan.dayNumber} $monthName ${plan.year}"
                statusText.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(plan, dayData) }
        }
    }
}
