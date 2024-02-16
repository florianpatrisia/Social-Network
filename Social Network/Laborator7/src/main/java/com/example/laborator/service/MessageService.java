package com.example.laborator.service;

import com.example.laborator.domain.Message;
import com.example.laborator.repository.MessageDBPagingRepository;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.utils.events.ChangeEventType;
import com.example.laborator.utils.events.UtilizatorChangeEventType;
import com.example.laborator.utils.observer.Observable;
import com.example.laborator.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService extends Service<Long, Message> implements Observable<UtilizatorChangeEventType> {
    protected MessageDBPagingRepository messageRepo;
    protected List<Observer<UtilizatorChangeEventType>> observers=new ArrayList<>();


    public MessageService(MessageDBPagingRepository messageDBRepository) {
        super(messageDBRepository);
        this.messageRepo=messageDBRepository;
    }

    @Override
    public Optional<Message> save(Message message) {
        Optional<Message> saved = super.save(message);
        if(saved.isEmpty())
            notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, message));
        return saved;
    }

    public ArrayList<Message> findAllForTwoUsers(Long id1, Long id2) {
        return messageRepo.findAllForTwoUsers(id1, id2);
    }
    @Override
    public Optional<Message> delete(Long id) {
        Optional<Message> deleted = super.delete(id);
        deleted.ifPresent(message -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.DELETE, message)));
        return deleted;
    }

    @Override
    public Optional<Message> update(Long id, Message message) {
        Optional<Message> updated = super.update(id, message);
        updated.ifPresent(message1 -> notifyObservers(new UtilizatorChangeEventType(ChangeEventType.UPDATE, message1)));
        return updated;
    }
    public Message replyToOne(Message message, Long recipient) {
        try {
            ArrayList<Long> recipients = new ArrayList<>();
            recipients.add(recipient);
            message.setTo(recipients);
            var o = messageRepo.save(message);
            this.notifyObservers(new UtilizatorChangeEventType(ChangeEventType.ADD, o.get()));
            if(o.isPresent())
                return o.get();
            else
                return null;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public List<Message> getConversation(Long id1, Long id2)
    {
        Iterable<Message> all = messageRepo.findAll();
        List<Message> messages = new ArrayList<>();
        for(Message message : all)
            for(Long id : message.getTo())
                if(id.equals(id2) && message.getFrom().equals(id1) || id.equals(id1) && message.getFrom().equals(id2))
                {
                    messages.add(message);
                }
        return messages;
    }
    public Message findMessageById(Long id) {

        var o= messageRepo.findOne(id);
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
    public Page<Message> mesajeOnPage(Pageable pageable){
        return messageRepo.findAll(pageable);
    }

}
