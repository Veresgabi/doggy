package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
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
import repository.DogRepositoryCustom;
import repository.DogRepositoryCustomImpl;
import util.DogUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DogServiceImpl implements DogService {

    @Value("${pagination.limit}")
    private int paginationLimit;

    @Autowired
    private DogUtil dogUtil;

    @Autowired
    private DogRepositoryCustom dogRepositoryCustomImpl;

    private final int minimumTextLengthToFilterName = 3;
    private final int minimumTextLengthToFilterLifeSpan = 2;

    @Autowired
    DogRepository dogRepository;

    @Override
    @Transactional
    public ResponseEntity<DogResponse> syncDogs() throws Exception {

        try {
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
                dogs = mapper.readValue(apiResponse, new TypeReference<List<Dog>>(){ });
            }

            if (!dogs.isEmpty()) {
                dogRepository.deleteAll();

                int index = 0;
                List<Dog> dogsToSave = new ArrayList<>();
                for (Dog dog : dogs) {
                    dogsToSave.add(dog);
                    if ((index + 1) % paginationLimit == 0 || index == dogs.size() - 1) {
                        dogUtil.getImageForDogs(dogsToSave);
                        dogRepository.saveAll(dogsToSave);
                        dogsToSave = new ArrayList<>();
                    }
                    index++;
                }
                float numberOfDogs = dogs.size();
                float paginationLimitFloat = paginationLimit;
                numberOfPages = (int) Math.ceil(numberOfDogs / paginationLimitFloat);

                List<Dog> paginedDogs = dogs.stream().limit(paginationLimit).collect(Collectors.toList());

                return ResponseEntity.ok().body(
                    new DogResponse("Syncing dogs was successful!",
                        paginedDogs,
                        null,
                        numberOfPages, 1)
                );
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    @Override
    public ResponseEntity<DogResponse> getDogPage(Integer page) {
        DogResponse response = new DogResponse();

        try {
            float numberOfDogs = dogRepository.getCountOfAll();
            float paginationLimitFloat = paginationLimit;
            Integer numberOfPages = (int) Math.ceil(numberOfDogs / paginationLimitFloat);
            List<Dog> dogs = dogRepository.findAll(PageRequest.of(page - 1, paginationLimit));

            ObjectMapper mapper = new ObjectMapper();
            mapper.coercionConfigFor(LogicalType.Enum)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

            dogUtil.getImageForDogs(dogs);
            dogRepository.saveAll(dogs);

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
            dogs = dogRepositoryCustomImpl.findDogsByFilterRequest(request, paginationLimit);
            float numberOfDogs = dogRepositoryCustomImpl.countDogsByFilterRequest(request);
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
