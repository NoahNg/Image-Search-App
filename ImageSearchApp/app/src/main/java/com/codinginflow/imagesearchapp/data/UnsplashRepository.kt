package com.codinginflow.imagesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.codinginflow.imagesearchapp.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

//Inject has the same effects as Provide - tell dagger how to create an UnsplashRepository later when we need it somewhere
//and when we instantiate the private val -> forward one of the unsplashAPI instances, which it gets from the module

//Using Inject because we own this class

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {//bc we want to forward only one value, we put it as private

    fun getSearchResults(query: String) =//later will be called by our ViewModel
        Pager(//pager will use our paging source to create paging data
            //arguments
            config = PagingConfig(
                //constructors
                pageSize = 20,//page size -> later will be passed as parameter loadSize value to our seachPhoto method
                maxSize = 100,//define at what number of item in Recycler View we want to start dropping item, otherwise it will keep scrolling down, the dataset in our RV will keep growing -> use lots of memory
                enablePlaceholders = false//paging library can display placeholder for objects that haven't been loaded yet, but we don't want to use that feature here
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }//create an instance of our UnsplashPagingSource(unsplashApi that has been injected by dagger into this repository, query = where we pasted
                                                                              //dynamically at runtime when we make a search request by calling getSearchResults function from the ViewModel
        ).liveData//turn this pager into a stream of paging data contained in liveData -> which later can be observed in our fragment to get live updates. This can be done by flow as well but we don't need flow here since it has many advanced features that we dont need
}//now we will have to call this method from our ViewModel bc VM holds data for the fragment with the right configuration changes