package com.harmony.lark.model

interface ListResult<T> {

    companion object {
        fun <T> simple(
            hasMore: Boolean,
            pageToken: String,
            total: Int = -1,
            items: List<T> = listOf(),
        ): ListResult<T> = object : ListResult<T> {

            override fun getHasMore(): Boolean {
                return hasMore
            }

            override fun getPageToken(): String {
                return pageToken
            }

            override fun getTotal(): Int {
                return total
            }

            override fun getItems(): List<T> {
                return items
            }
        }
    }

    fun getHasMore(): Boolean

    fun getPageToken(): String

    fun getTotal(): Int

    fun getItems(): List<T>

}
