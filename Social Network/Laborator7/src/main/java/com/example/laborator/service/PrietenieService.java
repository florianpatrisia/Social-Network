package com.example.laborator.service;

import com.example.laborator.domain.Prietenie;
import com.example.laborator.domain.Tuple;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.repository.PrietenieDBPagingRepository;
import com.example.laborator.repository.UserDBRepository;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.utils.events.ChangeEventType;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observable;
import com.example.laborator.utils.observer.Observer;

import java.util.*;
import java.util.stream.StreamSupport;

public class PrietenieService extends Service<Tuple<Long, Long>, Prietenie> implements Observable<UtilizatorChangeEventType>
{
    protected PrietenieDBPagingRepository prietenieRepo;
    protected UserDBRepository utilizatorRepo;
    protected UtilizatorService utilizatorService;
    protected List<Observer<UtilizatorChangeEventType>> observers=new ArrayList<>();

    public PrietenieService(PrietenieDBPagingRepository prietenieRepo, UserDBRepository repo2, UtilizatorService serv) {
        super(prietenieRepo);
        this.prietenieRepo=prietenieRepo;
        this.utilizatorRepo=repo2;
        this.utilizatorService=serv;
    }
    @Override
    public Optional<Prietenie> save(Prietenie prietenie) {
        Optional<Prietenie> saved = super.save(prietenie);
        if(saved.isEmpty())
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, prietenie));
        return saved;
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> id) {
        Optional<Prietenie> deleted = super.delete(id);
        deleted.ifPresent(prietenie -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.DELETE, prietenie)));
        return deleted;
    }

    @Override
    public Optional<Prietenie> update(Tuple<Long, Long> id, Prietenie prietenie) {
        Optional<Prietenie> updated = super.update(id, prietenie);
        updated.ifPresent(prietenie1 -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.UPDATE, prietenie1)));
        return updated;
    }
    public List<Long> getFriends(Long id_utilizator)
    {
        // returneaza o lista cu id-urile utilizatorilor cu care Utilizatoul dat este prieten
        List<Long> prieteni=new ArrayList<>();
        var toate_prieteniile=prietenieRepo.findAll();
        for(var p:toate_prieteniile)
        {
            if(p.getId().getLeft().equals(id_utilizator))
                prieteni.add(p.getId().getRight());
            if(p.getId().getRight().equals(id_utilizator))
                prieteni.add(p.getId().getLeft());
        }
        return prieteni;
    }

    public Page<Utilizator> getPrieteniPage(Long userId, Pageable pageable) {
        List<Long> prieteni = getFriends(userId);

        // Pagina actuală de prieteni
        List<Utilizator> prieteniPage = new ArrayList<>();

        // Construiți pagina de prieteni din listă, bazată pe offset și limit
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        for (int i = offset; i < Math.min(offset + limit, prieteni.size()); i++) {
            Long friendId = prieteni.get(i);
            Utilizator friend = utilizatorService.findID(friendId);
            friend.setId(friendId);
            prieteniPage.add(friend);
        }

        // Totalul de prieteni
        int totalElements = prieteni.size();

        return new Page<>(prieteniPage, totalElements);
    }

    public Utilizator findID(Long id) {

        var o= utilizatorRepo.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }

    public ArrayList<Utilizator> getFriends2(long id)
    {
        ArrayList<Utilizator> prietenii_lui_x = new ArrayList<>();
        Iterable<Prietenie> iterable = prietenieRepo.findAll();
        StreamSupport.stream(iterable.spliterator(), false)
                .filter(x -> (x.getId().getLeft().equals(id) || x.getId().getRight().equals(id)))
                .forEach( friendship -> {
                            Utilizator friend;
                            if(friendship.getId().getLeft().equals(id))
                                friend = findID(friendship.getId().getRight());
                            else
                                friend = findID(friendship.getId().getLeft());
                            prietenii_lui_x.add(friend);
                        }
                );

        return prietenii_lui_x;
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
    public Page<Prietenie> prieteniiOnPage(Pageable pageable){
        return prietenieRepo.findAll(pageable);
    }
//    public Page<Prietenie> prieteniiOnPage2(Pageable pageable, Long id_utilizator){
////        return prietenieRepo.findAll(pageable);
//        List<Long> prieteni=new ArrayList<>();
//        var toate_prieteniile=prietenieRepo.findAll(pageable);
//            for(var p:toate_prieteniile)
//            {
//                if(p.getId().getLeft().equals(id_utilizator))
//                    prieteni.add(p.getId().getRight());
//                if(p.getId().getRight().equals(id_utilizator))
//                    prieteni.add(p.getId().getLeft());
//            }
//            return prieteni;
//
//    }

}
/*
public class PrietenieService extends Service<Tuple<Long, Long>, Prietenie> implements Observable<PrietenieChangeEventType> {
    private PrietenieDBRepository repoPrietenie;
    private List<Observer<PrietenieChangeEventType>> observers=new ArrayList<>();
    //private ArrayList<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    public PrietenieService(PrietenieDBRepository repoPrietenie) {
        super(repoPrietenie);
        //this.repoPrietenie=repoPrietenie;
    }

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        return super.findOne(longLongTuple);
    }

    @Override
    public Iterable<Prietenie> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        Optional<Prietenie> saved = super.save(entity);
        if(saved.isEmpty())
            notifyObservers(new PrietenieChangeEventType(ChangeEventType.ADD, entity));
        return saved;
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        Optional<Prietenie> deleted = super.delete(longLongTuple);
        deleted.ifPresent(prietenie -> notifyObservers(new PrietenieChangeEventType(ChangeEventType.DELETE, prietenie)));
        return deleted;
    }

    @Override
    public Optional<Prietenie> update(Tuple<Long, Long> longLongTuple, Prietenie entity) {
        Optional<Prietenie> updated = super.update(longLongTuple, entity);
        if(updated.isPresent())
            notifyObservers(new PrietenieChangeEventType(ChangeEventType.UPDATE, updated.get()));
        return updated;
    }

    @Override
    public void addObserver(Observer<PrietenieChangeEventType> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<PrietenieChangeEventType> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(PrietenieChangeEventType t) {
        observers.forEach(x -> x.update(t));

    }



    /*
    PrietenieDBRepository repoPrietenie;
    UserDBRepository repoUtilizator;

    private List<Observer<UtilizatorChangeEventType>> observers=new ArrayList<>();

    public PrietenieService(PrietenieDBRepository repoPrietenie, UserDBRepository repoUtilizator) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtilizator = repoUtilizator;
        //loadFriends();
    }


    NU STIU CE AM FACUT AICIII
    @Override
    public Optional<Prietenie> add(Prietenie entity) {
        Long id1=entity.getId().getLeft();
        Long id2=entity.getId().getRight();

        Optional<Utilizator> utilizator1_Optional=repoUtilizator.findOne(id1);
        Optional<Utilizator> utilizator2_Optional=repoUtilizator.findOne(id2);
        if(utilizator1_Optional.isPresent()&&utilizator2_Optional.isPresent())
        {
            Utilizator utilizator1=utilizator1_Optional.get();
            Utilizator utilizator2=utilizator2_Optional.get();

            repoUtilizator.update(utilizator1);
            repoUtilizator.update(utilizator2);
            return repoPrietenie.save(entity);
        }
        else {
            throw new IllegalArgumentException("Prietenie Service-Add!");
        }
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        Long id1=longLongTuple.getLeft();
        Long id2=longLongTuple.getRight();
        Optional<Utilizator> utilizatorOptional1=repoUtilizator.findOne(id1);
        Optional<Utilizator> utilizatorOptional2=repoUtilizator.findOne(id2);

        if(utilizatorOptional1.isEmpty()||utilizatorOptional2.isEmpty())
        {
            throw new IllegalArgumentException("Utilizator inexistent!");
        }

        Optional<Prietenie> deleted=repoPrietenie.delete(longLongTuple);
        return  deleted;
    }

}
*/