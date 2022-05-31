package com.harmony.lark

import com.larksuite.oapi.service.doc.v2.DocService
import com.larksuite.oapi.service.doc.v2.model.DocContentResult

/**
 * @author wuxin
 */
class DocApi(private val larkApi: LarkApi) {

    val docService = larkApi.unwrap(DocService::class)

    fun getDocContent(docToken: String): DocContentResult {
        return docService.docs.content()
            .setDocToken(docToken)
            .execute()
            .ensureData { LarkException("", it) }
    }

}
