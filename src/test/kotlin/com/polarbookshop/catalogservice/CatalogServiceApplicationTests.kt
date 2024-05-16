package com.polarbookshop.catalogservice

import com.fasterxml.jackson.annotation.JsonProperty
import com.polarbookshop.catalogservice.domain.Book
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
@Testcontainers
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
      .headers {
        it.setBearerAuth(isabelleTokens.accessToken)
      }
      .bodyValue(expectedBook)
      .exchange()
      .expectStatus().isCreated
      .expectBody(Book::class.java).value {
        assertThat(it).isNotNull
        assertThat(it.isbn).isEqualTo(expectedBook.isbn)
      }
  }

  @Test
  fun `when post request unauthorized then 403`() {
    val expectedBook = Book(
      isbn = "1231231231",
      title = "Title",
      author = "Author",
      price = 9.90,
      publisher = "Polarsophia"
    )

    webTestClient
      .post()
      .uri("/books")
      .headers {
        it.setBearerAuth(bjornTokens.accessToken)
      }
      .bodyValue(expectedBook)
      .exchange()
      .expectStatus().isForbidden()
  }

  @Test
  fun `when post request unauthenticated then 401`() {
    val expectedBook = Book(
      isbn = "1231231231",
      title = "Title",
      author = "Author",
      price = 9.90,
      publisher = "Polarsophia"
    )

    webTestClient
      .post()
      .uri("/books")
      .bodyValue(expectedBook)
      .exchange()
      .expectStatus().isUnauthorized()
  }

  companion object {
    @JvmStatic
    private val keycloakContainer: KeycloakContainer =
      KeycloakContainer("quay.io/keycloak/keycloak:24.0.3")
        .withRealmImportFiles("test-realm-config.json")

    @JvmStatic
    @DynamicPropertySource
    fun dynamicProperties(registry: DynamicPropertyRegistry) {
      registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
        keycloakContainer.authServerUrl + "/realms/PolarBookshop"
      }
    }

    private lateinit var bjornTokens: KeycloakToken
    private lateinit var isabelleTokens: KeycloakToken

    @JvmStatic
    @BeforeAll
    fun generateAccessTokens() {
      keycloakContainer.start()

      val webClient = WebClient.builder()
        .baseUrl("${keycloakContainer.authServerUrl}/realms/PolarBookshop/protocol/openid-connect/token")
        .defaultHeader(
          HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_FORM_URLENCODED_VALUE
        )
        .build()

      isabelleTokens = authenticateWith("isabelle", "password", webClient)
      bjornTokens = authenticateWith("bjorn", "password", webClient)
    }

    private fun authenticateWith(username: String, password: String, webClient: WebClient): KeycloakToken {
      return webClient
        .post()
        .body(
          BodyInserters.fromFormData("grant_type", "password")
            .with("client_id", "polar-test")
            .with("username", username)
            .with("password", password)
        )
        .retrieve()
        .bodyToMono(KeycloakToken::class.java)
        .blockOptional()
        .orElseThrow()
    }

    data class KeycloakToken(
      @JsonProperty("access_token")
      var accessToken: String
    )
  }
}
