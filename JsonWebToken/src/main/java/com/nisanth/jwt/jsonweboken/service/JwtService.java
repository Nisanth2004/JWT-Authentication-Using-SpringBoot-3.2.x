package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY = Base64.getUrlEncoder().encodeToString("MCgCIQCcu0xMHg6DDckcI2/RkGdF9Ba50jG/LlOKcJDhPZG/twIDAQAB".getBytes());

    // generate the token
    public String generateToken(User user)
    {
        String  token= Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+24*60*60*1000))
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
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
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
