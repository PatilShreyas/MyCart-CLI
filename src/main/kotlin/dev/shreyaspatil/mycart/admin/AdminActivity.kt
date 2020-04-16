package dev.shreyaspatil.mycart.admin

import dev.shreyaspatil.mycart.model.User
import dev.shreyaspatil.mycart.session.SessionManager
import java.util.*

class AdminActivity {

    private lateinit var adminUser: User
    private val scanner = Scanner(System.`in`)

    init {
        try {
            adminUser = SessionManager.currentUser!!
        } catch (e: Exception) {
            println("ACCESS DENIED!")
        }
    }

    fun start() {
        showMainMenu()
    }

    private fun showMainMenu() {

        var input = 0

        while (input != 4) {
            val mainMenuString = """
            ------------------------------------------------------------
            WELCOME ${adminUser.name} (Admin) to MyCart
            ------------------------------------------------------------
            MENU
            
            1) Categories
            2) Products
            3) Coupons
            4) Sign Out
            
            Select Option: 
        """.trimIndent()

            println(mainMenuString)

            input = scanner.nextInt()

            when (input) {
                1 -> CategoryActivity().start()
                2 -> ProductActivity().start()
                3 -> CouponActivity().start()
                4 -> {
                    SessionManager.signOut()
                    println("Signed out!\n")
                }


                else -> println("INVALID INPUT!")
            }
        }
    }
}