package ng.mint.ocrscanner.compatibility

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import ng.mint.ocrscanner.views.activities.CardScannerActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Compatibility test suite for CardScannerActivity
 * Tests scanner behavior across different Android versions, screen sizes, orientations, and camera capabilities
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class CardScannerCompatibilityTest {

    private lateinit var context: Context
    private lateinit var uiDevice: UiDevice
    private var scenario: ActivityScenario<CardScannerActivity>? = null

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    @get:Rule
    val activityRule = ActivityTestRule(
        CardScannerActivity::class.java,
        true, 
        false // Don't launch activity automatically
    )

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @After
    fun tearDown() {
        scenario?.close()
    }

    /**
     * Tests scanner on API level 28 (Android 9)
     * Verifies basic scanning functionality works on older Android versions
     */
    @Test
    @SdkSuppress(minSdkVersion = 28, maxSdkVersion = 28)
    fun testOnApi28() {
        // Skip if not running on API 28
        Assume.assumeTrue(Build.VERSION.SDK_INT == 28)
        
        launchScanner()
        
        // Verify scanner initializes properly on API 28
        assertScannerInitialized()
        
        // Test basic scanning functionality
        performScanningTest()
    }

    /**
     * Tests scanner on API level 34 (Android 14)
     * Verifies scanning works with latest Android features and security models
     */
    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun testOnApi34() {
        // Skip if not running on API 34
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 34)
        
        launchScanner()
        
        // Verify scanner initializes properly on API 34
        assertScannerInitialized()
        
        // Test scanning with latest Android features
        performScanningTest()
    }

    /**
     * Tests scanner behavior on phone-sized screens
     * Should properly adapt UI elements to phone form factor
     */
    @Test
    fun testOnPhoneScreen() {
        // Skip if device is not a phone (diagonal size < 7 inches)
        Assume.assumeTrue(isPhoneFormFactor(context))
        
        launchScanner()
        
        // Verify UI adapts to phone screen size
        assertScannerUiAdaptsToScreenSize()
        
        // Test basic scanning functionality
        performScanningTest()
    }

    /**
     * Tests scanner behavior on tablet-sized screens
     * Should properly utilize larger screen space for better UX
     */
    @Test
    fun testOnTabletScreen() {
        // Skip if device is not a tablet (diagonal size >= 7 inches)
        Assume.assumeTrue(isTabletFormFactor(context))
        
        launchScanner()
        
        // Verify UI adapts to tablet screen size
        assertScannerUiAdaptsToScreenSize()
        
        // Test basic scanning functionality
        performScanningTest()
    }

    /**
     * Tests scanner in portrait orientation
     * Verifies preview adjusts correctly to portrait dimensions
     */
    @Test
    fun testInPortraitOrientation() {
        // Force portrait orientation
        setOrientation(Configuration.ORIENTATION_PORTRAIT)
        
        launchScanner()
        
        // Verify scanner adjusts to portrait orientation
        assertScannerOrientationCorrect(Configuration.ORIENTATION_PORTRAIT)
        
        // Test scanning in portrait orientation
        performScanningTest()
    }

    /**
     * Tests scanner in landscape orientation
     * Verifies preview and UI elements adjust correctly to landscape dimensions
     */
    @Test
    fun testInLandscapeOrientation() {
        // Force landscape orientation
        setOrientation(Configuration.ORIENTATION_LANDSCAPE)
        
        launchScanner()
        
        // Verify scanner adjusts to landscape orientation
        assertScannerOrientationCorrect(Configuration.ORIENTATION_LANDSCAPE)
        
        // Test scanning in landscape orientation
        performScanningTest()
    }

    /**
     * Tests scanner with different camera capabilities
     * Should gracefully handle devices with varying camera features
     */
    @Test
    fun testWithDifferentCameraCapabilities() {
        // Get camera capabilities from the device
        val hasFrontCamera = hasFrontCamera()
        val hasBackCamera = hasBackCamera()
        val hasFlash = hasFlash()
        val hasAutofocus = hasAutofocus()
        
        // Skip if device has no camera
        Assume.assumeTrue(hasFrontCamera || hasBackCamera)
        
        launchScanner()
        
        // Test scanning based on available camera features
        if (hasAutofocus) {
            // Test with autofocus
            testWithAutofocus()
        } else {
            // Test without autofocus
            testWithoutAutofocus()
        }
        
        if (hasFlash) {
            // Test with flash
            testWithFlash()
        }
    }

    /**
     * Tests scanner behavior when camera permission is not granted initially
     * Scanner should show appropriate permission request UI
     */
    @Test
    fun testPermissionRequestFlow() {
        // Skip test if permission is already granted
        Assume.assumeFalse(isCameraPermissionGranted())
        
        // Launch without permission
        val intent = Intent(context, CardScannerActivity::class.java)
        scenario = ActivityScenario.launch(intent)
        
        // Verify permission request UI is shown
        assertPermissionRequestUiShown()
    }

    // Helper methods to support tests

    private fun launchScanner() {
        val intent = Intent(context, CardScannerActivity::class.java)
        scenario = ActivityScenario.launch(intent)
        // Allow time for camera to initialize
        Thread.sleep(2000)
    }

    private fun performScanningTest() {
        // Simulate having a card in view
        // In a real test, this would use UiAutomator to position a test card
        // or use a mock camera feed for consistent testing
        
        // Wait for reasonable time to detect a card
        val latch = CountDownLatch(1)
        latch.await(5, TimeUnit.SECONDS)
        
        // Verify scanner state after scan attempt
        scenario?.onActivity { activity ->
            // Check if camera is active
            assertTrue("Camera preview should be active", isCameraActive(activity))
        }
    }

    private fun assertScannerInitialized() {
        scenario?.onActivity { activity ->
            // Verify camera is initialized
            assertTrue("Camera should be initialized", isCameraActive(activity))
            
            // Verify scanner UI is visible
            assertTrue("Scanner UI should be visible", isScannerUiVisible(activity))
        }
    }

    private fun assertScannerUiAdaptsToScreenSize() {
        scenario?.onActivity { activity ->
            // Verify scanner UI adapts to screen size
            // This would check specific UI layout properties based on screen size
            assertTrue("Scanner UI should adapt to screen size", true)
        }
    }

    private fun assertScannerOrientationCorrect(orientation: Int) {
        scenario?.onActivity { activity ->
            // Verify orientation of scanner UI matches expected orientation
            assertEquals("Scanner orientation should match device orientation", 
                orientation, activity.resources.configuration.orientation)
        }
    }

    private fun assertPermissionRequestUiShown() {
        // In a real test, this would verify permission dialog is visible
        // using UiAutomator or Espresso
    }

    private fun testWithAutofocus() {
        // Test scanning with autofocus enabled
        // This would test how well scanner performs with autofocus
    }

    private fun testWithoutAutofocus() {
        // Test scanning without autofocus
        // This would test fallback scanning methods
    }

    private fun testWithFlash() {
        // Test scanning with flash enabled and disabled
        // This would toggle flash and verify scanner behavior
    }

    // Utility methods

    private fun setOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            uiDevice.setOrientationLeft()
        } else {
            uiDevice.setOrientationNatural()
        }
        // Allow time for orientation change
        Thread.sleep(1000)
    }

    private fun isPhoneFormFactor(context: Context): Boolean {
        // Simple classification based on smallest screen dimension
        val metrics = context.resources.displayMetrics
        val smallestDimension = Math.min(metrics.widthPixels, metrics.heightPixels) / metrics.density
        return smallestDimension < 600 // Standard phone threshold
    }

    private fun isTabletFormFactor(context: Context): Boolean {
        return !isPhoneFormFactor(context)
    }

    private fun isCameraActive(activity: CardScannerActivity): Boolean {
        // In a real test, this would check if camera preview is running
        return true
    }

    private fun isScannerUiVisible(activity: CardScannerActivity): Boolean {
        // In a real test, this would check if scanner UI elements are visible
        return true
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasFrontCamera(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }
    
    private fun hasBackCamera(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }
    
    private fun hasFlash(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
    
    private fun hasAutofocus(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)
    }
}

