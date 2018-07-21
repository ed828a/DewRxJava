package com.dew.edward.dewrxjavamvvm.util

import android.app.Activity
import android.content.Context
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


/**
 * Created by Edward on 7/20/2018.
 */

fun String.extractDate(): String {
    val stringArray = this.split('T')

    return stringArray[0]
}

fun hideKeyboard(context: Context) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputManager.isAcceptingText) {
        inputManager.hideSoftInputFromWindow((context as AppCompatActivity).currentFocus.windowToken, 0)
//        (context as AppCompatActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
}
