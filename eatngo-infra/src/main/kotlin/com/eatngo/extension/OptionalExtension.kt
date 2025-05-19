package com.eatngo.extension

import java.util.*


fun <T, R> Optional<T>.mapOrNull(mapper: (T) -> R): R? =
    this.map(mapper).orElse(null)