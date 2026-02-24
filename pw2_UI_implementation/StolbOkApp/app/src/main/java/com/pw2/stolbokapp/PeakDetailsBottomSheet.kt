package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PeakDetailsBottomSheet : BottomSheetDialogFragment(), OnMapReadyCallback {

    companion object {
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
    private var peakLat: Double = 0.0
    private var peakLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
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
        val name = args.getString(ARG_NAME, "")
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

        view.findViewById<TextView>(R.id.peakSheetTitle).text = name
        view.findViewById<TextView>(R.id.peakSheetDescription).text = description
        view.findViewById<TextView>(R.id.peakHeight).text = height
        view.findViewById<TextView>(R.id.peakTime).text = time
        view.findViewById<TextView>(R.id.peakDistance).text = distance
        view.findViewById<TextView>(R.id.peakMapDistance).text = mapDistance

        // Photos
        if (imageRes1 != 0) view.findViewById<ImageView>(R.id.peakPhoto1).setImageResource(imageRes1)
        if (imageRes2 != 0) view.findViewById<ImageView>(R.id.peakPhoto2).setImageResource(imageRes2)
        if (imageRes3 != 0) view.findViewById<ImageView>(R.id.peakPhoto3).setImageResource(imageRes3)

        // Difficulty badge
        val difficulty = Difficulty.entries[difficultyOrdinal]
        val difficultyBadge = view.findViewById<TextView>(R.id.peakDetailDifficulty)
        val (badgeText, badgeBg) = when (difficulty) {
            Difficulty.EASY -> "Простой" to R.drawable.bg_badge_easy
            Difficulty.MEDIUM -> "Средний" to R.drawable.bg_badge_medium
            Difficulty.HARD -> "Сложный" to R.drawable.bg_badge_hard
        }
        difficultyBadge.text = badgeText
        difficultyBadge.background = ContextCompat.getDrawable(requireContext(), badgeBg)

        // Close button
        view.findViewById<ImageButton>(R.id.peakSheetClose).setOnClickListener { dismiss() }

        // Map
        mapView = view.findViewById(R.id.peakMapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val location = LatLng(peakLat, peakLng)
        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
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
        mapView?.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
