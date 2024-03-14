package com.michael.librarymanager.controller;

import com.michael.librarymanager.config.JwtGenerator;
import com.michael.librarymanager.dtos.BookDto;
import com.michael.librarymanager.model.Book;
import com.michael.librarymanager.model.UserEntity;
import com.michael.librarymanager.repository.BookRepository;
import com.michael.librarymanager.repository.UserRepository;
import com.michael.librarymanager.utils.UtilClass;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/library")
public class LibraryController {

  private BookRepository bookRepository;
  private JwtGenerator jwtGenerator;
  private UserRepository userRepository;

  @Autowired
  public LibraryController(
    BookRepository bookRepository,
    JwtGenerator jwtGenerator,
    UserRepository userRepository
  ) {
    this.bookRepository = bookRepository;
    this.jwtGenerator = jwtGenerator;
    this.userRepository = userRepository;
  }

  @GetMapping("/books")
  public List<Book> getBooks() {
    return bookRepository.findAll();
  }

  @GetMapping("/book/{id}")
  public Book getBookById(@PathVariable Integer id) {
    return bookRepository.findById(id).get();
  }

  @PostMapping("/book")
  public void addBook(@RequestBody BookDto bookDto) {
    Book book = Book
      .builder()
      .title(bookDto.getTitle())
      .author(bookDto.getAuthor())
      .genre(bookDto.getGenre())
      .description(bookDto.getDescription())
      .url_image(bookDto.getUrl_image())
      .build();

    bookRepository.save(book);
  }

  @PutMapping("/book/{id}")
  public ResponseEntity<String> editBook(
    @PathVariable Integer id,
    @RequestBody BookDto bookDto
  ) {
    try {
      Book bookFound = bookRepository
        .findById(id)
        .orElseThrow(() -> new Exception("Book not fund"));

      bookFound.setAuthor(bookDto.getAuthor());
      bookFound.setGenre(bookDto.getGenre());
      bookFound.setDescription(bookDto.getDescription());
      bookFound.setTitle(bookDto.getTitle());
      bookFound.setUrl_image(bookDto.getUrl_image());

      bookRepository.save(bookFound);

      return new ResponseEntity<>("Book Edited", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/book/{id}")
  public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
    bookRepository.deleteById(id);

    return new ResponseEntity<>("Book Deleted", HttpStatus.OK);
  }

  // //add to favourites
  // @PostMapping("/book/add-favourite/{id}")
  // public String addToFavourites(
  //   HttpServletRequest request,
  //   @PathVariable Integer id
  // ) {
  //   UtilClass utilClass = new UtilClass();
  //   String token = utilClass.getTokenFromRequest(request);
  //   String username = jwtGenerator.getUsernameFromToken(token);
  //   UserEntity userEntity = userRepository.findByUsername(username).get();

  //   userEntity.getFavouriteBooks().add(id);

  //   userRepository.save(userEntity);

  //   return "Added to favoirites";
  // }

  //!Chat-gpt Code

  @PostMapping("/book/add-favourite/{id}")
  public String addToFavourites(
    HttpServletRequest request,
    @PathVariable Integer id
  ) {
    UtilClass utilClass = new UtilClass();
    String token = utilClass.getTokenFromRequest(request);
    String username = jwtGenerator.getUsernameFromToken(token);

    // Retrieve the user entity from the repository
    Optional<UserEntity> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      UserEntity userEntity = userOptional.get();
      List<Integer> favouriteBooks = userEntity.getFavouriteBooks();
      if (favouriteBooks == null) {
        favouriteBooks = new ArrayList<>();
        userEntity.setFavouriteBooks(favouriteBooks);
      }
      favouriteBooks.add(id);
      userRepository.save(userEntity);
      return "Added to favourites";
    } else {
      return "User not found";
    }
  }

  //Get my favourites books
  @GetMapping("/book/favourites")
  public List<Book> getFavourites(HttpServletRequest request) {
    UtilClass utilClass = new UtilClass();
    String token = utilClass.getTokenFromRequest(request);
    String username = jwtGenerator.getUsernameFromToken(token);
    UserEntity userEntity = userRepository.findByUsername(username).get();

    List<Book> favBooks = new ArrayList<>();

    for (Integer id : userEntity.getFavouriteBooks()) {
      Book book = bookRepository.findById(id).get();

      favBooks.add(book);
    }

    return favBooks;
  }

  @DeleteMapping("/book/remove-fav/{id}")
  public void removeFromFavourites(
    @PathVariable Integer id,
    HttpServletRequest request
  ) {
    UtilClass utilClass = new UtilClass();
    String token = utilClass.getTokenFromRequest(request);
    String username = jwtGenerator.getUsernameFromToken(token);
    UserEntity userEntity = userRepository.findByUsername(username).get();

    userEntity.getFavouriteBooks().remove(id);

    userRepository.save(userEntity);
  }
}
