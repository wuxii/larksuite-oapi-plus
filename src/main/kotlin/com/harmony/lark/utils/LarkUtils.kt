package com.harmony.lark.utils

import com.harmony.lark.BitableAddress
import java.net.URL

object LarkUtils {

    fun toBitableAddress(address: String): BitableAddress {
        val url = URL(address)
        val pathSegments = url.path.split("/")
        val queryParams = toQueryParams(url.query)
        return BitableAddress(
            address = address,
            appToken = pathSegments.last(),
            table = queryParams["table"]!!,
            view = queryParams["view"]!!
        )
    }

    private fun toQueryParams(queryString: String): Map<String, String> {
        return queryString
            .split("&")
            .associate {
                val pair = it.split("=")
                pair[0] to pair[1]
            }
    }

}
