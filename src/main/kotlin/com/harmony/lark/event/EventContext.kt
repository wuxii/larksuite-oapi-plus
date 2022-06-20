package com.harmony.lark.event

import com.harmony.lark.LarkApi
import com.harmony.lark.getLarkApi
import com.larksuite.oapi.core.Context

data class EventContext<T>(val context: Context, val data: T) {

    fun getLarkApi(): LarkApi = context.getLarkApi()!!

}
