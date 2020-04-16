package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import dev.shreyaspatil.mycart.model.Category
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
class CategoryRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    private val category1 = Category(1, "Category")
    private val category2 = Category(2, "Category")

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

        whenever(resultSet.next()).thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        whenever(resultSet.getInt(anyString())).thenReturn(1).thenReturn(2)
        whenever(resultSet.getString(anyString())).thenReturn("Category")

        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_get_category() {
        val expected = listOf(category1, category2)

        val categoryRepository = CategoryRepository(connection)

        val actual = categoryRepository.getAllCategories()

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun test_add_category() {
        val categoryRepository: CategoryRepository = mock()

        categoryRepository.addCategory(category1, null)

        verify(categoryRepository).addCategory(category1, null)

        verifyNoMoreInteractions(categoryRepository)
    }
}