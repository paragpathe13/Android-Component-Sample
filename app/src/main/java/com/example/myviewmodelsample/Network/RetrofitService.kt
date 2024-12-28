package com.example.myviewmodelsample.Network

import com.example.myviewmodelsample.Data.AuthorList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("/users")
    suspend fun getAllAuthors(@Query("page")page : Int) : Response<AuthorList>
}