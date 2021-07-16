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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class WebsocketClient() {
    private val out = Output()
    private val client = HttpClient {
        install(WebSockets)
    }
    private lateinit var outgoing: SendChannel<Frame>
    private var session: DefaultClientWebSocketSession? = null
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
            val gson = Gson()
            val getdata = GetData()
            outgoing = session.outgoing
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
        } catch (i: IllegalBlockSizeException){
            i.printStackTrace()
        } catch (b: BadPaddingException){
            b.printStackTrace()
        }
    }

    suspend fun sendCmd(cmd:String){
        try {
            var params = Params(cmd = cmd, id = 0)
            var sendcmd = SendCmd("runcmdrequest", params, "pack")
            var json = Gson().toJson(sendcmd)
            out.pushMessageToGroup(json)
            var callJson = GetData().send(json)
            out.pushMessageToGroup(callJson)
            outgoing.send(Frame.Text(callJson))
        } catch (e:Exception){
            out.pushMessageToGroup(e.toString())
        }
    }

}
data class SendCmd(
    val action: String,
    val params: Params,
    val type: String
)

data class Params(
    val cmd: String,
    val id: Int
)