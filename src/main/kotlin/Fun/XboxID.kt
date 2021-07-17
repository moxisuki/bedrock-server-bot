package com.minimalist.micat.Fun

import com.minimalist.micat.BedrockServerBot
import com.minimalist.micat.Config.PlayerData
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Util.MessageUtil
import kotlinx.coroutines.delay
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import java.util.*

class XboxID(e: GroupMessageEvent) {
    private var message = e.message
    private var subject = e.subject
    private var sender = e.sender
    private var event = e

    suspend fun registered() {
        if (subject.id in Setting.groups) {
            when (message.contentToString().lowercase(Locale.getDefault())) {
                "绑定xboxid" -> bindXboxId()
                "xboxid绑定" -> bindXboxId()

                "绑定xbox" -> bindXboxId()
                "xbox绑定" -> bindXboxId()

                "绑定id" -> bindXboxId()
                "id绑定" -> bindXboxId()

                "解绑xbox" -> unBindXboxId()
                "xbox解绑" -> unBindXboxId()

                "解绑id" -> unBindXboxId()
                "id解绑" -> unBindXboxId()

                "解绑xboxid" -> unBindXboxId()
                "xboxid解绑" -> unBindXboxId()
            }
            if (PlayerData.status[sender.id] == true) {
                if (MessageUtil.isContainChinese(message.contentToString()) || message.contentToString() == "") {
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Question.png", subject)
                                + PlainText("您提供的ID不规范哦！")
                    )
                } else {
                    PlayerData.status.remove(sender.id)
                    var id = MessageUtil.removeDoubleQuotes(message.contentToString())
                    PlayerData.account[sender.id] = id
                    subject.sendMessage(
                        At(sender.id) +
                                MessageUtil.getUploadImage("/faces/Star.png", subject)
                                + PlainText("ID:${id}\n您的XboxID绑定成功啦！")
                    )
                    BedrockServerBot.logger.info("Player(${sender.id} bound to (${message.contentToString()})")
                }

            }
        }
    }

    private suspend fun unBindXboxId() {
      if (!isBindXboxID(sender.id)) {
          subject.sendMessage(
              At(sender.id) +
                      MessageUtil.getUploadImage("/faces/Question.png", subject)
                      + PlainText("您都还没有绑定呢")
          )
      }else{
          subject.sendMessage(
              At(sender.id) +
                      MessageUtil.getUploadImage("/faces/Star.png", subject)
                      + PlainText("原ID:${PlayerData.account[sender.id]}\n您的XboxID解绑成功啦！")
          )
          BedrockServerBot.logger.info("Player(${sender.id} remove to (${message.contentToString()})")
          PlayerData.account.remove(sender.id)
      }
    }




    private suspend fun bindXboxId() {
        if (isBindXboxID(sender.id) && PlayerData.status[sender.id] != true) {
            subject.sendMessage(
                At(sender.id) +
                        MessageUtil.getUploadImage("/faces/Question.png", subject)
                        + PlainText("您之前已经绑定过了唉\n如需换绑请先「解绑xboxid」哦")
            )
        } else {
            subject.sendMessage(
                At(sender.id)
                        + PlainText("\n请在60s内发送您的XboxID哦")
            )
            PlayerData.status[sender.id] = true
            delay(60000L)
            if ( PlayerData.status[sender.id] == true){
                PlayerData.status.remove(sender.id)
                subject.sendMessage(
                    At(sender.id) +
                            MessageUtil.getUploadImage("/faces/Fail.png", subject)
                            + PlainText("您的绑定操作超时了唉")
                )
            }
        }

    }

    /**
     * 查询绑定状态
     */
    public fun isBindXboxID(qqID: Long): Boolean {
        return PlayerData.account[qqID] != null
    }
}