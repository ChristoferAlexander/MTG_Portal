package com.example.mtgportal.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mtgportal.model.Card

@Database(entities = [Card::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCardsDao(): FavoriteCardsDao
}