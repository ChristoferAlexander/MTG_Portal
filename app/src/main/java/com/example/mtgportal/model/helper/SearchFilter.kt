package com.example.mtgportal.model.helper

import java.io.Serializable

data class SearchFilter(
    var searchQuery: String? = null,
    val colorFilters: MutableList<String> = mutableListOf(),
    val rarityFilters: MutableList<String> = mutableListOf(),
    val setFilters: MutableList<String> = mutableListOf()
) : Serializable