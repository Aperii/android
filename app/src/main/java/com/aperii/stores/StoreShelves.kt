package com.aperii.stores

class StoreShelves {
    companion object {
        var users: StoreUsers = StoreUsers(this)
        var posts: StorePosts = StorePosts()
        var navigation = StoreNavigation()
    }
}