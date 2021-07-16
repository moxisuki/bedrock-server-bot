package com.minimalist.micat.Api

import com.google.gson.Gson
import com.minimalist.micat.BedrockServerBot
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Util.GetData
import com.minimalist.micat.Util.Output
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import kotlin.properties.Delegates

object data_w{
    lateinit var outgoing: SendChannel<Frame>
    private var cmdSize = 0
   public fun getCmdSize(): Int {
        cmdSize += 1
        return cmdSize
    }
   public val mutableMap = mutableMapOf<String,String>()
}
@KtorExperimentalAPI
class WebsocketClient() {
    private val out = Output()
    private var session: DefaultClientWebSocketSession? = null
    private val client = HttpClient {
        install(WebSockets)
    }
    suspend fun connect() {
        BedrockServerBot.launch {
            try {
                client.ws(
                    method = HttpMethod.Get,
                    host = Setting.host,
                    port = Setting.port,
                    path = Setting.path
                ) {
                    process(this)
                }
            } catch (e: ClosedReceiveChannelException) {
                ServerClient.notifyDrop()
            } catch (e: ConnectException) {
                ServerClient.notifyDrop()
            } catch (e: Exception) {
                ServerClient.notifyClose()
                e.printStackTrace()
            }
        }
    }


    private suspend fun process(session: DefaultClientWebSocketSession) {
        try {
            this.session?.close()
            this.session = session
            data_w.outgoing = session.outgoing
            val gson = Gson()
            val getdata = GetData()
            ServerClient.notifyConnect()
            BedrockServerBot.logger.info("Connect successful")
            while (true) {
                when (val frame = session.incoming.receive()) {
                    is Frame.Text -> {
                        ServerClient.onReceive(getdata.call( frame.readText()))
                    }
                }
            }
        } catch (cancel: CancellationException) {
            BedrockServerBot.logger.info("Bedrock Server reboot")
        } catch (i: IllegalBlockSizeException) {
            i.printStackTrace()
        } catch (b: BadPaddingException) {
            b.printStackTrace()
        }
    }

    suspend fun sendResultCmd(cmd: String): String {
        val id = data_w.getCmdSize().toString()
        var params = Params(cmd = cmd, id)
        var sendcmd = SendCmd("runcmdrequest", params, "pack")
        var json = Gson().toJson(sendcmd)
        var callJson = GetData().send(json)
        data_w.outgoing.send(Frame.Text(callJson))
        Thread.sleep(2000)
        var data = data_w.mutableMap.get(id).toString()
        if (data.isEmpty()){
            out.pushMessageToAdminGroup("错误！指令执行结果获取超时\n->cmd:$cmd\n->id:$id")
            return "fail"
        }
        return data
    }
    suspend fun sendCmd(cmd: String){
        val id = data_w.getCmdSize().toString()
        var params = Params(cmd = cmd, id)
        var sendcmd = SendCmd("runcmdrequest", params, "pack")
        var json = Gson().toJson(sendcmd)
        var callJson = GetData().send(json)
        data_w.outgoing.send(Frame.Text(callJson))
    }

}

data class SendCmd(
    val action: String,
    val params: Params,
    val type: String
)

data class Params(
    val cmd: String,
    val id: String
)