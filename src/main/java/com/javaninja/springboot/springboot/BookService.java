package com.javaninja.springboot.springboot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


/**
 * @author norris.shelton
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Iterable findAll() {
        return bookRepository.findAll();
    }

    public Book findOne(Long id) {
        return bookRepository.findById(id)
                             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
    }

    public Book updateBook(Book book) {
        bookRepository.findById(book.getId())
                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Already exists"));
        return bookRepository.save(book);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
