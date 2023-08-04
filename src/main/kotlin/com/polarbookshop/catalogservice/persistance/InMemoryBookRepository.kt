package com.polarbookshop.catalogservice.persistance

import com.polarbookshop.catalogservice.domain.Book
import com.polarbookshop.catalogservice.domain.BookRepository
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryBookRepository : BookRepository {

  override fun findAll(): Iterable<Book> =
    books.values

  override fun findByIsbn(isbn: String): Optional<Book> =
    Optional.ofNullable(books.getOrDefault(isbn, null))

  override fun existsByIsbn(isbn: String): Boolean =
    findByIsbn(isbn).isPresent

  override fun save(book: Book): Book {
    books[book.isbn] = book
    return book
  }

  override fun deleteByIsbn(isbn: String) {
    books.remove(isbn)
  }

  companion object {
    private val books: MutableMap<String, Book> = ConcurrentHashMap()
  }
}
