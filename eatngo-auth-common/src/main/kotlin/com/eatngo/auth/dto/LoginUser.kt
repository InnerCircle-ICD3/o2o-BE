package com.eatngo.auth.dto

interface LoginUser {
    val userAccountId: Long
    val roles: List<String>
}