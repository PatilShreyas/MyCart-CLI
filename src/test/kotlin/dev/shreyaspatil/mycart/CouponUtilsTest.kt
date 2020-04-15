package dev.shreyaspatil.mycart

import dev.shreyaspatil.mycart.utils.isValidCouponCode
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CouponUtilsTest {

    @Test
    fun test_valid_coupon() {
        var couponCode = "ABCDEF"
        assertThat(isValidCouponCode(couponCode), equalTo(true))

        couponCode = "ABC123"
        assertThat(isValidCouponCode(couponCode), equalTo(true))

        couponCode = "123ABC"
        assertThat(isValidCouponCode(couponCode), equalTo(true))

        couponCode = "COOL50"
        assertThat(isValidCouponCode(couponCode), equalTo(true))
    }

    @Test
    fun test_invalid_coupon() {
        var couponCode = "ABCDEFGHIJ"
        assertThat(isValidCouponCode(couponCode), equalTo(false))

        couponCode = "AB"
        assertThat(isValidCouponCode(couponCode), equalTo(false))

        couponCode = ""
        assertThat(isValidCouponCode(couponCode), equalTo(false))
    }
}