package dev.shreyaspatil.mycart.common

import dev.shreyaspatil.mycart.model.Coupon
import dev.shreyaspatil.mycart.model.UsageType

/**
 * Shows All Offers / Coupons of MyCart
 */
class ShowCouponsActivity {
    fun show(coupons: List<Coupon>) {
        print("\n-------------------------------------------------------------------------------------")
        System.out.format(
            "\n%-8s%-20s%-12s%-12s%10s",
            "COUPON",
            "USAGE LIMIT",
            "VALID FROM",
            "EXPIRE ON",
            "DISCOUNT"
        )
        print("\n-------------------------------------------------------------------------------------")

        if (coupons.isEmpty()) {
            print("\n ++ NO OFFERS / COUPONS ++")
        }

        coupons.forEach { coupon ->
            val usageLimit = when (coupon.usageType) {
                is UsageType.OneTime -> "One Time"
                is UsageType.MultipleTime -> "${coupon.usageType.limit} Times"
                is UsageType.UnlimitedTime -> "Unlimited"
            }
            System.out.format(
                "\n%-8s%-20s%-12s%-12s%10s",
                coupon.couponCode,
                usageLimit,
                coupon.startDate,
                coupon.endDate,
                "${coupon.discountPercentage}%"
            )
        }

        print("\n-------------------------------------------------------------------------------------")
    }
}