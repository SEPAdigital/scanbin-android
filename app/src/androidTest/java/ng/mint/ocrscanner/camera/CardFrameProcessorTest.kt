package ng.mint.ocrscanner.camera

import android.content.Context
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.processor.CardNumberExtractor
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class CardFrameProcessorTest {
    private lateinit var context: Context
    private lateinit var cardNumberExtractor: CardNumberExtractor
    private lateinit var binValidator: BinValidator
    private lateinit var cardFrameProcessor: CardFrameProcessor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        cardNumberExtractor = mock()
        binValidator = mock()
        cardFrameProcessor = CardFrameProcessor(context, cardNumberExtractor, binValidator)
    }

    @Test
    fun testCardNumberValidation() {
        val testCardNumber = "4532015112830366"
        whenever(cardNumberExtractor.validateCardNumber(testCardNumber)).thenReturn(true)
        assertTrue("Card number validation should pass", 
            cardFrameProcessor.validateCardNumber(testCardNumber))
    }

    @Test
    fun testEuCardDetection() {
        val testCardNumber = "4532015112830366"
        whenever(binValidator.isEuCard(testCardNumber)).thenReturn(true)
        assertTrue("Card should be detected as EU card", 
            cardFrameProcessor.isEuCard(testCardNumber))
    }

    @Test
    fun testCardDetectionCallback() {
        var detectedCardNumber: String? = null
        var detectedBounds: Rect? = null

        cardFrameProcessor.setCardDetectionListener(object : CardFrameProcessor.CardDetectionListener {
            override fun onCardDetected(cardNumber: String, bounds: Rect) {
                detectedCardNumber = cardNumber
                detectedBounds = bounds
            }

            override fun onCardDetectionFailed(error: String) {
                // Not testing failure case in this test
            }
        })

        val testImage: ImageProxy = mock()
        whenever(testImage.width).thenReturn(1080)
        whenever(testImage.height).thenReturn(1920)

        cardFrameProcessor.analyze(testImage)

        assertTrue("Card number should match test value", 
            detectedCardNumber == "4532015112830366")
        assertTrue("Bounds width should match image width", 
            detectedBounds?.width() == 1080)
        assertTrue("Bounds height should match image height", 
            detectedBounds?.height() == 1920)
    }

    @Test
    fun testCardTypeDetection() {
        val testCardNumber = "4532015112830366"
        whenever(binValidator.getCardType(testCardNumber)).thenReturn("Visa")
        assertTrue("Card type should be Visa", 
            cardFrameProcessor.getCardType(testCardNumber) == "Visa")
    }
}
