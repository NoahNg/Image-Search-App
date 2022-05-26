package com.codinginflow.imagesearchapp
//contains back button when we're in the details screen
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set up the navHostFragment because we can't put it directly in onCreate (from FragmentContainerView in activity_main.xml). Just calling navController will cause a crash
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        //reference to navController
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)//this will later connect our app bar to our nav graph
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    //handle up navigation
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()//this will navigate up in our nav graph and then return true to indicate that it has successfully handled the up button
        //if this return false, then it will call the default implementation of onSupportNavigateUp
    }
}