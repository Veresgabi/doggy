package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import dto.AbstractResponse;
import dto.DogResponse;
import dto.FilterDogDTO;
import model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import repository.DogRepository;
import repository.DogRepositoryCustomImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DogServiceImpl implements DogService {

    @Value("${pagination.limit}")
    private int paginationLimit;

    private final int minimumTextLengthToFilterName = 3;
    private final int minimumTextLengthToFilterLifeSpan = 2;

    @Autowired
    DogRepository dogRepository;

    @Override
    @Transactional
    public ResponseEntity<DogResponse> syncDogs() throws Exception {

        List<Dog> dogs = new ArrayList<>();

        WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String apiResponse = client
                .get()
                .uri("https://api.thedogapi.com/v1/breeds")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        mapper.coercionConfigFor(LogicalType.Enum)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        int numberOfPages = 0;
        if (apiResponse != null) {
            dogs = mapper.readValue(apiResponse, new TypeReference<>(){ });
        }

        if (!dogs.isEmpty()) {
            dogRepository.deleteAll();
            dogRepository.saveAll(dogs);
            float numberOfDogs = dogs.size();
            float paginationLimitFloat = paginationLimit;
            numberOfPages = (int) Math.ceil(numberOfDogs / paginationLimitFloat);
        }

        return ResponseEntity.ok().body(
                new DogResponse("Syncing dogs was successful!",
                        dogs.stream().limit(paginationLimit).collect(Collectors.toList()),
                        null,
                        numberOfPages, 1)
        );
    }

    @Override
    public ResponseEntity<DogResponse> getDogPage(Integer page) {
        DogResponse response = new DogResponse();

        try {
            float numberOfDogs = dogRepository.getCountOfAll();
            float paginationLimitFloat = paginationLimit;
            Integer numberOfPages = (int) Math.ceil(numberOfDogs / paginationLimitFloat);
            List<Dog> dogs = dogRepository.findAll(PageRequest.of(page - 1, paginationLimit));
            response.setDogs(dogs);
            response.setMessage("Finding dogs was successful!");
            response.setNumberOfPages(numberOfPages);
            response.setCurrentPage(page);
        }
        catch (Exception exception) {
            response.setMessage(exception.getMessage() != null ? exception.getMessage() : exception.toString());
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<DogResponse> filter(FilterDogDTO request) {

        DogRepositoryCustomImpl dogRepoCustom = new DogRepositoryCustomImpl();
        List<Dog> dogs;
        DogResponse response = new DogResponse();

        if (request.getDogName().length() < minimumTextLengthToFilterName) {
            request.setDogName("");
        }
        if (request.getDogLifeSpan().length() < minimumTextLengthToFilterLifeSpan) {
            request.setDogLifeSpan("");
        }

        int numberOfPages;
        try {
            dogs = dogRepoCustom.findDogsByFilterRequest(request, paginationLimit);
            float numberOfDogs = dogRepoCustom.countDogsByFilterRequest(request);
            float paginationLimitFloat = paginationLimit;
            numberOfPages = (int) Math.ceil(numberOfDogs / paginationLimitFloat);
        }
        catch (Exception exception) {
            response.setMessage(exception.getMessage() != null ? exception.getMessage() : exception.toString());
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok().body(
                new DogResponse("Finding dogs was successful!", dogs, null, numberOfPages, request.getPage()));
    }

    @Override
    public ResponseEntity<DogResponse> getDogById(Long id) {
        DogResponse response = new DogResponse();

        try {
            Dog dog = dogRepository.findById(id).orElse(null);
            response.setDog(dog);
            response.setMessage("Finding dog was successful!");
        }
        catch (Exception exception) {
            response.setMessage(exception.getMessage() != null ? exception.getMessage() : exception.toString());
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<DogResponse> saveDog(Dog dog) {
        DogResponse response = new DogResponse();

        try {
            dogRepository.save(dog);
            response.setDog(dog);
            response.setMessage("Saving dog was successful!");
        }
        catch (Exception exception) {
            response.setMessage(exception.getMessage() != null ? exception.getMessage() : exception.toString());
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<DogResponse> delete(Long id) {
        DogResponse response = new DogResponse();

        try {
            dogRepository.deleteById(id);
            response.setMessage("Deleting dog was successful!");
        }
        catch (Exception exception) {
            response.setMessage(exception.getMessage() != null ? exception.getMessage() : exception.toString());
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

}
