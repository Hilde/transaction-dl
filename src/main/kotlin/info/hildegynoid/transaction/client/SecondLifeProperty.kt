package info.hildegynoid.transaction.client

data class SecondLifeProperty(
    var loginUrl: String = "https://id.secondlife.com/openid/login",
    var loginSubmitUrl: String = "https://id.secondlife.com/openid/loginsubmit",
    var openIdAuthorizationUrl: String = "https://secondlife.com/auth/oid_return.php?redirect=https://secondlife.com/index.php&openid_identifier=https://id.secondlife.com/id/",
    var transactionHistoryUrl: String = "https://accounts.secondlife.com/transaction_history",
    var transactionDownloadUrl: String = "https://accounts.secondlife.com/get_transaction_history_csv",
    var username: String = "",
    var password: String = ""
)
