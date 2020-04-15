package dev.shreyaspatil.mycart.session

import dev.shreyaspatil.mycart.model.Response

/**
 * Session to manage user presence in whole execution of the application.
 */
interface Session {

    /**
     * Signs in the user and creates new session with specified [username] and [password].
     * @param type Type of a user (ADMIN/USER)
     * @param username Username of account
     * @param password Password of account
     * @param callback Response of result called back
     */
    fun signIn(
        type: UserType,
        username: String,
        password: String,
        callback: ((Response<*>) -> Unit)?
    )

    /**
     * Signs out the current user and cleares the session.
     */
    fun signOut()
}