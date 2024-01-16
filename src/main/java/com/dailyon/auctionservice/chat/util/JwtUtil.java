package com.dailyon.auctionservice.chat.util;

import com.google.common.base.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class JwtUtil {

  @Autowired private Environment environment;

  @Value("${secretKey}")
  private String key;

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
        .parseClaimsJws(token)
        .getBody();
  }

  public String generateToken(
      String username, Map<String, Object> claims) {
    String jwtToken = createToken(claims, username);
    return jwtToken;
  }

  private String createToken(Map<String, Object> claims, String subject) {

    long accessExpInMillis =
        Long.parseLong(Objects.requireNonNull(environment.getProperty("accessExp")));

    String accessToken =
        Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + accessExpInMillis))
            .signWith(Keys.hmacShaKeyFor(environment.getProperty("secretKey").getBytes()))
            .compact();

    return accessToken;
  }
}
