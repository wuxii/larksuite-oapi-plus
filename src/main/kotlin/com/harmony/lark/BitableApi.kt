package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.larksuite.oapi.service.bitable.v1.BitableService
import com.larksuite.oapi.service.bitable.v1.model.AppTableCreateReqBody
import com.larksuite.oapi.service.bitable.v1.model.AppTableField
import com.larksuite.oapi.service.bitable.v1.model.ReqTable

class BitableApi(val larkApi: LarkApi) {

    private val bitableService: BitableService = larkApi.unwrap(BitableService::class.java)

    fun createBitable(appToken: String, name: String, fields: List<AppTableField> = listOf()): String {
        val body = AppTableCreateReqBody()
        body.table = ReqTable()
        body.table.name = name
        return bitableService.appTables.create(body)
            .setAppToken(appToken)
            .execute()
            .ensureData()
            .tableId
    }

    fun addBitableField(appToken: String, tableId: String, field: AppTableField): String {
        return bitableService.appTableFields.create(field)
            .setAppToken(appToken)
            .setTableId(tableId)
            .execute()
            .ensureData()
            .field
            .fieldId
    }

    fun getBitableFields(appToken: String, tableId: String): ContinuouslyResult<AppTableField> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableFields.list()
                .setAppToken(appToken)
                .setTableId(tableId)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData()
        }
    }

    fun removeBitableFields(appToken: String, tableId: String, fieldIds: List<String>) {
        bitableService.appTableFields.delete()
            .setAppToken(appToken)
            .setTableId(tableId)
            .setFieldId("")
    }

}
