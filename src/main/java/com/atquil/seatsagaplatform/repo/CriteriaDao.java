package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author atquil
 */
@Repository
@RequiredArgsConstructor
public class CriteriaDao {
    private final EntityManager entityManager;

    public List<Movie> findMoviesByCriteria(String city, String genre) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = builder.createQuery(Movie.class);


        // Select * from Employee

        Root<Movie> movieRoot = criteriaQuery.from(Movie.class);
        // Predicate
        Predicate firstNamePredicate = builder.like(movieRoot.get("city"), "%" + city + "%");
        Predicate lastNamePredicate = builder.like(movieRoot.get("genre"), "%" + genre + "%");

        criteriaQuery.where(firstNamePredicate, lastNamePredicate);

        TypedQuery<Movie> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();

    }
}
