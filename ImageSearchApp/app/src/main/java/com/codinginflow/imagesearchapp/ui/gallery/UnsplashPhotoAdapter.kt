package com.codinginflow.imagesearchapp.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.data.UnsplashPhoto
import com.codinginflow.imagesearchapp.databinding.ItemUnsplashPhotoBinding
//PagingDataAdapter knows how to handle paging data
class UnsplashPhotoAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {//"PHOTO_COMPARATOR" is an item callback that knows how to calculate changes between 2 datasets when we later change our RecyclerView
    //, this makes our RV more efficient bc if the new and old list have similar items in them, then it's not necessary for the RV to recycle all items in the view -> just recall the items that have changed

    //2 types of arguments: the type of data we want to put in our RecyclerView items - UnsplashPhoto, the type of viewHolder (small class that holds references to the view inside the item layout
    //, so that it can be better recycled) that we want to use

    //Ctrl+I
    //Creating the view holder happens before binding the view holder logically
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {//where we have to inflate an item view layout
        //view binding -> create a binding object
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)//parent is the recycle view that this item will be placed in. LayoutInflater needs this to get a context for it to work
            //we need this in the inflate method bc it uses this to calculate the appropriate view dimension for this item
            //attachToParent false means that we don't want to put this item into the Recycle View right now, instead, the adapter is responsible for doing that later
        return PhotoViewHolder(binding)//return one of our PhotoViewHolder objects, where we call the binding variable bc we declared it as a constructor argument like in class PhotoViewHolder below
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {//we have to decide what piece of data from our unsplash photo object belongs into which view in the unsplash photo item layout
        val currentItem = getItem(position)//reference to the item at the current position

        //take the item and put its data into the correct place, right view, inside the item layout
        if (currentItem != null) {//getItem can return null so we want to check
            holder.bind(currentItem)//holder is what we get passed from this method, then forward the currentItem to bind it
        }
    }

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) ://"ItemUnsplashPhotoBinding" is automatically generated bc we're using view binding
        RecyclerView.ViewHolder(binding.root) {//super class: binding.root - inflated root layout/ the whole inflated "item_unsplash_photo" layout

        //setting onClickListener and we want to send this click to an underline fragment because we can't handle navigation inside an adapter so we need to create an interface
        init {
            binding.root.setOnClickListener {
                //first we need the position of this viewHolder to know which item we have to get out of the dataset
                val position = bindingAdapterPosition
                //before we handle the click, we have to check if position is not equal to -1 (click an item when it's animating off the screen = -1, and it will crash)
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)//reference to the click item
                    //getItem can return null, so we first have to check
                    if (item != null) {
                        listener.onItemClick(item)//call the onItemClick on this listener, where we forward the click item
                    }
                }
            }
        }

        //where we pass the current photo at this(currentItem) position
        fun bind(photo: UnsplashPhoto) {//now we can use the binding object to access our view
            binding.apply {
                //avoid writing the same thing over and over again (binding.imageView), binding.(textView), etc. We can access our views by their variable name directly
                //Our unsplashPhoto item has 2 views that we want to fill with data (refer to "item_unsplash_photo): ImageView which will contain the photo, and the text_view_user_name which displays the username of the img
                Glide.with(itemView)
                    .load(photo.urls.regular)//urls of the images we want to use in our imageView. Regular contains the regular url to the regular-sized image ("UnsplashPhoto.kt")
                    .centerCrop()//image is cropped and fit width and height of our layout to make it look better
                    .transition(DrawableTransitionOptions.withCrossFade())//cross-fade transition (belongs to glide)
                    .error(R.drawable.ic_error)//placeholder when we don't have internet connection
                    .into(imageView)//pass the imageView for dagger to load the photo from the url into the imageView

                textViewUserName.text = photo.user.username//to put this value into the correct textView
            }
        }
    }

    //declare an interface
    interface OnItemClickListener {
        fun onItemClick(photo: UnsplashPhoto)//one method to forward to click photo
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            //Ctrl+I
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =//2 photos represent the same logical item
                oldItem.id == newItem.id//use id to identify objects uniquely

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =//the RV knows when to refresh the layout of a particular item when the content of the item has changed
                oldItem == newItem//compare all the values/properties from the UnsplashPhoto class
        }
    }
}