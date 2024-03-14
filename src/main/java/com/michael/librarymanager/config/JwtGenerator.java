package com.michael.librarymanager.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtGenerator {

  private final String firma = "mamammamamamamamamam";

  public String generateToken(Authentication authentication) {
    String username = authentication.getName();

    String token = Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(new Date().getTime() * 360000))
      .signWith(SignatureAlgorithm.HS256, firma)
      .compact();

    return token;
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts
      .parser()
      .setSigningKey(firma)
      .parseClaimsJws(token)
      .getBody();

    return claims.getSubject();
  }

  public Boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(firma).parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException ex) {
      // El token ha expirado
      return false;
    } catch (
      UnsupportedJwtException
      | MalformedJwtException
      | SignatureException
      | IllegalArgumentException ex
    ) {
      // El token es inválido
      return false;
    }
  }
}
