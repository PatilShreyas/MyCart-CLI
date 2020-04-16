package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.*
import dev.shreyaspatil.mycart.model.Cart
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

@RunWith(MockitoJUnitRunner::class)
class CartRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    private val cart = Cart(listOf(fakeItem()))

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

        whenever(resultSet.next()).thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        whenever(resultSet.getInt(anyString())).thenReturn(0)
        whenever(resultSet.getString(anyString())).thenReturn("")
        whenever(resultSet.getDouble(anyString())).thenReturn(0.0)

        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_get_cart() {
        val cartRepository = CartRepository(connection, fakeUser())
        val newCart = cartRepository.getCart()

        assertThat(newCart, equalTo(cart))
    }

    @Test
    fun test_cart_add_items() {
        val cartRepository: CartRepository = mock()

        cartRepository.addItemToCart(fakeItem(), null)
        cartRepository.addItemToCart(fakeItem(), null)

        verify(cartRepository, times(2)).addItemToCart(fakeItem(), null)

        verifyNoMoreInteractions(cartRepository)
    }

    @Test
    fun test_remove_items() {
        val cartRepository: CartRepository = mock()

        whenever(cartRepository.removeAllItems(null)).then {}
        cartRepository.removeAllItems(null)

        verify(cartRepository).removeAllItems(null)

        verifyNoMoreInteractions(cartRepository)
    }
}