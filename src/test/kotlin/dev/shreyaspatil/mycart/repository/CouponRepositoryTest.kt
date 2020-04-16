package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import dev.shreyaspatil.mycart.model.Coupon
import dev.shreyaspatil.mycart.model.UsageType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement

@RunWith(MockitoJUnitRunner::class)
class CouponRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    private val testDate = Date(System.currentTimeMillis())

    private val coupon1 = Coupon(
        "ABCDEF",
        UsageType.OneTime(),
        testDate,
        testDate,
        10.0
    )

    private val coupon2 = Coupon(
        "VWXYZ",
        UsageType.MultipleTime(5),
        testDate,
        testDate,
        20.0
    )

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

        whenever(resultSet.first()).thenReturn(true).thenReturn(false)

        whenever(resultSet.next())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        whenever(resultSet.getInt(anyString())).thenReturn(1).thenReturn(2)

        whenever(resultSet.getString(anyString()))
            .thenReturn(coupon1.couponCode)
            .thenReturn(coupon2.couponCode)

        whenever(resultSet.getDate(anyString())).thenReturn(testDate)

        whenever(resultSet.getDouble(anyString()))
            .thenReturn(coupon1.discountPercentage)
            .thenReturn(coupon2.discountPercentage)

        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_get_coupon_by_code() {
        val couponRepository = CouponRepository(connection)

        val newCoupon = couponRepository.getCouponByCode(coupon1.couponCode)!!

        assertThat(newCoupon.couponCode, equalTo(coupon1.couponCode))
        assertThat(newCoupon.discountPercentage, equalTo(coupon1.discountPercentage))
    }

    @Test
    fun test_get_all_coupons() {
        val expectedCoupons = listOf(coupon1, coupon2)
        val couponRepository = CouponRepository(connection)

        val actualCoupons = couponRepository.getAllCoupons()

        assertThat(expectedCoupons.size, equalTo(actualCoupons.size))
        assertThat(expectedCoupons[0].couponCode, equalTo(actualCoupons[0].couponCode))
        assertThat(expectedCoupons[1].couponCode, equalTo(actualCoupons[1].couponCode))
    }

    @Test
    fun test_add_coupon() {
        val couponRepository: CouponRepository = mock()

        couponRepository.addCoupon(coupon1, null)

        verify(couponRepository).addCoupon(coupon1, null)

        verifyNoMoreInteractions(couponRepository)
    }
}