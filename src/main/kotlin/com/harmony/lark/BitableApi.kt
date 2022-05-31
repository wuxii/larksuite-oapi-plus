package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.service.bitable.v1.BitableService
import com.larksuite.oapi.service.bitable.v1.model.*

class BitableApi(val larkApi: LarkApi) {

    private val bitableService: BitableService = larkApi.unwrap(BitableService::class.java)

    fun createBitable(appToken: String, name: String, fields: List<AppTableField> = listOf()): String {
        val body = AppTableCreateReqBody()
        body.table = ReqTable()
        body.table.name = name
        val tableId = bitableService.appTables.create(body)
            .setAppToken(appToken)
            .execute()
            .ensureData { LarkException("", it) }
            .tableId

        val existsFields = getBitableFields(appToken, tableId)



        addBitableFields(appToken, tableId, fields)
        return tableId
    }

    fun addBitableFields(bitableAddress: BitableAddress, fields: List<AppTableField>): List<String> {
        return addBitableFields(bitableAddress.appToken, bitableAddress.table, fields)
    }

    fun addBitableFields(appToken: String, tableId: String, fields: List<AppTableField>): List<String> {
        return fields.map { this.addBitableField(appToken, tableId, it) }
    }

    fun addBitableField(appToken: String, tableId: String, field: AppTableField): String {
        return bitableService.appTableFields.create(field)
            .setAppToken(appToken)
            .setTableId(tableId)
            .execute()
            .ensureData { LarkException("添加表格字段失败", it) }
            .field
            .fieldId
    }

    fun getBitableFields(bitableAddress: BitableAddress): ContinuouslyResult<AppTableField> {
        return getBitableFields(bitableAddress.appToken, bitableAddress.table)
    }

    fun getBitableFields(appToken: String, tableId: String): ContinuouslyResult<AppTableField> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableFields.list()
                .setAppToken(appToken)
                .setTableId(tableId)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格表字段失败", it) }
        }
    }

    fun removeBitableFields(appToken: String, tableId: String, fieldIds: List<String>) {
        bitableService.appTableFields.delete()
            .setAppToken(appToken)
            .setTableId(tableId)
            .setFieldId("")
    }

    fun getBitableRecords(bitableAddress: BitableAddress): ContinuouslyResult<AppTableRecord> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableRecords.list()
                .setAppToken(bitableAddress.appToken)
                .setTableId(bitableAddress.table)
                .setUserIdType("union_id")
                .setViewId(bitableAddress.view)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格数据失败", it) }
        }
    }

    fun getBitableRecords(
        bitableAddress: BitableAddress,
        filter: BitableRecordFilter = BitableRecordFilter(),
        userIdType: String? = null,
    ): ContinuouslyResult<AppTableRecord> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableRecords.list()
                .setAppToken(bitableAddress.appToken)
                .setTableId(bitableAddress.table)
                .setUserIdType(userIdType)
                .setViewId(filter.viewId ?: bitableAddress.view)
                .setFilter(filter.filter)
                .setSort(filter.sort)
                .setFieldNames(Jsons.DEFAULT_GSON.toJson(filter.fieldNames))
                .setTextFieldAsArray(filter.textFieldAsArray)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格数据失败", it) }
        }
    }

    fun addBitableRecords(bitableAddress: BitableAddress, vararg records: Map<String, Any>) {
        val tableRecords = records.map {
            val record = AppTableRecord()
            record.fields = it
            record
        }.toTypedArray()
        addBitableRecords(bitableAddress, *tableRecords)
    }

    fun addBitableRecords(bitableAddress: BitableAddress, vararg records: AppTableRecord) {
        val body = AppTableRecordBatchCreateReqBody()
        body.records = records
        bitableService.appTableRecords
            .batchCreate(body)
            .setAppToken(bitableAddress.appToken)
            .setTableId(bitableAddress.table)
            .execute()
            .ensureData { LarkException("添加多为表格数据失败", it) }
    }
}
