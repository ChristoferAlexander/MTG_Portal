package com.example.mtgportal.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mtgportal.R
import com.example.mtgportal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val _navController by lazy { (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController }

    //region declaration
    private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //endregion

    //region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        setNavigation()
    }

    override fun onResume() {
        super.onResume()
        _navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        _navController.removeOnDestinationChangedListener(listener)
    }

    override fun onSupportNavigateUp(): Boolean {
        return (_navController.navigateUp() || super.onSupportNavigateUp())
    }
    //endregion

    //region init
    private fun setNavigation() {
        NavigationUI.setupWithNavController(_binding.bottomNavView, _navController)
        NavigationUI.setupActionBarWithNavController(this, _navController)
    }
    //endregion

    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.cardDetailsFragment -> _binding.bottomNavView.visibility = GONE
            else -> _binding.bottomNavView.visibility = VISIBLE
        }
    }
}