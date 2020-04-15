package dev.shreyaspatil.mycart.model

data class Category(
    val id: Int = 0,
    val name: String
) {
    companion object {
        const val TABLE_NAME = "categories"

        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "CATEGORY_NAME"
    }
}