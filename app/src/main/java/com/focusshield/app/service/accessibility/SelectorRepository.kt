package com.focusshield.app.service.accessibility

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectorRepository @Inject constructor() {

    private val configs = listOf(
        // Instagram
        SelectorConfig(
            packageName = "com.instagram.android",
            surfaceType = "REEL",
            resourceIds = listOf(
                "com.instagram.android:id/reels_viewer_container",
                "com.instagram.android:id/reels_tab_layout",
                "com.instagram.android:id/reels_video_container"
            ),
            contentDescriptionMatches = listOf("Reels", "Reel")
        ),
        SelectorConfig(
            packageName = "com.instagram.android",
            surfaceType = "STORY",
            resourceIds = listOf(
                "com.instagram.android:id/reel_viewer_image_view",
                "com.instagram.android:id/story_item_container",
                "com.instagram.android:id/viewer_container"
            ),
            contentDescriptionMatches = listOf("Story by")
        ),
        SelectorConfig(
            packageName = "com.instagram.android",
            surfaceType = "EXPLORE",
            resourceIds = listOf(
                "com.instagram.android:id/explore_tab",
                "com.instagram.android:id/explore_grid"
            ),
            contentDescriptionMatches = listOf("Search and Explore", "Explore")
        ),
        
        // YouTube
        SelectorConfig(
            packageName = "com.google.android.youtube",
            surfaceType = "SHORT",
            resourceIds = listOf(
                "com.google.android.youtube:id/shorts_player",
                "com.google.android.youtube:id/shorts_video_player",
                "com.google.android.youtube:id/shorts_sheet_container"
            ),
            contentDescriptionMatches = listOf("Shorts player", "Shorts")
        ),
        
        // Facebook
        SelectorConfig(
            packageName = "com.facebook.katana",
            surfaceType = "REEL",
            resourceIds = listOf(
                "com.facebook.katana:id/reels_viewer_activity",
                "com.facebook.katana:id/reel_viewer_fragment",
                "com.facebook.katana:id/reels_video_view"
            ),
            textMatches = listOf("Reels", "Reels & short videos")
        )
    )

    fun getConfigsForPackage(packageName: String): List<SelectorConfig> {
        return configs.filter { it.packageName == packageName }
    }
}
