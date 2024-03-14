package com.michael.librarymanager.config;

import com.michael.librarymanager.model.Roles;
import com.michael.librarymanager.model.UserEntity;
import com.michael.librarymanager.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  private Collection<GrantedAuthority> mapAuthorities(List<Roles> roles) {
    return roles
      .stream()
      .map(r -> new SimpleGrantedAuthority(r.getName()))
      .collect(Collectors.toList());
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    UserEntity user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new User(
      user.getUsername(),
      user.getPassword(),
      mapAuthorities(user.getRoles())
    );
  }
}
