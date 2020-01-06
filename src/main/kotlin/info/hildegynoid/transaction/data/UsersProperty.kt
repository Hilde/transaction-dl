package info.hildegynoid.transaction.data

data class UsersProperty(
    var users: List<User> = emptyList()
) {
    data class User(
        var username: String = "",
        var sessionToken: String = ""
    )
}
