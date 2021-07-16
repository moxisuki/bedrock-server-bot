package com.minimalist.micat.Message

import Message.Params

data class MobData(
    val cause: String,
    val params: Params,
    val type: String
)

data class Pos(
    val x: Double,
    val y: Double,
    val z: Double
)