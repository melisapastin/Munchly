package com.example.munchly.domain.services

/**
 * Service responsible for phone number operations.
 * Handles Romanian phone number validation and formatting.
 */
object PhoneFormatter {

    /**
     * Cleans phone number by removing all non-digit characters except +
     * This ensures consistent processing across validation and formatting.
     */
    fun clean(phone: String): String {
        return phone.replace(Regex("[^0-9+]"), "")
    }

    /**
     * Validates Romanian phone number format.
     * Accepts international (+40) and national (07) formats.
     */
    fun isValid(phone: String): Boolean {
        val cleanPhone = clean(phone)
        val romanianPhoneRegex = Regex("^(\\+40|0)(7[0-9]{8})$")
        return cleanPhone.matches(romanianPhoneRegex)
    }

    /**
     * Formats phone number to Romanian standard display format.
     *
     * Supported formats:
     * - International: +40 7XX XXX XXX (e.g., "+40 756 123 456")
     * - National: 07XX XXX XXX (e.g., "0756 123 456")
     *
     * @param phone Raw phone number (may contain spaces, dashes, etc.)
     * @return Formatted phone number, or original if format not recognized
     */
    fun format(phone: String): String {
        val cleanPhone = clean(phone)
        return when {
            cleanPhone.startsWith("+40") && cleanPhone.length == 12 -> {
                "+40 ${cleanPhone.substring(3, 6)} ${cleanPhone.substring(6, 9)} ${cleanPhone.substring(9)}"
            }
            cleanPhone.startsWith("07") && cleanPhone.length == 10 -> {
                "${cleanPhone.substring(0, 4)} ${cleanPhone.substring(4, 7)} ${cleanPhone.substring(7)}"
            }
            else -> phone
        }
    }
}