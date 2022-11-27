package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "DOG_WEIGHT")
public class DogWeight extends DogWeightHeight {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "weight_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weight_id")
    @JsonIgnore
    private Dog dog;

    public DogWeight(String imperial, String metric, Long id, Dog dog) {
        super(imperial, metric);
        this.id = id;
        this.dog = dog;
    }

    public DogWeight() { }

    public DogWeight(Long id, Dog dog) {
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
