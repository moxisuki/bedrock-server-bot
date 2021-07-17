package com.minimalist.micat.Fun


import com.google.gson.Gson
import com.minimalist.micat.Config.PlayerData
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Message.BlackCheck
import com.minimalist.micat.Util.MessageUtil
import com.minimalist.micat.Util.Output
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WhiteList(e: GroupMessageEvent) {
    private var message = e.message
    private var subject = e.subject
    private var sender = e.sender
    private var event = e
    suspend fun registered() {
        if (subject.id in Setting.groups) {
            if (PlayerData.status_bind[sender.id] == true && message.contentToString() == "确认申请") {
                PlayerData.status_bind.remove(sender.id)
                subject.sendMessage(
                    At(sender.id) +
                            MessageUtil.getUploadImage("/faces/Star.png", subject)
                            + PlainText("您的申请已经提交\n请等待管理层成员受理")
                )
                channelToList(PlayerData.account[sender.id].toString(), sender.id)
                PlayerData.pending_Account[sender.id] = false
            } else if (message.contentToString() == "申请白名单") {
                if (PlayerData.account[sender.id] == null) {
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Question.png", subject)
                                + PlainText("您都还没有绑定呢")
                    )
                } else if (PlayerData.pending_Account[sender.id] == false) {
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Question.png", subject)
                                + PlainText("您之前已经申请过了唉")
                    )
                } else if (PlayerData.pending_Account[sender.id] == true) {
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Star.png", subject)
                                + PlainText("您的申请已经受理\n可以前往服务器了哦")
                    )
                } else {
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Good_job.png", subject)
                                + PlainText("ID：${PlayerData.account[sender.id]}\nQQ：${sender.id}\n请在10s内发送「确认申请」")
                    )
                    PlayerData.status_bind[sender.id] = true
                    delay(30000L)
                    if (PlayerData.status_bind[sender.id] != null) {
                        PlayerData.status_bind.remove(sender.id)
                        subject.sendMessage(
                            At(sender.id) +
                                    MessageUtil.getUploadImage("/faces/Fail.png", subject)
                                    + PlainText("您的确认操作超时了唉")
                        )
                    }

                }
            }
        }
        if (message.contentToString() == "delete") {
            PlayerData.account.remove(sender.id)
            subject.sendMessage("success")
        }

    }

    private suspend fun channelToList(s: String, id: Long) {
        Output().pushMessageToAdminGroup(
            "收到白名单申请"
                    + "\nQQ：$id"
                    + "\nID：$s\n"
                    + SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(Date())
        )


        //
        PlayerData.pending_list.add(s)
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^


        Output().pushMessageToAdminGroup("正在查询该玩家的云黑信息")
        val client = HttpClient()
        try {
            val content: String = client.get("http://api.blackbe.xyz/api/check?v2=true&id=$s")
            val data_id = Gson().fromJson(content, BlackCheck::class.java)
            if (data_id.error == 2003) {
                Output().pushMessageToAdminGroup(
                    "查询条目：xboxid\n"
                            + "查询内容：${s}\n"
                            + "查询结果：${data_id.message}"
                )
            } else if (data_id.error == 2001) {
                Output().pushMessageToAdminGroup(
                    "查询条目：xboxid\n"
                            + "查询内容：${s}\n"
                            + "查询结果：${data_id.message}"
                )
            } else if (data_id.error == 2002) {
                Output().pushMessageToAdminGroup(
                    "查询条目：xboxid\n"
                            + "查询内容：${s}\n"
                            + "查询结果：${data_id.message}\n"
                            + "记录QQ：${data_id.data.qq}\n"
                            + "记录ID：${data_id.data.name}\n"
                            + "记录等级：${data_id.data.level}\n"
                            + "记录原因：${data_id.data.info}"
                )
            } else {
                Output().pushMessageToAdminGroup(
                    "查询条目：xboxid\n"
                            + "查询内容：${s}\n"
                            + "查询结果：查询无结果"
                )
            }
        } catch (es: Exception) {
            Output().pushMessageToAdminGroup(es.toString())
        }
        try {

            val content2: String = client.get("http://api.blackbe.xyz/api/qqcheck?v2=true&qq=${sender.id}")
            val data_id2 = Gson().fromJson(content2, BlackCheck::class.java)
            if (data_id2.error == 2003) {
                Output().pushMessageToAdminGroup(
                    "查询条目：QQ\n"
                            + "查询内容：${sender.id}\n"
                            + "查询结果：${data_id2.message}"
                )
            } else if (data_id2.error == 2001) {
                Output().pushMessageToAdminGroup(
                    "查询条目：QQ\n"
                            + "查询内容：${sender.id}\n"
                            + "查询结果：${data_id2.message}"
                )
            } else if (data_id2.error == 2002) {
                Output().pushMessageToAdminGroup(
                    "查询条目：QQ\n"
                            + "查询内容：${sender.id}\n"
                            + "查询结果：${data_id2.message}\n"
                            + "记录QQ：${data_id2.data.qq}\n"
                            + "记录ID：${data_id2.data.name}\n"
                            + "记录等级：${data_id2.data.level}\n"
                            + "记录原因：${data_id2.data.info}"
                )
            } else {
                Output().pushMessageToAdminGroup(
                    "查询条目：QQ\n"
                            + "查询内容：${sender.id}\n"
                            + "查询结果：查询无结果"
                )
            }
            Output().pushMessageToAdminGroup(
                "您可以：\n" +
                        "使用「查询列表」查看申请列表\n" +
                        "使用「通过+序号」通过某人申请\n" +
                        "使用「查询+序号」查询某人信息"
            )
        } catch (es: Exception) {
            Output().pushMessageToAdminGroup(es.toString())
        }
    }


}