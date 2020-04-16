package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import dev.shreyaspatil.mycart.model.Invoice
import dev.shreyaspatil.mycart.model.OrderDetails
import dev.shreyaspatil.mycart.testUtils.fakeItem
import dev.shreyaspatil.mycart.testUtils.fakeUser
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Timestamp

@RunWith(MockitoJUnitRunner::class)
class OrderRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    private val testTimestamp = Timestamp(System.currentTimeMillis())

    private val order = OrderDetails(
        orderId = 1,
        invoice = Invoice(1, "", 0.0, 0.0, 0.0),
        timestamp = testTimestamp,
        orderItems = listOf(fakeItem())
    )

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

        whenever(resultSet.next())
            .thenReturn(true)
            .thenReturn(false)

        whenever(resultSet.getInt(anyString())).thenReturn(1)

        whenever(resultSet.getString(anyString())).thenReturn("")

        whenever(resultSet.getTimestamp(anyString())).thenReturn(testTimestamp)

        whenever(resultSet.getDouble(anyString())).thenReturn(0.0)

        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_used_coupons_timestamp() {
        val expected = listOf(testTimestamp)

        val orderRepository = OrderRepository(connection, fakeUser())

        val actual = orderRepository.usedCouponTimestamp("")

        assertThat(expected, equalTo(actual))
    }

    @Test
    fun test_get_all_orders() {
        val expectedOrder = listOf(order)
        val orderRepository = OrderRepository(connection, fakeUser())

        val actualOrder = orderRepository.getOrderDetails()

        assertThat(actualOrder.size, equalTo(expectedOrder.size))
        assertThat(actualOrder[0].orderId, equalTo(expectedOrder[0].orderId))
        assertThat(actualOrder[0].timestamp, equalTo(expectedOrder[0].timestamp))
    }

    @Test
    fun test_add_order() {
        val orderRepository: OrderRepository = mock()

        orderRepository.addOrder(order, null)

        verify(orderRepository).addOrder(order, null)

        verifyNoMoreInteractions(orderRepository)
    }
}