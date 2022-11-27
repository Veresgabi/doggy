package repository;

import model.Dog;
import model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DogRepository extends CrudRepository<Dog, Long> {
    List<Dog> findAll(Pageable pageable);

    @Query("SELECT d FROM Dog d WHERE d.name = ?1")
    List<Dog> findByName(String name, Pageable pageable);

    @Query("SELECT d FROM Dog d WHERE d.lifeSpan = ?1")
    List<Dog> findByLifeSpan(String lifeSpan, Pageable pageable);

    @Query("SELECT d FROM Dog d WHERE d.breedGroup in ?1")
    List<Dog> findByBreedGroup(List<Dog.BreedGroup> breedGroups, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Dog d")
    Integer getCountOfAll();

    Optional<Dog> findById(Long id);

    Dog save(Dog dog);

    void deleteAll();
}
