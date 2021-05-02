package com.example.mtgportal.ui.dialog

import com.example.mtgportal.App
import com.example.mtgportal.R

object DialogFactory {

    const val DIALOG_TAG_NETWORK_ERROR = "DialogNetworkError"
    fun createNetworkErrorDialog(): SimpleDialog = SimpleDialog(
        title = App.instance.getString(R.string.network_error_title),
        message = App.instance.getString(R.string.network_error_body)
    )

    const val DIALOG_TAG_ERROR = "DialogError"
    fun createErrorDialog(errorMessage: String?): SimpleDialog = SimpleDialog(
        title = App.instance.getString(R.string.unknown_error_title),
        message = errorMessage ?: "An error occurred, please try again alter"
    )
}