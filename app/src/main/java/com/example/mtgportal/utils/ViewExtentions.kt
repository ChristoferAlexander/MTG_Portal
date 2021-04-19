package com.example.mtgportal.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.setTitle(title: String?) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}