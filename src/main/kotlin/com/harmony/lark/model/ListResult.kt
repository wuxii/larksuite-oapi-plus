package com.harmony.lark.model

interface ListResult<T> {

    fun getHasMore() = false

    fun getPageToken() = ""

    fun getTotal() = -1

    fun getItems() = listOf<T>()

}
