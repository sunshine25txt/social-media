package com.focusshield.app.service.accessibility

sealed class DetectionResult {
    object Allowed : DetectionResult()
    data class Blocked(
        val surfaceType: String, // REEL, SHORT, STORY, EXPLORE, INFINITE_FEED, WEBSITE
        val appPackage: String,
        val reason: String
    ) : DetectionResult()
}
