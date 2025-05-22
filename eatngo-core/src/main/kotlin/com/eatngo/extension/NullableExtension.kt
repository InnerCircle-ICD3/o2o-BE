package com.eatngo.extension

/**
 * nullable 객체가 null이면 exceptionSupplier() 가 던져주는 예외를 throw
 * @param exceptionSupplier 예외를 생성할 람다
 */
inline fun <T> T?.orThrow(exceptionSupplier: () -> Throwable): T =
    this ?: throw exceptionSupplier()