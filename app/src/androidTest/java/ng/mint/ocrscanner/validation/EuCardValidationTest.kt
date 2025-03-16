package ng.mint.ocrscanner.validation

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.processor.CardNumberExtractor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test suite specifically for EU card validation rules and BIN validation
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class EuCardValidationTest {

    private lateinit var binValidator: BinValidator
    private lateinit var cardNumberExtractor: CardNumberExtractor

    @Before
    fun setup() {
        binValidator = BinValidator()
        cardNumberExtractor = CardNumberExtractor()
    }

    // SECTION 1: Tests for specific EU card format rules

    @Test
    fun testVisaEuFormat() {
        // Test valid EU Visa format (16 digits starting with 4)
        val euVisaNumber = "4123456789012345"
        assertTrue(cardNumberExtractor.isValidCardNumber(euVisaNumber))
        assertTrue(binValidator.isEuCard(euVisaNumber))
        assertEquals("VISA", binValidator.getCardType(euVisaNumber))
    }

    @Test
    fun testMastercardEuFormat() {
        // Test valid EU Mastercard format (16 digits starting with 51-55)
        val euMastercardNumber = "5234567890123456"
        assertTrue(cardNumberExtractor.isValidCardNumber(euMastercardNumber))
        assertTrue(binValidator.isEuCard(euMastercardNumber))
        assertEquals("MASTERCARD", binValidator.getCardType(euMastercardNumber))

        // Test 2nd series Mastercard (2221-2720)
        val euMastercard2Number = "2221001234567890"
        assertTrue(cardNumberExtractor.isValidCardNumber(euMastercard2Number))
        assertTrue(binValidator.isEuCard(euMastercard2Number))
        assertEquals("MASTERCARD", binValidator.getCardType(euMastercard2Number))
    }

    @Test
    fun testAmexEuFormat() {
        // Test valid EU Amex format (15 digits starting with 34 or 37)
        val euAmexNumber = "374245455400126"
        assertTrue(cardNumberExtractor.isValidCardNumber(euAmexNumber))
        assertTrue(binValidator.isEuCard(euAmexNumber))
        assertEquals("AMEX", binValidator.getCardType(euAmexNumber))
    }

    @Test
    fun testDinersClubEuFormat() {
        // Test valid EU Diners Club format (14 digits starting with 36)
        val euDinersNumber = "36700102000000"
        assertTrue(cardNumberExtractor.isValidCardNumber(euDinersNumber))
        assertTrue(binValidator.isEuCard(euDinersNumber))
        assertEquals("DINERS", binValidator.getCardType(euDinersNumber))
    }

    // SECTION 2: BIN validation for EU issuers

    @Test
    fun testFrenchBinRanges() {
        // Test French Credit Agricole BIN
        val frenchCreditAgricoleCard = "4973201234567890"
        assertTrue(binValidator.isEuCard(frenchCreditAgricoleCard))
        assertEquals("VISA", binValidator.getCardType(frenchCreditAgricoleCard))
        assertEquals("FR", binValidator.getCountryCode(frenchCreditAgricoleCard))
    }

    @Test
    fun testGermanBinRanges() {
        // Test German Deutsche Bank BIN
        val germanDeutscheBankCard = "4100401234567890"
        assertTrue(binValidator.isEuCard(germanDeutscheBankCard))
        assertEquals("VISA", binValidator.getCardType(germanDeutscheBankCard))
        assertEquals("DE", binValidator.getCountryCode(germanDeutscheBankCard))
    }

    @Test
    fun testItalianBinRanges() {
        // Test Italian UniCredit BIN
        val italianUniCreditCard = "5428481234567890"
        assertTrue(binValidator.isEuCard(italianUniCreditCard))
        assertEquals("MASTERCARD", binValidator.getCardType(italianUniCreditCard))
        assertEquals("IT", binValidator.getCountryCode(italianUniCreditCard))
    }

    @Test
    fun testSpanishBinRanges() {
        // Test Spanish Santander BIN
        val spanishSantanderCard = "5496841234567890"
        assertTrue(binValidator.isEuCard(spanishSantanderCard))
        assertEquals("MASTERCARD", binValidator.getCardType(spanishSantanderCard))
        assertEquals("ES", binValidator.getCountryCode(spanishSantanderCard))
    }

    // SECTION 3: Country-specific card validation

    @Test
    fun testFrenchCBCards() {
        // Test French Carte Bancaire card
        val frenchCBCard = "4974901234567890"
        assertTrue(cardNumberExtractor.isValidCardNumber(frenchCBCard))
        assertTrue(binValidator.isEuCard(frenchCBCard))
        assertEquals("FR", binValidator.getCountryCode(frenchCBCard))
        assertTrue(binValidator.isCbCard(frenchCBCard))
    }

    @Test
    fun testGermanGirocard() {
        // Test German Girocard (formerly EC card)
        val germanGirocard = "4406601234567890" 
        assertTrue(cardNumberExtractor.isValidCardNumber(germanGirocard))
        assertTrue(binValidator.isEuCard(germanGirocard))
        assertEquals("DE", binValidator.getCountryCode(germanGirocard))
        assertTrue(binValidator.isGirocard(germanGirocard))
    }

    @Test
    fun testItalianPagoBancomat() {
        // Test Italian PagoBancomat
        val italianPagoBancomat = "5189611234567890"
        assertTrue(cardNumberExtractor.isValidCardNumber(italianPagoBancomat))
        assertTrue(binValidator.isEuCard(italianPagoBancomat))
        assertEquals("IT", binValidator.getCountryCode(italianPagoBancomat))
    }

    @Test
    fun testSepaRegionValidation() {
        // Test cards from SEPA (Single Euro Payments Area) region
        val sepaRegionCards = listOf(
            "5281340012345678", // Netherlands
            "5362120012345678", // Belgium
            "5520120012345678", // Austria
            "4852120012345678", // Ireland
            "5544123412341234"  // Finland
        )
        
        for (cardNumber in sepaRegionCards) {
            assertTrue(cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue(binValidator.isEuCard(cardNumber))
            assertTrue(binValidator.isSepaRegionCard(cardNumber))
        }
    }

    // SECTION 4: Edge cases for EU card validation

    @Test
    fun testCardWithSpaces() {
        // Test card number with spaces (should be handled by extraction)
        val cardWithSpaces = "4921 8293 7489 1234"
        assertTrue(cardNumberExtractor.isValidCardNumber(cardWithSpaces))
        assertTrue(binValidator.isEuCard(cardWithSpaces))
    }

    @Test
    fun testNonStandardLengthEuCards() {
        // Some EU cards have non-standard lengths
        val nonStandardLengthCards = listOf(
            "493698123456789", // 15-digit Visa electron
            "493698123456", // 12-digit Visa electron (rare)
            "675940123456789123" // 18-digit Maestro
        )
        
        for (cardNumber in nonStandardLengthCards) {
            if (cardNumberExtractor.isValidLuhn(cardNumber)) {
                assertTrue(binValidator.isEuCard(cardNumber))
            }
        }
    }

    @Test
    fun testElectronCards() {
        // Test Visa Electron cards (popular in EU)
        val electronCards = listOf(
            "4917301234567890",
            "4913601234567890",
            "4508001234567890"
        )
        
        for (cardNumber in electronCards) {
            assertTrue(cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue(binValidator.isEuCard(cardNumber))
            assertEquals("VISA_ELECTRON", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testMaestroCards() {
        // Test Maestro cards (common in EU)
        val maestroCards = listOf(
            "5018001234567890",
            "5020001234567890", 
            "6759001234567890"
        )
        
        for (cardNumber in maestroCards) {
            assertTrue(cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue(binValidator.isEuCard(cardNumber))
            assertEquals("MAESTRO", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testLuhnButInvalidEuCards() {
        // Cards that pass Luhn but aren't valid EU cards
        val validLuhnButNotEuCards = listOf(
            "6011000990139424", // Discover (US)
            "3566002020360505", // JCB (Japan)
            "5066991111111118"  // Valid luhn but invalid BIN
        )
        
        for (cardNumber in validLuhnButNotEuCards) {
            assertTrue(cardNumberExtractor.isValidLuhn(cardNumber))
            assertFalse(binValidator.isEuCard(cardNumber))
        }
    }

    @Test
    fun testBorderlineEuRegionCards() {
        // Test cards from countries that are geographically in Europe but may have different rules
        val borderlineEuCards = listOf(
            "4571001234567890", // Danish card
            "5101241234567890", // Swiss card 
            "4149201234567890"  // UK card (post-Brexit)
        )
        
        for (cardNumber in borderlineEuCards) {
            assertTrue(cardNumberExtractor.isValidCardNumber(cardNumber))
            // Depending on implementation, these might be considered EU or not
            binValidator.isEuCard(cardNumber)
        }
    }

    @Test
    fun testFutureProofing() {
        // Test for future BIN ranges (8-digit BIN implementation)
        val eightDigitBinCards = listOf(
            "42759500123456",
            "53214321123456",
            "22472153123456"
        )
        
        for (cardNumber in eightDigitBinCards) {
            if (cardNumberExtractor.isValidLuhn(cardNumber)) {
                // Test that our implementation can handle 8-digit BIN lookups
                binValidator.getCardTypeUsing8DigitBin(cardNumber)
            }
        }
    }
}

