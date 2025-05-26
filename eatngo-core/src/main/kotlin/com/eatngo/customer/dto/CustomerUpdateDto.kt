package com.eatngo.customer.dto

import com.eatngo.user_account.dto.UserAccountUpdateDto

data class CustomerUpdateDto(
    override val nickname: String?,
) : UserAccountUpdateDto

