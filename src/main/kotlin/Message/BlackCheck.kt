package com.minimalist.micat.Message

data class BlackCheck(
    val `data`: Data,
    val error: Int,
    val message: String,
    val success: Boolean,
    val version: String
)

data class Data(
    val info: String,
    val level: Int,
    val name: String,
    val qq: Int,
    val server: String,
    val trial: Int,
    val xbox_id: Any
)