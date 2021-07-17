package com.minimalist.micat.Config

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PlayerData: AutoSavePluginData("PlayerData") {

    val account by value(mutableMapOf<Long,String>())
    val loginDate by value(mutableMapOf<Long,String>())
    val status  by value(mutableMapOf<Long, Boolean>())
    val status_bind  by value(mutableMapOf<Long, Boolean>())

    //admin whitelist
    val pending_list by value(mutableListOf<String>())
    val pending_Account by value(mutableMapOf<Long,Boolean>())
}