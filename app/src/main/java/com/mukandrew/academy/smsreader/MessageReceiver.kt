package com.mukandrew.academy.smsreader

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import java.text.DateFormat
import java.util.*

class MessageReceiver : BroadcastReceiver() {
    companion object {
        private var mListener: MessageListener? = null
        fun bindListener(listener: MessageListener) {
            mListener = listener
        }
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        val data = intent?.extras ?: return
        val pdus = data["pdus"] as Array<*>? ?: return
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val type = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            manager.dataNetworkType
        } else {
            manager.networkType
        }
        val format = when (type) {
            TelephonyManager.NETWORK_TYPE_CDMA -> "3gpp2"
            else -> "3gpp"
        }
        for (i in pdus.indices) {
            val smsMessage: SmsMessage =
                SmsMessage.createFromPdu(pdus[i] as ByteArray, format) ?: return
            val message = "Sender : " + smsMessage.displayOriginatingAddress?.toString() +
                    "\nDisplay message body: " + smsMessage.displayMessageBody?.toString() +
                    "\nTime in millisecond: " + DateFormat.getDateInstance()
                .format(Date(smsMessage.timestampMillis)) +
                    "\nMessage: " + smsMessage.messageBody?.toString() +
                    "\nPhoneFormat: $type $format"
            mListener?.messageReceived(message)
        }
    }
}

interface MessageListener {
    fun messageReceived(message: String)
}
