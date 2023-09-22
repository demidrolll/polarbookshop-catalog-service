package com.polarbookshop.catalogservice.domain

import org.springframework.stereotype.Service

@Service
class BookService(
  private val bookRepository: BookRepository
) {

  fun viewBookList(): Iterable<Book> =
    bookRepository.findAll()

  fun viewBookDetails(isbn: String): Book =
    bookRepository.findByIsbn(isbn)
      .orElseThrow { BookNotFoundException(isbn) }

  fun addBookToCatalog(book: Book): Book {
    if (bookRepository.existsByIsbn(book.isbn)) {
      throw BookAlreadyExistsException(book.isbn)
    }
    return bookRepository.save(book)
  }

  fun removeBookFromCatalog(isbn: String) {
    bookRepository.deleteByIsbn(isbn)
  }

  fun editBookDetails(isbn: String, book: Book): Book =
    bookRepository.findByIsbn(isbn)
      .map { existBook ->
        val updated = Book(
          id = existBook.id,
          isbn = existBook.isbn,
          title = book.title,
          author = book.author,
          price = book.price,
          version = existBook.version,
          createdDate = existBook.createdDate,
          lastModifiedDate = existBook.lastModifiedDate,
        )
        bookRepository.save(updated)
      }
      .orElseGet {
        addBookToCatalog(book)
      }
}
