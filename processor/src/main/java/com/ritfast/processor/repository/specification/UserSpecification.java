package com.ritfast.processor.repository.specification;

import com.ritfast.processor.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(Long clientId, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by Client ID (Security)
            predicates.add(criteriaBuilder.equal(root.get("client").get("id"), clientId));

            // Dynamic search (Search in email, firstName, lastName)
            if (search != null && !search.isEmpty()) {
                String likeSearch = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("emailId")), likeSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likeSearch));
                predicates.add(searchPredicate);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}