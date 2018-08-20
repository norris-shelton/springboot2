package com.javaninja.springboot.springboot;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
public class BookControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private Book createRandomBook() {
        Book book = new Book();
        book.setTitle(randomAlphabetic(10));
        book.setAuthor(randomAlphabetic(15));
        return book;
    }

    @Test
    public void testGetAllBooks() {

        try {
            mockMvc.perform(get("/api/books"))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(header().string(HttpHeaders.CONTENT_TYPE,
                                              startsWith(MediaType.APPLICATION_JSON.toString())));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testUnknownUrl() {
        try {
            mockMvc.perform(get("/random")).andDo(print()).andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetCreatedBookById() {
        Book book = createRandomBook();
        try {
            mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                                              .content(new ObjectMapper().writeValueAsString(book)))
                   .andDo(print())
                   .andExpect(status().isCreated())
                   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, startsWith(MediaType.APPLICATION_JSON_VALUE)))
                   .andExpect(jsonPath("$.title", is(book.getTitle())));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetNotExistBookByIdUnknown() {
        try {
            mockMvc.perform(get("/api/books/4"))
                   .andDo(print())
                   .andExpect(status().isNotFound())
                   .andExpect(header().string(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                                              startsWith(MediaType.APPLICATION_JSON.toString())));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testCreateBookInvalid() {
        Book book = createRandomBook();
        book.setAuthor(null);

        try {
            mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                                              .content(new ObjectMapper().writeValueAsString(book)))
                   .andDo(print())
                   .andExpect(status().isInternalServerError())
                   .andExpect(header().string(HttpHeaders.CONTENT_TYPE,
                                              startsWith(MediaType.APPLICATION_JSON.toString())));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void whenUpdateCreatedBook_thenUpdated() {
        Book book = createRandomBook();
        try {
            ResultActions
                resultActions =
                mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                                                  .content(new ObjectMapper().writeValueAsString(book)))
                       .andDo(print())
                       .andExpect(status().isCreated())
                       .andExpect(header().string(HttpHeaders.CONTENT_TYPE,
                                                  startsWith(MediaType.APPLICATION_JSON_VALUE)))
                       .andExpect(jsonPath("$.id", not("0")))
                       .andExpect(jsonPath("$.title", is(book.getTitle())))
                       .andExpect(jsonPath("$.author", not("newAuthor")));
            String json = resultActions.andReturn().getResponse().getContentAsString();
            book.setId(new ObjectMapper().readValue(json, Book.class).getId());
        } catch (Exception e) {
            fail(e.toString());
        }

        book.setAuthor("newAuthor");
        try {
            mockMvc.perform(put("/api/books/" + book.getId()).contentType(MediaType.APPLICATION_JSON)
                                                             .content(new ObjectMapper().writeValueAsString(book)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, startsWith(MediaType.APPLICATION_JSON_VALUE)))
                   .andExpect(jsonPath("$.title", is(book.getTitle())))
                   .andExpect(jsonPath("$.author", is("newAuthor")));
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
