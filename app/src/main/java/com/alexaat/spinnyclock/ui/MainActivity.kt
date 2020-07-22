package com.alexaat.spinnyclock.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.alexaat.spinnyclock.R
import com.alexaat.spinnyclock.databinding.ActivityMainBinding

private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding:ActivityMainBinding
private lateinit var drawerLayout: DrawerLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val navController = findNavController(R.id.nav_host)
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this,navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navDrawer,navController)
        navController.addOnDestinationChangedListener{nc,destination,bundle ->
            if(destination.id==nc.graph.startDestination){
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }else{
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }


        }

        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.mainActivityLayout.setBackgroundResource(R.drawable.spinny_clock_backgroung_landscape)
        } else if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.mainActivityLayout.setBackgroundResource(R.drawable.spinny_clock_backgroung_portrait)
        }






    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.mainActivityLayout.setBackgroundResource(R.drawable.spinny_clock_backgroung_landscape)
        } else if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.mainActivityLayout.setBackgroundResource(R.drawable.spinny_clock_backgroung_portrait)
        }

    }

}