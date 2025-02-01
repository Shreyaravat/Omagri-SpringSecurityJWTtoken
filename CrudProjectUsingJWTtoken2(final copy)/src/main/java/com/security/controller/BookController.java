package com.security.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.security.model.Book;
import com.security.repo.BookRepository;

@RestController
@RequestMapping("/books")
public class BookController 
{

    @Autowired
    private BookRepository bookRepository;
    
    private static final String UPLOAD_DIR = "C:\\Users\\Admin\\Desktop\\bookimages";
    
    @GetMapping
    public List<Book> getAllBooks()
    {
        return bookRepository.findAll();
    }
    

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = false) String title,
    							  @RequestParam(required = false) String author,
    							  @RequestParam(required = false) Double price,
    							  @RequestParam(required = false) Integer id)
    {
    	// Fetch all the books from the repository
    	List<Book> books = bookRepository.findAll().stream()
    						 .filter(book -> (title == null || book.getTitle().toLowerCase().contains(title.toLowerCase())))
    						 .filter(book -> (author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase())))
    						 .filter(book -> (price == null || book.getPrice()==(price)))
    						 .filter(book -> (id == null || book.getId() == id))
    						 .collect(Collectors.toList());
    	
    	if(books.isEmpty())
    	{
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No book found with the given search criteria.");
    	}
    	return ResponseEntity.ok(books);
    }
    
//    @GetMapping("/{id}")
//    public Book getBookById(@PathVariable int id) 
//    {
//        return bookRepository.findById(id).orElse(null);
//    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable int id) 
    {
        Optional<Book> book = bookRepository.findById(id);
        
        if (book.isPresent()) 
        {
            return ResponseEntity.ok(book.get());  // Return the book if found
        } 
        else 
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ID " + id + " not found");  // Return message if not found
        }
    }




//    @PostMapping
//    public Book addBook(@RequestBody Book book) 
//    {
//        return bookRepository.save(book);
//    }
    
    @PostMapping
    public ResponseEntity<?> addBook(@RequestParam("title") String title,
                                     @RequestParam("author") String author,
                                     @RequestParam("price") double price,
                                     @RequestParam("imageName") MultipartFile image) 
    {
        try 
        {
            String imageName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR + "/" + imageName);
            Files.copy(image.getInputStream(), imagePath);

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
            book.setImageName(imageName);

            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(savedBook);
        } 
        catch (IOException e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the book or image.");
        }
    }

    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) 
    {
        try 
        {
            Path imagePath = Paths.get(UPLOAD_DIR + "/" + imageName);
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } 
            else 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id,
                                        @RequestParam("title") String title,
                                        @RequestParam("author") String author,
                                        @RequestParam("price") double price,
                                        @RequestParam(value = "image", required = false) MultipartFile image)
    {
        try
        {
            // Fetch the existing book
            Book book = bookRepository.findById(id).orElse(null);
            if (book == null) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID " + id + " not found.");
            }

            // Update book details
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);

            // If an image file is provided, update the image
            if (image != null && !image.isEmpty()) 
            {
                String imageName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
                Path imagePath = Paths.get(UPLOAD_DIR + "/" + imageName);
                Files.copy(image.getInputStream(), imagePath);
                book.setImageName(imageName);
            }

            // Save the updated book
            Book updatedBook = bookRepository.save(book);
            return ResponseEntity.ok(updatedBook);

        } 
        catch (IOException e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update the book or image.");
        }
    }


//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody Book bookDetails) 
//    {
//        Book book = bookRepository.findById(id).orElse(null);
//        if (book != null) {
//            book.setTitle(bookDetails.getTitle());
//            book.setAuthor(bookDetails.getAuthor());
//            book.setPrice(bookDetails.getPrice());
//            Book updatedBook = bookRepository.save(book);
//            return ResponseEntity.ok(updatedBook); // Return the updated book with 200 OK
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id does not exist"); // Return error message with 404
//    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) 
    {
        if (bookRepository.existsById(id)) 
        {
            bookRepository.deleteById(id);
            return "Book deleted successfully.";
        }
        return "Book not found.";
    }
}












//Retrieve Image by Name:
//
//java
//Copy code
//@GetMapping("/image/{imageName}")
//public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
//    try {
//        Path imagePath = Paths.get(UPLOAD_DIR + "/" + imageName);
//        Resource resource = new UrlResource(imagePath.toUri());
//        if (resource.exists() || resource.isReadable()) {
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_JPEG)
//                    .body(resource);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }
//}