package com.codinginflow.imagesearchapp.api

import com.codinginflow.imagesearchapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//interface bc Retrofit has already set, generate implementations for this interface later, we just have to declare some methods and methods don't have body
interface UnsplashApi {

    companion object {
        const val BASE_URL = "https://api.unsplash.com/" //contains the base URL that we want to make our request on
        const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY //this will be automatically generated, containing my API access key that I have generated earlier
    }
    //by putting constants into this companion object, we created a static variable

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")//the method data that this API expects and our access key to authorize ourselves in order to execute this request
    @GET("search/photos")//annotate HTTP GET request
    suspend fun searchPhotos(//handle threading with Kotlin coroutine: this function can be paused and resumed later on
        //Query parameters
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashResponse //the envelope that wraps the list of photos that we want to have
}