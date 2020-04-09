package com.zfr.ctfzoneclient.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

import com.zfr.ctfzoneclient.BuildConfig
import com.zfr.ctfzoneclient.network.interfaces.*
import java.util.concurrent.TimeUnit


class ControllerApi {

    val BASE_URL: String = BuildConfig.API_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient()
        .newBuilder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)

    fun retrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .client(client.build())
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit
    }

    fun getUserApi(): UserApi = retrofit().create(UserApi::class.java)

    fun getTaskApi(): TaskApi = retrofit().create(TaskApi::class.java)

    fun getSolutionApi(): SolutionApi = retrofit().create(SolutionApi::class.java)

    fun getAuthApi(): AuthApi = retrofit().create(AuthApi::class.java)

    fun getLoggingApi(): LogApi = retrofit().create(LogApi::class.java)
}