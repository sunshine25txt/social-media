package com.focusshield.app.service.accessibility

data class SelectorConfig(
    val packageName: String,
    val surfaceType: String, // REEL, SHORT, STORY, EXPLORE, INFINITE_FEED
    val resourceIds: List<String> = emptyList(),
    val textMatches: List<String> = emptyList(),
    val contentDescriptionMatches: List<String> = emptyList()
)
