package com.example.myviewmodelsample.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myviewmodelsample.Data.AuthorList
import com.example.myviewmodelsample.Network.RetrofitService

class AuthorRepository(private val retrofitService: RetrofitService) {

    private val authorListData = MutableLiveData<AuthorList>()

    val authorList : LiveData<AuthorList>
        get()= authorListData

    suspend fun getAuthors(page: Int) {
        val result = retrofitService.getAllAuthors(page)
        if (result?.body() != null){
            authorListData.postValue(result.body())
        }
    }
}