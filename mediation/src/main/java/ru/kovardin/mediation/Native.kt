package ru.kovardin.mediation

import android.content.Context
import android.view.View
import android.widget.TextView

class Native {
    fun init(id: String) {

    }

    fun view(context: Context): View {
        return TextView(context)
    }
}