package com.minimalist.micat.Util

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

object MessageUtil {

    suspend fun getUploadImage(filename: String, subject: Group): Image {
        val file = javaClass.getResource(filename).toURI()
        val externalResource = File(file).toExternalResource()
        return subject.uploadImage(externalResource)
    }

    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符  false 不包含中文字符
     * @throws EmptyException
     */
    fun isContainChinese(str: String): Boolean {
        val p: Pattern = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]")
        val m: Matcher = p.matcher(str)
        return m.find()
    }
    fun removeDoubleQuotes(result: String): String {
        //去掉" "号
        return result.replace("\"", "")
    }
}