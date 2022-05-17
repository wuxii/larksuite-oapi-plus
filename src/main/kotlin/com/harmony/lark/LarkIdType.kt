package com.harmony.lark

/**
 * @author wuxin
 */
enum class LarkIdType(val regex: Regex, val type: String) {

    UNION_ID("on_[a-zA-Z\\d]{32}".toRegex(), "union_id"),

    OPEN_ID("ou_[a-zA-Z\\d]{32}".toRegex(), "open_id"),

    CHAT_ID("oc_[a-zA-Z\\d]{32}".toRegex(), "chat_id"),

    MSG_ID("om_".toRegex(), "msg_id"),

    EMAIL("\\w[-\\w.+]*@([A-Za-z\\d][-A-Za-z\\d]+\\.)+[A-Za-z]{2,14}".toRegex(), "email");

    companion object {

        fun of(id: String): LarkIdType {
            return values().firstOrNull { it.regex.matches(id) }
                ?: throw IllegalArgumentException("$id not lark id type")
        }

        fun ofType(id: String): String {
            return of(id).type
        }

    }
}
