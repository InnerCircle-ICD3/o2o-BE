package com.eatngo.aop

import org.springframework.transaction.annotation.Transactional


@Transactional
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SoftDeletedFilter
