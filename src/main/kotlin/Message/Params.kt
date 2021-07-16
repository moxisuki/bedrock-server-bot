package Message

import com.minimalist.micat.Message.Pos

data class Params(
    val cmd: String,
    val ip: String,
    val sender: String,
    val xuid: String,
    val text: String,
    val dmcase: Int,
    val dmname: String,
    val mobname: String,
    val mobtype: String,
    val pos: Pos,
    val srcname: String,
    val srctype: String,
    val mode: String,
    val raw: String,
    val id: String,
    val result: String
)