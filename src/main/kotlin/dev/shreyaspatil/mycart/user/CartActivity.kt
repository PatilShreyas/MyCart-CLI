package dev.shreyaspatil.mycart.user

import dev.shreyaspatil.mycart.model.*
import dev.shreyaspatil.mycart.repository.CartRepository
import dev.shreyaspatil.mycart.repository.CouponRepository
import dev.shreyaspatil.mycart.repository.OrderRepository
import dev.shreyaspatil.mycart.utils.discountFromPercent
import dev.shreyaspatil.mycart.utils.hoursDifference
import java.sql.Timestamp
import java.util.*

class CartActivity {

    private val repository: CartRepository by lazy { CartRepository() }
    private val scanner = Scanner(System.`in`)

    fun start() {
        println(
            """
            =================
            My Cart
            =================
            ITEMS IN YOUR CART
            
        """.trimIndent()
        )

        showCartItems()
    }

    private fun showCartItems() {
        print("\n-------------------------------------------------------------------------------------")
        System.out.format(
            "\n%-20s%-10s%-10s",
            "PRODUCT",
            "QUANTITY",
            "PRICE"
        )
        print("\n-------------------------------------------------------------------------------------")

        repository.getCart()?.let { cart ->
            if (cart.items.isEmpty()) {
                print("\n++ YOUR CART IS EMPTY ++\n")
                return
            }

            var totalAmount = 0.toDouble()

            cart.items.forEach { item ->
                val price = item.product.price * item.quantity
                totalAmount += price

                System.out.format(
                    "\n%-20s%-10d%-10s",
                    item.product.name,
                    item.quantity,
                    "Rs.$price"
                )
            }
            print("\n-------------------------------------------------------------------------------------")
            if (totalAmount != 0.0) {
                System.out.format(
                    "\n%-30s%-30s",
                    "CART TOTAL :=",
                    "Rs.$totalAmount"
                )
                print("\n-------------------------------------------------------------------------------------\n")
                println(
                    """
                    =========
                    MENU
                    =========
                    
                    1) Clear Cart
                    2) Proceed to Buy
                    3) Navigate to Back
                    
                    Select Option: 
                """.trimIndent()
                )

                when (scanner.nextInt()) {
                    1 -> repository.removeAllItems { response ->
                        when (response) {
                            is Response.Success -> println("${response.data} ITEMS REMOVED FROM YOUR CART")
                            is Response.Error -> println("Error Occurred: ${response.message}")
                        }
                    }
                    2 -> proceedToBuy(cart)
                    3 -> return
                    else -> println("INVALID CHOICE!")
                }
            }
        }

        print("\n-------------------------------------------------------------------------------------")
    }

    private fun proceedToBuy(cart: Cart) {
        var coupon: Coupon? = null
        var discountPrice = 0.0
        var isCouponApplied = false


        val basePrice = cart.items.map { it.product.price * it.quantity }.sum()

        print("Want to apply discount coupon? (Y/N): ")

        val couponChoice = scanner.next()
        if (couponChoice.toLowerCase() == "y") {
            print("Coupon Code: ")
            val couponCode = scanner.next()

            coupon = CouponRepository().getCouponByCode(couponCode)

            if (coupon == null) {
                println("INVALID COUPON CODE!")
            } else {
                when (canApplyCoupon(coupon)) {
                    CouponStatus.CAN_USE -> {
                        isCouponApplied = true
                        println("Coupon Applied Successfully!")
                        println("You'll avail ${coupon.discountPercentage}% discount on this order!")

                        discountPrice = discountFromPercent(basePrice, coupon.discountPercentage)
                    }
                    CouponStatus.NOT_VALID -> println("This coupon is not valid! Skipping...")
                    CouponStatus.ALREADY_USED -> println("This Coupon is Already Used! Skipping..")
                    CouponStatus.USAGE_WITHIN_4_HOURS -> println("This coupon can be only used after 4 hours! Skipping")
                }
            }
        }

        if (!isCouponApplied && basePrice >= 10000.0) {
            discountPrice = 500.0
        }

        val payableAmount = basePrice - discountPrice

        println(
            """
            ===============================
            Total Amount:   $basePrice
            Discount:       -$discountPrice
            ===============================
            Amount to Pay:  Rs.$payableAmount
            ===============================
        """.trimIndent()
        )

        print("\n\nContinue with Order? (Y/N): ")
        if (scanner.next().toLowerCase() == "y") {
            val order = OrderDetails(
                orderItems = cart.items,
                invoice = Invoice(
                    appliedCoupon = if (isCouponApplied && coupon != null) coupon.couponCode else null,
                    baseAmount = basePrice,
                    discountAmount = discountPrice,
                    totalAmount = payableAmount
                )
            )

            OrderRepository().addOrder(order) { response ->
                when (response) {
                    is Response.Success -> {
                        println(
                            """
                            ======================================
                            PURCHASE SUCCESSFUL! YOU SAVED Rs.$discountPrice
                            YOUR ORDER ID: ${response.data}
                            ======================================
                        """.trimIndent()
                        )
                    }
                    is Response.Error -> {
                        println(
                            """
                            ======================================
                            Error Occurred: ${response.message}
                            ======================================
                        """.trimIndent()
                        )
                    }
                }
            }
        } else {
            println("Order Cancelled!")
        }
    }

    /**
     * Checks if current user can apply [coupon] or not.
     *
     * It returns-
     * [CouponStatus.CAN_USE] If coupon can be used.
     * [CouponStatus.ALREADY_USED] If coupon can't be used (It's one time only).
     * [CouponStatus.NOT_VALID] If coupon code is not valid.
     * [CouponStatus.USAGE_WITHIN_4_HOURS] If coupon is already applied and applying again
     * within 4 hours of previous coupon.
     */
    private fun canApplyCoupon(coupon: Coupon): CouponStatus {

        // Get current timestamp.
        val now = System.currentTimeMillis()

        // Check if coupon is valid to use or not.
        if (coupon.startDate.time <= now && coupon.endDate.time >= now) {
            val couponUsedTimestamp = OrderRepository().usedCouponTimestamp(coupon.couponCode)

            // If timestamp list is empty, means it's not used by the user.
            if (couponUsedTimestamp.isEmpty()) {
                return CouponStatus.CAN_USE
            } else {
                // Check 4 hours duration
                if (couponUsedTimestamp[0].hoursDifference(Timestamp(now)) >= 4) {
                    // Check Unlimited
                    return when (coupon.usageType) {
                        is UsageType.UnlimitedTime -> {
                            CouponStatus.CAN_USE
                        }

                        is UsageType.MultipleTime -> {
                            if (coupon.usageType.limit >= couponUsedTimestamp.size) {
                                CouponStatus.CAN_USE
                            } else {
                                CouponStatus.ALREADY_USED
                            }
                        }

                        is UsageType.OneTime -> {
                            CouponStatus.ALREADY_USED
                        }
                    }
                } else {
                    return CouponStatus.USAGE_WITHIN_4_HOURS
                }
            }
        } else {
            return CouponStatus.NOT_VALID
        }

    }
}