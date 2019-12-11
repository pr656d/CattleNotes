package com.pr656d.cattlenotes.shared.utils.display

import android.content.Context
import android.graphics.PorterDuff
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pr656d.cattlenotes.R

object Toaster {
    fun show(context: Context, text: CharSequence) {
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.view.background.setColorFilter(
            ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_IN
        )
        val textView = toast.view.findViewById(R.id.message) as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        toast.show()
    }
}