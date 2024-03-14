package com.michael.librarymanager.dtos;

import lombok.Data;

@Data
public class ResponseDto {

  private String accessType;
  private String type = "Bearer ";

  public ResponseDto(String accessType) {
    this.accessType = accessType;
  }
}
