package ng.mint.ocrscanner.ui

import android.Manifest
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import ng.mint.ocrscanner.R
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.processor.CardNumberExtractor
import ng.mint.ocrscanner.view.CardScannerView
import ng.mint.ocrscanner.views.activities.CardScannerActivity
import java.lang.Thread.sleep

/**
 * Comprehensive UI test for the Card Scanner flow using Espresso
 * Tests include UI flow, EU card format validation, user interactions, and result verification
 */
@RunWith(AndroidJUnit4::class)
class CardScannerFlowTest {

    @get:Rule
    val activityRule = ActivityTestRule(CardScannerActivity::class.java, true, false)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    private lateinit var scannerIdlingResource: ScannerIdlingResource

    @Before
    fun setup() {
        Intents.init()
        // Register the custom idling resource to synchronize with card scanning process
        scannerIdlingResource = ScannerIdlingResource()
        IdlingRegistry.getInstance().register(scannerIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(scannerIdlingResource)
    }

    /**
     * Test the complete scanning workflow for a valid EU card
     */
    @Test
    fun testCompleteScanningFlow_validEuCard() {
        // Launch the activity with test intent
        val intent = Intent(ApplicationProvider.getApplicationContext(), CardScannerActivity::class.java)
        activityRule.launchActivity(intent)

        // Allow time for camera to initialize
        sleep(1000)

        // Verify scanner UI is visible
        onView(withId(R.id.cardScannerView))
            .check(matches(isDisplayed()))

        // Simulate card detection (we'll inject a test card number via custom action)
        onView(withId(R.id.cardScannerView))
            .perform(injectTestCardNumber("5399123456789123", true))

        // Verify successful scan UI feedback (the success message should be displayed)
        onView(withId(R.id.scanStatusText))
            .check(matches(withText(R.string.card_scan_successful)))

        // Verify the result was processed
        onView(withId(R.id.cardTypeImage))
            .check(matches(isDisplayed()))

        // Check that the result intent contains the correct data
        Intents.intended(IntentMatchers.hasExtraWithKey("card_number"))
        Intents.intended(IntentMatchers.hasExtraWithKey("is_eu_card"))
        Intents.intended(IntentMatchers.hasExtraWithKey("card_type"))
    }

    /**
     * Test validation for EU card formats
     */
    @Test
    fun testEuCardFormatValidation() {
        // Launch the activity
        val intent = Intent(ApplicationProvider.getApplicationContext(), CardScannerActivity::class.java)
        activityRule.launchActivity(intent)

        // Allow time for camera to initialize
        sleep(1000)

        // Test various EU card formats
        val euCards = listOf(
            "5399123456789123", // Mastercard (EU)
            "4123456789012345", // Visa (EU)
            "5526123456789123", // Mastercard (EU)
            "4921123456789123"  // Visa (EU)
        )

        for (cardNumber in euCards) {
            // Inject EU card number
            onView(withId(R.id.cardScannerView))
                .perform(injectTestCardNumber(cardNumber, true))

            // Verify card was detected as EU card
            onView(withId(R.id.scanStatusText))
                .check(matches(withText(R.string.card_scan_successful)))

            // Reset for next card
            activityRule.finishActivity()
            activityRule.launchActivity(intent)
            sleep(1000)
        }

        // Test non-EU cards
        val nonEuCards = listOf(
            "371234567890123", // Amex (non-EU)
            "6011123456789123" // Discover (non-EU)
        )

        for (cardNumber in nonEuCards) {
            // Inject non-EU card number
            onView(withId(R.id.cardScannerView))
                .perform(injectTestCardNumber(cardNumber, false))

            // Verify card was detected as non-EU card
            onView(withId(R.id.scanStatusText))
                .check(matches(withText(R.string.non_eu_card_detected)))

            // Reset for next card
            activityRule.finishActivity()
            activityRule.launchActivity(intent)
            sleep(1000)
        }
    }

    /**
     * Test user interaction with manual card entry
     */
    @Test
    fun testManualCardEntryInteraction() {
        // Launch the activity
        val intent = Intent(ApplicationProvider.getApplicationContext(), CardScannerActivity::class.java)
        activityRule.launchActivity(intent)

        // Click on the manual entry button
        onView(withId(R.id.manualEntryButton))
            .check(matches(isDisplayed()))
            .perform(click())

        // Verify manual entry form is displayed
        onView(withId(R.id.cardNumberEditText))
            .check(matches(isDisplayed()))

        // Enter a valid EU card number
        onView(withId(R.id.cardNumberEditText))
            .perform(typeText("5399123456789123"), closeSoftKeyboard())

        // Click submit button
        onView(withId(R.id.submitButton))
            .perform(click())

        // Verify success
        onView(withId(R.id.scanStatusText))
            .check(matches(withText(R.string.card_scan_successful)))

        // Verify the result intent contains the correct data
        Intents.intended(IntentMatchers.hasExtraWithKey("card_number"))
        Intents.intended(IntentMatchers.hasExtraWithKey("is_eu_card"))
    }

    /**
     * Test BIN extraction and validation
     */
    @Test
    fun testBinExtractionAndValidation() {
        // Launch the activity
        val intent = Intent(ApplicationProvider.getApplicationContext(), CardScannerActivity::class.java)
        activityRule.launchActivity(intent)

        // Allow time for camera to initialize
        sleep(1000)

        // Test case for MasterCard BIN
        onView(withId(R.id.cardScannerView))
            .perform(injectTestCardNumber("5399123456789123", true))

        // Verify the BIN was extracted correctly (first 8 digits)
        Intents.intended(IntentMatchers.hasExtra("card_number", "53991234"))
        
        // Reset activity
        activityRule.finishActivity()
        activityRule.launchActivity(intent)
        sleep(1000)
        
        // Test case for Visa BIN
        onView(withId(R.id.cardScannerView))
            .perform(injectTestCardNumber("4123456789012345", true))

        // Verify the BIN was extracted correctly (first 8 digits)
        Intents.intended(IntentMatchers.hasExtra("card_number", "41234567"))
    }

    /**
     * Test error handling for invalid cards
     */
    @Test
    fun testErrorHandlingForInvalidCards() {
        // Launch the activity
        val intent = Intent(ApplicationProvider.getApplicationContext(), CardScannerActivity::class.java)
        activityRule.launchActivity(intent)

        // Allow time for camera to initialize
        sleep(1000)

        // Test card with invalid checksum (fails Luhn algorithm)
        onView(withId(R.id.cardScannerView))
            .perform(injectTestCardNumber("5399123456789124", true))

        // Verify error message for invalid card
        onView(withId(R.id.scanStatusText))
            .check(matches(withText(R.string.invalid_card_number)))

        // Test invalid card format
        activityRule.finishActivity()
        activityRule.launchActivity(intent)
        sleep(1000)
        
        onView(withId(R.id.cardScannerView))
            .perform(injectTestCardNumber("12345", true))

        // Verify error message for invalid format
        onView(withId(R.id.scanStatusText))
            .check(matches(withText(R.string.invalid_card_format)))
    }

    /**
     * Custom ViewAction to inject a test card number into the CardScannerView
     * This simulates card detection without needing a physical card
     */
    private fun injectTestCardNumber(cardNumber: String, isEuCard: Boolean): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(CardScannerView::class.java)
            }

