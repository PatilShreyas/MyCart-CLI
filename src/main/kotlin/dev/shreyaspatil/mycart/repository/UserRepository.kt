package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.User
import dev.shreyaspatil.mycart.session.UserType
import java.sql.Connection

interface AbstractUserRepository {
    fun getUserByUsernameAndPassword(type: UserType, username: String, password: String): User?
}

/**
 * Users and Admin repository of MyCart
 */
class UserRepository(private val connection: Connection) : AbstractUserRepository {

    /**
     * Returns a user by specified [type], [username] and [password].
     */
    @Throws(Exception::class)
    override fun getUserByUsernameAndPassword(
        type: UserType,
        username: String,
        password: String
    ): User? {
        var user: User? = null

        val tableName = when (type) {
            UserType.ADMIN -> "admin"
            UserType.USER -> "user"
        }

        val query = """
            SELECT * FROM $tableName 
            WHERE ${User.COLUMN_NAME_USERNAME} = '$username' 
            AND 
            ${User.COLUMN_NAME_PASSWORD} = '$password'
        """.trimIndent()

        connection.createStatement()?.let { statement ->
            val userResultSet = statement.executeQuery(query)

            if (userResultSet.next()) {
                user = User(
                    userResultSet.getString(User.COLUMN_NAME_UID),
                    userResultSet.getString(User.COLUMN_NAME_NAME),
                    userResultSet.getString(User.COLUMN_NAME_USERNAME)
                )
            }

            userResultSet.close()
            statement.close()
        }

        return user
    }
}