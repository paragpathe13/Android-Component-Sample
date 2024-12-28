package com.example.myviewmodelsample

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myviewmodelsample.Worker.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var btnOneTimeWorker: Button
    private lateinit var btnPeriodicWorker: Button
    private lateinit var btnChainedWorker: Button
    private lateinit var btnStopAllWorker: Button
    private lateinit var btnStopOneByOnelWorker: Button
    private lateinit var tvItemList: TextView
    var itemList = arrayListOf<String>()
    var stringItemList = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(5000)
        setContentView(R.layout.activity_main)

        setTheme(R.style.Theme_MyViewModelSample)

        initData()
        btnOneTimeWorker.setOnClickListener {
            scheduleOneTimeNotification()
        }

        btnPeriodicWorker.setOnClickListener{
            schedulePeriodicNotification()
        }

        btnStopOneByOnelWorker.setOnClickListener {
           stopOneByOneWorker()
        }
        btnStopAllWorker.setOnClickListener {
            stopAlleWorker()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

    }

    private fun initData() {
        btnOneTimeWorker = findViewById(R.id.scheduleNotificationButton)
        btnPeriodicWorker = findViewById(R.id.schedulePeriodicButton)
        btnChainedWorker = findViewById(R.id.scheduleChainedButton)
        btnStopAllWorker = findViewById(R.id.stopAllButton)
        btnStopOneByOnelWorker = findViewById(R.id.stopOneByOneButton)
        tvItemList = findViewById(R.id.tvItemList)
    }

    private fun stopOneByOneWorker() {
        var lastItem = itemList.last()
        WorkManager.getInstance(this).cancelAllWorkByTag(lastItem)
        itemList.remove(lastItem)
    }

    private fun stopAlleWorker() {
        WorkManager.getInstance(this).pruneWork()
    }

    private fun scheduleOneTimeNotification() {
        // Create a one-time work request with a 1-hour delay
        val notificationWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(false).build())
                .addTag("OneTimeWorkerScheduled")
                .build()

        itemList.add("OneTimeWorkerScheduled")
        tvItemList.text = itemList.toString()
        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }

    private fun schedulePeriodicNotification() {
        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag("PeriodicWorkerScheduled")
                .build()

        itemList.add("PeriodicWorkerScheduled")
        tvItemList.text = itemList.toString()

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }
}
