package com.polarbookshop.catalogservice

import com.polarbookshop.catalogservice.config.SecurityConfig
import com.polarbookshop.catalogservice.domain.BookNotFoundException
import com.polarbookshop.catalogservice.domain.BookService
import com.polarbookshop.catalogservice.web.BookController
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookController::class)
@Import(SecurityConfig::class)
class BookControllerMvcTests {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var bookService: BookService

  @MockBean
  private lateinit var jwtDecoder: JwtDecoder

  @Test
  fun `when get book not existing then should return 404`() {
    val isbn = "73737313940"
    given(bookService.viewBookDetails(isbn))
      .willThrow(BookNotFoundException::class.java)

    mockMvc
      .perform(get("/books/$isbn"))
      .andExpect(status().isNotFound)
  }

  @Test
  fun `when delete book with employee role then should return 204`() {
    val isbn = "7373731394"
    mockMvc
      .perform(
        delete("/books/$isbn")
          .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority("ROLE_employee")))
      )
      .andExpect(status().isNoContent)
  }

  @Test
  fun `when delete book with customer role then should return 403`() {
    val isbn = "7373731394"
    mockMvc
      .perform(
        delete("/books/$isbn")
          .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority("ROLE_customer")))
      )
      .andExpect(status().isForbidden)
  }

  @Test
  fun `when delete book not authenticated then should return 401`() {
    val isbn = "7373731394"
    mockMvc
      .perform(delete("/books/$isbn"))
      .andExpect(status().isUnauthorized)
  }
}
