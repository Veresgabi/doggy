package repository;

import configuration.DbSessionConfig;
import dto.FilterDogDTO;
import model.Dog;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class DogRepositoryCustomImpl implements DogRepositoryCustom {

    private Session session = DbSessionConfig.dbSession;
    private EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

    @Override
    public List<Dog> findDogsByFilterRequest(FilterDogDTO request, Integer maxResults) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dog> query = criteriaBuilder.createQuery(Dog.class);
        Root<Dog> dog = query.from(Dog.class);

        Predicate filterPredicate = getFilterPredicate(request, dog, criteriaBuilder);

        query.select(dog)
                .where(filterPredicate);

        return entityManager.createQuery(query)
                .setFirstResult((request.getPage() - 1) * maxResults)
                .setMaxResults(maxResults)
                .getResultList();
    }

    @Override
    public Long countDogsByFilterRequest(FilterDogDTO request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Dog> dog = query.from(Dog.class);

        Predicate filterPredicate = getFilterPredicate(request, dog, criteriaBuilder);

        query.select(criteriaBuilder.count(dog))
                .where(filterPredicate);

        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate getFilterPredicate(FilterDogDTO request, Root<Dog> dog, CriteriaBuilder criteriaBuilder ) {
        Path<String> breedGroupPath = dog.get("breedGroup");

        String name = request.getDogName().toLowerCase();
        String lifeSpan = request.getDogLifeSpan().toLowerCase();

        Predicate predicateForName = criteriaBuilder.like(criteriaBuilder.lower(dog.get("name")), "%" + name + "%");
        Predicate predicateForLifeSpan = criteriaBuilder.like(dog.get("lifeSpan"), "%" + lifeSpan + "%");

        if (!request.getBreedGroups().isEmpty()) {
            List<Predicate> predicatesForBreedGroups = new ArrayList<>();
            for (Dog.BreedGroup breedGroup : request.getBreedGroups()) {
                predicatesForBreedGroups.add(criteriaBuilder.equal(breedGroupPath, breedGroup.ordinal()));
            }
            Predicate predicateForBreedGroups = criteriaBuilder.or(predicatesForBreedGroups.toArray(new Predicate[0]));

            return criteriaBuilder.and(predicateForName, predicateForLifeSpan, predicateForBreedGroups);
        }

        return criteriaBuilder.and(predicateForName, predicateForLifeSpan);
    }
}
