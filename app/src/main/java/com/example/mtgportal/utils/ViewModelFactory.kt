package com.example.mtgportal.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.mtgportal.api.ApiService
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.home.HomeViewModel

class ViewModelFactory(
    _activity: Activity,
    private val _apiService: ApiService
) : AbstractSavedStateViewModelFactory(_activity as SavedStateRegistryOwner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                modelClass.getConstructor(SavedStateHandle::class.java, CardRepository::class.java)
                    .newInstance(handle, CardRepository.getInstance(_apiService))
            }
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.javaClass.canonicalName}")
        }
    }
}