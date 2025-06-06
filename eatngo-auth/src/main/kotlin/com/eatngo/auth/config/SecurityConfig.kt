package com.eatngo.auth.config

import com.eatngo.auth.constants.AuthenticationConstants.ACCESS_TOKEN
import com.eatngo.auth.filter.JwtAuthenticationFilter
import com.eatngo.auth.handler.OAuth2LoginSuccessHandler
import com.eatngo.auth.token.TokenProvider
import com.eatngo.oauth2.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val oAuth2UserService: CustomOAuth2UserService,
    private val authenticationSuccessHandler: OAuth2LoginSuccessHandler,
    private val tokenProvider: TokenProvider
) {

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(tokenProvider)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .formLogin { it.disable() }
            .httpBasic { it.disable() }

//            .authorizeHttpRequests {
//                it.requestMatchers("/", "/oauth2/**", "/login/**", "/api/v1/test/hello").permitAll()
//                    .anyRequest().authenticated()
//            }

            .authorizeHttpRequests { it.anyRequest().permitAll() } // TODO 모든 요청 허용 제거

            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

            .oauth2Login {
                it.userInfoEndpoint { userInfo -> userInfo.userService(oAuth2UserService) }
                    .successHandler(authenticationSuccessHandler)
            }

            .logout {
                it.logoutRequestMatcher(AntPathRequestMatcher("/oauth2/logout", "POST"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler { request, response, authentication ->
                        request.cookies
                            ?.find { cookie -> cookie.name == ACCESS_TOKEN }
                            ?.value
                            ?.let { tokenProvider.deleteRefreshToken(it) }
                    }
                    .deleteCookies(ACCESS_TOKEN)
            }

        return http.build()
    }
}