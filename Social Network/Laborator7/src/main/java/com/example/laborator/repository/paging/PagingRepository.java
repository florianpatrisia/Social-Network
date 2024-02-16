package com.example.laborator.repository.paging;
import com.example.laborator.domain.Entity;
import com.example.laborator.repository.Repository;



public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
