package com.example.mtgportal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(_binding.root)
        setNavigation()
    }
    //endregion

    //region init
    private fun setNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        _binding.bottomNavBar.setupWithNavController(navController)
    }
    //endregion
}