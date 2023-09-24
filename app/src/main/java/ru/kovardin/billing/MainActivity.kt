package ru.kovardin.billing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.kovardin.billing.ui.theme.BillingTheme
import ru.kovardin.getappbilling.Billing

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Billing.init(
            app = "1",
            api = "https://service.getapp.store",
            context = this,
        )

        super.onCreate(savedInstanceState)
        setContent {
            BillingTheme {
                BillingApp()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillingTheme {
        BillingApp()
    }
}