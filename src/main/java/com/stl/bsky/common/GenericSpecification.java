package com.stl.bsky.common;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {

    private PagingAndSearching search;

    public void searchWith(PagingAndSearching pagingAndSearching) {
        search = pagingAndSearching;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(search.getKey()), search.getValue()));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
