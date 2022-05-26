package com.codinginflow.imagesearchapp.api
//UnplashResponse which contains our UnsplashPhoto object
//Envelope like in the json file
import com.codinginflow.imagesearchapp.data.UnsplashPhoto

data class UnsplashResponse(
    //Same as the json file and we only care about the results value
    val results: List<UnsplashPhoto>
)