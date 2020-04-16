package dev.shreyaspatil.mycart

import dev.shreyaspatil.mycart.admin.AdminActivity
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.session.SessionManager
import dev.shreyaspatil.mycart.session.UserType
import dev.shreyaspatil.mycart.user.UserActivity
import dev.shreyaspatil.mycart.utils.DatabaseUtils
import java.sql.Connection
import java.util.*

/**
 * Main class of MyCart Application
 */
class MyCart(private val connection: Connection) {

    private val scanner = Scanner(System.`in`)

    fun start() {
        showMainMenu()
        cleanup()
    }

    /**
     * Cleanup resources at the end of execution
     */
    private fun cleanup() {
        scanner.close()
        connection.close()
    }

    private fun showMainMenu() {

        val mainMenuString =
            """
            ------------------------------------------------------------
            WELCOME TO MyCart
            ------------------------------------------------------------
            MENU
            
            1) Login
            2) Exit
            
            Select Action: 
        """.trimIndent()

        var input = 0

        while (input != 2) {
            println(mainMenuString)
            input = scanner.nextInt()

            when (input) {
                1 -> showLoginMenu()
                2 -> println("Exiting")
                else -> println("INVALID INPUT!")
            }
        }
    }

    private fun showLoginMenu() {

        val loginMenuString =
            """
            ------------------------------------------------------------
            ACTION: LOGIN
            ------------------------------------------------------------
            Login as
            
            1) User
            2) Admin
            3) Navigate Back
            
            Select Option: 
        """.trimIndent()

        var action = 0

        while (action != 3) {
            println(loginMenuString)

            action = scanner.nextInt()

            val userType = when (action) {
                1 -> UserType.USER
                2 -> UserType.ADMIN
                3 -> {
                    println("Navigating back")
                    null
                }
                else -> {
                    null
                }
            }

            userType?.let { type ->
                println("\nEnter Your Credentials:")

                print("\nUsername: ")
                val username = scanner.next()

                print("Password: ")
                val password = scanner.next()

                SessionManager.signIn(type, username, password) { response ->
                    when (response) {
                        is Response.Success -> {
                            println(
                                """
                                ================================================
                                LOGIN SUCCEED
                                ================================================
                            """.trimIndent()
                            )

                            when (SessionManager.currentUserType) {
                                UserType.USER -> UserActivity().start()
                                UserType.ADMIN -> AdminActivity().start()
                            }
                        }

                        is Response.Error -> {
                            println(
                                """
                                ================================================
                                ERROR OCCURRED: ${response.message}
                                ================================================
                            """.trimIndent()
                            )
                        }
                    } // END when
                } // END SessionManager Scope
            } // END UserType Scope
        } // END While
    }
}

fun main() {
    // Start Application
    try {
        // Get Database connection instance.
        MyCart(DatabaseUtils.getConnection()!!).start()
    } catch (e: Exception) {
        println("!!! Failed to connect with MyCart Database !!!")
    }
}
