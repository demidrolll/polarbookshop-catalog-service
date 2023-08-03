package com.polarbookshop.catalogservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HomeController {

  @GetMapping("/")
  fun getGreeting(): Mono<String> = Mono.just("Welcome to the book catalog!")
}
