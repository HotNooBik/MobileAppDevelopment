package com.pw2.stolbokapp.data.peaks

enum class Difficulty { EASY, MEDIUM, HARD }

data class PeakItem(
    val id: Int = 0,
    val name: String,
    val description: String,
    val difficulty: Difficulty,
    val height: String,
    val climbTime: String,
    val distanceFromPereval: String,
    val mapDistanceLabel: String,
    val lat: Double,
    val lng: Double,
    val imageRes1: Int = 0,
    val imageRes2: Int = 0,
    val imageRes3: Int = 0,
    val isVisited: Boolean = false
)

