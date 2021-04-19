package com.example.mtgportal.utils

import android.content.Context

fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()
