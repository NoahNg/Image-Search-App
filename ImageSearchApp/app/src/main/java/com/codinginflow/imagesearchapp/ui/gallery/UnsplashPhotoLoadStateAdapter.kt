package com.codinginflow.imagesearchapp.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.imagesearchapp.databinding.UnsplashPhotoLoadStateFooterBinding

class UnsplashPhotoLoadStateAdapter(private val retry: () -> Unit) ://declare a function type, which means that later we will have to pass a function that doesn't take any argument and it returns unit (basically doesn't return anything)
    LoadStateAdapter<UnsplashPhotoLoadStateAdapter.LoadStateViewHolder>() {//the argument is the type of ViewHolder that we want to use

    //Ctrl+I
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = UnsplashPhotoLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        //parent is the recycle view that this item will be placed in. LayoutInflater needs this to get a context for it to work
        //we need this in the inflate method bc it uses this to calculate the appropriate view dimension for this item
        //attachToParent false means that we don't want to put this item into the Recycle View right now, instead, the adapter is responsible for doing that later

        return LoadStateViewHolder(binding)//return an instance of LoadStateViewHolder
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {//loadState: see if we're currently loading and decide which case we want to show progress bar, which case we want to show retry button
        holder.bind(loadState)
    }

    //create the ViewHolder
    inner class LoadStateViewHolder(private val binding: UnsplashPhotoLoadStateFooterBinding) ://make this an inner class so we can access the properties of the surrounding class (private) - adapter properties
        RecyclerView.ViewHolder(binding.root) {

        init {
            //do something when we click the retry button. If we put this in the binding, it will be repeated over and over again
            binding.buttonRetry.setOnClickListener {
                retry.invoke()//we later forward this function, which takes care of reloading the data
            }
        }

        //binding method to encapsulate loadState
        fun bind(loadState: LoadState) {
            //this adapter will only hold one item at a time: either a photo or a footer or none
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading//progress bar is visible when we're currently loading new item. If we're not loading but the header or footer is visible, then that means smth went wrong
                buttonRetry.isVisible = loadState !is LoadState.Loading//if loadState is not an instance of LoadState.loading
                textViewError.isVisible = loadState !is LoadState.Loading//same for textViewError
            }
        }
    }
}