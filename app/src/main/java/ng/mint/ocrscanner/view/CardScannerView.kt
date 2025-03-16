package ng.mint.ocrscanner.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import ng.mint.ocrscanner.camera.CardFrameProcessor
import ng.mint.ocrscanner.processor.CardNumberExtractor

interface CardScanListener {
    fun onCardScanned(cardNumber: String)
    fun onScanCancelled()
    fun onScanError(error: String)
}

class CardScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cardFrameProcessor: CardFrameProcessor? = null
    private var cardNumberExtractor: CardNumberExtractor? = null
    private var cameraInitCallback: (() -> Unit)? = null
    private var permissionDeniedListener: (() -> Unit)? = null
    private var cardScanListener: CardScanListener? = null
    private var cardDetectionListener: CardFrameProcessor.CardDetectionListener? = null

    enum class ScanningState {
        IDLE,
        SCANNING,
        CARD_DETECTED,
        ERROR
    }

    private var currentScanningState: ScanningState = ScanningState.IDLE

    fun setCardFrameProcessor(processor: CardFrameProcessor) {
        cardFrameProcessor = processor
    }

    fun setCardNumberExtractor(extractor: CardNumberExtractor) {
        cardNumberExtractor = extractor
    }

    fun setCameraInitializationCallback(callback: () -> Unit) {
        cameraInitCallback = callback
    }

    fun setPermissionDeniedListener(listener: () -> Unit) {
        permissionDeniedListener = listener
    }

    fun setCardScanListener(listener: CardScanListener) {
        cardScanListener = listener
    }

    fun setCardDetectionListener(listener: CardFrameProcessor.CardDetectionListener) {
        cardDetectionListener = listener
        cardFrameProcessor?.setCardDetectionListener(listener)
    }

    fun startScanning() {
        updateScanningState(ScanningState.SCANNING)
        // In a real implementation, this would start the camera preview
    }

    fun stopScanning() {
        updateScanningState(ScanningState.IDLE)
        // In a real implementation, this would stop the camera preview
    }

    fun initCamera() {
        // In a real implementation, this would initialize the camera
        cameraInitCallback?.invoke()
    }

    fun onPermissionDenied() {
        permissionDeniedListener?.invoke()
    }


    fun onCardDetected(cardNumber: String, bounds: Rect) {
        cardDetectionListener?.onCardDetected(cardNumber, bounds)
        cardScanListener?.onCardScanned(cardNumber)
        updateScanningState(ScanningState.CARD_DETECTED)
    }

    fun onCardDetectionError() {
        cardDetectionListener?.onCardDetectionFailed("Failed to detect card")
        cardScanListener?.onScanError("Failed to detect card")
        updateScanningState(ScanningState.ERROR)
    }

    fun updateScanningState(state: ScanningState) {
        currentScanningState = state
        // In a real implementation, this would update the UI
    }

    fun getCurrentScanningState(): ScanningState {
        return currentScanningState
    }
}
