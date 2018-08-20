package com.javaninja.springboot.springboot;


import org.springframework.data.repository.CrudRepository;


/**
 * @author norris.shelton
 */
public interface BookRepository extends CrudRepository<Book, Long> {

}
