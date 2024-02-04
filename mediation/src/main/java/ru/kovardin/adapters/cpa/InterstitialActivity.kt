package ru.kovardin.adapters.cpa

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext


class InterstitialActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Column {

//                val activity = (LocalContext.current as? Activity)

                Text("Hello Compose World!")
                OutlinedButton(onClick = {
                    this@InterstitialActivity.finish()
                }) {
                    Text("Close")
                }
            }
        }
    }
}