package com.michael.librarymanager.dtos;

import lombok.Data;

@Data
public class BookDto {
  private String title;
  private String author;
  private String genre;
  private String url_image;
  private String description;
}
