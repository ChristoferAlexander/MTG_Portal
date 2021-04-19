package com.example.mtgportal.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mtgportal.model.helper.FavorableItem
import java.io.Serializable

@Entity
data class Card(
    @PrimaryKey
    var id: String,
    var name: String,
    var text: String?,
    var manaCost: String,
    var cmc: Int,
    var rarity: String,
    var power: String?,
    var toughness: String?,
    var type: String,
    var set: String,
    var artist: String,
    var imageUrl: String?
) : FavorableItem(), Serializable

