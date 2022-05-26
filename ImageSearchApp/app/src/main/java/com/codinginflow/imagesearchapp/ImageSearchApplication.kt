package com.codinginflow.imagesearchapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp//trigger code generation that this library needs
class ImageSearchApplication : Application() {
}