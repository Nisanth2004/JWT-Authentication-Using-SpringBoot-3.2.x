# JWT Authentication in Spring Boot

This project demonstrates how to implement JWT-based authentication and authorization in a Spring Boot application.

## Overall Flow of the Project

1. **User Registration**
   - First, register a user and obtain an `access_token` and `refresh_token`.

2. **User Login**
   - After registering, log in to obtain a new `access_token` and `refresh_token`.
   - Use the `access_token` to access the protected endpoints.
   - If the `access_token` expires, it will no longer grant access to the endpoints.

3. **Using the Refresh Token**
   - When the `access_token` expires, use the `refresh_token` obtained during login.
   - Pass the `refresh_token` in the header and call the `refresh_token` endpoint.
   - This will generate a new `access_token` and `refresh_token`.
   - Use the newly created `access_token` to access the endpoints.

4. **Potential Issue**
   - A potential issue in the current implementation is that an old `refresh_token` can be used to obtain a new `access_token` and `refresh_token`.
   - This is not ideal, and the project should be modified to prevent this from happening.

## Project Setup

1. **Create a New Spring Boot Project**
   - Include the following dependencies:
     - Spring Data JPA
     - Spring Security
     - Spring Web
     - MySQL Driver

2. **Database Configuration**
   - Set up a database connection in IntelliJ IDEA.
   - Update the `application.yml` file with the correct database URL schema.

## Entity and Repository

3. **Create User Entity**
   - Define the `User` model as an entity with necessary roles.
   - Implement the `UserDetails` interface:
     - Return `true` in all required methods.
     - Return roles in the `getAuthorities()` method.

4. **Create UserRepository**
   - Add a method `findByUsername` in `UserRepository`.

## Service Implementation

5. **Create UserServiceImpl**
   - Implement `UserDetailsService` in the `UserServiceImpl` class.
   - Fill in the `loadUserByUsername` method.

6. **Add JWT Dependencies**
   - Add the following dependencies for JWT:
     - `jjwt-api`
     - `jjwt-jackson`
     - `jjwt-impl`

## JWT Service

7. **Create JwtService**
   - Add a `JwtService` in the service package.
   - Generate an RSA key from [this website](https://www.csfieldguide.org.nz/en/interactives/rsa-key-generator/) and store it in a variable in `JwtService`.

8. **Generate Token**
   - Create a `generateToken` method to generate the JSON Web Token.
   - Implement a `getSigningKey()` method for signing the token.

9. **Extract Claims**
   - Create `extractAllClaims()` to extract all details from the token.
   - Implement `extractClaims()` to extract specific properties from the payload.
   - Create `extractUsername()` to extract the username from the token using `extractClaims()`.

10. **Validate Token**
    - Implement `isValid()` to validate the token:
      - Check the authenticated username against the username in the token.
      - Verify that the token is not expired.
    - Add `isTokenExpired()` to check token expiration.

## JWT Authentication Filter

11. **Create JwtAuthenticationFilter**
    - In the `filter` package, create `JwtAuthenticationFilter` extending `OncePerRequestFilter`. This ensures it runs for every incoming request.
    - Use the service class to validate the token. If valid, authenticate the user.

12. **Register JwtAuthenticationFilter**
    - Register `JwtAuthenticationFilter` in Spring Security to indicate token-based authentication.

## Security Configuration

13. **Create SecurityFilterChain**
    - In the `config` package, create a `SecurityFilterChain` to configure credentials.

## Authentication and Registration

14. **Create AuthenticationController**
    - Implement `AuthenticationController` and `AuthenticationService` to handle user registration and login.
    - Write methods `register()` and `authenticate()` in the service class.

15. **Test Endpoint**
    - Create a demo controller to test the endpoint.

## Logout Functionality

16. **Implement Logout Endpoint**
    - Create a logout endpoint in the application.

17. **Enhance Token Management**
    - Before saving the token, write logic in the `register()` method to find all tokens. When a user registers, they generate a token. This token is valid for one-time use to access the endpoint, after which `isLoggedOut` is set to true. Invalidate the token manually after use.

18. **Enhance isValid() Method**
    - In the `JwtService` class, update the `isValid()` method to check if the user is logged out before validating the token.

19. **Token Expiration Concept**
    - Once a user registers, they receive a token to access the endpoint. After some time, if the user logs in again, a new token is generated. The old token should no longer be valid, and only the newly created token should be used.

20. **Logout in SecurityConfig**
    - Add logout functionality in the `SecurityConfig` class.
    - Create a `CustomLogoutHandler` similar to `JwtAuthenticationFilter`.

## Token Management

21. **Test in Postman**
    - Register a user and obtain a token via Postman.
    - Access the logout URL with the token, which should return a 200 OK response.

22. **Create Refresh Token Functionality**
    - Implement a `refreshToken()` method in the `JwtService` to generate a new refresh token.
    - Update `AuthenticationResponse` to return the refresh token.
    - Create a controller method to access the refresh token.
    - Implement the `refreshToken()` method in `AuthenticationService`.

---

This README file provides a comprehensive overview of the JWT authentication implementation in the Spring Boot project, including detailed steps for user registration, login, token validation, logout, and refresh token management.
