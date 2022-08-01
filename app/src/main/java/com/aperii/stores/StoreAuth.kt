package com.aperii.stores

import com.aperii.models.user.User
import com.aperii.rest.RestAPIParams
import com.aperii.utilities.Logger
import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rest.RestAPI

class StoreAuth(
    private val authApi: AuthAPI,
    private val users: StoreUsers,
    private val api: RestAPI
) {

    suspend fun login(username: String, password: String): User? {
        val result = authApi.login(RestAPIParams.LoginBody(username, password))
        return if (result.isSuccessful) {
            result.body()?.token?.let { api.currentToken = it }
            users.fetchCurrentUser()
        } else null
    }

    fun login(token: String) {
        api.currentToken = token
    }

}