package info.hildegynoid.transaction.data

data class SettingProperty(
    var downloadDir: String = "",
    var downloadType: DownloadType = DownloadType(),
    var users: MutableList<User> = mutableListOf()
) {
    data class User(
        var premium: Boolean = false,
        var sessionToken: String = "",
        var username: String = ""
    )

    data class DownloadType(
        var csv: Boolean = true,
        var xls: Boolean = false,
        var xml: Boolean = false
    )
}
