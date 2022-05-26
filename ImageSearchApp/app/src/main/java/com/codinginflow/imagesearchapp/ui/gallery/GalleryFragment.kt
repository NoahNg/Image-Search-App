package com.codinginflow.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.data.UnsplashPhoto
import com.codinginflow.imagesearchapp.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

//fragments and activities, for example: services, can't be constructed injected bc they don't have normal constructors in android
//instead we have to use this special annotation
@AndroidEntryPoint//injects the fields of this class instead, so when we later create a viewModel property in here,
//the annotation will take care of injecting the viewModel properly
class GalleryFragment : Fragment(R.layout.fragment_gallery), UnsplashPhotoAdapter.OnItemClickListener {//inflate the layout

    private val viewModel by viewModels<GalleryViewModel>()//this viewmodel will be injected by dagger, and the viewModel itself will have its repository injected and all dependencies we need further down the road

    private var _binding: FragmentGalleryBinding? = null//binding object to access our Recycler View -- nullable and assign it null initially
    /*when we use viewBinding in a fragment, we have to be careful bc the view of a fragment can be destroyed while the fragment instance itself
    * is still in memory. If this is the case, we have to null all our binding variables, otherwise it will keep an unnecessary reference to the
    * whole view hierarchy, which is a memory leak*/

    private val binding get() = _binding!!//override the getter: _binding!! means that we don't care if _binding is null, just return us not nullable type. If something goes wrong and _binding is null, it will
    //throw null point exception, this way we can use this binding variable w/o using the safe call operator all the time

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)//"view" is R.layout.fragment_gallery argument and it's an inflated layout that we defined in the fragment constructor

        val adapter = UnsplashPhotoAdapter(this)//instantiate our adapter

        //access our recyclerView
        binding.apply {
            recyclerView.setHasFixedSize(true)//width and height of the RecyclerView will not change depending on its content
            recyclerView.itemAnimator = null//disable animation for this RecyclerView completely. Fine for our app because we're not updating any items

            //connect 2 adapters
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                //forward the retry function
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },//adapter.retry() is a function from the paging adapter (UnsplashPhotoAdapter above) that knows how to retry the loading of another page
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
            )
            //Retry button
            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }

        //observe the viewModel photos live data
        viewModel.photos.observe(viewLifecycleOwner) {
            //it's important to passed the viewLifecycleOwner bc we want to stop updating our UI when the view of the fragment is destroyed and this can happen when the fragment instance is still in the mermory (put in backstack)

            //connect data to the adapter
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        //show the progress bar when we're loading new item, and retry button when the search result is empty
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading//display progress bar if the list is refreshing with new data set
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading//RecyclerView should be visible when loading has finished but there's no error
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error//retry button should be visible when something goes wrong (ex when there's no internet connection)
                textViewError.isVisible = loadState.source.refresh is LoadState.Error//same for text view error

                //empty view - We're not loading right now, but also there's no error
                if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&//there are no more results to be loaded afterwards
                        adapter.itemCount < 1) {//if the item count of RecyclerView is less than 1 (=0)
                            //=> we've reached the end of the result, and not loading anymore, but there are no item in the RecyclerView => no results to begin with
                    recyclerView.isVisible = false//set RecyclerView to invisible bc there's no data in this adapter
                    textViewEmpty.isVisible = true//text view empty to visible to show this error
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)//activate the options menu fragment - make it visible
    }

    //Function to handle the click on a photo
    override fun onItemClick(photo: UnsplashPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)//"photo" is forwarded from the adapter
        findNavController().navigate(action)//navigation to our fragment is set up
    }

    //toolbar for search
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_gallery, menu)//inflate the menu_gallery we just created/activate our menu_gallery as the menu for this fragment

        val searchItem = menu.findItem(R.id.action_search)
        //get a reference to the searchView
        val searchView = searchItem.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Ctrl+I
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    //if the new data set and old data set of the RecyclerView have similar items in them, it can happen that the RecyclerView scroll position stays at this item bc this is how DiffUtil works
                        //so we make sure that scroll stays at the top whenever we execute a new search
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(query)//function we created earlier in GalleryViewModel that changes the current query's value, and triggers the switch map, which executes the search -> change the photo live data
                    searchView.clearFocus()//hide the keyboard when we submit the search
                }
                return true//to indicate that we've handled the submit button
            }

            override fun onQueryTextChange(newText: String?): Boolean {//we don't want to do anything because we don't want to execute the search when we're still typing
                return true//means that we've handled the action properly and it shouldn'do anything else
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null//reference will be released when the view of the fragment is destroyed
    }
}