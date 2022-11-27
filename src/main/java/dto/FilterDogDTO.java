package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Dog;

import java.util.HashSet;
import java.util.List;

@Data
@AllArgsConstructor
public class FilterDogDTO {

    private String dogName;
    private String dogLifeSpan;
    private List<Dog.BreedGroup> breedGroups;
    private Integer page;
}
