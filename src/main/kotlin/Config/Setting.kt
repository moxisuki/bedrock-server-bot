package com.minimalist.micat.Config

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.value


object Setting : ReadOnlyPluginConfig("Setting") {

    val host:String by value("127.0.0.1")

    val port:Int by value(12099)

    val path:String by value("/websocket")

    val password:String by value("password")

    val re_try_times:Int by value(5)

    val wait_time:Int by value(5)

    val bots by value(listOf<Long>(123456))

    val admin_Groups by value(listOf<Long>(123456789))

    val groups by value(listOf<Long>(12345678))
}