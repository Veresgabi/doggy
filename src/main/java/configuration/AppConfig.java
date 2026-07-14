package configuration;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import repository.DogRepository;
import repository.DogRepositoryCustom;
import repository.DogRepositoryCustomImpl;
import util.DogUtil;

@Configuration
public class AppConfig {

    @Bean
    public DogUtil dogUtil(@Value("${pagination.limit}") int paginationLimit) {
        return new DogUtil(paginationLimit);
    }

    @Bean
    public DogRepositoryCustom dogRepositoryCustomImpl(
        EntityManager entityManager, DogUtil dogUtil, DogRepository dogRepository) {
        return new DogRepositoryCustomImpl(entityManager, dogUtil, dogRepository);
    }
}
