package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.shreyaspatil.mycart.testUtils.fakeProduct
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
class ProductsRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

//        whenever(resultSet.next())
//            .thenReturn(true)
//            .thenReturn(true)
//            .thenReturn(false)

        whenever(resultSet.getInt(anyString())).thenReturn(0)

        whenever(resultSet.getString(anyString())).thenReturn("")

        whenever(resultSet.getDouble(anyString())).thenReturn(0.0)

        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_get_product_by_id() {
        val expectedProduct = fakeProduct()

        whenever(resultSet.next())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        val productsRepository = ProductsRepository(connection)

        val actualProduct = productsRepository.getProductById(0)

        assertThat(expectedProduct, equalTo(actualProduct))
    }

    @Test
    fun test_get_product_by_category() {
        val expectedProducts = listOf(fakeProduct())

        whenever(resultSet.next())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        val productsRepository = ProductsRepository(connection)

        val actualProducts = productsRepository.getProductsByCategory(0)

        assertThat(expectedProducts, equalTo(actualProducts))
    }

    @Test
    fun test_get_all_products() {
        val expectedProducts = listOf(fakeProduct(), fakeProduct())

        whenever(resultSet.next())
            .thenReturn(true)   // For ProductsRepository ResultSet 1
            .thenReturn(true)   // For CategoryRepository ResultSet
            .thenReturn(false)  // Terminate Category Repository ResultSet
            .thenReturn(true)   // For ProductsRepository ResultSet 2
            .thenReturn(false)  // Terminate ProductsRepository ResultSet

        val productsRepository = ProductsRepository(connection)

        val actualProducts = productsRepository.getAllProducts()

        assertThat(expectedProducts, equalTo(actualProducts))
    }
}