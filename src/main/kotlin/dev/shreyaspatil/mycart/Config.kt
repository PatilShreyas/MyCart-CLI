package dev.shreyaspatil.mycart

import java.sql.DriverManager

object Config {

    /**
     * Database driver required to initialize connection with the database.
     */
    const val DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver"

    /**
     * Database host address
     */
    const val DATABASE_HOST = "localhost:3306"

    /**
     * Database username
     */
    const val DATABASE_USERNAME = "root"

    /**
     * Database password
     */
    const val DATABASE_PASSWORD = ""

    /**
     * Name of the database where application's all data is stored
     */
    const val DATABASE_NAME = "mykart"

    /**
     * Database URL which can be used with [DriverManager.getConnection] method.
     */

    const val DATABASE_URL = "jdbc:mysql://$DATABASE_HOST/$DATABASE_NAME?serverTimezone=IST"
}
