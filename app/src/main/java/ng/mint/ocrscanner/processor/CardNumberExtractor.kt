package ng.mint.ocrscanner.processor

class CardNumberExtractor {
    fun validateCardNumber(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        
        // Check for valid input
        if (!cleanNumber.matches("^[0-9]+$".toRegex())) return false
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false
        
        return validateLuhnChecksum(cleanNumber)
    }

    fun validatePartialCardNumber(partialNumber: String): Boolean {
        val cleanNumber = partialNumber.replace("\\s".toRegex(), "")
        return cleanNumber.matches("^[0-9]+$".toRegex())
    }

    internal fun validateLuhnChecksum(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        if (!cleanNumber.matches("^[0-9]+$".toRegex())) return false
        
        var sum = 0
        var alternate = true
        
        // Starting from the rightmost digit
        for (i in cleanNumber.length - 1 downTo 0) {
            var digit = cleanNumber[i] - '0'
            
            if (alternate) {
                sum += digit
            } else {
                digit *= 2
                sum += if (digit > 9) digit - 9 else digit
            }
            
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }

    fun checkLuhn(cardNumber: String): Boolean {
        // Clean the input of spaces before validating
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        if (!cleanNumber.matches("^[0-9]+$".toRegex())) return false
        return validateLuhnChecksum(cleanNumber)
    }

    fun extractCardNumber(rawText: String): String? {
        val regex = "\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}".toRegex()
        val match = regex.find(rawText) ?: return null
        val number = match.value.replace("[\\s-]".toRegex(), "")
        return if (validateCardNumber(number)) formatCardNumber(number) else null
    }

    fun getCardTypeFromBin(cardNumber: String): String {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        val bin = if (cleanNumber.length >= 6) cleanNumber.substring(0, 6) else cleanNumber

        return when {
            bin.startsWith("4") -> "Visa"
            bin.matches("^(51|52|53|54|55)".toRegex()) -> "Mastercard"
            bin.matches("^(34|37)".toRegex()) -> "American Express"
            else -> "Unknown"
        }
    }

    fun extractBin(cardNumber: String, length: Int = 8): String {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        return if (cleanNumber.length >= length) {
            cleanNumber.substring(0, length)
        } else {
            cleanNumber
        }
    }

    fun formatCardNumber(cardNumber: String): String {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        return cleanNumber.chunked(4).joinToString(" ")
    }
}
