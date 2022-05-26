package com.codinginflow.imagesearchapp.data

import androidx.paging.PagingSource
import com.codinginflow.imagesearchapp.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1 //number of page that we want to start at.
//We dont put this const inside the companion object bc the starting page index is not related to the UnsplashPagingSource class

class UnsplashPagingSource(
    //declare properties
    private val unsplashApi: UnsplashApi,
    private val query: String//search query in form of a string - something we don't know at compile time, only at runtime
) : PagingSource<Int, UnsplashPhoto>() {//Int: page 1, 2, 3 etc. Second argument is the data that we want to use to fill the pages -> Photos

    //trigger the API request and turn the data into pages
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX//the current page that we're on.

        return try {//return bc we want to return a value of out this catch box
            //API call
        val response = unsplashApi.searchPhotos(query, position, params.loadSize)//from UnsplashApi.kt class: query string, position calculated above, the page size contained in the params argument in loadSize
        val photos = response.results//where the list of UnsplashPhoto objects are contained: UnsplashResponse.kt

            //if everything went well, we will return a LoadResult.Page, which represents one page of result in our RecyclerView
            //if there's a problem with our search request, we return a LoadResult error
            LoadResult.Page(
                //constructors
                data = photos,//data that we want to put into this page: the photos
                //then we tell this page how the number of the previous page and of the next page can be calculated so that the next time we load this card, the params key value above contains the correct position
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,//current page - 1, unless we're on the very first page, then we pass null
                nextKey = if (photos.isEmpty()) null else position + 1//position + 1, unless we reach the end of search result - when the photo list that we get back is empty
            )
        } catch (exception: IOException) {//thrown when there's no internet connection when we're trying to make a request
            LoadResult.Error(exception)
        } catch (exception: HttpException) {//will be thrown when we make a request but then there's something wrong in the server - not authorized to do the request, or forgot to paste the unsplash access key, or no data on the page
            LoadResult.Error(exception)
        }
    }
}