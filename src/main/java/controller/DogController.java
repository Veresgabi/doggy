package controller;

import dto.*;
import model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import service.DogService;
import service.UserService;
import util.SecurityCipher;

import javax.validation.Valid;

@RestController
@RequestMapping("/dog")
public class DogController {

    @Autowired
    DogService dogService;
    @Autowired
    private UserService userService;

    @GetMapping("/sync-dogs")
    public ResponseEntity<DogResponse> syncDogs() {
        try {
            return dogService.syncDogs();
        }
        catch (Exception exception) {
            return ResponseEntity.internalServerError().body(
                    new DogResponse(exception.getMessage() != null ? exception.getMessage() : exception.toString())
            );
        }
    }

    @GetMapping("/get-dog-page")
    public ResponseEntity<DogResponse> getDogPage(@RequestParam Integer page) {
        return dogService.getDogPage(page);
    }

    @PostMapping(value = "/filter")
    public ResponseEntity<DogResponse> filter(@RequestBody FilterDogDTO filterObject) {
        return dogService.filter(filterObject);
    }

    @GetMapping("/get-dog")
    public ResponseEntity<DogResponse> getDogById(@RequestParam Long id) {
        return dogService.getDogById(id);
    }

    @PostMapping(value = "/save-dog")
    public ResponseEntity<DogResponse> saveDog(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @Valid @RequestBody Dog dog) {

        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);

        ResponseEntity<AuthResponse> authResponseEntity = userService.refresh(decryptedAccessToken, decryptedRefreshToken);


        if (authResponseEntity.getStatusCode().value() != 200) {
            return ResponseEntity.ok().body(
                    new DogResponse(
                            authResponseEntity.getBody() != null ? authResponseEntity.getBody().getMessage() : null,
                            null, null, null, null));
        }
        else {
            ResponseEntity<DogResponse> dogResponseEntity = dogService.saveDog(dog);

            if (dogResponseEntity.getStatusCode().value() != 200) {

                return ResponseEntity.internalServerError().body(
                        new DogResponse(
                                dogResponseEntity.getBody() != null ? dogResponseEntity.getBody().getMessage() : null,
                                null, null, null, null));
            }

            return ResponseEntity.ok().headers(authResponseEntity.getHeaders()).body(dogResponseEntity.getBody());
        }
    }

    @DeleteMapping(value = "/delete-dog")
    public ResponseEntity<DogResponse> deleteDog(@RequestParam  Long id) {
        return dogService.delete(id);
    }
}
