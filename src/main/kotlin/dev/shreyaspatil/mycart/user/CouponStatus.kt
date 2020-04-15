package dev.shreyaspatil.mycart.user

/**
 * Enum class for coupon status.
 * @see CartActivity for usage.
 */
enum class CouponStatus {
    CAN_USE,
    NOT_VALID,
    ALREADY_USED,
    USAGE_WITHIN_4_HOURS
}