package com.example.demo

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @SuppressLint("SuspiciousIndentation")
    override fun doWork(): Result {
        Log.d("check","DoWork called success")
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //val drawable = ResourcesCompat.getDrawable(applicationContext.resources, R.drawable.reminder, null)
        // val bitmapDrawable = drawable as BitmapDrawable
        //val largeIcon = bitmapDrawable.bitmap
        //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+applicationContext.packageName+"/"+R.raw.ringtone)

        val builder = NotificationCompat.Builder(applicationContext, "Notification")
            .setContentText("It's Time To Drink Water")
            .setContentTitle("Reminder")
            .setPriority(NotificationCompat.PRIORITY_MAX )
            //.setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            // .setSound(sound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(applicationContext)){
            notify(200, builder.build())
        }

        return Result.success()
    }
}
