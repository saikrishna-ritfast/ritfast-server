package com.ritfast.processor.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BaseSpecification {

    public static <T> Specification<T> filterByClientAndSearch(Long clientId, String search,
            List<String> searchFields) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by Client ID (Security)
            predicates.add(criteriaBuilder.equal(root.get("client").get("id"), clientId));

            // Dynamic search across the specified fields
            if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
                String likeSearch = "%" + search.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();
                for (String field : searchFields) {
                    searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), likeSearch));
                }

                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
