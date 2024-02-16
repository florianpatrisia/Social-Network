package com.example.laborator.service;

import com.example.laborator.domain.*;
import com.example.laborator.repository.FriendRequestDBRepository;
import com.example.laborator.repository.PrietenieDBRepository;
import com.example.laborator.repository.UserDBRepository;
import com.example.laborator.utils.events.ChangeEventType;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observable;
import com.example.laborator.utils.observer.Observer;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService extends Service<Tuple<Long, Long>, FriendRequest> implements Observable<UtilizatorChangeEventType> {
    protected FriendRequestDBRepository requestRepo;
    protected PrietenieDBRepository prietenieRepo;
    protected UserDBRepository utilizatorRepo;
    protected List<Observer<UtilizatorChangeEventType>> observers=new ArrayList<>();
    public FriendRequestService(FriendRequestDBRepository requestRepo, UserDBRepository repo1, PrietenieDBRepository repo2) {
        super(requestRepo);
        this.requestRepo = requestRepo;
        this.utilizatorRepo=repo1;
        this.prietenieRepo=repo2;
    }
    public void setUtilizatorRepo(UserDBRepository utilizatorRepo) {
        this.utilizatorRepo = utilizatorRepo;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest request) {
        Optional<FriendRequest> saved = super.save(request);
        if(saved.isEmpty())
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, request));
        return saved;
    }

    public Prietenie findFriendshipByUserPair(Utilizator e1, Utilizator e2) {
        for (var it : prietenieRepo.findAll()) {
            var pair = it.getId();
            if (pair.getLeft().equals(e1.getId()) && pair.getRight().equals(e2.getId()))
                return it;
        }
        return null;
    }
    public FriendRequest saveFriendRequest(FriendRequest friendRequest) {
        try{

            Optional<Utilizator> from = utilizatorRepo.findOne(friendRequest.getId().getLeft());
            Optional<Utilizator> to = utilizatorRepo.findOne(friendRequest.getId().getRight());

            if(from.isEmpty() || to.isEmpty())
                throw new Exception("One or both users do not exist!");

            Prietenie friendship1=findFriendshipByUserPair(from.get(),to.get());
            Prietenie friendship2=findFriendshipByUserPair(to.get(),from.get());

            if(friendship2!=null || friendship1!=null){
                throw new Exception("Already friends!");
            }

            Tuple<Long,Long> pair=new Tuple<>(to.get().getId(), from.get().getId());
            Optional<FriendRequest> f = findOne(pair);

            if(f.isPresent() && f.get().getStatus() == FriendRequestStatus.PENDING)
                throw  new Exception("You have already received a friend request from this user with Pending status");


            Tuple<Long,Long> pair2=new Tuple<>(from.get().getId(), to.get().getId());
            Optional<FriendRequest> f2 = findOne(pair2);

            if(f2.isPresent() && f2.get().getStatus() == FriendRequestStatus.PENDING)
                throw  new Exception("You have already sent a friend request to this user with Pending status");

            var o=requestRepo.save(friendRequest);
            if(o.isPresent()) {
                FriendRequest a=o.get();
                notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD,a));
                return a;
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return null;

    }

    /*
    private void notifyObservers(UtilizatorChangeEventType t) {
        observers.forEach(x -> x.update(t));
    }*/

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        return requestRepo.findOne(id);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        Optional<FriendRequest> deleted = super.delete(id);
        deleted.ifPresent(request -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.DELETE, request)));
        return deleted;
    }

    @Override
    public Optional<FriendRequest> update(Tuple<Long, Long> id, FriendRequest request) {
        Optional<FriendRequest> updated = super.update(id, request);
        if(updated.isPresent())
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.UPDATE, updated.get()));
        return updated;
    }

    public void accept(long from, long to){

        Optional<FriendRequest> optionalFriendRequest = requestRepo.findOne(new Tuple<>(from, to));
        try {
            if (optionalFriendRequest.isEmpty())
                throw new Exception("FRIEND REQUEST SERVICE: Nu exista nici-o cerere de prietenie de la utilizator cu acest id " + from + "!");

            var friendRequest=optionalFriendRequest.get();
            if(friendRequest.getStatus() != FriendRequestStatus.PENDING)
                throw new Exception("Nu se poate accepta aceasta cerere de prietenie");

            friendRequest.setStatus(FriendRequestStatus.APPROVED);
            friendRequest.setDate(Timestamp.valueOf(LocalDate.now().atStartOfDay()).toString());

            requestRepo.update(friendRequest);
            this.notifyObservers(new UtilizatorChangeEventType(ChangeEventType.UPDATE, friendRequest));

            Tuple<Long,Long> id_request=new Tuple<>(from, to);
            Prietenie friendship = new Prietenie(id_request);
            friendship.setId(id_request);
            //System.out.println("friendship id "+friendship.getId());

            Optional<Prietenie> friendship1=prietenieRepo.save(friendship);
            //System.out.println("friendship id "+friendship1.get().getId());
            friendship.setId(friendship1.get().getId());
            //System.out.println("friendship id "+friendship1.get().getId());
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, friendship1.get()));
        }
        catch(Exception e){
            System.out.println(e);
        }

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

    /*
    @Override
    public void notifyObservers(FriendRequestChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }*/

}
