package com.example.mtgportal.repository

import com.example.mtgportal.database.FavoriteCardsDao
import com.example.mtgportal.model.Card
import timber.log.Timber

class FavoritesCardsRepository private constructor(private val _favoriteCardsDao: FavoriteCardsDao) : BaseRepository() {

    suspend fun getFavorites(): List<Card> {
        Timber.i("Fetching favorites from database..")
        return _favoriteCardsDao.getAll()
    }

    suspend fun addFavorite(card: Card) {
        Timber.i("Card added to favorite")
        _favoriteCardsDao.insert(card)
    }

    suspend fun deleteFavorite(card: Card) {
        _favoriteCardsDao.delete(card)
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritesCardsRepository? = null

        fun getInstance(favoriteCardsDao: FavoriteCardsDao): FavoritesCardsRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesCardsRepository(favoriteCardsDao).also { INSTANCE = it }
            }
    }
}