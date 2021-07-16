package com.minimalist.micat

import com.minimalist.micat.Api.WebsocketClient
import com.minimalist.micat.Config.Setting
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

@OptIn(ConsoleExperimentalApi::class)
object BedrockSimpleCommand : CompositeCommand(
    BedrockServerBot,"bedrock","服务器",
    description = "bedrock server websocket"
) {
    @SubCommand
    suspend fun CommandSender.client(){
        BedrockServerBot.retrytimes = Setting.re_try_times
        BedrockServerBot.launchWebsocket()
    }
    @SubCommand
    suspend fun CommandSender.test(){
        WebsocketClient().sendCmd("list")
    }
}