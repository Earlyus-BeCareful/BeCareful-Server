package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.global.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    private final JwtProperties jwtProperties;
    private  SecretKey secretKey;

    @PostConstruct
    public void init(){
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //refreshToken으로 accessToken을 생성할 때 사용
    public String getPhoneNumber(String token){
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        }catch(Exception e){
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String getInstitutionRank(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("institutionRank", String.class);
    }

    public String getAssociationRank(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("associationRank", String.class);
    }

    public String createAccessToken(String phoneNumber, String institutionRank, String associationRank){
        return Jwts.builder()
                .subject(phoneNumber)
                .claim("institutionRank", "ROLE_" + institutionRank)
                .claim("associationRank", "ROLE_" + associationRank)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiry()*1000L))
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String phoneNumber, String institutionRank, String associationRank) {
        return Jwts.builder()
                .subject(phoneNumber)
                .claim("institutionRank", "ROLE_" + institutionRank)
                .claim("associationRank", "ROLE_" + associationRank)
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiry()*1000L))
                .signWith(secretKey)
                .compact();
    }


    // 토큰 유효성 검사
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
