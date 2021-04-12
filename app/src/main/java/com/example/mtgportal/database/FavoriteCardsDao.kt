package com.example.mtgportal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mtgportal.model.Card

@Dao
interface FavoriteCardsDao {
    @Query("SELECT * FROM card")
    suspend fun getAll(): List<Card>

    @Insert
    suspend fun insert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}