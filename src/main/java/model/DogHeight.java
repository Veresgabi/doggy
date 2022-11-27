package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "DOG_HEIGHT")
public class DogHeight extends DogWeightHeight {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "height_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "height_id")
    @JsonIgnore
    private Dog dog;

    public DogHeight(String imperial, String metric, Long id, Dog dog) {
        super(imperial, metric);
        this.id = id;
        this.dog = dog;
    }

    public DogHeight() { }

    public DogHeight(Long id, Dog dog) {
        this.id = id;
        this.dog = dog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
