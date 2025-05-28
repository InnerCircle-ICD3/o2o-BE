package com.eatngo.customer.dto

import com.eatngo.user_account.dto.UserAccountUpdateDto
import com.eatngo.user_account.vo.Nickname

data class CustomerUpdateDto(
    override val nickname: Nickname?,
) : UserAccountUpdateDto

