package com.example.mtgportal.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.*
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.mtgportal.App
import com.example.mtgportal.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.IllegalStateException

class SimpleDialog : DialogFragment {

    private var _title: String? = null
    private var _message: String? = null
    private var _positiveBtnText: String = App.instance.getString(R.string.OK)
    private var _negativeBtnText: String? = null
    private var _positiveClickListener: OnClickListener = OnClickListener { dialog, _ -> dialog.dismiss() }
    private var _negativeClickListener: OnClickListener? = null
    private var _isDismissible: Boolean = true

    constructor(
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String,
        positiveBtnClickListener: OnClickListener,
        negativeBtnClickListener: OnClickListener,
        isDismissible: Boolean
    ) {
        _title = title
        _message = message
        _positiveBtnText = positiveBtnText
        _negativeBtnText = negativeBtnText
        _positiveClickListener = positiveBtnClickListener
        _negativeClickListener = negativeBtnClickListener
        _isDismissible = isDismissible
    }

    constructor(
        title: String,
        message: String,
    ) {
        _title = title
        _message = message
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(_title)
                .setMessage(_message)
                .setPositiveButton(_positiveBtnText, _positiveClickListener)
                .setNegativeButton(_negativeBtnText, _negativeClickListener)
                .setCancelable(_isDismissible)
                .create()
        } ?: throw IllegalStateException("Activity can not be null")
    }
}