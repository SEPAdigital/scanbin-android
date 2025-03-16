package ng.mint.ocrscanner.validation

import androidx.test.ext.junit.runners.AndroidJUnit4
import ng.mint.ocrscanner.processor.BinValidator
import ng.mint.ocrscanner.processor.CardNumberExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.math.min

/**
 * Test class for validating card formats specific to different European countries:
 * - Austria (AT)
 * - Germany (DE)
 * - Italy (IT)
 * - Greece (GR)
 * - Croatia (HR)
 */
@RunWith(AndroidJUnit4::class)
class CountrySpecificCardTest {
    private lateinit var cardNumberExtractor: CardNumberExtractor
    private lateinit var binValidator: BinValidator

    @Before
    fun setUp() {
        cardNumberExtractor = CardNumberExtractor()
        binValidator = BinValidator()
    }

    @Test
    fun testAustrianMaestroCards() {
        val maestroCards = listOf(
            generateValidCardNumber("51552", 16),
            generateValidCardNumber("51559", 16),
            generateValidCardNumber("53690", 16),
            generateValidCardNumber("53699", 16)
        )

        maestroCards.forEach { cardNumber ->
            assertTrue("Austrian Maestro card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Austrian Maestro card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Maestro", 
                "Maestro", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testAustrianPSACards() {
        val psaCards = listOf(
            generateValidCardNumber("52100", 16),
            generateValidCardNumber("52109", 16),
            generateValidCardNumber("58970", 16),
            generateValidCardNumber("58979", 16)
        )

        psaCards.forEach { cardNumber ->
            assertTrue("Austrian PSA card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Austrian PSA card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testGermanGirocardCards() {
        val girocardCards = listOf(
            generateValidCardNumber("49000", 16),
            generateValidCardNumber("49009", 16),
            generateValidCardNumber("49110", 16),
            generateValidCardNumber("49119", 16),
            generateValidCardNumber("49210", 16),
            generateValidCardNumber("49219", 16)
        )

        girocardCards.forEach { cardNumber ->
            assertTrue("German Girocard should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("German Girocard should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Visa", 
                "Visa", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testGermanVPayCards() {
        val vpayCards = listOf(
            generateValidCardNumber("42820", 16),
            generateValidCardNumber("42829", 16),
            generateValidCardNumber("42860", 16),
            generateValidCardNumber("42869", 16)
        )

        vpayCards.forEach { cardNumber ->
            assertTrue("German VPay card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("German VPay card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Visa", 
                "Visa", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testItalianPagoBancomatCards() {
        val pagoBancomatCards = listOf(
            generateValidCardNumber("50700", 16),
            generateValidCardNumber("50709", 16),
            generateValidCardNumber("53550", 16),
            generateValidCardNumber("53559", 16)
        )

        pagoBancomatCards.forEach { cardNumber ->
            assertTrue("Italian PagoBancomat card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Italian PagoBancomat card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testItalianCartaSiCards() {
        val cartaSiCards = listOf(
            generateValidCardNumber("55740", 16),
            generateValidCardNumber("55749", 16),
            generateValidCardNumber("55990", 16),
            generateValidCardNumber("55999", 16)
        )

        cartaSiCards.forEach { cardNumber ->
            assertTrue("Italian CartaSi card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Italian CartaSi card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testGreekEthnocashCards() {
        val ethnocashCards = listOf(
            generateValidCardNumber("53330", 16),
            generateValidCardNumber("53339", 16),
            generateValidCardNumber("53960", 16),
            generateValidCardNumber("53969", 16)
        )

        ethnocashCards.forEach { cardNumber ->
            assertTrue("Greek Ethnocash card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Greek Ethnocash card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testGreekAlphaBankCards() {
        val alphaBankCards = listOf(
            generateValidCardNumber("55760", 16),
            generateValidCardNumber("55769", 16),
            generateValidCardNumber("55890", 16),
            generateValidCardNumber("55899", 16)
        )

        alphaBankCards.forEach { cardNumber ->
            assertTrue("Greek Alpha Bank card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Greek Alpha Bank card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testCroatianMaestroCards() {
        val maestroCards = listOf(
            generateValidCardNumber("52730", 16),
            generateValidCardNumber("52739", 16),
            generateValidCardNumber("55910", 16),
            generateValidCardNumber("55919", 16)
        )

        maestroCards.forEach { cardNumber ->
            assertTrue("Croatian Maestro card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Croatian Maestro card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Maestro", 
                "Maestro", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testCroatianPBZCards() {
        val pbzCards = listOf(
            generateValidCardNumber("42880", 16),
            generateValidCardNumber("42889", 16),
            generateValidCardNumber("54340", 16),
            generateValidCardNumber("54349", 16)
        )

        pbzCards.forEach { cardNumber ->
            assertTrue("Croatian PBZ card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Croatian PBZ card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            val expectedType = if (cardNumber.startsWith("4")) "Visa" else "Mastercard"
            assertEquals("Card should be identified correctly", 
                expectedType, binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testCroatianZABACards() {
        val zabaCards = listOf(
            generateValidCardNumber("52540", 16),
            generateValidCardNumber("52549", 16),
            generateValidCardNumber("55930", 16),
            generateValidCardNumber("55939", 16)
        )

        zabaCards.forEach { cardNumber ->
            assertTrue("Croatian ZABA card should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("Croatian ZABA card should be identified as EU card", 
                binValidator.isEuCard(cardNumber))
            assertEquals("Card should be identified as Mastercard", 
                "Mastercard", binValidator.getCardType(cardNumber))
        }
    }

    @Test
    fun testCrossCountryValidation() {
        val allEuCards = listOf(
            generateValidCardNumber("51552", 16),
            generateValidCardNumber("52100", 16),
            generateValidCardNumber("49000", 16),
            generateValidCardNumber("42820", 16),
            generateValidCardNumber("50700", 16),
            generateValidCardNumber("55740", 16),
            generateValidCardNumber("53330", 16),
            generateValidCardNumber("55760", 16),
            generateValidCardNumber("52730", 16),
            generateValidCardNumber("42880", 16),
            generateValidCardNumber("52540", 16)
        )

        allEuCards.forEach { cardNumber ->
            assertTrue("All EU card formats should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertTrue("All EU card formats should be identified as EU cards", 
                binValidator.isEuCard(cardNumber))
        }
    }

    @Test
    fun testNonEuCards() {
        val nonEuCards = listOf(
            generateValidCardNumber("37123", 15),
            generateValidCardNumber("60110", 16),
            generateValidCardNumber("35301", 16)
        )

        nonEuCards.forEach { cardNumber ->
            assertTrue("Card number should be valid: $cardNumber", 
                cardNumberExtractor.isValidCardNumber(cardNumber))
            assertFalse("Card should not be identified as EU card", 
                binValidator.isEuCard(cardNumber))
        }
    }

    @Test
    fun testBankSpecificRules() {
        val deutscheBankCard = generateValidCardNumber("49127", 16)
        assertTrue("Deutsche Bank card should be valid", 
            cardNumberExtractor.isValidCardNumber(deutscheBankCard))
        assertTrue("Deutsche Bank card should be identified as EU card", 
            binValidator.isEuCard(deutscheBankCard))

        val unicreditItalyCard = generateValidCardNumber("53831", 16)
        assertTrue("Unicredit Italy card should be valid", 
            cardNumberExtractor.isValidCardNumber(unicreditItalyCard))
        assertTrue("Unicredit Italy card should be identified as EU card", 
            binValidator.isEuCard(unicreditItalyCard))

        val nbgCard = generateValidCardNumber("54991", 16)
        assertTrue("National Bank of Greece card should be valid", 
            cardNumberExtractor.isValidCardNumber(nbgCard))
        assertTrue("National Bank of Greece card should be identified as EU card", 
            binValidator.isEuCard(nbgCard))

        val ersteCard = generateValidCardNumber("52120", 16)
        assertTrue("Erste Bank Croatia card should be valid", 
            cardNumberExtractor.isValidCardNumber(ersteCard))
        assertTrue("Erste Bank Croatia card should be identified as EU card", 
            binValidator.isEuCard(ersteCard))
    }

    @Test
    fun testCountryCardPatternValidation() {
        val austrianPSAPattern = "421575000012345678"
        assertTrue("Austrian PSA card should be valid", 
            cardNumberExtractor.isValidCardNumber(austrianPSAPattern))

        val germanGirocardPattern = "4913450123456789"
        assertTrue("German Girocard pattern should be valid", 
            cardNumberExtractor.isValidCardNumber(germanGirocardPattern))

        val italianPagoBancomatPattern = "5779250123456789012"
        assertTrue("Italian PagoBancomat pattern should be valid with non-standard length", 
            cardNumberExtractor.isValidCardNumber(italianPagoBancomatPattern))

        val greekEthnocashPattern = "5387260987654321"
        assertTrue("Greek Ethnocash pattern should be valid", 
            cardNumberExtractor.isValidCardNumber(greekEthnocashPattern))

        val croatianBankPattern = "5262350123456789"
        assertTrue("Croatian bank card pattern should be valid", 
            cardNumberExtractor.isValidCardNumber(croatianBankPattern))
    }

    private fun generateValidCardNumber(binPrefix: String, length: Int = 16): String {
        val prefix = binPrefix.take(min(binPrefix.length, length - 1))
        val randomPart = StringBuilder()
        val remainingDigits = length - prefix.length - 1

        repeat(remainingDigits) {
            randomPart.append(Random.nextInt(0, 10))
        }

        val cardWithoutChecksum = prefix + randomPart.toString()
        val checksumDigit = calculateLuhnChecksum(cardWithoutChecksum)

        return cardWithoutChecksum + checksumDigit
    }

    private fun calculateLuhnChecksum(cardNumber: String): Int {
        val digitsOnly = cardNumber.filter { it.isDigit() }
        var sum = 0
        var alternate = false

        for (i in digitsOnly.length - 1 downTo 0) {
            var digit = digitsOnly[i].toString().toInt()
            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            sum += digit
            alternate = !alternate
        }

        return (10 - (sum % 10)) % 10
    }
}

