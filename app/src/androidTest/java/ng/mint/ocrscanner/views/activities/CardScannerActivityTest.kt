package ng.mint.ocrscanner.views.activities

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import ng.mint.ocrscanner.R
import ng.mint.ocrscanner.view.CardScannerView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumentation test for CardScannerActivity to verify the complete card scanning flow.
 */
@RunWith(AndroidJUnit4::class)
class CardScannerActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    private lateinit var scannerIdlingResource: ScannerIdlingResource

    @Before
    fun setUp() {
        Intents.init()
        scannerIdlingResource = ScannerIdlingResource()
        IdlingRegistry.getInstance().register(scannerIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(scannerIdlingResource)
    }

    @Test
    fun testActivityLaunchesSuccessfully() {
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Verify scanner view is displayed
        onView(withId(R.id.card_scanner_view))
            .check(matches(isDisplayed()))
        
        // Close the scenario
        scenario.close()
    }

    @Test
    fun testCameraPermissionGranted() {
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Check if camera preview is active
        scenario.onActivity { activity ->
            val scannerView = activity.findViewById<CardScannerView>(R.id.card_scanner_view)
            assert(scannerView != null)
            assert(scannerView.isCameraActive())
        }
        
        scenario.close()
    }

    @Test
    fun testCardDetectionAndResult() {
        // Set up result intent
        val resultData = Intent().apply {
            putExtra("card_number", "4111111111111111")
            putExtra("is_eu_card", true)
            putExtra("card_type", "VISA")
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Simulate card detection
        scenario.onActivity { activity ->
            val scannerView = activity.findViewById<CardScannerView>(R.id.card_scanner_view)
            // Simulate successful card scan
            scannerView.simulateCardDetected("4111111111111111")
            scannerIdlingResource.setIdle(true)
        }
        
        // Verify success status is shown
        onView(withId(R.id.status_text))
            .check(matches(withText(R.string.card_detected)))
        
        // Verify result is returned
        Intents.intended(IntentMatchers.hasExtraWithKey("card_number"))
        Intents.intended(IntentMatchers.hasExtraWithKey("is_eu_card"))
        Intents.intended(IntentMatchers.hasExtraWithKey("card_type"))
        
        scenario.close()
    }

    @Test
    fun testInvalidCardRejection() {
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Simulate invalid card detection
        scenario.onActivity { activity ->
            val scannerView = activity.findViewById<CardScannerView>(R.id.card_scanner_view)
            // Invalid card number that fails Luhn check
            scannerView.simulateCardDetected("1234567890123456")
            scannerIdlingResource.setIdle(true)
        }
        
        // Verify error status is shown
        onView(withId(R.id.status_text))
            .check(matches(withText(R.string.invalid_card)))
        
        scenario.close()
    }

    @Test
    fun testManualEntryButton() {
        // Set up result intent for manual entry
        val resultData = Intent().apply {
            putExtra("manual_entry", true)
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(result)
        
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Click on manual entry button
        onView(withId(R.id.manual_entry_button))
            .check(matches(isDisplayed()))
            .perform(click())
        
        // Verify correct intent was sent
        Intents.intended(IntentMatchers.hasExtraWithKey("manual_entry"))
        
        scenario.close()
    }

    @Test
    fun testBackButtonClosesActivity() {
        // Launch the activity
        val scenario = ActivityScenario.launch(CardScannerActivity::class.java)
        
        // Click on back button
        onView(withId(R.id.back_button))
            .check(matches(isDisplayed()))
            .perform(click())
        
        // Verify activity is finished
        scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
        
        scenario.close()
    }

    /**
     * Custom IdlingResource to wait for card scanning to complete
     */
    inner class ScannerIdlingResource : IdlingResource {
        private var idle = false
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = "CardScannerIdlingResource"

        override fun isIdleNow(): Boolean {
            return idle
        }

        fun setIdle(idle: Boolean) {
            this.idle = idle
            callback?.onTransitionToIdle()
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }

    /**
     * Extension function to simulate card detection in the CardScannerView
     */
    private fun CardScannerView.simulateCardDetected(cardNumber: String) {
        // This is a test helper method that needs to match the actual implementation
        // Call the card detection callback directly to simulate card detection
        val field = this.javaClass.getDeclaredField("cardDetectionListener")
        field.isAccessible = true
        val listener = field.get(this)
        val method = listener.javaClass.getDeclaredMethod("onCardDetected", String::class.java)
        method.isAccessible = true
        method.invoke(listener, cardNumber)
    }

    /**
     * Extension function to check if camera is active
     */
    private fun CardScannerView.isCameraActive(): Boolean {
        val field = this.javaClass.getDeclaredField("cameraActive")
        field.isAccessible = true
        return field.getBoolean(this)
    }
}

