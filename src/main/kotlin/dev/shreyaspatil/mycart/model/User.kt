package dev.shreyaspatil.mycart.model

data class User(
    private val _uid: String? = null,
    private val _name: String? = null,
    private val _username: String? = null
) {
    val uid
        get() = _uid

    val name
        get() = _name

    val userName
        get() = _username

    companion object {
        const val COLUMN_NAME_UID = "UID"
        const val COLUMN_NAME_NAME = "NAME"
        const val COLUMN_NAME_USERNAME = "USERNAME"
        const val COLUMN_NAME_PASSWORD = "PASSWORD"
    }
}