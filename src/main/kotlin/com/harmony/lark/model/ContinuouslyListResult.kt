package com.harmony.lark.model

class ContinuouslyListResult<T>(
    result: ListResult<T>,
    private val loader: (token: String?) -> ListResult<T>
) : ListResult<T>(
    result.hasMore,
    result.pageToken,
    result.total,
    result.items
) {

    fun next(): ContinuouslyListResult<T> {
        if (!hasNext()) {
            throw IllegalStateException("no more result")
        }
        val nextResult = loader.invoke(pageToken)
        return ContinuouslyListResult(nextResult, loader)
    }

    fun hasNext(): Boolean {
        return hasMore
    }

}
