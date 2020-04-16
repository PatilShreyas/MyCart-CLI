package dev.shreyaspatil.mycart.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.shreyaspatil.mycart.model.User
import dev.shreyaspatil.mycart.session.UserType
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
class UserRepositoryTest {

    private val connection: Connection = mock()
    private val statement: Statement = mock()
    private val resultSet: ResultSet = mock()

    lateinit var user: User

    @Before
    fun setUp() {
        whenever(connection.createStatement()).thenReturn(statement)

        val userValue = "ADMIN"
        user = User(userValue, userValue, userValue)

        whenever(resultSet.next()).thenReturn(true).thenReturn(false)
        whenever(resultSet.getString(anyString())).thenReturn(userValue)
        whenever(statement.executeQuery(anyString())).thenReturn(resultSet)
    }

    @Test
    fun test_get_user() {
        val userRepository = UserRepository(connection)

        val nowUser = userRepository.getUserByUsernameAndPassword(UserType.ADMIN, "ADMIN", "ADMIN")

        assertThat(nowUser, equalTo(user))
    }
}