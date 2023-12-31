package com.polarbookshop.catalogservice

import com.polarbookshop.catalogservice.domain.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests(
  @Autowired
  private val webTestClient: WebTestClient
) {

  @Test
  fun contextLoads() {
  }

  @Test
  fun `when post request then book created`() {
    val expectedBook = Book(
      isbn = "1231231231",
      title = "Title",
      author = "Author",
      price = 9.90
    )
    webTestClient
      .post()
      .uri("/books")
      .bodyValue(expectedBook)
      .exchange()
      .expectStatus().isCreated
      .expectBody(Book::class.java).value {
        assertThat(it).isNotNull
        assertThat(it.isbn).isEqualTo(expectedBook.isbn)
      }
  }
}
