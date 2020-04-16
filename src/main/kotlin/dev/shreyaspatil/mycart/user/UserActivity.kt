package dev.shreyaspatil.mycart.user

import dev.shreyaspatil.mycart.common.ShowCouponsActivity
import dev.shreyaspatil.mycart.model.User
import dev.shreyaspatil.mycart.repository.CouponRepository
import dev.shreyaspatil.mycart.repository.OrderRepository
import dev.shreyaspatil.mycart.session.SessionManager
import dev.shreyaspatil.mycart.utils.DatabaseUtils
import java.util.*

class UserActivity {

    private val connection = DatabaseUtils.getConnection()!!
    private lateinit var user: User
    private val scanner = Scanner(System.`in`)

    init {
        try {
            user = SessionManager.currentUser!!
        } catch (e: Exception) {
            println("ACCESS DENIED!")
        }
    }

    fun start() {
        showMainMenu()
    }

    private fun showMainMenu() {

        var input = 0

        while (input != 5) {
            val mainMenuString = """
            ------------------------------------------------------------
            WELCOME ${user.name} to MyCart
            ------------------------------------------------------------
            MENU
            
            1) Browse Categories
            2) My Cart
            3) View Offers
            4) My Orders
            5) Sign Out
            
            Select Option: 
        """.trimIndent()

            println(mainMenuString)

            input = scanner.nextInt()

            when (input) {
                1 -> BrowseActivity().start()
                2 -> CartActivity().start()
                3 -> showCoupons()
                4 -> showOrders()
                5 -> {
                    SessionManager.signOut()
                    println("Signed out!\n")
                }
                else -> println("INVALID INPUT!")
            }
        }
    }

    private fun showOrders() {
        val ordersRepository = OrderRepository(connection, user)
        OrdersActivity(ordersRepository.getOrderDetails()).start()
    }

    private fun showCoupons() {
        val couponRepository = CouponRepository(connection)
        ShowCouponsActivity().show(couponRepository.getAllCoupons())
    }
}