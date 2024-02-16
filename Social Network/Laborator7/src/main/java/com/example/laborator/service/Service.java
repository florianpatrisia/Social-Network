package com.example.laborator.service;



import com.example.laborator.domain.Entity;
import com.example.laborator.repository.RepoException;
import com.example.laborator.repository.Repository;
import com.example.laborator.utils.events.UtilizatorChangeEventType;

import java.util.Optional;

public abstract class Service<ID, E extends Entity<ID>> {
    private Repository<ID, E> repo;

    public Service(Repository<ID, E> repo) {
        this.repo = repo;
    }

    public Optional<E> findOne(ID id) {
        return repo.findOne(id);
    }

    public Iterable<E> findAll() {
        return repo.findAll();
    }


    public Optional<E> save(E entity) {
        Optional<E> saved = repo.save(entity);
        if (saved.isPresent())
            throw new RepoException("This entity already exists!\n");
        return saved;
    }

    public Optional<E> delete(ID id) {
        Optional<E> deleted = repo.delete(id);
        if (deleted.isEmpty())
            throw new RepoException("This entity doesn't exist!\n");
        return deleted;
    }

    public Optional<E> update(ID id, E entity) {
        return repo.update(id, entity);
    }

    public abstract void notifyObservers(UtilizatorChangeEventType t);
}
