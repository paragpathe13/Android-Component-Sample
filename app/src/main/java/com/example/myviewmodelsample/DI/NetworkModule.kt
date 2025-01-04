package com.example.myviewmodelsample.DI

import com.example.myviewmodelsample.Network.RetrofitHelper.BASE_URL
import com.example.myviewmodelsample.Network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }


    @Singleton
    @Provides
    fun providesUserAPI(retrofit: Retrofit) : RetrofitService{
        return retrofit.create(RetrofitService::class.java)
    }
}