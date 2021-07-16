package com.minimalist.micat.Util

import Message.Params
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.minimalist.micat.Config.Setting
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class GetData {
    fun encode(password: String): String {
        try {
            val  instance:MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest:ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                var i :Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0" + hexString//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun decode(keya: String, keyb: String,Original:String): String {
        var var1 = Cipher.getInstance("AES/CBC/PKCS5Padding")
            var1.init(2, SecretKeySpec(keya.toByteArray(), "AES"),
                IvParameterSpec(keyb.toByteArray()))
        val clipher = var1
        val decrypted = String(clipher.doFinal(Base64.getDecoder().decode(Original)))
        return decrypted
    }
    fun encrypt(input:String,keya:String,keyb:String): String {
        //创建cipher对象
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        //初始化:加密/解密
        cipher.init(2,SecretKeySpec(keya.toByteArray(),"AES"),
            IvParameterSpec(keyb.toByteArray()))
        //加密
        val encrypt = cipher.doFinal(input.toByteArray())
        return String(Base64.getEncoder().encode(encrypt))
    }
    public fun call(original: String): String {
        val from_json = JsonParser.parseString(original).asJsonObject.getAsJsonObject("params").get("raw").asString
        val key = encode(Setting.password).uppercase(Locale.getDefault())
        val aes_key = key.substring(0, 16)
        val aes_iv = key.substring(16)
        return decode(aes_key,aes_iv,from_json)
    }
    public suspend fun send(msg:String):String{
        val key = encode(Setting.password).uppercase(Locale.getDefault())
        val aes_key = key.substring(0, 16)
        val aes_iv = key.substring(16)
        var rawdat = encrypt(aes_key,aes_iv,msg)
        var parame = Params(mode = "aes_cbc_pck7padding",rawdat)
        var data = SendData(parame,"encrypted")
        val gson = Gson()
        var json = gson.toJson(data)
        return json.toString()
    }

    data class Params(
        var mode:String,
        var raw:String
    )

    data class SendData(
        var params: Params,
        val type: String
    )
}