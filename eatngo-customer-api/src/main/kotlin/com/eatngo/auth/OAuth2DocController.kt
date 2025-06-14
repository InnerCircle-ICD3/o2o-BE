package com.eatngo.auth

import com.eatngo.user_account.oauth2.constants.OAuth2Provider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "OAuth2", description = "OAuth2 관련 인증(회원가입/로그인), 로그아웃 API")
@RestController
class OAuth2DocController {

    @Operation(
        summary = "카카오 로그인 리디렉션",
        description = """
            사용자를 Kakao OAuth2 로그인 페이지로 리디렉션합니다.
            UserAccount를 조회 또는 생성하며 이후 StoreOwner를 조회 또는 생성한 뒤 이를 기반으로 토큰을 생성 합니다.
            생성된 토큰은 쿠키에 HttpOnly로 저장됩니다.
            토큰에는 UserAccountId, StoreOwnerId, roles가 포함됩니다.
            Swagger에서는 정상 테스트되지 않습니다.
        """
    )
    @ApiResponse(
        responseCode = "302",
        description = "로그인 성공 시 '/'로, 닉네임이 없으면 '/mypage/complete-profile'로 리디렉션",
        content = [],
        headers = [
            Header(
                name = "Location",
                description = "로그인 후 리디렉션 URL",
                required = true
            )
        ]
    )
    @GetMapping("/api/v1/oauth2/authorization/{provider}")
    fun redirectToKakao(
        @Parameter(
            description = "OAuth2 제공자",
            example = "KAKAO",
            required = true
        )
        @PathVariable provider: OAuth2Provider = OAuth2Provider.KAKAO
    ) {
        // 실제 리디렉션은 Spring Security가 수행하므로 여기는 비어 있음
    }

    @Operation(
        summary = "로그아웃",
        description = """
            쿠키에서 토큰을 제거하고 연결 된 Oauth2 세션에서 로그아웃합니다.
            Swagger에서는 정상 테스트되지 않습니다.
        """
    )
    @ApiResponse(
        responseCode = "302",
        description = "로그아웃 성공",
        content = [],
        headers = [
            Header(
                name = "Location",
                description = "로그아웃 후 리디렉션 URL",
                required = true
            )
        ]
    )
    @PostMapping("/api/v1/oauth2/logout")
    fun logOut() {
        // 실제 리디렉션은 Spring Security가 수행하므로 여기는 비어 있음
    }

}