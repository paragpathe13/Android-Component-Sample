package com.example.myviewmodelsample.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkerViewModel : ViewModel() {

    private val _categories = MutableLiveData<MutableList<String>>(mutableListOf())
    val categories: LiveData<MutableList<String>> get() = _categories

    fun addWorkerInList(category: String) {
        _categories.value?.add(category)
        _categories.value = _categories.value // Trigger LiveData update
    }

    fun getWorkerList() : List<String>{
        return _categories.value?.toList()?: emptyList()
    }


}