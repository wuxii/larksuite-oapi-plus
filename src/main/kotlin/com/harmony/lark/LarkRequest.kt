package com.harmony.lark

import com.larksuite.oapi.core.Config
import com.larksuite.oapi.core.api.AccessTokenType
import com.larksuite.oapi.core.api.Api
import com.larksuite.oapi.core.api.request.Request
import com.larksuite.oapi.core.api.response.Response

class LarkRequest(
    private val httpMethod: String,
    private val config: Config,
) {

    private lateinit var path: String
    private var body: Any? = null
    private val pathVariables: MutableMap<String, Any?> = linkedMapOf()
    private val queryParams: MutableMap<String, Any?> = linkedMapOf()

    private val accessTokenType: MutableSet<AccessTokenType> = linkedSetOf(AccessTokenType.Tenant)

    fun setPath(path: String): LarkRequest {
        this.path = path
        return this
    }

    fun setAccessTokenType(vararg accessTokenType: AccessTokenType): LarkRequest {
        this.accessTokenType.clear()
        this.accessTokenType.addAll(accessTokenType)
        return this
    }

    fun addAccessTokenType(vararg accessTokenType: AccessTokenType): LarkRequest {
        this.accessTokenType.addAll(accessTokenType)
        return this
    }

    fun setPathVariable(name: String, value: Any?): LarkRequest {
        this.pathVariables[name] = value
        return this
    }

    fun setQueryParams(name: String, value: Any?): LarkRequest {
        this.queryParams[name] = value
        return this
    }

    fun setPageParams(pageToken: String?, pageSize: Int = 100): LarkRequest {
        setQueryParams("page_token", pageToken)
        setQueryParams("page_size", pageSize)
        return this
    }

    fun setBody(body: Any?): LarkRequest {
        this.body = body
        return this
    }

    fun execute(): Response<Map<String, Any>> {
        return execute(mutableMapOf())
    }

    fun <DATA> execute(dataType: Class<DATA>): Response<DATA> {
        return execute(dataType.declaredConstructors.first { it.parameterCount == 0 }.newInstance() as DATA)
    }

    fun <DATA> execute(data: DATA): Response<DATA> {
        val request = newRequest(
            httpPath = path,
            httpMethod = httpMethod,
            accessTokenType = accessTokenType.toList(),
            body = body,
            result = data,
            Request.setPathParams(pathVariables),
            Request.setQueryParams(queryParams)
        )
        return Api.send(config, request)
    }

}
