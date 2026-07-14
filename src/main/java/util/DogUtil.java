package util;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import lombok.AllArgsConstructor;
import model.Dog;
import model.DogImage;
import model.DogImageNew;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public class DogUtil {

    @Value("${pagination.limit}")
    private int paginationLimit;

    public void getImageForDogs(List<Dog> dogs) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.coercionConfigFor(LogicalType.Enum)
            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        WebClient client = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        int index = 0;
        for (Dog dog: dogs) {
            if (index < paginationLimit && dog.getImage() == null) {
                String imageApiResponse = client
                    .get()
                    .uri("https://api.thedogapi.com/v1/images/" + dog.getReferenceImageId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                if (imageApiResponse != null) {
                    DogImageNew image = mapper.readValue(imageApiResponse, new TypeReference<DogImageNew>(){ });

                    DogImage dogimage = new DogImage();
                    dogimage.setUrl(image.getUrl());
                    dog.setImage(dogimage);
                }
                Thread.sleep(200);
            }
            index++;
        }
    }

    private int getPaginationLimit() {
        return this.paginationLimit;
    }
}
