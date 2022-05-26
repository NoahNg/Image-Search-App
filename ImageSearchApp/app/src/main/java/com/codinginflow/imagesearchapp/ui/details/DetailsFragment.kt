package com.codinginflow.imagesearchapp.ui.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {//inflate the fragment

    private val args by navArgs<DetailsFragmentArgs>()//contains our navigation args. We can get our photo object out of this args property

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding object to access our view in a safe way
        val binding = FragmentDetailsBinding.bind(view)//view is the inflated layout (R.layout.fragment_details)

        binding.apply {
            //local variable for our photo argument
            val photo = args.photo

            //bind our photo to this layout
            //for image, we'll use glide
            /*when you are in a fragment or activity, you should paste this fragment or activity to a glide.with and not a view, bc a view is less efficient and has some problems
            //we use a view inside our adapter because we didnt have a reference to the fragment but here we have the fragment bc it's the containing class so we call Glide.with and pass this@fragment*/
            Glide.with(this@DetailsFragment)
                .load(photo.urls.full)//url of the image we want to load
                .error(R.drawable.ic_error)//error placeholder
                .listener(object : RequestListener<Drawable> {//parse a Drawable bc we want to load a Drawable into the imageView
                    //implement 2 methods Ctrl+I
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false//set the progress bar to invisible
                        return false//otherwise glide will not load the image
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false //set the progress bar to invisible
                        //set the next 2 to visible bc our image has finished loading
                        textViewCreator.isVisible = true
                        textViewDescription.isVisible = photo.description != null//some images don't have a description, and we will have a little gap if there's nothing in the description
                        return false//otherwise glide will not load the image
                    }
                })
                .into(imageView)//load image into imageView, which is the ImageView of our details_fragment

            //description of the photo
            textViewDescription.text = photo.description

            val uri = Uri.parse(photo.user.attributionUrl)//uri is the address of an image which then we can parse to an intent
            val intent = Intent(Intent.ACTION_VIEW, uri)
            /*when we create a uri and then parse it to an intent with ACTION_VIEW class and then start this intent, we let the system pick appropriate action
            *to view the content, and then the system will do the right thing with it to view this url, which will usually be opening a browser and opening the url in it*/

            //create another apply block for our textviewCreator
            textViewCreator.apply {
                text = "Photo by ${photo.user.name} on Unsplash"
                //make the "text" clickable
                setOnClickListener {
                    context.startActivity(intent) //as explained above, the system will display this in a browser
                }
                //underline the text to make it look like a link
                paint.isUnderlineText = true
            }
        }
    }
}