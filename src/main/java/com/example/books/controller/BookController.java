package com.example.books.controller;

import com.example.books.entity.Book;
import com.example.books.exception.BookErrorResponse;
import com.example.books.exception.BookNotFoundException;
import com.example.books.request.BookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Books Rest API Endpoints",description = "Operations Related to Books")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<Book> books = new ArrayList<>();

    public BookController(){
        initializeBooks();
    }

    private void initializeBooks() {
        books.addAll(List.of(
                new Book(1,"Title One", "Author One", "Science",5),
                new Book(2,"Title Two", "Author Two", "Science",4),
                new Book(3,"Title Three", "Author Three", "History",5),
                new Book(4,"Title Four", "Author Four", "Technology",5),
                new Book(5,"Title Five", "Author Five", "Technology",5)
        ));
    }

    @Operation(summary = "Get all books",description = "Retrieve a list of all available books")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Book> getBooks(@Parameter(description = "Optional query parameter")
                                   @RequestParam(required = false) String category){
    if (category==null){
        return books;
    }
    return books.stream()
            .filter(book -> book.getCategory().equalsIgnoreCase(category))
            .toList();
    }



    @Operation(summary = "Get book by Id",description = "Retrieve a specific book by Id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Book getBookById(@Parameter(description = "Id of a book to be retrieved")
                                @PathVariable @Min(1) long id){
        return books.stream()
                .filter(book -> book.getId()==id)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id - "+id));
    }


    @Operation(summary = "Create a new book",description = "Add a new book to the list")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createBook(@Valid @RequestBody BookRequest bookRequest){
        long id = books.isEmpty() ? 1 : books.getLast().getId()+1;
        Book book = convertToBook(id,bookRequest);
        books.add(book);
    }

    
    @Operation(summary = "Update a book",description = "update a details of an existing book")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public Book updateBook(@Parameter(description = "Id of a book to update")
                               @PathVariable  @Min(1) long id,
                           @Valid @RequestBody BookRequest bookRequest) {
        for (int i=0;i<books.size();i++){
            if (books.get(i).getId()==id){
                Book updatedBook = convertToBook(id,bookRequest);
                books.set(i,updatedBook);
                return updatedBook;
            }
        }
        throw new BookNotFoundException("Book not found with Id - "+id);
    }


    @Operation(summary = "Delete a book",description = "Delete a book from the list")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@Parameter(description = "Id of a book to delete")
                               @PathVariable @Min(1) long id){
        books.stream()
                .filter(book -> book.getId()==id)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found with Id - "+id));

        books.removeIf(book -> book.getId()==id);

    }


    private Book convertToBook(long id , BookRequest bookRequest){
        return new Book(id,bookRequest.getTitle(),bookRequest.getAuthor(),bookRequest.getCategory(),bookRequest.getRating());
    }

}
