package com.zw.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.ttl}")
    private String ttl;

    @Value("${jwt.token-name}")
    private String tokenName;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 从令牌中获取用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取指定的声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析令牌获取所有声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断令牌是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 获取令牌名称
     */
    public String getTokenName() {
        return tokenName;
    }

    /**
     * 为用户生成令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 为用户生成令牌，包含用户ID
     */
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 创建令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long expiration = parseTtl();
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析TTL配置，将字符串转换为毫秒数
     */
    private long parseTtl() {
        if (ttl.endsWith("h") || ttl.endsWith("H")) {
            return Duration.ofHours(Long.parseLong(ttl.substring(0, ttl.length() - 1))).toMillis();
        } else if (ttl.endsWith("m") || ttl.endsWith("M")) {
            return Duration.ofMinutes(Long.parseLong(ttl.substring(0, ttl.length() - 1))).toMillis();
        } else if (ttl.endsWith("s") || ttl.endsWith("S")) {
            return Duration.ofSeconds(Long.parseLong(ttl.substring(0, ttl.length() - 1))).toMillis();
        } else if (ttl.endsWith("d") || ttl.endsWith("D")) {
            return Duration.ofDays(Long.parseLong(ttl.substring(0, ttl.length() - 1))).toMillis();
        } else {
            // 默认按秒处理
            return Long.parseLong(ttl) * 1000;
        }
    }

    /**
     * 验证令牌
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 