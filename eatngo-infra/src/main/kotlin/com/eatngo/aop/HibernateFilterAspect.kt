package com.eatngo.aop

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.hibernate.Session
import org.springframework.stereotype.Component

@Component
@Aspect
class HibernateFilterAspect(
    @PersistenceContext private val entityManager: EntityManager
) {
    @Before("@annotation(com.eatngo.aop.SoftDeletedFilter)")
    fun enableSoftDeleteFilter() {
        val session = entityManager.unwrap(Session::class.java)
        session.enableFilter("deletedFilter").setParameter("deletedAt", false)
    }
}