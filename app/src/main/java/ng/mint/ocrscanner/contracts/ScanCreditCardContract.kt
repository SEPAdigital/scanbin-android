package ng.mint.ocrscanner.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ng.mint.ocrscanner.views.activities.CardScannerActivity

class ScanCreditCardContract : ActivityResultContract<Int, String?>() {

    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(context, CardScannerActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when {
            resultCode != Activity.RESULT_OK -> null
            intent == null -> null
            else -> intent.getStringExtra("card_number")
        }
    }
}