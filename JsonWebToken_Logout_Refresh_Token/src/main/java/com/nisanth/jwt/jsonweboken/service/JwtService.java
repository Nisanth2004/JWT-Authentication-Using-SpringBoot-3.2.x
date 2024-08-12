package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.model.User;
import com.nisanth.jwt.jsonweboken.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY = Base64.getUrlEncoder().encodeToString("MCgCIQCcu0xMHg6DDckcI2/RkGdF9Ba50jG/LlOKcJDhPZG/twIDAQAB".getBytes());
private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // generate the token
    public String generateAccessToken(User user)
    {
        return generateToken(user,2*60*1000);

    }

    public String generateRefreshToken(User user)
    {
        return generateToken(user,7*24*60*60*1000);

    }

    public boolean isValidRefreshToken(String token, User user) {
        String username = extractUsername(token);

        boolean validRefreshToken = tokenRepository
                .findByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validRefreshToken;
    }

    private String generateToken(User user,long expirationTime)
    {
        String  token= Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expirationTime)) // 1 day
                .signWith(getSigninKey())
                .compact();
        return token;
    }

    // for secret key to be loaded
    public SecretKey getSigninKey() {
        byte[] keyBytes= Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the details in token
    public Claims extractAllclaims(String token)
    {
        return Jwts.parser()
                // validate the token with secret key
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();   // subject,issuedat,expiration will be taken for validate the token
    }


    // extract the specific property from Payload Eg: subject only
    // This method is genric type
    public <T> T extractClaims(String token, Function<Claims,T> resolver)
    {
        Claims claims=extractAllclaims(token);
        return resolver.apply(claims);

    }

    // extract the username
    public String extractUsername(String token)
    {
        return extractClaims(token,Claims::getSubject);
    }

    // Validate the token
    public boolean isValid(String token, UserDetails user) // authenticated user
    {
        String username=extractUsername(token);

        // Additionally added If the user is loggedout
        boolean isValidToken=tokenRepository.findByAccessToken(token)
                // using map check if token is not logged out
                .map(t->!t.isLoggedOut()).orElse(false);

        return (username.equals(user.getUsername()) && !isTokenExpired(token)&&isValidToken);
    }

    // check the token is expired or not
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token)
    {
        return extractClaims(token,Claims::getExpiration);
    }

}
