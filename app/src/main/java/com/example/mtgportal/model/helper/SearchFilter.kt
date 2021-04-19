package com.example.mtgportal.model.helper

import com.example.mtgportal.model.definitions.MtgColors
import com.example.mtgportal.model.definitions.MtgRarities
import java.io.Serializable

data class SearchFilter(
    var searchQuery: String? = null,
    val colorFilters: MutableList<@MtgColors String> = mutableListOf(),
    val rarityFilters: MutableList<@MtgRarities String> = mutableListOf(),
    val setFilters: MutableList<String> = mutableListOf()
) : Serializable