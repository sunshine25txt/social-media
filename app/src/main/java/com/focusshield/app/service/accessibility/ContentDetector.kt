package com.focusshield.app.service.accessibility

import android.view.accessibility.AccessibilityNodeInfo

interface ContentDetector {
    fun detect(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult
}
