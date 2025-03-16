package ng.mint.ocrscanner.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ng.mint.ocrscanner.databinding.ActivityCardScannerBinding
import ng.mint.ocrscanner.databinding.ManualEntryLayoutBinding
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.view.CardScanListener
import ng.mint.ocrscanner.view.CardScannerView
import ng.mint.ocrscanner.R

class CardScannerActivity : AppCompatActivity(), CardScanListener {
    private lateinit var binding: ActivityCardScannerBinding
    private lateinit var manualEntryBinding: ManualEntryLayoutBinding
    private lateinit var cardScannerView: CardScannerView
    private val binValidator = BinValidator()
    private val TAG = "CardScannerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        manualEntryBinding = ManualEntryLayoutBinding.bind(binding.manualEntryLayout.root)
        cardScannerView = binding.cardScannerView
        setupCardScanner()
        setupButtons()
    }

    private fun setupCardScanner() {
        cardScannerView.setCardScanListener(this)
        if (checkAndRequestCameraPermission()) {
            startCardScanner()
        }
    }

    private fun setupButtons() {
        binding.backButton.setOnClickListener {
            onScanCancelled()
        }
        binding.manualEntryButton.setOnClickListener {
            showManualEntry()
        }
        manualEntryBinding.submitButton.setOnClickListener {
            handleManualEntry()
        }
    }

    private fun startCardScanner() {
        binding.cardScannerView.startScanning()
        binding.manualEntryLayout.root.visibility = View.GONE
        binding.statusText.setText(R.string.scanning_position_card)
    }

    override fun onCardScanned(cardNumber: String) {
        val bin = cardNumber.take(8)
        handleScannedBin(bin)
    }

    override fun onScanCancelled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onScanError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun handleScannedBin(bin: String) {
        if (!binValidator.isEuropeanCard(bin)) {
            binding.statusText.setText(R.string.invalid_card)
            cardScannerView.startScanning()
            return
        }

        val resultIntent = Intent().apply {
            putExtra("bin", bin)
            putExtra("isEuropeanCard", binValidator.isEuropeanCard(bin))
            putExtra("cardType", binValidator.getCardScheme(bin))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun showManualEntry() {
        binding.manualEntryLayout.root.visibility = View.VISIBLE
        binding.cardScannerView.stopScanning()
    }

    private fun handleManualEntry() {
        val cardNumber = manualEntryBinding.cardNumberEditText.text.toString()
        if (cardNumber.isNotEmpty()) {
            onCardScanned(cardNumber)
        }
    }

    private fun checkAndRequestCameraPermission(): Boolean {
        // In a real implementation, check and request camera permissions
        return true
    }
}
