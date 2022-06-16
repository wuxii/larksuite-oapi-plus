package com.harmony.lark

import com.larksuite.oapi.core.api.response.Response

/**
 * @author wuxin
 */
open class LarkException(
    message: String?,
    val larkResponse: Response<*>? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    constructor(message: String?, cause: Throwable?) : this(message, null, cause)

    override val message: String?
        get() = if (larkResponse?.msg == null) super.message else "${super.message}, ${larkResponse.code} ${larkResponse.msg}"

}
