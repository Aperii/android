package com.aperii.stores

import com.aperii.models.user.MeUser
import com.aperii.models.user.User
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import java.util.concurrent.atomic.AtomicReference

class StoreUsers(shelves: StoreShelves.Companion) {
    private val usersSnapshot = HashMap<String, User>()
    private var meUserId: String? = null
    var me: MeUser?
        get() = if (meUserId != null) getUser(meUserId!!) as MeUser? else null
        set(value) {
            meUserId = value!!.id
            updateUser(value)
        }

    fun getUser(id: String) = usersSnapshot[id]

    fun fetchUser(id: String): User? {
        if (getUser(id) != null) return getUser(id)
        val user = AtomicReference<User>()
        RestAPI.INSTANCE.getMe().observe {
            user.set(MeUser.fromApi(this))
        }
        return user.get()
    }

    fun updateUser(user: User) {
        usersSnapshot[user.id] = user
    }
}