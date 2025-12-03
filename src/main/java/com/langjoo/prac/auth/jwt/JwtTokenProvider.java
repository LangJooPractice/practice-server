package com.langjoo.prac.auth.jwt;

import com.langjoo.prac.common.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    // 📌 1. 설정값 주입
    @Value("${jwt.secret}")
    private String secretKey;

    // Access Token 만료 시간 (예: 30분)
    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    // Refresh Token 만료 시간 (예: 7일)
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private Key key;

    // 📌 2. Secret Key 초기화
    // 빈이 생성된 후, 주입받은 문자열 Secret Key를 암호화에 사용할 Key 객체로 변환합니다.
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // -------------------------------------------------------------
    // 3. 토큰 생성 메서드 (Access Token)
    // -------------------------------------------------------------
    public String createAccessToken(Long userId, String loginId) {
        return createToken(userId, loginId, accessTokenExpiration);
    }

    // 4. 토큰 생성 메서드 (Refresh Token)
    public String createRefreshToken(Long userId, String loginId) {
        return createToken(userId, loginId, refreshTokenExpiration);
    }

    // 5. 실제 토큰 생성 로직
    private String createToken(Long userId, String loginId, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(loginId) // 토큰 주체 (Subject): 여기서는 loginId 사용
                .claim("userId", userId) // Custom Claim: DB 기본키 포함
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명에 사용할 Key와 알고리즘
                .compact(); // 토큰 생성
    }

    // -------------------------------------------------------------
    // 6. 토큰 유효성 검증
    // -------------------------------------------------------------
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new AuthException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // -------------------------------------------------------------
    // 7. 토큰에서 사용자 정보(Claims) 추출
    // -------------------------------------------------------------
    public Claims getClaimsFromToken(String authToken) {
        try {
            return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(authToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 클레임은 필요할 때 (예: 재발급 시)
            return e.getClaims();
        } catch (Exception e) {
            log.error("JWT 클레임 추출 실패", e);
            throw new AuthException("유효하지 않은 토큰입니다.");
        }
    }
}