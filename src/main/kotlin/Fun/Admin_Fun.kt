package com.minimalist.micat.Fun

import com.minimalist.micat.Config.PlayerData
import com.minimalist.micat.Config.Setting
import com.minimalist.micat.Util.Output
import net.mamoe.mirai.event.events.GroupMessageEvent

class Admin_Fun(e: GroupMessageEvent) {
    private var message = e.message
    private var subject = e.subject
    private var sender = e.sender
    private var event = e
    suspend fun registered() {

        PlayerData.pending_list.add("testPlayer")

        PlayerData.pending_list.add("sb")

        if (subject.id in Setting.admin_Groups) {
            if(message.contentToString()=="查询列表"){
                var i = 1
                var liststr:String = ""
                PlayerData.pending_list.forEach {
                    if (i==1) {
                        liststr = "1、\nID：$it\nQQ：${getkey(it,PlayerData.account)}"
                        i += 1
                    }else{
                        liststr = "$liststr\n------------------\n$i、\n" + "ID：$it\n" + "QQ：${getkey(it,PlayerData.account)}"
                        i += 1
                    }
                }
                Output().pushMessageToAdminGroup(liststr)
            }
        }
    }

    private fun getkey(it: String, account: MutableMap<Long, String>): Long {
        for(data in account){
            if (data.value == it){
                return data.key
            }
        }
        return 1145144444L
    }
}