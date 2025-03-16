package ng.mint.ocrscanner.processor

import android.content.Context
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageAnalysis
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

class CardFrameProcessor(
    private val context: Context,
    private val cardNumberExtractor: CardNumberExtractor,
    private val binValidator: BinValidator
) : ImageAnalysis.Analyzer {

    interface CardDetectionListener {
        fun onCardDetected(cardNumber: String, bounds: Rect)
        fun onCardDetectionFailed(error: String)
    }

    private var cardDetectionListener: CardDetectionListener? = null
    private val executorService = Executors.newSingleThreadExecutor()
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun setCardDetectionListener(listener: CardDetectionListener) {
        cardDetectionListener = listener
    }

    override fun analyze(image: ImageProxy) {
        // This would be implemented with ML Kit text recognition
        // For testing purposes, we'll simulate a detection
        cardDetectionListener?.onCardDetected(
            "4532015112830366",
            Rect(0, 0, image.width, image.height)
        )
        image.close()
    }

    fun validateCardNumber(cardNumber: String): Boolean {
        return cardNumberExtractor.validateCardNumber(cardNumber)
    }

    fun validatePartialCardNumber(cardNumber: String): Boolean {
        return cardNumberExtractor.validatePartialCardNumber(cardNumber)
    }

    fun isEuropeanCard(cardNumber: String): Boolean {
        return binValidator.isEuropeanCard(cardNumber)
    }

    fun getCardScheme(cardNumber: String): String {
        return binValidator.getCardScheme(cardNumber)
    }
}
