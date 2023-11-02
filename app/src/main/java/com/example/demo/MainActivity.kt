package com.example.demo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.demo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var pendingIntent: PendingIntent
    private lateinit var binding: ActivityMainBinding
    private var isNotificationEnabled = true
    private var intervalMinutes: Long = 0

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel(soundUri = null)
        pendingIntent = PendingIntent
            .getBroadcast(
                applicationContext,
                0,
                Intent(this, NotificationWorker::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

        binding.setTime.setOnClickListener {
            showIntervalDialog()
        }

        binding.toggle.setOnClickListener {
            if (isNotificationEnabled) {
                WorkManager.getInstance(this).cancelUniqueWork("notificationWorker")
                isNotificationEnabled = false
            } else {
                scheduleNotificationWorker(intervalMinutes)
                isNotificationEnabled = true
            }
        }
        binding.soundBtn.setOnClickListener {
            soundSelectionDialog()
        }
    }
    private val sharedPrefs by lazy {
        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    @SuppressLint("MissingInflatedId")
    private fun showIntervalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog, null)
        val intervalEditText = dialogView.findViewById<EditText>(R.id.editText)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Enter Time Interval")
            .setView(dialogView)
            .setPositiveButton("Set") { dialog, _ ->
                val intervalText = intervalEditText.text.toString()
                if (intervalText.isNotEmpty()) {
                    val intervalMinutes = intervalText.toLong()
                    scheduleNotificationWorker(intervalMinutes * 60 * 1000)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }

    private fun scheduleNotificationWorker(intervalMinutes: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val inputData = workDataOf("title" to "Reminder", "content" to "It's Time To Drink Water")

        val request = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            intervalMinutes,
            TimeUnit.MINUTES
        )
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag("notificationWorker")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notificationWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    private fun createNotificationChannel(soundUri: Uri?) {
        val name = "Reminder"
        val description = "It's Time To Drink Water"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel("Notification", name, importance)
        channel.description = description
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(soundUri,null)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }
    private fun setNotificationSound(selectedSound: String) {
        sharedPrefs.edit().putString("selectedSound", selectedSound).apply()
        val soundUri = getNotificationSoundUri(selectedSound)
        createNotificationChannel(soundUri)
    }
    private fun soundSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.sounddialog, null)
        val soundListView = dialogView.findViewById<ListView>(R.id.soundListView)
        val soundList = getNotificationSounds()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, soundList)
        soundListView.adapter = adapter

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Select Notification Sound")
            .setView(dialogView)
            .create()
        soundListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSound = soundList[position]
            setNotificationSound(selectedSound)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    private fun getNotificationSounds(): List<String> {
        val soundList = ArrayList<String>()

        val ringtoneManager = RingtoneManager(this)
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION)
        val cursor = ringtoneManager.cursor

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            soundList.add(title)
        }
        return soundList
    }
    private fun getNotificationSoundUri(selectedSound: String): Uri? {
        val ringtoneManager = RingtoneManager(this)
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION)
        val cursor = ringtoneManager.cursor

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            if (title == selectedSound) {
                return ringtoneManager.getRingtoneUri(cursor.position)
            }
        }
        return null
    }
}
