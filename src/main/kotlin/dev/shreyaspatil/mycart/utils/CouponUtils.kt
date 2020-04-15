package dev.shreyaspatil.mycart.utils

/**
 * Checks if [couponCode] is valid of not.
 * Returns true if coupon length is in range of 3-6 and it contains only letters or digit.
 * Otherwise false.
 */
fun isValidCouponCode(couponCode: String): Boolean =
    (couponCode.length in 3..6 && couponCode.chars().allMatch(Character::isLetterOrDigit))
