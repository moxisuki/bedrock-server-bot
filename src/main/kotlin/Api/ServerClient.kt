package com.minimalist.micat.Api

import Message.ChatData
import Message.CmdData
import Message.JoinData
import Message.LeftData
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.minimalist.micat.BedrockServerBot
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Message.*
import com.minimalist.micat.Util.Output

object ServerClient {
    private val out = Output()
    private val gson = Gson()
    suspend fun onReceive(data:String) {
        when (JsonParser.parseString(data).asJsonObject.get("cause").asString) {
            "chat" -> onMemberMessage(gson.fromJson(data, ChatData::class.java))
            "join" -> onMemberJoin(gson.fromJson(data,JoinData::class.java))
            "left" -> onMemberLeave(gson.fromJson(data, LeftData::class.java))
            "cmd" -> onMemberCmd(gson.fromJson(data, CmdData::class.java))
            "mobdie" -> onMobDie(gson.fromJson(data, MobData::class.java))
            "runcmdfeedback" -> onCmdResp(gson.fromJson(data, ResultData::class.java))
          //  is ServerCrash -> onServerCrash(pkg)
        }
    }

    private suspend fun onCmdResp(fromJson: ResultData) {
        var id = fromJson.params.id
        var result = fromJson.params.result
        data_w.mutableMap.put(id,result)
    }

    private suspend fun onMobDie(fromJson: MobData) {
        if (fromJson.params.mobtype.equals(fromJson.params.mobname)) {
            val tmp = "玩家「${fromJson.params.mobname}」被\n◆「${fromJson.params.srctype}」杀死"
            out.pushMessageToGroup(tmp)
        }
    }

    

    private suspend fun onMemberCmd(fromJson: CmdData) {
        val tmp = "玩家「${fromJson.params.sender}」执行\n-> /${fromJson.params.cmd}"
        out.pushMessageToGroup(tmp)
    }

    private suspend fun onMemberLeave(fromJson: LeftData) {
        val randoms = (0..4).random()
        var tmp: String = "遗憾的离开了"
        when(randoms){
            0 -> tmp = "被杰哥带出了服务器"
            1 -> tmp = "带着可莉溜逃出了服务器"
            2 -> tmp = "Left the game soon"
            3 -> tmp = "由于肝衰竭爬出了服务器"
            4 -> tmp = "莫名其妙地离开了服务器"
        }
        out.pushMessageToGroup("玩家「${fromJson.params.sender}」\n▼$tmp")
    }

    private suspend fun onMemberJoin(fromJson: JoinData) {
        val randoms = (0..4).random()
        var tmp: String = "悄悄爬进了服务器"
        when(randoms){
            0 -> tmp = "被杰哥拉进了服务器"
            1 -> tmp = "带着可莉溜进了服务器"
            2 -> tmp = "Join the server soon"
            3 -> tmp = "艰难的挤进了服务器"
            4 -> tmp = "不知所措的进入了服务器"
        }
        out.pushMessageToGroup("玩家「${fromJson.params.sender}」\n▲$tmp")
    }

    private suspend fun onMemberMessage(fromJson: ChatData) {
        val tmp = "玩家「${fromJson.params.sender}」说\n-> ${fromJson.params.text}"
        out.pushMessageToGroup(tmp)
    }

    public suspend fun notifyConnect() {
        BedrockServerBot.retrytimes = Setting.re_try_times
        out.pushMessageToGroup("服务器连接成功")
    }


    public suspend fun notifyDrop() {
        out.pushMessageToGroup("无法找到服务器")
        Thread.sleep((Setting.wait_time*1000).toLong())
        BedrockServerBot.launchWebsocket()
    }

    public suspend fun notifyClose() {
            if(Setting.re_try_times == BedrockServerBot.retrytimes) {
                out.pushMessageToGroup("服务器已关闭连接\n->将于10s后尝试重新连接")
                Thread.sleep(10000)
            }
            BedrockServerBot.launchWebsocket()
    }

}