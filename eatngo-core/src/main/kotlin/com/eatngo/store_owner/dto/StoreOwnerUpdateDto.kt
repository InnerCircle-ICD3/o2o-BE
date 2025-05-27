package com.eatngo.store_owner.dto

import com.eatngo.user_account.dto.UserAccountUpdateDto
import com.eatngo.user_account.vo.Nickname

data class StoreOwnerUpdateDto(
    override val nickname: Nickname? = null,
) : UserAccountUpdateDto