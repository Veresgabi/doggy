package service;

import dto.DogResponse;
import dto.FilterDogDTO;
import model.Dog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface DogService {

    ResponseEntity<DogResponse> syncDogs() throws Exception;
    ResponseEntity<DogResponse> getDogPage(Integer page);
    ResponseEntity<DogResponse> filter(FilterDogDTO request);
    ResponseEntity<DogResponse> getDogById(Long id);
    ResponseEntity<DogResponse> saveDog(Dog dog);
    ResponseEntity<DogResponse> delete(Long id);

}
