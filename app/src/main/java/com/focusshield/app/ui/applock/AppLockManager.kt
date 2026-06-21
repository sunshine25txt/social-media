package com.focusshield.app.ui.applock

object AppLockManager {
    private val unlockedPackages = mutableMapOf<String, Long>()

    fun unlockPackage(packageName: String) {
        unlockedPackages[packageName] = System.currentTimeMillis()
    }

    fun isPackageUnlocked(packageName: String): Boolean {
        val lastUnlocked = unlockedPackages[packageName] ?: return false
        // Allow for 30 seconds bypass on initial entry
        return (System.currentTimeMillis() - lastUnlocked) < 30000
    }
    
    fun clearBypass() {
        unlockedPackages.clear()
    }
}
