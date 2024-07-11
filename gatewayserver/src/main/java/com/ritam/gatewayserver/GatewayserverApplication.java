package com.ritam.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator customGatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()

                // http://localhost:8072/eazybank/account/**
                .route(predicateSpec -> predicateSpec
                        .path("/eazybank/account/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .rewritePath("/eazybank/account/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Respose-Header", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("accountsCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                        )
                        .uri("lb://ACCOUNTS")
                )

                // http://localhost:8072/eazybank/card/**
                .route(predicateSpec -> predicateSpec
                        .path("/eazybank/loan/**")
                        .filters(gatewayFilterSpec ->
                                gatewayFilterSpec
                                        .rewritePath("/eazybank/loan/(?<segment>.*)", "/${segment}")
                                        .addResponseHeader("X-Respose-Header", LocalDateTime.now().toString())
                                        .retry(retryConfig -> retryConfig
                                                .setRetries(5)
                                                .setMethods(HttpMethod.GET)
                                                .setBackoff(Duration.ofMillis(1000),
                                                        Duration.ofMillis(3000), 2, true))
                        )
                        .uri("lb://LOANS")
                )

                // http://localhost:8072/eazybank/loan/**
                .route(predicateSpec -> predicateSpec
                        .path("/eazybank/card/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .rewritePath("/eazybank/card/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Respose-Header", LocalDateTime.now().toString())
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())
                                )
                        )
                        .uri("lb://CARDS")
                ).build();
    }


    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(6))
                        .build())
                .build()
        );
    }

	@Bean
	public KeyResolver userKeyResolver(){
		return exchange -> Mono.justOrEmpty(exchange
				.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}

	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(1, 1, 1);
	}
}
