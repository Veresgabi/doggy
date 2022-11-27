package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "DOG_IMAGE")
@AllArgsConstructor
public class DogImage {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long imageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonIgnore
    private Dog dog;

    @JsonProperty("id")
    @Column(name = "scraped_id")
    private String scrapedId;

    private Integer width;

    private Integer height;

    @NotNull
    private String url;

    public DogImage() { }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long id) {
        this.imageId = id;
    }

    public String getScrapedId() {
        return scrapedId;
    }

    public void setScrapedId(String scrapedId) {
        this.scrapedId = scrapedId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
