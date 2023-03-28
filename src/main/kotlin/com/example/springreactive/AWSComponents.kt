package com.example.springreactive

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrElse

private const val HTTPS_IP_RANGES_AMAZONAWS_COM = "https://ip-ranges.amazonaws.com"

private const val IP_RANGES_JSON = "/ip-ranges.json"

@Service
class AWSService(builder: WebClient.Builder) {
    val webClient = builder
        .baseUrl(HTTPS_IP_RANGES_AMAZONAWS_COM)
        .codecs {
            it.defaultCodecs().maxInMemorySize(4 * 1024 * 1024)
        }
        .build()

    fun getIpRanges(): Mono<IPRanges> =
        webClient.get()
            .uri(IP_RANGES_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<IPRanges>() {})
}

@Component
class AWSRequestHandler(private val awsService: AWSService) {
    fun getIpRanges(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
            .body(
                awsService.getIpRanges().flatMapIterable(IPRanges::prefixes)
                    .filter { prefix ->
                        request.queryParam("region").map { region ->
                            prefix.region.startsWith(region, true)
                        }.orElse(true)
                    },
                object : ParameterizedTypeReference<IPRanges>() {})
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class IPRanges(
    val prefixes: List<Prefix>
)

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Prefix(
    val ipPrefix: String,
    val region: String,
    val service: String,
    val networkBorderGroup: String
)