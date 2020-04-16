package dev.shreyaspatil.mycart.utils

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CalculationUtilsTest {

    @Test
    fun test_discount() {
        val amount = 10000.0
        val discountPercent = 25.0

        assertThat(discountFromPercent(amount, discountPercent), equalTo(2500.0))
    }
}