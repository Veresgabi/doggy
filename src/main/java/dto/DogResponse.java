package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Dog;

import java.util.List;

@Data
public class DogResponse extends AbstractResponse {

    Iterable<Dog> dogs;

    Dog dog;
    Integer numberOfPages;
    Integer currentPage;

    public DogResponse(String message, Iterable<Dog> dogs, Dog dog, Integer numberOfPages, Integer currentPage) {
        super(message);
        this.dogs = dogs;
        this.dog = dog;
        this.numberOfPages = numberOfPages;
        this.currentPage = currentPage;
    }

    public DogResponse(String message) {
        super(message);
    }

    public DogResponse() { }
}
