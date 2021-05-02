package com.example.mtgportal.utils.viewModel

import android.app.Activity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.mtgportal.App
import com.example.mtgportal.database.FavoriteCardsDao
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.favorite.FavoritesViewModel
import com.example.mtgportal.ui.home.HomeViewModel

class ViewModelFactory(_activity: Activity) :
    AbstractSavedStateViewModelFactory(_activity as SavedStateRegistryOwner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                modelClass.getConstructor(SavedStateHandle::class.java, CardRepository::class.java)
                    .newInstance(
                        handle,
                        CardRepository.getInstance(
                            App.instance.apiService,
                            App.instance.database.favoriteCardsDao()
                        )
                    )
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                modelClass.getConstructor(FavoriteCardsDao::class.java)
                    .newInstance(App.instance.database.favoriteCardsDao())
            }
            else -> throw IllegalArgumentException("Unknown view model class: ${modelClass.javaClass.canonicalName}")
        }
    }
}