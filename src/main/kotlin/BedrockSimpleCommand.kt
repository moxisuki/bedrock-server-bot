package com.minimalist.micat

import com.minimalist.micat.Api.WebsocketClient
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Util.Output
import io.ktor.util.*
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

@OptIn(ConsoleExperimentalApi::class)
object BedrockSimpleCommand : CompositeCommand(
    BedrockServerBot,"bedrock","服务器",
    description = "bedrock server websocket"
) {
    @SubCommand
    suspend fun client() {
        BedrockServerBot.retrytimes = Setting.re_try_times
        BedrockServerBot.launchWebsocket()
    }
    @OptIn(KtorExperimentalAPI::class)
    @SubCommand
    suspend fun test() {
        var re = WebsocketClient().sendResultCmd("list")
        Output().pushMessageToGroup(re)
    }
}