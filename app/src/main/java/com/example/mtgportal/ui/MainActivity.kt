package com.example.mtgportal.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mtgportal.R
import com.example.mtgportal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    //region declaration
    private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //endregion

    //region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = null
        setContentView(_binding.root)
        setNavigation()
    }
    //endregion

    //region init
    private fun setNavigation() {
        val navController = (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
        _binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> _binding.bottomNavView.visibility = VISIBLE
                R.id.favoritesFragment -> _binding.bottomNavView.visibility = VISIBLE
                R.id.informationFragment -> _binding.bottomNavView.visibility = VISIBLE
                else -> _binding.bottomNavView.visibility = GONE
            }
        }
    }
    //endregion
}