package com.pw2.stolbokapp.ui.guide

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pw2.stolbokapp.R

class GuideFragment : Fragment() {

    // Data for each card
    private data class GuideItem(
        val title: String,
        val description: String,
        val imageRes: Int
    )

    private val clothingItem = GuideItem(
        title = "Кеды",
        description = "Лучшая обувь и для прогулки, и для того, чтобы лазать по Столбам. " +
                "Кеды плотно сидят на ноге, дают хорошее сцепление с камнем и не скользят на влажных поверхностях. " +
                "Рекомендуем выбирать модели с тонкой подошвой — они дают лучшее чувство опоры.",
        imageRes = R.drawable.guide_sneakers
    )

    private val safetyItem = GuideItem(
        title = "Средства от клещей",
        description = "На Столбах встречаются клещи, особенно активные в весенне-летний период. " +
                "Используйте репелленты с содержанием ДЭТА или перметрина. " +
                "После каждого похода осматривайте одежду и тело. " +
                "Рекомендуется также пройти вакцинацию от клещевого энцефалита.",
        imageRes = R.drawable.guide_anti_tick
    )

    private val seasonsItem = GuideItem(
        title = "Зима",
        description = "Зима на Столбах — особое время. Скалы покрываются инеем и снегом, " +
                "создавая неповторимые пейзажи. Важно одеваться многослойно, " +
                "использовать нескользящую обувь или специальные накладки на подошву. " +
                "Некоторые маршруты в зимнее время могут быть закрыты — уточняйте информацию заранее.",
        imageRes = R.drawable.guide_winter
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabClothing = view.findViewById<TextView>(R.id.tabClothing)
        val tabSafety = view.findViewById<TextView>(R.id.tabSafety)
        val tabSeasons = view.findViewById<TextView>(R.id.tabSeasons)
        val tabIndicator = view.findViewById<View>(R.id.tabIndicator)

        val contentClothing = view.findViewById<View>(R.id.contentClothing)
        val contentSafety = view.findViewById<View>(R.id.contentSafety)
        val contentSeasons = view.findViewById<View>(R.id.contentSeasons)

        // Setup card data
        setupCard(view.findViewById(R.id.clothingCard1), clothingItem)
        setupCard(view.findViewById(R.id.safetyCard1), safetyItem)
        setupCard(view.findViewById(R.id.seasonsCard1), seasonsItem)

        // Select tab helper
        fun selectTab(selectedTab: TextView, selectedContent: View) {
            // Reset all tabs
            listOf(tabClothing, tabSafety, tabSeasons).forEach {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                it.setTypeface(null, Typeface.NORMAL)
            }
            // Activate selected tab
            selectedTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
            selectedTab.setTypeface(null, Typeface.BOLD)

            // Show/hide content
            contentClothing.visibility = View.GONE
            contentSafety.visibility = View.GONE
            contentSeasons.visibility = View.GONE
            selectedContent.visibility = View.VISIBLE

            // Move indicator — align with selected tab
            selectedTab.post {
                val indicatorParams = tabIndicator.layoutParams
                indicatorParams.width = selectedTab.width
                tabIndicator.layoutParams = indicatorParams
                // selectedTab.left is relative to tabRow; tabRow.left is relative to parent LinearLayout
                val tabRowView = view.findViewById<View>(R.id.tabRow)
                tabIndicator.translationX = (tabRowView.left + selectedTab.left).toFloat()
            }
        }

        // Set click listeners
        tabClothing.setOnClickListener { selectTab(tabClothing, contentClothing) }
        tabSafety.setOnClickListener { selectTab(tabSafety, contentSafety) }
        tabSeasons.setOnClickListener { selectTab(tabSeasons, contentSeasons) }

        // Initial selection — wait for layout
        tabClothing.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                tabClothing.viewTreeObserver.removeOnGlobalLayoutListener(this)
                selectTab(tabClothing, contentClothing)
            }
        })
    }

    private fun setupCard(cardView: View, item: GuideItem) {
        cardView.findViewById<ImageView>(R.id.cardImage).setImageResource(item.imageRes)
        cardView.findViewById<TextView>(R.id.cardTitle).text = item.title
        cardView.findViewById<TextView>(R.id.cardDescription).text = item.description

        cardView.setOnClickListener {
            GuideDetailsBottomSheet.newInstance(item.title, item.description, item.imageRes)
                .show(childFragmentManager, "guide_details")
        }
    }
}
