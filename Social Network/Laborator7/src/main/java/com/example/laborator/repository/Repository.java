package com.example.laborator.repository;

import com.example.laborator.domain.Entity;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.ValidationException;

import java.util.Optional;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    Optional<E> findOne(ID id);

    Iterable<E> findAll();


    Optional<E> save(E entity);


    Optional<E> delete(ID id);

    Optional<E> update(E entity);
    Optional<E> update(ID id, E entity);
}


