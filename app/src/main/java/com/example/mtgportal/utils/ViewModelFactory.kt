package com.example.mtgportal.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mtgportal.api.ApiService
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.HomeViewModel

class ViewModelFactory(private val _apiService: ApiService): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->{
                modelClass.getConstructor(CardRepository::class.java)
                    .newInstance(CardRepository(_apiService))
            }
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.javaClass.canonicalName}")
        }
    }
}