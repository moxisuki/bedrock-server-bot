package com.minimalist.micat.Message

import Message.Params

data class ResultData(
    val cause: String,
    val params: Params,
    val type: String
)

