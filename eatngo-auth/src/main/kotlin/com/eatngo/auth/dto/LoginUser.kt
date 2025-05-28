package com.eatngo.auth.dto

interface LoginUser {
    val userAccountId: Long
    val nickname: String?
    val roles: List<String>
}