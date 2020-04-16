package dev.shreyaspatil.mycart.user

import dev.shreyaspatil.mycart.common.ShowCategoriesActivity
import dev.shreyaspatil.mycart.common.ShowProductActivity
import dev.shreyaspatil.mycart.repository.CategoryRepository
import dev.shreyaspatil.mycart.repository.ProductsRepository
import dev.shreyaspatil.mycart.utils.DatabaseUtils
import java.util.*

class BrowseActivity {

    private val connection = DatabaseUtils.getConnection()!!
    private val scanner by lazy { Scanner(System.`in`) }
    private val productsRepository by lazy { ProductsRepository(connection) }
    private val categories by lazy { CategoryRepository(connection).getAllCategories() }

    fun start() {
        showCategories()

        println("\n++ VIEW PRODUCTS UNDER THE CATEGORY ++ Enter -1 to Exit")
        print("\nCategory ID: ")

        val categoryId = scanner.nextInt()

        if (categoryId == -1) {
            return
        }

        val category = categories.find { it.id == categoryId }
        if (category == null) {
            println("INVALID CATEGORY ID!")
            return
        }

        println(
            """
            ------------------------------
            CATEGORY: ${category.name}
            ------------------------------
        """.trimIndent()
        )

        val products = productsRepository.getProductsByCategory(categoryId)
        ShowProductActivity().show(products)

        if (products.isNotEmpty()) {
            println("\n\n++ SELECT PRODUCT TO VIEW IN DETAIL ++ Enter -1 to Exit")
            print("\n\nProduct ID: ")

            val productId = scanner.nextInt()

            if (productId == -1) {
                return
            }

            val product = products.find { it.id == productId }

            if (product == null) {
                println("INVALID PRODUCT ID!")
                return
            }

            ProductDetailsActivity(product).start()
        }
    }

    private fun showCategories() {
        ShowCategoriesActivity().show(categories)
    }
}