            override fun getDescription(): String {
                return "Inject test card number $cardNumber"
            }

            override fun perform(uiController: UiController, view: View) {
                val scannerView = view as CardScannerView

                // Create test detector result
                val isValidCard = CardNumberExtractor.validateCardNumber(cardNumber)
                val isValidLuhn = CardNumberExtractor.validateLuhnChecksum(cardNumber)
                val bin = cardNumber.take(8)
                val cardType = BinValidator.getCardTypeFromBin(bin)
                
                // Simulate card detection by calling the detection callback
                scannerView.onCardDetected(cardNumber, isEuCard, isValidCard && isValidLuhn, cardType)
                
                // Signal that the idle state has changed
                scannerIdlingResource.setIdle(false)
                
                // Wait a bit for UI to update
                uiController.loopMainThreadForAtLeast(1000)
                
                // Signal that we're idle again
                scannerIdlingResource.setIdle(true)
            }
        }
    }

    /**
     * Custom IdlingResource to synchronize Espresso tests with card scanning process
     */
    inner class ScannerIdlingResource : IdlingResource {
        private var isIdle = true
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = "ScannerIdlingResource"

        override fun isIdleNow(): Boolean = isIdle

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            this.callback = callback
        }

        fun setIdle(isIdle: Boolean) {
            this.isIdle = isIdle
            if (isIdle && callback != null) {
                callback?.onTransitionToIdle()
            }
        }
    }
}

