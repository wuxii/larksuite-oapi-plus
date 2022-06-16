package com.harmony.lark.model

class ContinuouslyResult<T>(
    private val listResult: ListResult<T>,
    private val loader: (token: String?) -> ListResult<T>,
) {

    fun next(): ContinuouslyResult<T> {
        if (!hasNext()) {
            throw IllegalStateException("no more list result")
        }
        val nextResult = loader.invoke(listResult.getPageToken())
        return ContinuouslyResult(nextResult, loader)
    }

    fun hasNext(): Boolean {
        return listResult.getHasMore() && listResult.getPageToken().isNotBlank()
    }

    /**
     * all remain <T> element iterator
     */
    fun toIterator(): Iterator<T> {
        return ItemIterator(this)
    }

    private fun itemIterator(): Iterator<T> {
        return listResult.getItems().iterator()
    }

    private class ItemIterator<T>(first: ContinuouslyResult<T>) : Iterator<T> {

        private var current: ContinuouslyResult<T> = first;
        private var iterator: Iterator<T> = current.itemIterator()

        override fun hasNext(): Boolean {
            return iterator.hasNext() || current.hasNext()
        }

        override fun next(): T {
            if (!hasNext()) {
                throw IllegalStateException("no more item")
            }
            if (iterator.hasNext()) {
                return iterator.next()
            }
            return doNextIterator().next()
        }

        private fun doNextIterator(): Iterator<T> {
            this.current = current.next();
            this.iterator = this.current.itemIterator()
            return this.iterator
        }

    }
}
