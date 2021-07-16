package com.minimalist.micat

import com.minimalist.micat.Api.ServerClient
import com.minimalist.micat.Api.WebsocketClient
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Util.Output
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.executeCommand
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info


object BedrockServerBot : KotlinPlugin(
    JvmPluginDescription(
        id = "com.minimalist.micat.bedrock-server-bot",
        name = "bedrock-server-bot",
        version = "1.0-SNAPSHOT",
    ) {
        author("moxicat")
    }
) {
    var retrytimes = Setting.re_try_times
    private lateinit var Websocket: WebsocketClient
    private val out = Output()
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override fun onEnable() {
        GlobalScope.async {
            launchWebsocket()
        }
        Setting.reload()
        Websocket = WebsocketClient()
        CommandManager.INSTANCE.registerCommand(BedrockSimpleCommand,true)

        val e = GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            // this: GroupMessageEvent
            // event: GroupMessageEvent
            CommandManager.INSTANCE.executeCommand(this.toCommandSender(),this.message,false)
        }




        logger.info { "Plugin loaded" }
    }

    internal suspend fun launchWebsocket() {
        if(Setting.re_try_times == retrytimes){
            out.pushMessageToGroup("正在尝试连接到服务器\n->${Setting.host}:${Setting.port}")
            logger.info("Try to connect ${Setting.host}:${Setting.port}")
            Websocket.connect()
            retrytimes--
        }
        else if (retrytimes > 0) {
            retrytimes--
            out.pushMessageToGroup("正在尝试第${Setting.re_try_times-retrytimes}次连接服务器")
            logger.info("Try to connect ${Setting.host}:${Setting.port} [${Setting.re_try_times-retrytimes}]")
            Websocket.connect()
        } else {
            out.pushMessageToGroup("服务器连接失败\n->${Setting.host}:${Setting.port}\n->Pwd:${Setting.password}\n->Path:${Setting.path}")
        }
    }
}