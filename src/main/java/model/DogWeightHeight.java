package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Data
@AllArgsConstructor
@MappedSuperclass
public abstract class DogWeightHeight {

    @Column(length = 50)
    private String imperial;

    @Column(length = 50)
    private String metric;
    public DogWeightHeight() { }
}
