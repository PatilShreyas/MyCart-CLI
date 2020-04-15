package dev.shreyaspatil.mycart.model

import java.sql.Date

sealed class UsageType {
    class OneTime : UsageType()
    class MultipleTime(val limit: Int) : UsageType()
    class UnlimitedTime : UsageType()
}

data class Coupon(
    val couponCode: String,
    val usageType: UsageType,
    val startDate: Date,
    val endDate: Date,
    val discountPercentage: Double
) {

    companion object {
        fun from(
            couponCode: String,
            usageType: Int,
            usageLimit: Int,
            startDate: Date,
            endDate: Date,
            discountPercentage: Double
        ): Coupon {
            val type = when (usageType) {
                1 -> UsageType.OneTime()
                2 -> {
                    if (usageLimit < 1) {
                        throw IllegalArgumentException("Usage Limit should be positive")
                    }

                    UsageType.MultipleTime(usageLimit)
                }

                3 -> UsageType.UnlimitedTime()

                else -> {
                    throw Exception("Invalid `usageType`")
                }
            }

            return Coupon(
                couponCode = couponCode,
                usageType = type,
                startDate = startDate,
                endDate = endDate,
                discountPercentage = discountPercentage
            )
        }

        const val TABLE_NAME = "coupon"

        const val COLUMN_COUPON_CODE = "COUPON_CODE"
        const val COLUMN_USAGE_TYPE = "USAGE_TYPE"
        const val COLUMN_USAGE_LIMIT = "USAGE_LIMIT"
        const val COLUMN_START_DATE = "START_DATE"
        const val COLUMN_END_DATE = "END_DATE"
        const val COLUMN_DISCOUNT = "DISCOUNT_PERCENTAGE"

        const val VALUE_ONE_TIME_USAGE = 1
        const val VALUE_MULTIPLE_TIME_USAGE = 2
        const val VALUE_UNLIMITED_TIME_USAGE = 3
    }
}
