package com.example.myviewmodelsample.Network

import com.example.myviewmodelsample.Data.AuthorList
import com.example.myviewmodelsample.Data.UserLoginRequest
import com.example.myviewmodelsample.Data.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {
    @GET("/users")
    suspend fun getAllAuthors(@Query("page")page : Int) : Response<AuthorList>

    @POST("/auth/login")
    suspend fun userLogin(@Body userLoginRequest: UserLoginRequest) : Response<UserLoginResponse>


}