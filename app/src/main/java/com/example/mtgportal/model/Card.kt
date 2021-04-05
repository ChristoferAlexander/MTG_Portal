package com.example.mtgportal.model

data class Card(
    var id: String,
    var name: String,
    var text: String,
    var manaCost: String,
    var cmc: Int,
    var rarity: String,
    var power: String,
    var toughness: String,
    var type: String,
    var set: String,
    var artist: String,
    var imageUrl: String
)

