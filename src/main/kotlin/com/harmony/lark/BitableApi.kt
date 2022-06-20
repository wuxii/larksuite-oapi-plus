package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.larksuite.oapi.service.bitable.v1.BitableService
import com.larksuite.oapi.service.bitable.v1.model.*

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
            .field.fieldId
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

    fun updateBitableField(appToken: String, tableId: String, fieldId: String, field: AppTableField) {
        bitableService.appTableFields.update(field)
            .setAppToken(appToken)
            .setTableId(tableId)
            .setFieldId(fieldId)
            .execute()
            .ensureData()
    }

    fun removeBitableFields(appToken: String, tableId: String, fieldIds: List<String>) {
        bitableService.appTableFields.delete()
            .setAppToken(appToken)
            .setTableId(tableId)
            .setFieldId("")
    }

    fun addBitableRecords(appToken: String, tableId: String, records: List<AppTableRecord>) {
        val body = AppTableRecordBatchCreateReqBody()
        body.records = records.toTypedArray()
        bitableService.appTableRecords
            .batchCreate(body)
            .setAppToken(appToken)
            .setTableId(tableId)
            .execute()
            .ensureData()
    }

}
