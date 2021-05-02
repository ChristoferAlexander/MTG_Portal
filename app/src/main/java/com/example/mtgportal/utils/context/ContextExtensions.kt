package com.example.mtgportal.utils.context

import android.content.Context

fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()
