package com.eatngo.order.domain

enum class Status {
    CREATED {
        override fun cancel(): Status = CANCELED
        override fun ready(): Status = READY
    },
    READY{
        override fun confirm(): Status = CONFIRMED
        override fun cancel(): Status = CANCELED
    },
    CONFIRMED {
        override fun done(): Status = DONE
        override fun cancel(): Status = CANCELED
        override fun ready(): Status = READY
    },
    CANCELED,
    DONE;

    open fun confirm(): Status = invalidTransition("confirm")
    open fun ready(): Status = invalidTransition("ready")
    open fun cancel(): Status = invalidTransition("cancel")
    open fun done(): Status = invalidTransition("done")

    private fun invalidTransition(action: String): Status =
        throw IllegalStateException("Cannot '$action' when status is '$this'")
}