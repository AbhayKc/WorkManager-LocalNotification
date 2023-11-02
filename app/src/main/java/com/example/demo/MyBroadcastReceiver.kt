//package com.example.demo
//
//import android.annotation.SuppressLint
//import android.app.Notification
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.graphics.drawable.BitmapDrawable
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.NotificationCompat
//import androidx.core.content.res.ResourcesCompat
//
//
//class MyBroadcastReceiver : BroadcastReceiver() {
//    @SuppressLint("UnsafeProtectedBroadcastReceiver")
//    override fun onReceive(context: Context, intent: Intent) {
//        val repeatingIntent = Intent(context, MainActivity::class.java)
//        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val resources = context.resources
//
//        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.reminder, null)
//         val bitmapDrawable = drawable as BitmapDrawable
//        val largeIcon = bitmapDrawable.bitmap
//
//        val pendingIntent = PendingIntent.getActivity(context, 0, repeatingIntent, PendingIntent.FLAG_IMMUTABLE)
//        val builder = NotificationCompat.Builder(context, "Notification")
//            .setContentIntent(pendingIntent)
//            .setLargeIcon(largeIcon)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Reminder")
//            .setContentText("It's Time To Drink Water")
//            .setAutoCancel(true)
//
//        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(200, builder.build())
//    }
//}
