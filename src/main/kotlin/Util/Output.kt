package com.minimalist.micat.Util

import com.minimalist.micat.BedrockServerBot
import com.minimalist.micat.Config.Setting
import net.mamoe.mirai.Bot

class Output {
   private suspend fun Bot.pushGroupMessage(msg:String,groups:List<Long>){
       groups.forEach{
           try {
               this.getGroup(it)?.sendMessage(msg)
           } catch (e:NoSuchElementException){
               BedrockServerBot.logger.info("Bot($id)中没有群($it)")
           }
       }
    }
    suspend fun pushMessageToGroup(msg:String){
        if(msg.isEmpty()) return
        Setting.bots.forEach {
        Bot.getInstance(it).pushGroupMessage(msg,Setting.groups)
        }
    }
    suspend fun pushMessageToAdminGroup(msg: String){
        if(msg.isEmpty()) return
        Setting.bots.forEach {
            Bot.getInstance(it).pushGroupMessage(msg,Setting.admin_Groups)
        }
    }
}