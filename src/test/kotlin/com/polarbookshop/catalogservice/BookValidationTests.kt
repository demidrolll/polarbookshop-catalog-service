package com.polarbookshop.catalogservice

import com.polarbookshop.catalogservice.domain.Book
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class BookValidationTests {

  @Test
  fun `when all fields correct then validation succeeds`() {
    val book = Book(
      isbn = "1234567890",
      title = "Title",
      author = "Author",
      price = 9.90
    )
    val violations = validator.validate(book)
    assertThat(violations).isEmpty()
  }

  @Test
  fun `when isbn defined but incorrect then validation fails`() {
    val book = Book(
      isbn = "a234567890",
      title = "Title",
      author = "Author",
      price = 9.90
    )
    val violations = validator.validate(book)
    assertThat(violations).hasSize(1)
    assertThat(violations.first().message).isEqualTo("The ISBN format must be valid.")
  }

  companion object {

    private lateinit var validator: Validator

    @JvmStatic
    @BeforeAll
    fun setup() {
      val validatorFactory = Validation.buildDefaultValidatorFactory()
      validator = validatorFactory.validator
    }
  }
}
