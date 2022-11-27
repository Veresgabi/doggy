package repository;

import dto.FilterDogDTO;
import model.Dog;

import java.util.List;

public interface DogRepositoryCustom {

    List<Dog> findDogsByFilterRequest(FilterDogDTO request, Integer maxResults);
    Long countDogsByFilterRequest(FilterDogDTO request);
}
