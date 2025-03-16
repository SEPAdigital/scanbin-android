package ng.mint.ocrscanner.camera

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import kotlinx.coroutines.guava.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Manages camera initialization, configuration and lifecycle
 * using CameraX library for card scanning.
 */
class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView
) {
    private val TAG = "CameraManager"
    
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    /**
     * Initializes and starts the camera with specified configuration
     */
    suspend fun startCamera(
        analyzer: ImageAnalysis.Analyzer? = null
    ): Boolean {
        return try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            
            // Configure camera preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            
            // Configure image capture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
            
            // Configure image analysis if analyzer is provided
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    if (analyzer != null) {
                        it.setAnalyzer(cameraExecutor, analyzer)
                    }
                }
            
            // Select back camera as default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // Unbind any previous use cases
                cameraProvider.unbindAll()
                
                // Bind camera to lifecycle
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
                
                // Apply auto-focus mode
                camera?.cameraControl?.enableTorch(false)
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Camera initialization failed", e)
            false
        }
    }
    
    /**
     * Sets up the camera with the card frame processor for card scanning
     */
    suspend fun setupCardScanning(cardFrameProcessor: CardFrameProcessor): Boolean {
        return startCamera(cardFrameProcessor)
    }
    
    /**
     * Toggles the torch/flashlight for better scanning in low light
     */
    fun toggleTorch(enable: Boolean) {
        camera?.cameraControl?.enableTorch(enable)
    }
    
    /**
     * Cleanup camera resources
     */
    fun shutdown() {
        cameraExecutor.shutdown()
    }
}

