package repository;

import dto.FilterDogDTO;
import lombok.AllArgsConstructor;
import model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import util.DogUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DogRepositoryCustomImpl implements DogRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DogUtil dogUtil;

    @Autowired
    private DogRepository dogRepository;

    @Override
    public List<Dog> findDogsByFilterRequest(FilterDogDTO request, Integer maxResults) throws Exception {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dog> qry = cb.createQuery(Dog.class);
        Root<Dog> rootDog = qry.from(Dog.class);

        Predicate predFilter = getFilterPredicate(request, rootDog, cb);

        qry.select(rootDog)
                .where(predFilter);

        List<Dog> lstDogs = entityManager.createQuery(qry)
            .setFirstResult((request.getPage() - 1) * maxResults)
            .setMaxResults(maxResults)
            .getResultList();

        dogUtil.getImageForDogs(lstDogs);
        dogRepository.saveAll(lstDogs);

        return null;
    }

    @Override
    public Long countDogsByFilterRequest(FilterDogDTO request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> qry = cb.createQuery(Long.class);
        Root<Dog> rootDog = qry.from(Dog.class);

        Predicate predFilter = getFilterPredicate(request, rootDog, cb);

        qry.select(cb.count(rootDog))
                .where(predFilter);

        return entityManager.createQuery(qry).getSingleResult();
    }

    private Predicate getFilterPredicate(FilterDogDTO request, Root<Dog> rootDog, CriteriaBuilder cb ) {
        Path<String> pathBreedGroup = rootDog.get("breedGroup");

        String strName = request.getDogName().toLowerCase();
        String strLifeSpan = request.getDogLifeSpan().toLowerCase();

        Predicate predForName = cb.like(cb.lower(rootDog.get("name")), "%" + strName + "%");
        Predicate predForLifeSpan = cb.like(rootDog.get("lifeSpan"), "%" + strLifeSpan + "%");

        if (!request.getBreedGroups().isEmpty()) {
            List<Predicate> lstPredicatesForBreedGroups = new ArrayList<>();
            for (Dog.BreedGroup breedGroup : request.getBreedGroups()) {
                lstPredicatesForBreedGroups.add(cb.equal(pathBreedGroup, breedGroup.ordinal()));
            }
            Predicate predForBreedGroups = cb.or(lstPredicatesForBreedGroups.toArray(new Predicate[0]));

            return cb.and(predForName, predForLifeSpan, predForBreedGroups);
        }

        return cb.and(predForName, predForLifeSpan);
    }
}
