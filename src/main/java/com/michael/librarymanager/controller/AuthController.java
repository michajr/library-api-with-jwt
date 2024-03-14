package com.michael.librarymanager.controller;

import com.michael.librarymanager.dtos.AuthDto;
import com.michael.librarymanager.dtos.ResponseDto;
import com.michael.librarymanager.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/auth")
public class AuthController {

  private AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody AuthDto authDto) {
    return authService.registerUser(authDto);
  }

  @PostMapping("/register-adm")
  public ResponseEntity<String> registerAdmin(@RequestBody AuthDto authDto) {
    return authService.registerAdmin(authDto);
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseDto> login(@RequestBody AuthDto authDto) {
    return authService.login(authDto);
  }
}
