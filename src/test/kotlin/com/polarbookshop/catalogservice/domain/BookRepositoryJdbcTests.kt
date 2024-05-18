package com.polarbookshop.catalogservice.domain

import com.polarbookshop.catalogservice.config.DataConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles

@DataJdbcTest
@Import(DataConfig::class)
@AutoConfigureTestDatabase(
  replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests(
  @Autowired
  private val bookRepository: BookRepository,
  @Autowired
  private val jdbcAggregateTemplate: JdbcAggregateTemplate
) {

  @Test
  fun findBookByIsbnWhenExisting() {
    val bookIsbn = "1234561237";
    val book = Book(isbn = bookIsbn, title = "Title", author = "Author", price = 12.90)
    jdbcAggregateTemplate.insert(book)
    val actualBook = bookRepository.findByIsbn(bookIsbn)

    assertThat(actualBook).isPresent()
    assertThat(actualBook.get().isbn).isEqualTo(book.isbn)
  }

  @Test
  fun `when create book not authenticated then no audit metadata`() {
    val bookToCreate = Book(
      isbn = "1232343456",
      title = "Title",
      author = "Author",
      price = 12.90,
      publisher = "Polarsophia"
    )
    val createdBook = bookRepository.save(bookToCreate)
    assertThat(createdBook.createdBy).isNull()
    assertThat(createdBook.lastModifiedBy).isNull()
  }

  @Test
  @WithMockUser("john")
  fun `when create book authenticated then audit metadata`() {
    val bookToCreate = Book(
      isbn = "1232343456",
      title = "Title",
      author = "Author",
      price = 12.90,
      publisher = "Polarsophia"
    )
    val createdBook = bookRepository.save(bookToCreate)
    assertThat(createdBook.createdBy).isEqualTo("john")
    assertThat(createdBook.lastModifiedBy).isEqualTo("john")
  }
}
