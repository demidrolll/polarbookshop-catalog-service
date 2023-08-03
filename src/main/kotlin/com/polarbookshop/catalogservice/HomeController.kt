package com.polarbookshop.catalogservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

  @GetMapping("/")
  fun getGreeting(): String = "Welcome to the book catalog!"
}
