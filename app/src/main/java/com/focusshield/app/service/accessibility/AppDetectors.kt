package com.focusshield.app.service.accessibility

import android.view.accessibility.AccessibilityNodeInfo
import com.focusshield.app.data.local.entity.AppRuleEntity

class GenericAppDetector(
    private val selectorRepository: SelectorRepository
) : ContentDetector {

    override fun detect(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        // Retrieve selector configurations for the app
        val configs = selectorRepository.getConfigsForPackage(packageName)
        
        for (config in configs) {
            if (findMatchingNode(rootNode, config)) {
                return DetectionResult.Blocked(
                    surfaceType = config.surfaceType,
                    appPackage = packageName,
                    reason = "Matched selector pattern for ${config.surfaceType}"
                )
            }
        }
        return DetectionResult.Allowed
    }

    private fun findMatchingNode(node: AccessibilityNodeInfo?, config: SelectorConfig): Boolean {
        if (node == null) return false

        // Check Resource ID
        val nodeId = node.viewIdResourceName
        if (nodeId != null && config.resourceIds.contains(nodeId)) {
            return true
        }

        // Check Text Matches
        val nodeText = node.text?.toString()
        if (nodeText != null && config.textMatches.any { pattern -> nodeText.contains(pattern, ignoreCase = true) }) {
            return true
        }

        // Check Content Description Matches
        val nodeDesc = node.contentDescription?.toString()
        if (nodeDesc != null && config.contentDescriptionMatches.any { pattern -> nodeDesc.contains(pattern, ignoreCase = true) }) {
            return true
        }

        // Recurse down children
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (findMatchingNode(child, config)) {
                return true
            }
        }
        return false
    }
}

class TikTokDetector : ContentDetector {
    override fun detect(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        // TikTok is fully restricted under monitoring rules
        return DetectionResult.Blocked(
            surfaceType = "INFINITE_FEED",
            appPackage = packageName,
            reason = "TikTok complete app block active"
        )
    }
}

class SnackVideoDetector : ContentDetector {
    override fun detect(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        // SnackVideo is fully restricted under monitoring rules
        return DetectionResult.Blocked(
            surfaceType = "INFINITE_FEED",
            appPackage = packageName,
            reason = "SnackVideo complete app block active"
        )
    }
}

class BrowserDetector(
    private val blockedDomains: List<String>
) : ContentDetector {

    override fun detect(rootNode: AccessibilityNodeInfo, packageName: String): DetectionResult {
        val url = findBrowserUrl(rootNode) ?: return DetectionResult.Allowed
        
        // Clean URL to extract domain name
        val cleanUrl = cleanUrlString(url)
        
        for (domain in blockedDomains) {
            if (cleanUrl.contains(domain, ignoreCase = true)) {
                return DetectionResult.Blocked(
                    surfaceType = "WEBSITE",
                    appPackage = packageName,
                    reason = "Blocked website: $domain"
                )
            }
        }
        return DetectionResult.Allowed
    }

    private fun findBrowserUrl(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null
        
        val id = node.viewIdResourceName
        if (id != null && (id.endsWith("url_bar") || id.endsWith("location_bar_edit_text") || id.endsWith("url_bar_title") || id.contains("address_bar"))) {
            val text = node.text?.toString() ?: node.contentDescription?.toString()
            if (text != null && text.isNotBlank() && (text.contains(".") || text.contains("http"))) {
                return text
            }
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val url = findBrowserUrl(child)
            if (url != null) return url
        }
        return null
    }

    private fun cleanUrlString(url: String): String {
        var clean = url.trim().lowercase()
        if (clean.startsWith("http://")) clean = clean.substring(7)
        if (clean.startsWith("https://")) clean = clean.substring(8)
        if (clean.startsWith("www.")) clean = clean.substring(4)
        return clean
    }
}
