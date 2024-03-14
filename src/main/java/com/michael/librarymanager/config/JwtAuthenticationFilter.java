package com.michael.librarymanager.config;

import com.michael.librarymanager.utils.UtilClass;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private JwtGenerator jwtGenerator;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    
    UtilClass utilClass = new UtilClass();
    String token = utilClass.getTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)) {
      String username = jwtGenerator.getUsernameFromToken(token);

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(
        username
      );

      List<String> userRoles = userDetails
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

      if (userRoles.contains("USER") || userRoles.contains("ADMIN")) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );

        authenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder
          .getContext()
          .setAuthentication(authenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
