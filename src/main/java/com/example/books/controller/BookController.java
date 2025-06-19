package com.example.books.controller;

import com.example.books.entity.Book;
import com.example.books.request.BookRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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


    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String category){
    if (category==null){
        return books;
    }
    return books.stream()
            .filter(book -> book.getCategory().equalsIgnoreCase(category))
            .toList();
    }


    @GetMapping("/{id}")
    public Book getBookById(@PathVariable long id){
        return books.stream()
                .filter(book -> book.getId()==id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public void createBook(@RequestBody BookRequest bookRequest){
        long id = books.isEmpty() ? 1 : books.getLast().getId()+1;
        Book book = convertToBook(id,bookRequest);
        books.add(book);
    }

    @PutMapping("/{id}")
    public void updateBook(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        for (int i=0;i<books.size();i++){
            if (books.get(i).getId()==id){
                Book updatedBook = convertToBook(id,bookRequest);
                books.set(i,updatedBook);
            }
        }
    }


    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable long id){
        books.removeIf(book -> book.getId()==id);

    }

    private Book convertToBook(long id , BookRequest bookRequest){
        return new Book(id,bookRequest.getTitle(),bookRequest.getAuthor(),bookRequest.getCategory(),bookRequest.getRating());
    }

}
