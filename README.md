
##  ANDROID SMS READER

It is an exemple how to read SMS in android application.
`minAPI: 23`
`targetAPI: 30`

**This is just an example, understand how this works and implement it in your project in your ways**

### How to use
- You need to request the permission on the application level in the AndroidManifest

```xml=
<uses-permission android:name="android.permission.RECEIVE_SMS" />  
<uses-permission android:name="android.permission.READ_SMS" />  
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

- Also you need to request the permission to the user in your Activity or Fragment

```kotlin=
requestPermissions(  
  arrayOf(
	  Manifest.permission.RECEIVE_SMS, 
	  Manifest.permission.READ_SMS
  ),  
  readSmsRequestId  
)
requestPermissions(  
  arrayOf(Manifest.permission.READ_PHONE_STATE),  
  phoneStateRequestId  
)
```
- Create a broadcast which will receive the SMS messages
See `com\mukandrew\academy\smsreader\MessageReceiver.kt`

- After asking permission for the user and accepted, register the broadcast with follow const in the IntentFilter
```kotlin=
registerReceiver(  
  MessageReceiver(),  
  IntentFilter("android.provider.Telephony.SMS_RECEIVED")  
)
```
### How it works
In the `onReceive` of the Broadcast, you will receive an extra named `pdus`, that is a List of the ByteArray.
To read every ByteArray, you need to know what is the network the phone is connected. See types in the [docs](https://developer.android.com/reference/android/telephony/TelephonyManager) as const, `NETWORK_TYPE_N`.
**Just the type CDMA you need to worry**

- get pdu list
```kotlin=
val pdus = intent?.extras["pdus"] as Array<*>? ?: DoSomething
```

- get network type
```kotlin=
val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager  
val type = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {  
  manager.dataNetworkType  
} else {  
  manager.networkType  
}
```
**the `manager.networkType` is deprecated above API 23**

The `SmsMessage` native class of the Android, has an static function to read the SMS by ByteArray, but you need to send the format of the message, which to know it, you need to check the network type and send the correct const to the reader function.

- get format
```kotlin=
val format = when (type) {  
  TelephonyManager.NETWORK_TYPE_CDMA -> "3gpp2"  
  else -> "3gpp"  
}
```

- read message
```kotlin=
val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
```
- **remember, pdus is a list, so the `i` in the code above is the index**
- **PDUS is a list, because you can receive one or more SMS at the time**

After that, explore the props of the native class named [`SmsMessage`](https://developer.android.com/reference/android/telephony/SmsMessage) to get the message, number, time received, etc

- sender number: [`SmsMessage.displayOriginatingAddress`](https://developer.android.com/reference/android/telephony/SmsMessage#getDisplayOriginatingAddress())
- display body message: [`SmsMessage.displayMessageBody`](https://developer.android.com/reference/android/telephony/SmsMessage#getDisplayMessageBody())
- message: [`SmsMessage.messageBody`](https://developer.android.com/reference/android/telephony/SmsMessage#getMessageBody())
- timestamp: [`SmsMessage.timestampMillis`](https://developer.android.com/reference/android/telephony/SmsMessage#getTimestampMillis())