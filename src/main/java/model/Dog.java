package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "dog")
@AllArgsConstructor
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "dogid")
    Long dogId;

    @JsonProperty("id")
    @Column(name = "id")
    Long scrapedId;
    @OneToOne(cascade = CascadeType.ALL)
    private DogWeight weight;

    @OneToOne(cascade = CascadeType.ALL)
    private DogHeight height;

    @OneToOne(cascade = CascadeType.ALL)
    private DogImage image;

    private String name;

    @JsonProperty("country_code")
    @Column(name = "country_code")
    private String countryCode;

    @JsonProperty("life_span")
    @Column(name = "life_span")
    private String lifeSpan;

    @JsonProperty("bred_for")
    @Column(name = "bred_for")
    private String bredFor;

    @JsonProperty("breed_group")
    @Column(name = "breed_group")
    private BreedGroup breedGroup;

    private String temperament;

    @Column(length = 50)
    private String origin;

    @JsonProperty("reference_image_id")
    @Column(name = "reference_image_id")
    private String referenceImageId;

    @Column(length = 1000)
    private String description;

    private String history;

    public Dog() { }

    public Long getDogId() {
        return dogId;
    }

    public void setDogId(Long dogId) {
        this.dogId = dogId;
    }

    public DogWeight getWeight() {
        return weight;
    }

    public void setWeight(DogWeight weight) {
        this.weight = weight;
    }

    public DogHeight getHeight() {
        return height;
    }

    public void setHeight(DogHeight height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(String lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getBredFor() {
        return bredFor;
    }

    public void setBredFor(String bredFor) {
        this.bredFor = bredFor;
    }

    public BreedGroup getBreedGroup() {
        return breedGroup;
    }

    public void setBreedGroup(BreedGroup breedGroup) {
        this.breedGroup = breedGroup;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReferenceImageId() {
        return referenceImageId;
    }

    public void setReferenceImageId(String referenceImageId) {
        this.referenceImageId = referenceImageId;
    }

    public DogImage getImage() {
        return image;
    }

    public void setImage(DogImage image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public enum BreedGroup {
        @JsonProperty("Herding")
        HERDING,
        @JsonProperty("Hound")
        HOUND,
        @JsonProperty("Mixed")
        MIXED,
        @JsonProperty("Non-Sporting")
        NON_SPORTING,
        @JsonProperty("Sporting")
        SPORTING,
        @JsonProperty("Terrier")
        TERRIER,
        @JsonProperty("Toy")
        TOY,
        @JsonProperty("Working")
        WORKING
    }
}
