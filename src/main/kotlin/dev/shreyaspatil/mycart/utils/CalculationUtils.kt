package dev.shreyaspatil.mycart.utils

/**
 * Calculates the percent discount.
 * @param amount Amount of which discount is to be calculated
 * @param percent Percent discount to be calculated
 * @return Discount value
 */
fun discountFromPercent(amount: Double, percent: Double) = amount * (percent / 100.0)