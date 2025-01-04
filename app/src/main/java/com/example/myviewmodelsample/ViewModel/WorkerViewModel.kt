package com.example.myviewmodelsample.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery

class WorkerViewModel : ViewModel() {

    private val _categories = MutableLiveData<MutableList<String>>(mutableListOf())
    val categories: LiveData<MutableList<String>> get() = _categories

    fun addWorkerInList(category: String, context: Context) {
/*
        _categories.value?.add(category)
        _categories.value = _categories.value // Trigger LiveData update
*/


        val wq = WorkQuery.fromStates(listOf(
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.RUNNING,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED))

        //_categories.value?.clear()
        /*WorkManager.getInstance(context).getWorkInfosLiveData(wq).observe(this).forEach {
            _categories.value?.add(it.tags.last() +" - " +it.state)
            _categories.value = _categories.value // Trigger LiveData update
        }*/
    }

    fun getWorkerList() : List<String>{
        return _categories.value?.toList()?: emptyList()
    }


}