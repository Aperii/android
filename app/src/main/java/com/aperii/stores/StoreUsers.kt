package com.aperii.stores

import com.aperii.models.user.CoreUser
import com.aperii.models.user.MeUser
import com.aperii.models.user.User
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.settings.settings

class StoreUsers(private val api: RestAPI) {
    private val store by settings("StoreUsers")
    private val prefs by settings()

    private var meUserId: String?
        get() = prefs["current_user_id", null]
        set(value) {
            prefs["current_user_id"] = value
        }

    var me: MeUser?
        get() = if (meUserId != null) store.getObject(meUserId!!) else null
        set(value) {
            meUserId = value!!.id
            store[value.id] = value
        }

    fun getUser(id: String): CoreUser? = store.getObject(id)
    fun updateUser(user: CoreUser) = store.set(user.id, user)

    suspend fun fetchUser(id: String): User? = getUser(id) ?: try {
        CoreUser.fromApi(api.getUser(id).body()!!).also { updateUser(it) }
    } catch (e: Throwable) { null }

    suspend fun fetchCurrentUser(): MeUser? = try {
        MeUser.fromApi(api.getMe().body()!!).also { me = it }
    } catch (e: Throwable) { null }

}