package ng.mint.ocrscanner.view

import android.content.Context
import android.graphics.Rect
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ng.mint.ocrscanner.processor.CardFrameProcessor
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.processor.CardNumberExtractor
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class CardScannerViewTest {
    private lateinit var context: Context
    private lateinit var cardScannerView: CardScannerView
    private lateinit var cardFrameProcessor: CardFrameProcessor
    private lateinit var cardNumberExtractor: CardNumberExtractor
    private lateinit var binValidator: BinValidator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        cardNumberExtractor = mock()
        binValidator = mock()
        cardFrameProcessor = CardFrameProcessor(context, cardNumberExtractor, binValidator)
        cardScannerView = CardScannerView(context)
        cardScannerView.setCardFrameProcessor(cardFrameProcessor)
    }

    @Test
    fun testScannerInitialization() {
        var initCalled = false
        cardScannerView.setCameraInitializationCallback {
            initCalled = true
        }
        cardScannerView.initCamera()
        assertTrue("Camera initialization callback should be called", initCalled)
    }

    @Test
    fun testCardDetection() {
        var detectedCard: String? = null
        cardScannerView.setCardScanListener(object : CardScanListener {
            override fun onCardScanned(cardNumber: String) {
                detectedCard = cardNumber
            }
            override fun onScanCancelled() {}
            override fun onScanError(error: String) {}
        })

        cardScannerView.onCardDetected("4532015112830366", Rect(0, 0, 100, 100))
        assertTrue("Card number should be detected", detectedCard == "4532015112830366")
    }

    @Test
    fun testScanningState() {
        cardScannerView.startScanning()
        assertTrue("Scanner should be in scanning state", 
            cardScannerView.getCurrentScanningState() == CardScannerView.ScanningState.SCANNING)

        cardScannerView.stopScanning()
        assertTrue("Scanner should be in idle state", 
            cardScannerView.getCurrentScanningState() == CardScannerView.ScanningState.IDLE)
    }

    @Test
    fun testErrorHandling() {
        var errorReceived: String? = null
        cardScannerView.setCardScanListener(object : CardScanListener {
            override fun onCardScanned(cardNumber: String) {}
            override fun onScanCancelled() {}
            override fun onScanError(error: String) {
                errorReceived = error
            }
        })

        cardScannerView.onCardDetectionError()
        assertTrue("Error should be received", errorReceived != null)
    }
}
