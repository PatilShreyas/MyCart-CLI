package dev.shreyaspatil.mycart.model

data class Product(
    val id: Int = 0,
    val categoryId: Int,
    val categoryName: String = "",
    val name: String,
    val description: String,
    val price: Double
) {
    companion object {
        const val TABLE_NAME = "products"

        const val COLUMN_ID = "ID"
        const val COLUMN_CATEGORY_ID = "CATEGORY_ID"
        const val COLUMN_NAME = "PRODUCT_NAME"
        const val COLUMN_DESC = "PRODUCT_DESCRIPTION"
        const val COLUMN_PRICE = "PRICE"
    }
}