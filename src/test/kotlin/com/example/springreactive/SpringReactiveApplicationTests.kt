package com.example.springreactive

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClientConfigurer
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import java.util.function.Consumer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringReactiveApplicationTests {

    @Autowired
    lateinit var webClient: WebTestClient

    @Test
    fun contextLoads() {
        val result = webClient
            .mutateWith(WebTestClientConfigurer { builder: WebTestClient.Builder, _: WebHttpHandlerBuilder?, _: ClientHttpConnector? ->
                builder.codecs {
                    it.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)
                }
            })
            .get()
            .uri {
                it.path("/ip-ranges").queryParam("region", "EU").build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(object : ParameterizedTypeReference<String>() {})
            .returnResult()
            .responseBody
//        Assertions.assertThat(result).allMatch {
//            it.region.startsWith("eu")
//        }
        println(result)
    }

}
