package ng.mint.ocrscanner.processor

class BinValidator {

    private val countryCardValidator = CountryCardValidator()
    private val cardNumberExtractor = CardNumberExtractor()

    /**
     * Validates if a given BIN is valid
     * @param bin The BIN to validate (first 6-8 digits of card number)
     * @return true if the BIN is valid
     */
    fun isValidBin(bin: String): Boolean {
        if (!bin.matches("^[0-9]{6,8}$".toRegex())) return false
        val cardScheme = getCardScheme(bin)
        return cardScheme != "Unknown"
    }

    /**
     * Checks if the card is from the EU region
     * @param cardNumber The card number to check
     * @return true if the card is from the EU
     */
    fun isEuropeanCard(cardNumber: String?): Boolean {
        if (cardNumber.isNullOrEmpty()) return false
        val bin = extractBin(cardNumber, false)
        return when {
            bin.startsWith("4") -> true // Visa
            bin.matches("^(51|52|53|54|55).*".toRegex()) -> true // Mastercard
            bin.matches("^(22|23|24|25|26|27).*".toRegex()) -> true // Mastercard 2-series
            bin.matches("^(34|37).*".toRegex()) -> true // American Express
            bin.startsWith("50") -> true // Maestro
            bin.startsWith("67") -> true // Carte Bancaire
            else -> false
        }
    }

    /**
     * Extracts the BIN from a card number
     * @param cardNumber The card number
     * @param extended If true, returns 8-digit BIN, otherwise 6-digit BIN
     * @return The extracted BIN
     */
    fun extractBin(cardNumber: String, extended: Boolean = true): String {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        val length = if (extended) 8 else 6
        return if (cleanNumber.length >= length) {
            cleanNumber.substring(0, length)
        } else {
            cleanNumber
        }
    }

    /**
     * Gets the card scheme based on BIN
     * @param cardNumber The card number or BIN
     * @return The card scheme (e.g., "Visa", "Mastercard")
     */
    fun getCardScheme(cardNumber: String): String {
        val bin = if (cardNumber.length > 6) extractBin(cardNumber, false) else cardNumber
        return when {
            bin.startsWith("4") -> "Visa"
            bin.matches("^(51|52|53|54|55).*".toRegex()) -> "Mastercard"
            bin.matches("^(22|23|24|25|26|27).*".toRegex()) -> "Mastercard"
            bin.matches("^(34|37).*".toRegex()) -> "American Express"
            bin.startsWith("50") -> "Maestro"
            bin.startsWith("67") -> "Carte Bancaire"
            else -> "Unknown"
        }
    }

    /**
     * Checks if the card is a Carte Bancaire (French CB card)
     * @param bin The BIN to check
     * @return true if the card is a CB card
     */
    fun isCbCard(bin: String): Boolean {
        return countryCardValidator.isCbCard(bin)
    }

    /**
     * Checks if the card is a German Girocard
     * @param bin The BIN to check
     * @return true if the card is a Girocard
     */
    fun isGirocard(bin: String): Boolean {
        return countryCardValidator.isGirocard(bin)
    }
}
