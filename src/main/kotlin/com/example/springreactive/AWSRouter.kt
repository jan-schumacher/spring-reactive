package com.example.springreactive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration(proxyBeanMethods = false)
class AWSRouter {

    @Bean
    fun awsRoutes(awsRequestHandler: AWSRequestHandler): RouterFunction<ServerResponse> =
        RouterFunctions.route(RequestPredicates.GET("/ip-ranges"), awsRequestHandler::getIpRanges)
}