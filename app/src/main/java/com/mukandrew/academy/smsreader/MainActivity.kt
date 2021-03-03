package com.mukandrew.academy.smsreader

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), MessageListener {
    private lateinit var textView: TextView
    private val readSmsRequestId = 20
    private val phoneStateRequestId = 21
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        requestPermissions(
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
            readSmsRequestId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == readSmsRequestId &&
            permissions.any { it == Manifest.permission.READ_SMS }
        ) {
            val index = permissions.indexOf(Manifest.permission.READ_SMS)
            if (grantResults[index] == PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    phoneStateRequestId
                )
            }
        } else if (
            requestCode == phoneStateRequestId &&
            permissions.any { it == Manifest.permission.READ_PHONE_STATE }
        ) {
            val index = permissions.indexOf(Manifest.permission.READ_PHONE_STATE)
            if (grantResults[index] == PERMISSION_GRANTED) {
                registerReceiver(
                    MessageReceiver(),
                    IntentFilter("android.provider.Telephony.SMS_RECEIVED")
                )
                MessageReceiver.bindListener(this)
            }
        }
    }

    override fun messageReceived(message: String) {
        textView.text = message
    }
}