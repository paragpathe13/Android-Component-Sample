package com.example.myviewmodelsample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.example.myviewmodelsample.Adapter.WorkerListAdapter
import com.example.myviewmodelsample.ViewModel.WorkerViewModel
import com.example.myviewmodelsample.Worker.CompressWorker
import com.example.myviewmodelsample.Worker.DownloadWorker
import com.example.myviewmodelsample.Worker.NotificationWorker
import com.example.myviewmodelsample.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    //private var itemList = arrayListOf<String>()

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WorkerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //splash screen installation


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.Theme_MyViewModelSample)

        initData()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        viewModel.categories.observe(this, Observer { categories ->
            binding.workerRecyclerView.adapter = WorkerListAdapter(categories)
        })
        WorkManager.getInstance(this).getWorkInfosByTagLiveData("OneTimeWorkerScheduled").observe(this) { workInfos ->
            workInfos?.forEach { workInfo ->
                viewModel.categories.value?.add(workInfo.tags.last() +" - " +workInfo.state)
                //viewModel.categories.value.add(workInfo)
            }
        }
        }

    private fun initData() {
        binding.workerRecyclerView.layoutManager = LinearLayoutManager(this)

        //onclick listener
        binding.scheduleNotificationButton.setOnClickListener {
            scheduleOneTimeNotification()
        }

        binding.schedulePeriodicButton.setOnClickListener {
            schedulePeriodicNotification()
        }

        binding.stopOneByOneButton.setOnClickListener {
            //stopOneByOneWorker()
        }
        binding.stopAllButton.setOnClickListener {
            stopAlleWorker()
        }

        binding.scheduleChainedButton.setOnClickListener {
            scheduleChainedWorker()
        }
    }

   /* private fun stopOneByOneWorker() {
        if(viewModel.categories.value?.isNotEmpty() == true) {
            val lastItem = itemList.last()
            WorkManager.getInstance(this).cancelAllWorkByTag(lastItem)
            itemList.remove(lastItem)
            //binding.tvItemList.text = itemList.toString()
        }else{
            Toast.makeText(this, "No Worker Data!", Toast.LENGTH_LONG).show()
        }
    }*/

    private fun stopAlleWorker() {
        WorkManager.getInstance(this).pruneWork()

        val wq = WorkQuery.fromStates(listOf(
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED))


            /*val wq = WorkQuery.Builder()
                .addStates(listOf(
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.SUCCEEDED,
                    WorkInfo.State.FAILED,
                    WorkInfo.State.CANCELLED))
                .build()*/
       val latestData =  WorkManager.getInstance(this).getWorkInfosLiveData(wq)
        Log.d("testlog",latestData.toString())

    }

    private fun scheduleOneTimeNotification() {
        //setting input data for worker
        val inputData = Data.Builder()
            .putString("title", "One Time Worker")
            .putString("message", "This is your OneTime scheduled notification!")
            .build()

        // Create a one-time work request with a 1-hour delay
        val notificationWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInputData(inputData)
                .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(false).build())
                .addTag("OneTimeWorkerScheduled")
                .build()

        //itemList.add("OneTimeWorkerScheduled")
        //binding.tvItemList.text = itemList.toString()
        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
        //viewModel.addWorkerInList("OneTimeWorkerScheduled", this)

       /* val wq = WorkQuery.fromStates(listOf(
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED))

        val latestData =  WorkManager.getInstance(this).getWorkInfos(wq).get()
        Log.d("testlog",latestData.toString())*/

        binding.workerRecyclerView.adapter?.notifyDataSetChanged()

    }

    private fun schedulePeriodicNotification() {
        //setting input data for worker
        val inputData = Data.Builder()
            .putString("title", "Periodic Worker")
            .putString("message", "This is your Periodic scheduled notification!")
            .build()
        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInputData(inputData)
                .addTag("OneTimeWorkerScheduled")
                .build()

        //itemList.add("PeriodicWorkerScheduled")
        //binding.tvItemList.text = itemList.toString()

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
        //viewModel.addWorkerInList("PeriodicWorkerScheduled", this)

        /*val wq = WorkQuery.fromStates(listOf(
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED))

        val latestData =  WorkManager.getInstance(this).getWorkInfosLiveData(wq)
        Log.d("testlog",latestData.toString())*/

        binding.workerRecyclerView.adapter?.notifyDataSetChanged()

    }

    private fun scheduleChainedWorker() {
        val inputData = Data.Builder()
            .putString("title", "Chained Worker")
            .putString("message", "This is your Chained scheduled notification!")
            .build()

        // Create individual work requests
        val downloadWork = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag("OneTimeWorkerScheduled")
            .build()
        val compressWork = OneTimeWorkRequestBuilder<CompressWorker>()
            .addTag("OneTimeWorkerScheduled")
            .build()

        val notificationWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInputData(inputData)
                .addTag("OneTimeWorkerScheduled")
                .build()

        //itemList.add("ChainedWorkerScheduled")
        //binding.tvItemList.text = itemList.toString()

        // Enqueue the work request
        //WorkManager.getInstance(this).enqueue(notificationWorkRequest)
        WorkManager.getInstance(this)
            .beginWith(downloadWork)            // Start with download work
            .then(compressWork)                 // Follow with compression work
            .then(notificationWorkRequest)      // End with upload work
            .enqueue()                          // Enqueue the chain

        //viewModel.addWorkerInList("ChainedWorkerScheduled", this)

        /*val wq = WorkQuery.fromStates(listOf(
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED))

        val latestData =  WorkManager.getInstance(this).getWorkInfosLiveData(wq)
        Log.d("testlog",latestData.toString())*/
        binding.workerRecyclerView.adapter?.notifyDataSetChanged()

    }
}
