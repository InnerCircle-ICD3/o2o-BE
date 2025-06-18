package com.eatngo.auth.config

import com.eatngo.auth.constants.AuthenticationConstants.ANONYMOUS_USER_PRINCIPAL
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class SecurityAuditorAware : AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || authentication.name == ANONYMOUS_USER_PRINCIPAL) {
            return Optional.empty()
        }
        return if (authentication.isAuthenticated) {
            authentication.name.toLongOrNull()?.let { Optional.of(it) } ?: Optional.empty()
        } else {
            Optional.empty()
        }
    }
}