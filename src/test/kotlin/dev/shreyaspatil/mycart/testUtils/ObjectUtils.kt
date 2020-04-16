package dev.shreyaspatil.mycart.testUtils

import dev.shreyaspatil.mycart.model.Item
import dev.shreyaspatil.mycart.model.Product
import dev.shreyaspatil.mycart.model.User

fun fakeUser() = User("USER", "USER", "USER")

fun fakeItem(id: Int = 0) = Item(
    Product(id, 0, "", "", "", 0.0),
    0
)

fun fakeProduct(id: Int = 0) = Product(0, 0, "", "", "", 0.0)