package com.pw2.stolbokapp.ui.peaks

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.peaks.Difficulty
import com.pw2.stolbokapp.data.peaks.PeakItem
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class PeakDetailsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val START_LAT = 55.917827440941885
        private const val START_LNG = 92.7300190228174
        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_DIFFICULTY = "difficulty"
        private const val ARG_HEIGHT = "height"
        private const val ARG_TIME = "time"
        private const val ARG_DISTANCE = "distance"
        private const val ARG_MAP_DISTANCE = "mapDistance"
        private const val ARG_LAT = "lat"
        private const val ARG_LNG = "lng"
        private const val ARG_IMAGE_RES_1 = "imageRes1"
        private const val ARG_IMAGE_RES_2 = "imageRes2"
        private const val ARG_IMAGE_RES_3 = "imageRes3"

        fun newInstance(peak: PeakItem): PeakDetailsBottomSheet {
            return PeakDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, peak.name)
                    putString(ARG_DESCRIPTION, peak.description)
                    putInt(ARG_DIFFICULTY, peak.difficulty.ordinal)
                    putString(ARG_HEIGHT, peak.height)
                    putString(ARG_TIME, peak.climbTime)
                    putString(ARG_DISTANCE, peak.distanceFromPereval)
                    putString(ARG_MAP_DISTANCE, peak.mapDistanceLabel)
                    putDouble(ARG_LAT, peak.lat)
                    putDouble(ARG_LNG, peak.lng)
                    putInt(ARG_IMAGE_RES_1, peak.imageRes1)
                    putInt(ARG_IMAGE_RES_2, peak.imageRes2)
                    putInt(ARG_IMAGE_RES_3, peak.imageRes3)
                }
            }
        }
    }

    private var mapView: MapView? = null
    private var peakName: String = ""
    private var peakLat: Double = 0.0
    private var peakLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        Configuration.getInstance().userAgentValue = requireContext().packageName
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osm_prefs", 0)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_peak_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments ?: return
        peakName = args.getString(ARG_NAME, "")
        val description = args.getString(ARG_DESCRIPTION, "")
        val difficultyOrdinal = args.getInt(ARG_DIFFICULTY)
        val height = args.getString(ARG_HEIGHT, "")
        val time = args.getString(ARG_TIME, "")
        val distance = args.getString(ARG_DISTANCE, "")
        val mapDistance = args.getString(ARG_MAP_DISTANCE, "")
        peakLat = args.getDouble(ARG_LAT)
        peakLng = args.getDouble(ARG_LNG)
        val imageRes1 = args.getInt(ARG_IMAGE_RES_1)
        val imageRes2 = args.getInt(ARG_IMAGE_RES_2)
        val imageRes3 = args.getInt(ARG_IMAGE_RES_3)

        view.findViewById<TextView>(R.id.peakSheetTitle).text = peakName
        view.findViewById<TextView>(R.id.peakSheetDescription).text = description
        view.findViewById<TextView>(R.id.peakHeight).text = height
        view.findViewById<TextView>(R.id.peakTime).text = time
        view.findViewById<TextView>(R.id.peakDistance).text = distance
        view.findViewById<TextView>(R.id.peakMapDistance).text = mapDistance

        if (imageRes1 != 0) view.findViewById<ImageView>(R.id.peakPhoto1).setImageResource(imageRes1)
        if (imageRes2 != 0) view.findViewById<ImageView>(R.id.peakPhoto2).setImageResource(imageRes2)
        if (imageRes3 != 0) view.findViewById<ImageView>(R.id.peakPhoto3).setImageResource(imageRes3)

        val difficulty = Difficulty.entries[difficultyOrdinal]
        val difficultyBadge = view.findViewById<TextView>(R.id.peakDetailDifficulty)
        val (badgeText, badgeBg) = when (difficulty) {
            Difficulty.EASY -> "Простой" to R.drawable.bg_badge_easy
            Difficulty.MEDIUM -> "Средний" to R.drawable.bg_badge_medium
            Difficulty.HARD -> "Сложный" to R.drawable.bg_badge_hard
        }
        difficultyBadge.text = badgeText
        difficultyBadge.background = ContextCompat.getDrawable(requireContext(), badgeBg)

        view.findViewById<ImageButton>(R.id.peakSheetClose).setOnClickListener { dismiss() }

        mapView = view.findViewById(R.id.peakMapView)
        setupMap()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        mapView?.onDetach()
        mapView = null
        super.onDestroyView()
    }

    private fun setupMap() {
        val currentMapView = mapView ?: return
        val startPoint = GeoPoint(START_LAT, START_LNG)
        val destinationPoint = GeoPoint(peakLat, peakLng)
        val polylinePoints = listOf(startPoint, destinationPoint)

        currentMapView.setTileSource(TileSourceFactory.MAPNIK)
        currentMapView.setMultiTouchControls(true)
        currentMapView.isClickable = true
        currentMapView.isFocusable = true
        currentMapView.overlays.clear()

        currentMapView.overlays.add(
            createMarker(currentMapView, startPoint, "Старт", "Начальная точка маршрута")
        )
        currentMapView.overlays.add(
            createMarker(currentMapView, destinationPoint, peakName, "Точка назначения")
        )
        currentMapView.overlays.add(createRoutePolyline(polylinePoints))

        if (currentMapView.isLayoutOccurred) {
            applyInitialViewport(currentMapView, polylinePoints)
        } else {
            currentMapView.addOnFirstLayoutListener { _, _, _, _, _ ->
                if (mapView === currentMapView) {
                    applyInitialViewport(currentMapView, polylinePoints)
                }
            }
        }
    }

    private fun applyInitialViewport(currentMapView: MapView, points: List<GeoPoint>) {
        currentMapView.zoomToBoundingBox(buildBoundingBox(points), false, 64)
        currentMapView.invalidate()
    }

    private fun createMarker(
        currentMapView: MapView,
        position: GeoPoint,
        title: String,
        snippet: String
    ): Marker {
        return Marker(currentMapView).apply {
            this.position = position
            this.title = title
            this.snippet = snippet
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    private fun createRoutePolyline(points: List<GeoPoint>): Polyline {
        return Polyline().apply {
            setPoints(points)
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            outlinePaint.strokeWidth = 8f
            outlinePaint.style = Paint.Style.STROKE
        }
    }

    private fun buildBoundingBox(points: List<GeoPoint>): BoundingBox {
        val latitudes = points.map { it.latitude }
        val longitudes = points.map { it.longitude }

        return BoundingBox(
            latitudes.maxOrNull() ?: peakLat,
            longitudes.maxOrNull() ?: peakLng,
            latitudes.minOrNull() ?: peakLat,
            longitudes.minOrNull() ?: peakLng
        )
    }
}
