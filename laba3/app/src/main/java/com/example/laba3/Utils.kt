package com.example.laba3

import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color

class Utils {
    companion object
    {
        fun showSnackbar(view: View, message: String)
        {
            Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(Color.DKGRAY)
                .show()
        }
    }
}