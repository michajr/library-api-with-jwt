package com.michael.librarymanager.services;

import com.michael.librarymanager.config.JwtGenerator;
import com.michael.librarymanager.dtos.AuthDto;
import com.michael.librarymanager.dtos.ResponseDto;
import com.michael.librarymanager.model.Roles;
import com.michael.librarymanager.model.UserEntity;
import com.michael.librarymanager.repository.RoleRepository;
import com.michael.librarymanager.repository.UserRepository;

import lombok.NoArgsConstructor;

import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@NoArgsConstructor
@Service
public class AuthService {

  private JwtGenerator jwtGenerator;
  private AuthenticationManager authenticationManager;
  private PasswordEncoder passwordEncoder;
  private UserRepository userRepository;
  private RoleRepository roleRepository;

  public AuthService(
    JwtGenerator jwtGenerator,
    AuthenticationManager authenticationManager,
    PasswordEncoder passwordEncoder,
    UserRepository userRepository,
    RoleRepository roleRepository
  ) {
    this.jwtGenerator = jwtGenerator;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  public ResponseEntity<String> registerUser(AuthDto authDto) {
    if (userRepository.existsByUsername(authDto.getUsername())) {
      return new ResponseEntity<>("Username in use", HttpStatus.BAD_REQUEST);
    }

    UserEntity userEntity = new UserEntity();

    userEntity.setUsername(authDto.getUsername());
    userEntity.setPassword(passwordEncoder.encode(authDto.getPassword()));

    Roles role = roleRepository.findByName("USER").get();
    userEntity.setRoles(Collections.singletonList(role));
    userRepository.save(userEntity);

    return new ResponseEntity<>("User created", HttpStatus.OK);
  }

  //! ADMIN register

  public ResponseEntity<String> registerAdmin(AuthDto authDto) {
    if (userRepository.existsByUsername(authDto.getUsername())) {
      return new ResponseEntity<>("Username in use", HttpStatus.BAD_REQUEST);
    }

    UserEntity userEntity = new UserEntity();

    userEntity.setUsername(authDto.getUsername());
    userEntity.setPassword(passwordEncoder.encode(authDto.getPassword()));

    Roles role = roleRepository.findByName("ADMIN").get();
    userEntity.setRoles(Collections.singletonList(role));
    userRepository.save(userEntity);

    return new ResponseEntity<>("Admin user created", HttpStatus.OK);
  }

  //! Login

  public ResponseEntity<ResponseDto> login(@RequestBody AuthDto authDto) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        authDto.getUsername(),
        authDto.getPassword()
      )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = jwtGenerator.generateToken(authentication);

    return new ResponseEntity<>(new ResponseDto(token), HttpStatus.OK);
  }
}
