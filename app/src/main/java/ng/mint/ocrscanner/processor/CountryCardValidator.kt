package ng.mint.ocrscanner.processor

class CountryCardValidator {
    // BIN prefixes for SEPA region cards
    private val sepaRegionPrefixes = setOf(
        "45", // Visa EU
        "51", "52", "53", "54", "55", // Mastercard EU
        "34", "37", // American Express EU
        "50", "56", "57", "58", "67", // Maestro EU
        "4026", "4405", "4508", "4844", "4913", "4917", // Visa Electron EU
        "6759", "676770", "676774" // Maestro UK
    )

    // BIN prefixes for French CB cards
    private val cbCardPrefixes = setOf(
        "4035", "4360", "4387", // Visa CB
        "5135", "5136", "5137", "5138", "5139", // Mastercard CB
        "5355", "5356", "5357", "5358", "5359", // Mastercard CB
        "5713", "5714", "5715", "5716", "5717", // Mastercard CB
        "5725", "5726", "5727", "5728", "5729"  // Mastercard CB
    )

    // BIN prefixes for German Girocard
    private val girocardPrefixes = setOf(
        "4911", "4913", "4917", // Visa/Girocard co-branded
        "5091", "5092", "5093", "5094", "5095", // Mastercard/Girocard co-branded
        "6803", "6804", "6805", "6806", "6807", // Maestro/Girocard co-branded
        "6808", "6809", "6810", "6811", "6812"  // Maestro/Girocard co-branded
    )

    /**
     * Checks if the card is from the SEPA (Single Euro Payments Area) region
     * @param bin The BIN to check
     * @return true if the card is from the SEPA region
     */
    fun isSepaRegionCard(bin: String): Boolean {
        return sepaRegionPrefixes.any { prefix ->
            bin.startsWith(prefix)
        }
    }

    /**
     * Checks if the card is a French Carte Bancaire (CB)
     * @param bin The BIN to check
     * @return true if the card is a CB card
     */
    fun isCbCard(bin: String): Boolean {
        return cbCardPrefixes.any { prefix ->
            bin.startsWith(prefix)
        }
    }

    /**
     * Checks if the card is a German Girocard
     * @param bin The BIN to check
     * @return true if the card is a Girocard
     */
    fun isGirocard(bin: String): Boolean {
        return girocardPrefixes.any { prefix ->
            bin.startsWith(prefix)
        }
    }
}
