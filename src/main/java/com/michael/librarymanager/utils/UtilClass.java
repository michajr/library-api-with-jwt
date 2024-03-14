package com.michael.librarymanager.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

@NoArgsConstructor
public class UtilClass {

  public String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }

    return null;
  }
}
