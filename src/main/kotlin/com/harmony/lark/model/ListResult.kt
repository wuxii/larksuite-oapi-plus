package com.harmony.lark.model

open class ListResult<T>(
    val hasMore: Boolean,
    val pageToken: String,
    val total: Int,
    val items: Array<T>
)
