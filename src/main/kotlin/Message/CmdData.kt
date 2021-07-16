package Message

data class CmdData(
    val cause: String,
    val params: Params,
    val type: String
)