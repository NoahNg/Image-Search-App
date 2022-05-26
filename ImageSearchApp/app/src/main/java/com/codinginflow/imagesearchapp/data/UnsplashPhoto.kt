package com.codinginflow.imagesearchapp.data
//data is a class that you can implement some default methods, later will be useful when we create our RecyclerView bc we'll compare our objects
//and these comparison methods are already implemented in the data class
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
/*We have to implement the parcelable interface bc when we later in our app navigate to the details screen of an image
* we want to send this whole photo object over to the details screen, but you can only send specific type of data, either primitive type like strings and numbers, or parcelable type*/
data class UnsplashPhoto(
    //same as the json file
    val id: String,
    val description: String?,//nullable bc the json might not contain a description at all
    val urls: UnsplashPhotoUrls,//have to create another class for urls bc there are nested objects in the json file
    val user: UnsplashUser//same for user, bc it contains additional fields
) : Parcelable {

    //we create the 2 additional classes mentioned above in the same UnsplashPhoto because they are related logically
    @Parcelize
    data class UnsplashPhotoUrls(
        //same as the json file but we only use raw and full
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String,
    ) : Parcelable

    @Parcelize
    data class UnsplashUser(
        //same as the json file
        val name: String,
        val username: String
    ) : Parcelable {
        //this property belongs in the body bc it's a computed property -> generated dynamically from the username
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=ImageSearchApp&utm_medium=referral"
    }
}