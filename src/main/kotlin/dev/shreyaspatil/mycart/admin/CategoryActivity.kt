package dev.shreyaspatil.mycart.admin

import dev.shreyaspatil.mycart.common.ShowCategoriesActivity
import dev.shreyaspatil.mycart.model.Category
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.repository.CategoryRepository
import java.util.*

class CategoryActivity {

    private val scanner by lazy { Scanner(System.`in`) }
    private val repository by lazy { CategoryRepository() }

    fun start() {
        showMainMenu()
        scanner.close()
    }

    private fun showMainMenu() {
        val mainMenuString = """
            ------------------------------------------------------------
            CATEGORIES
            ------------------------------------------------------------
            MENU
            
            1) Add Category
            2) View Categories
            3) Navigate Back

            Select Action: 
        """.trimIndent()

        var input = 0

        loop@ while (input != 3) {
            println(mainMenuString)
            input = scanner.nextInt()

            when (input) {
                1 -> addCategory()
                2 -> showCategories()
                3 -> break@loop
                else -> println("INVALID CHOICE!")
            }
        }
    }

    private fun showCategories() {
        ShowCategoriesActivity().show(repository.getAllCategories())
    }

    private fun addCategory() {
        println("Enter the Category Details\n")
        print("Category Name: ")

        // To avoid skipping nextLine()
        scanner.nextLine()

        val name = scanner.nextLine()

        if (name.isBlank()) {
            println("INVALID CATEGORY NAME!")
            return
        }

        // Check if it already exists in database
        val categories = repository.getAllCategories()

        categories.find { it.name.toLowerCase() == name.toLowerCase() }.let {
            if (it != null) {
                println(
                    """
                        ================================================
                        '$name' CATEGORY ALREADY EXISTS
                        ================================================
                """.trimIndent()
                )

                return
            }
        }

        val category = Category(name = name)

        repository.addCategory(category) { response ->
            when (response) {
                is Response.Success -> {
                    println(
                        """
                            ================================================
                            '$name' CATEGORY ADDED
                            ================================================
                    """.trimIndent()
                    )
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
            }
        }
    }
}