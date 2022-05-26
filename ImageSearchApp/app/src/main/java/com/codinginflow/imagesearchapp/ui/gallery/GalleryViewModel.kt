package com.codinginflow.imagesearchapp.ui.gallery
//put it in gallery package bc it belongs to the gallery fragment
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.codinginflow.imagesearchapp.data.UnsplashRepository

class GalleryViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository,//bc we already know that we have a private val of repository of type UnsplashRepository
    @Assisted state: SavedStateHandle//allows us to save pieces of data through process death and then restore them afterward. Have to add @Assisted to let dagger inject it
) : ViewModel() {

    //private val currentQuery = MutableLiveData(DEFAULT_QUERY)//live data value that contains current search query in form of a string. Mutable bc we want to change it later
    //we gave it a default value because we want to see something right when we open the app - in this case it's cats

    //We can't store huge amount of data so we can't so photo paging data here but we actually don't need this, all we need is the last search query, bc then we can recreate the search by running it again
    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)//load the last query from the last state through process death, and if there's no value yet we'll load the default_value cache

    val photos = currentQuery.switchMap { queryString ->//later can be observed in our fragment. switchMap's lambda parameter will be executed whenever the value of the current live query changes
        //whenever this happens, we get passed a parameter that contains the newer query in form of a string, so we can either reference that value as it, or we can give it a name: queryString in this case
        repository.getSearchResults(queryString).cachedIn(viewModelScope)//value is whatever we get returned from our search request/ new live data value when the query changes/new value of the currentQuery
        //cachedIn to avoid crashing when we rotate our device
    }

    //call this method from the fragment when we actually type smth into the search view, when this happens, we will forward the query as a string
    fun searchPhotos(query: String) {
        currentQuery.value = query//set the value of the current live data to the query string we send over
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "cats"//default search we see when we open the app
    }
}