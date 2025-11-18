package com.codingsena.codingsena_backend.security;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                         .filter(c -> "jwt".equals(c.getName()))
                         .findFirst()
                         .map(c -> c.getValue())
                         .orElse(null);
        }
        // fallback to header (If token is not in cookie)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}