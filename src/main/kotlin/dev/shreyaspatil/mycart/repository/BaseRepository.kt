package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.utils.DatabaseUtils
import java.sql.Connection

/**
 * Abstract Base repository for all repositories of MyCart application.
 * Repository will be the single source of the data in this application.
 */
abstract class BaseRepository {
    protected lateinit var connection: Connection

    init {
        try {
            connection = DatabaseUtils.getConnection()!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}