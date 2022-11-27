package service;

import model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import util.CookieUtil;
import dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import util.SecurityCipher;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest authRequest, String accessToken, String refreshToken) {

        AuthResponse authResponse = new AuthResponse();
        try {
            String username = authRequest.getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() ->
                    new IllegalArgumentException("User not found with username " + username));

            /* if (!new BCryptPasswordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid password ");
            } */

            Boolean accessTokenValid = tokenProvider.validateToken(accessToken);
            Boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);

            HttpHeaders responseHeaders = new HttpHeaders();
            Token newAccessToken;
            Token newRefreshToken;
            if (!accessTokenValid && !refreshTokenValid) {
                newAccessToken = tokenProvider.generateAccessToken(user.getUsername());
                newRefreshToken = tokenProvider.generateRefreshToken(user.getUsername());
                addAccessTokenCookie(responseHeaders, newAccessToken);
                addRefreshTokenCookie(responseHeaders, newRefreshToken);
            }

            if (!accessTokenValid && refreshTokenValid) {
                newAccessToken = tokenProvider.generateAccessToken(user.getUsername());
                addAccessTokenCookie(responseHeaders, newAccessToken);
            }

            if (accessTokenValid && refreshTokenValid) {
                newAccessToken = tokenProvider.generateAccessToken(user.getUsername());
                newRefreshToken = tokenProvider.generateRefreshToken(user.getUsername());
                addAccessTokenCookie(responseHeaders, newAccessToken);
                addRefreshTokenCookie(responseHeaders, newRefreshToken);
            }
            authResponse.setMessage("Auth was successful.");
            authResponse.setUser(user);
            return ResponseEntity.ok().headers(responseHeaders).body(authResponse);
        }
        catch (Exception exception) {
            authResponse.setMessage("Unauthorized. See details: " +
                    (exception.getMessage() != null ? exception.getMessage() : exception.toString()));
            return ResponseEntity.status(401).body(authResponse);
        }
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(String accessToken, String refreshToken) {

        AuthResponse authResponse = new AuthResponse();

        try {
            boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);

            if (!refreshTokenValid) {
                throw new IllegalArgumentException("Refresh Token is invalid!");
            }

            String username = tokenProvider.getUsernameFromToken(accessToken);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username " + username));

            Token newAccessToken = tokenProvider.generateAccessToken(username);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

            authResponse.setMessage("Auth successful.");
            authResponse.setUser(user);
            return ResponseEntity.ok().headers(responseHeaders).body(authResponse);
        }
        catch (Exception exception) {
            authResponse.setMessage("Unauthorized. See details: " +
                    (exception.getMessage() != null ? exception.getMessage() : exception.toString()));
            return ResponseEntity.status(401).body(authResponse);
        }

    }

    @Override
    public ResponseEntity<AuthResponse> logout() {

        AuthResponse authResponse = new AuthResponse();
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
            responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString());
            authResponse.setMessage("Logout was successful.");
            return ResponseEntity.ok().headers(responseHeaders).body(authResponse);
        }
        catch (Exception exception) {
            authResponse.setMessage("Logout was failed. See details: " +
                    (exception.getMessage() != null ? exception.getMessage() : exception.toString()));
            return ResponseEntity.internalServerError().body(authResponse);
        }
    }

    @Override
    public ResponseEntity<?> getUserProfile() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(() ->
                    new IllegalArgumentException("User not found with username " + customUserDetails.getUsername()));

            return ResponseEntity.ok().body(user.toUserSummary());
        }
        catch (Exception exception) {
            return ResponseEntity.internalServerError().body(
                    exception.getMessage() != null ? exception.getMessage() : exception.toString());
        }
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }
}
