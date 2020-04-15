package dev.shreyaspatil.mycart.common

import dev.shreyaspatil.mycart.model.Product

/**
 * Shows Products of MyCart
 */
class ShowProductActivity {
    fun show(productList: List<Product>) {
        print("\n-------------------------------------------------------------------------------------")
        System.out.format(
            "\n%-3s%-20s%-20s%-13s",
            "ID",
            "CATEGORY",
            "NAME",
            "PRICE"
        )
        print("\n-------------------------------------------------------------------------------------")

        if (productList.isEmpty()) {
            print("\n ++ NO ITEMS ++")
        }

        productList.forEach { product ->
            System.out.format(
                "\n%-3d%-20s%-20sRs.%-10.2f",
                product.id,
                product.categoryName,
                product.name,
                product.price
            )
        }

        print("\n-------------------------------------------------------------------------------------")
    }
}