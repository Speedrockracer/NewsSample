package com.esvaru.newssample

import com.esvaru.newssample.models.ArticleResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines?country=us&apiKey=8f98ce82dd624e7ea0b84f34a164020f")
    fun getHeadLines(@Query("q") query: String?) : Observable<ArticleResult>
}

fun createNewsApiService() : NewsApiService {
    val gson: Gson = GsonBuilder()
            .create()

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    val okHttp = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()

    val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://newsapi.org/v2/")
            .build()

    return retrofit.create(NewsApiService::class.java)
}
