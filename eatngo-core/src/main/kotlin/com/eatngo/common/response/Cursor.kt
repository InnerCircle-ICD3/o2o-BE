package com.eatngo.common.response

data class Cursor<T>(
    val contents: List<T>,
    val lastId: Long?
) {

    companion object {
        fun <T> from(content: List<T>, lastId: Long?): Cursor<T> = Cursor(content, lastId)
    }
}