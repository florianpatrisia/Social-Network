package com.example.laborator.service;

import com.example.laborator.domain.Utilizator;
import com.example.laborator.repository.PrietenieDBRepository;
import com.example.laborator.repository.UserDBPagingRepository;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.utils.events.ChangeEventType;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observable;
import com.example.laborator.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorService extends Service<Long, Utilizator> implements Observable<UtilizatorChangeEventType> {
    protected UserDBPagingRepository utilizatorRepo;
    protected PrietenieDBRepository prietenieRepo;
    protected List<Observer<UtilizatorChangeEventType>> observers=new ArrayList<>();


    public UtilizatorService(UserDBPagingRepository utilizatorRepo) {
        super(utilizatorRepo);
        this.utilizatorRepo = utilizatorRepo;
    }

    @Override
    public Optional<Utilizator> save(Utilizator user) {
        Optional<Utilizator> saved = super.save(user);
        if(saved.isEmpty())
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, user));
        return saved;
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        Optional<Utilizator> deleted = super.delete(id);
        deleted.ifPresent(user -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.DELETE, user)));
        return deleted;
    }

    @Override
    public Optional<Utilizator> update(Long id, Utilizator user) {
        Optional<Utilizator> updated = super.update(id, user);
        updated.ifPresent(utilizator -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.UPDATE, utilizator)));
        return updated;
    }
    public Utilizator findID(Long id) {

        var o= utilizatorRepo.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }
    @Override
    public void addObserver(Observer<UtilizatorChangeEventType> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UtilizatorChangeEventType> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorChangeEventType t) {
        observers.forEach(x -> x.update(t));
    }

    public Page<Utilizator> utilizatoriOnPage(Pageable pageable){
        return utilizatorRepo.findAll(pageable);
    }

}
