package ng.mint.ocrscanner.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import ng.mint.ocrscanner.R

class CardScannerActivity : AppCompatActivity() {
    companion object {
        private const val SCAN_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_scanner)
        
        val scanIntent = Intent(this, CardIOActivity::class.java).apply {
            putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
            putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
            putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
            putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
        }
        startActivityForResult(scanIntent, SCAN_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SCAN_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val scanResult = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
                val resultIntent = Intent().apply {
                    putExtra("card_number", scanResult?.formattedCardNumber)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }
}

