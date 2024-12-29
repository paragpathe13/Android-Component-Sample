package com.example.myviewmodelsample

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myviewmodelsample.Worker.CompressWorker
import com.example.myviewmodelsample.Worker.DownloadWorker
import com.example.myviewmodelsample.Worker.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var btnOneTimeWorker: Button
    private lateinit var btnPeriodicWorker: Button
    private lateinit var btnChainedWorker: Button
    private lateinit var btnStopAllWorker: Button
    private lateinit var btnStopOneByOnelWorker: Button
    private lateinit var tvItemList: TextView
    private var itemList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //splash screen installation
        installSplashScreen()
        Thread.sleep(5000)
        setContentView(R.layout.activity_main)
        setTheme(R.style.Theme_MyViewModelSample)

        initData()

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

        //onclick listener
        btnOneTimeWorker.setOnClickListener {
            scheduleOneTimeNotification()
        }

        btnPeriodicWorker.setOnClickListener {
            schedulePeriodicNotification()
        }

        btnStopOneByOnelWorker.setOnClickListener {
            stopOneByOneWorker()
        }
        btnStopAllWorker.setOnClickListener {
            stopAlleWorker()
        }

        btnChainedWorker.setOnClickListener {
            scheduleChainedWorker()
        }
    }

    private fun stopOneByOneWorker() {
        if(itemList.isNotEmpty()) {
            val lastItem = itemList.last()
            WorkManager.getInstance(this).cancelAllWorkByTag(lastItem)
            itemList.remove(lastItem)
            tvItemList.text = itemList.toString()
        }else{
            Toast.makeText(this, "No Worker Data!", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopAlleWorker() {
        WorkManager.getInstance(this).pruneWork()
        tvItemList.text = itemList.toString()
    }

    private fun scheduleOneTimeNotification() {
        // Create a one-time work request with a 1-hour delay
        val notificationWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
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
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("PeriodicWorkerScheduled")
                .build()

        itemList.add("PeriodicWorkerScheduled")
        tvItemList.text = itemList.toString()

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }

    private fun scheduleChainedWorker() {
        // Create individual work requests
        val downloadWork = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag("ChainedWorkerScheduled")
            .build()
        val compressWork = OneTimeWorkRequestBuilder<CompressWorker>()
            .addTag("ChainedWorkerScheduled")
            .build()

        val notificationWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("ChainedWorkerScheduled")
                .build()

        itemList.add("ChainedWorkerScheduled")
        tvItemList.text = itemList.toString()

        // Enqueue the work request
        //WorkManager.getInstance(this).enqueue(notificationWorkRequest)
        WorkManager.getInstance(this)
            .beginWith(downloadWork)            // Start with download work
            .then(compressWork)                 // Follow with compression work
            .then(notificationWorkRequest)      // End with upload work
            .enqueue()                          // Enqueue the chain
    }
}
