package com.eatngo.oauth2.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "kakao-oauth2",
    url = "\${oauth2.kakao.url}",
)
interface KakaoOAuth2Client {

    @PostMapping(
        path = ["/v1/user/unlink"],
        consumes = ["application/x-www-form-urlencoded"]
    )
    fun unlink(
        @RequestHeader("Authorization") accessToken: String,
    )
}