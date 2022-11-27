package service;

import dto.AuthRequest;
import dto.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<AuthResponse> login(AuthRequest authRequest, String accessToken, String refreshToken);

    ResponseEntity<AuthResponse> refresh(String accessToken, String refreshToken);
    ResponseEntity<AuthResponse> logout();

    ResponseEntity<?> getUserProfile();
}
