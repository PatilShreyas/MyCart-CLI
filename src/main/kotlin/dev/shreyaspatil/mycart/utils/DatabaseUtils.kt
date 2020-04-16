package dev.shreyaspatil.mycart.utils

import dev.shreyaspatil.mycart.Config
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database utility class
 */
object DatabaseUtils {

    private var connection: Connection? = null

    /**
     * Returns the established database connection.
     * @throws Exception When database connection is failed.
     * @return Database [Connection]
     */
    @Throws(Exception::class)
    fun getConnection(): Connection? {
        synchronized(this) {
            if (connection == null) {

                Class.forName(Config.DATABASE_DRIVER)

                connection = DriverManager.getConnection(
                    Config.DATABASE_URL,
                    Config.DATABASE_USERNAME,
                    Config.DATABASE_PASSWORD
                )
            }

            return connection
        }
    }
}