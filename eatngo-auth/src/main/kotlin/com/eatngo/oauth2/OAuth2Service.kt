package com.eatngo.oauth2

import com.eatngo.user_account.oauth2.constants.OAuth2Provider

interface OAuth2Service {

    fun supports(provider: OAuth2Provider): Boolean

    fun unlink(accessToken: String)


}