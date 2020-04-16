package dev.shreyaspatil.mycart.admin

import dev.shreyaspatil.mycart.common.ShowCouponsActivity
import dev.shreyaspatil.mycart.model.Coupon
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.repository.CouponRepository
import dev.shreyaspatil.mycart.utils.DatabaseUtils
import dev.shreyaspatil.mycart.utils.isValidCouponCode
import dev.shreyaspatil.mycart.utils.minus
import java.sql.Date
import java.util.*


class CouponActivity {

    private val scanner by lazy { Scanner(System.`in`) }
    private val repository by lazy { CouponRepository(DatabaseUtils.getConnection()!!) }

    fun start() {
        showMainMenu()
    }

    private fun showMainMenu() {
        val mainMenuString =
            """
            ------------------------------------------------------------
            ** COUPON **
            ------------------------------------------------------------
            MENU
            
            1) Add Coupon
            2) View Coupons
            3) Navigate Back
        """.trimIndent()

        var input = 0

        loop@ while (input != 3) {
            println(mainMenuString)
            input = scanner.nextInt()

            when (input) {
                1 -> addCoupon()
                2 -> showCoupons()
                3 -> break@loop
                else -> println("INVALID CHOICE!")
            }
        }
    }

    private fun showCoupons() {
        ShowCouponsActivity().show(repository.getAllCoupons())
    }

    private fun addCoupon() {
        println(
            """
            ------------------------------------------------------------
            ** ADD COUPON **
            ------------------------------------------------------------
            
            ++ ENTER COUPON DETAILS ++
        """.trimIndent()
        )

        println("** Coupon Code should be Alpha-Numeric and Min 3 & Max 6-character long **\n")
        print("Coupon Code: ")
        val couponCode = scanner.next()

        if (!isValidCouponCode(couponCode)) {
            println("INVALID COUPON CODE!")
            return
        }

        repository.getAllCoupons().find { it.couponCode == couponCode }.let {
            if (it != null) {
                println("Coupon '$couponCode' Already Exists!")
                return
            }
        }

        val usageTypeMenuString = """
            --------------------------
            COUPON USAGE TYPE
            
            1) One Time 
            2) Multiple Time
            3) Unlimited Time

            Select Usage type: 
        """.trimIndent()

        print(usageTypeMenuString)

        val usageType = scanner.nextInt()

        if (usageType !in 1..3) {
            println("Invalid Usage Type Selected!")
            return
        }

        val usageLimit: Int
        if (usageType == Coupon.VALUE_MULTIPLE_TIME_USAGE) {
            print("Usage Limit: ")

            usageLimit = scanner.nextInt()

            if (usageLimit < 1) {
                println("Limit should be positive!")
                return
            }

        } else {
            usageLimit = 0
        }
        val startDate: Date
        val endDate: Date

        try {
            val calendar = Calendar.getInstance()

            // Get current date
            val now = Date.valueOf(
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
            )

            print("Start Date (YYYY-MM-DD): ")
            startDate = Date.valueOf(scanner.next())

            // Check if entered start date is passed or not
            if ((startDate - now) < 0) {
                println("Entered Start Date is already passed!")
                return
            }

            print("End Date (YYYY-MM-DD): ")
            endDate = Date.valueOf(scanner.next())

            // Check if entered end date is passed or not
            if ((endDate - now) < 0) {
                println("Entered End Date is already passed!")
                return
            }
        } catch (e: Exception) {
            println("Invalid Date Format!")
            return
        }

        print("Discount (%): ")
        val discountPercent = scanner.nextDouble()

        // Check if discount entered is in valid range or not.
        if (discountPercent !in 1.0..100.0) {
            println("Invalid Discount %")
            return
        }

        val coupon = Coupon.from(
            couponCode = couponCode,
            usageType = usageType,
            usageLimit = usageLimit,
            startDate = startDate,
            endDate = endDate,
            discountPercentage = discountPercent
        )

        repository.addCoupon(coupon) { response ->
            when (response) {
                is Response.Success -> {
                    println(
                        """
                            ==========================================================
                            $discountPercent% DISCOUNT COUPON '${couponCode}' ADDED SUCCESSFULLY!
                            ==========================================================
                    """.trimIndent()
                    )
                }
                is Response.Error -> {
                    println(
                        """
                            ================================================
                            Error Occurred: ${response.message}
                            ================================================
                    """.trimIndent()
                    )
                }
            }
        }
    }
}
