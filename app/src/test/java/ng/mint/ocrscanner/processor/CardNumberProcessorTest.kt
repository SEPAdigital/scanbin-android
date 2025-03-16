package ng.mint.ocrscanner.processor

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CardNumberProcessorTest {

    private lateinit var cardNumberExtractor: CardNumberExtractor
    private lateinit var binValidator: BinValidator

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        cardNumberExtractor = CardNumberExtractor()
        binValidator = BinValidator()
    }

    @Test
    fun testValidCardNumber() {
        // Test valid card numbers that pass both length check and Luhn check
        val validCards = listOf(
            // Known valid test card numbers from major providers
            "4532015112830366",  // Visa 16 digits
            "4916337563926287",  // Another known valid Visa
            "5555555555554444",  // Mastercard 16 digits
            "5105105105105100",  // Another known valid Mastercard
            "371449635398431",   // Amex 15 digits
            "378282246310005",   // Another known valid Amex
            "5020123456789012"   // Maestro 16 digits
        )
        
        validCards.forEach { cardNumber ->
            val isValid = cardNumberExtractor.validateCardNumber(cardNumber)
            assertTrue(
                "Card $cardNumber should be valid:\n" +
                "Length check: ${cardNumber.length in 13..19}\n" +
                "Numeric check: ${cardNumber.matches("^[0-9]+$".toRegex())}\n" +
                "Luhn check: ${cardNumberExtractor.validateLuhnChecksum(cardNumber)}",
                isValid
            )
        }

        // Test invalid card numbers
        val invalidCards = listOf(
            "12345",                    // Too short
            "12345678901234567890",     // Too long
            "abcd1234efgh5678",         // Non-numeric
            "",                         // Empty
            "1234567890123456",         // Valid length but fails Luhn
            "4532015112830367"          // Failed Luhn check (valid card with last digit changed)
        )
        
        invalidCards.forEach { cardNumber ->
            val isValid = cardNumberExtractor.validateCardNumber(cardNumber)
            assertFalse(
                "Card $cardNumber should be invalid:\n" +
                "Length check: ${cardNumber.length in 13..19}\n" +
                "Numeric check: ${cardNumber.matches("^[0-9]+$".toRegex())}\n" +
                "Luhn check: ${if (cardNumber.matches("^[0-9]+$".toRegex())) cardNumberExtractor.validateLuhnChecksum(cardNumber) else "N/A"}",
                isValid
            )
        }
    }

    @Test
    fun testCardNumberExtraction() {
        // Test with raw text containing card number
        val rawText = "Card number: 4532 0151 1283 0366"
        val expectedCardNumber = "4532 0151 1283 0366"
        assertEquals(expectedCardNumber, cardNumberExtractor.extractCardNumber(rawText))
        
        // Test with invalid card number in raw text
        val invalidRawText = "Card number: 1234 5678 9012"
        assertNull(cardNumberExtractor.extractCardNumber(invalidRawText))
    }

    @Test
    fun testLuhnAlgorithm() {
        // Valid card numbers passing Luhn check
        assertTrue(cardNumberExtractor.validateLuhnChecksum("4532015112830366")) // Visa
        assertTrue(cardNumberExtractor.validateLuhnChecksum("5555555555554444")) // Mastercard
        assertTrue(cardNumberExtractor.validateLuhnChecksum("371449635398431"))  // Amex
        assertTrue(cardNumberExtractor.validateLuhnChecksum("6011111111111117")) // Discover
        
        // Invalid card numbers failing Luhn check
        assertFalse(cardNumberExtractor.validateLuhnChecksum("4532015112830367"))
        assertFalse(cardNumberExtractor.validateLuhnChecksum("5555555555554445"))
        assertFalse(cardNumberExtractor.validateLuhnChecksum("371449635398432"))
    }

    @Test
    fun testBinExtraction() {
        // Test standard BIN extraction (first 6 digits)
        val cardNumber = "4532015112830366"
        assertEquals("453201", binValidator.extractBin(cardNumber, false))
        
        // Test extended BIN extraction (first 8 digits)
        assertEquals("45320151", binValidator.extractBin(cardNumber))
        
        // Test BIN extraction with formatted card number
        val formattedCardNumber = "4532 0151 1283 0366"
        assertEquals("45320151", binValidator.extractBin(formattedCardNumber))
    }

    @Test
    fun testBinValidatorExtraction() {
        // Test standard BIN extraction (6 digits)
        val cardNumber = "4532015112830366"
        assertEquals("453201", binValidator.extractBin(cardNumber, false))
        
        // Test extended BIN extraction (8 digits)
        assertEquals("45320151", binValidator.extractBin(cardNumber))
        
        // Test with formatted card number
        val formattedCardNumber = "4532 0151 1283 0366"
        assertEquals("45320151", binValidator.extractBin(formattedCardNumber))
    }

    @Test
    fun testEuropeanCardDetection() {
        // Test European card numbers
        val europeanCards = mapOf(
            "4532015112830366" to "Visa (4)",
            "5555555555554444" to "Mastercard (51-55)",
            "2221000000000009" to "Mastercard (22-27)",
            "371449635398431" to "Amex (37)",
            "341111111111111" to "Amex (34)",
            "5020000000000000" to "Maestro (50)",
            "6700000000000000" to "Carte Bancaire (67)"
        )
        
        europeanCards.forEach { (cardNumber, description) ->
            assertTrue("$description card $cardNumber should be identified as European",
                binValidator.isEuropeanCard(cardNumber))
        }

        // Test non-European cards
        val nonEuropeanCards = listOf(
            "9876543210123456",  // Unknown prefix
            "",                  // Empty
            null                 // Null
        )
        
        nonEuropeanCards.forEach { cardNumber ->
            assertFalse("Card $cardNumber should not be identified as European",
                binValidator.isEuropeanCard(cardNumber))
        }
    }

    @Test
    fun testCardSchemeIdentification() {
        // Test card schemes with 6 digit BINs
        val testCases = mapOf(
            "453201" to "Visa",
            "411111" to "Visa",
            "555555" to "Mastercard",
            "222100" to "Mastercard",
            "371449" to "American Express",
            "341111" to "American Express",
            "502000" to "Maestro",
            "670000" to "Carte Bancaire",
            "987654" to "Unknown"
        )
        
        testCases.forEach { (bin, expectedScheme) ->
            assertEquals("BIN $bin should be identified as $expectedScheme", 
                expectedScheme, binValidator.getCardScheme(bin))
        }
    }

    @Test
    fun testFormatCardNumber() {
        // Test formatting unformatted card number
        val unformattedNumber = "4532015112830366"
        assertEquals("4532 0151 1283 0366", cardNumberExtractor.formatCardNumber(unformattedNumber))
        
        // Test formatting pre-formatted card number
        val preformattedNumber = "4532 0151 1283 0366"
        assertEquals("4532 0151 1283 0366", cardNumberExtractor.formatCardNumber(preformattedNumber))
        
        // Test formatting short card number
        val shortNumber = "123456789012"
        assertEquals("1234 5678 9012", cardNumberExtractor.formatCardNumber(shortNumber))
    }

    @Test
    fun testBinValidation() {
        // Test valid BINs
        assertTrue(binValidator.isValidBin("453201"))  // 6-digit BIN
        assertTrue(binValidator.isValidBin("45320151")) // 8-digit BIN
        
        // Test invalid BINs
        assertFalse(binValidator.isValidBin("12345"))  // Too short
        assertFalse(binValidator.isValidBin("123456789")) // Too long
        assertFalse(binValidator.isValidBin("abcdef"))  // Non-numeric
    }
}
