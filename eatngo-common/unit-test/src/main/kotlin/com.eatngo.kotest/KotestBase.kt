package com.eatngo.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class KotestBase : StringSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var restTemplate: TestRestTemplate
}