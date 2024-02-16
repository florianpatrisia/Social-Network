package com.example.laborator.domain.validators;

import com.example.laborator.domain.Message;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        String erori="";
        if(entity.getId()<0)
            erori+="Id invalid! Id-ul trebuie sa fie numar intreg pozitiv!";
        if(entity.getId()== null)
            erori+="Id invalid! Id-ul nu trebuie sa fie null!";
        if(entity.getFrom()==null)
            erori+="Nu exista expeditorul cu id-ul dat!";
        if(entity.getTo().contains(null))
            erori+="Nu exista destinatari cu id-urile date!";
        if(entity.getMesaage()== null)
            erori+="Mesaj invalid! Mesajul nu trebuie sa fie gol!";
//        if(entity.getReplyMessage()== null)
//            erori+="Mesaj invalid! Reply nu trebuie sa fie gol!";
        if(!erori.isEmpty())
            throw new ValidationException(erori);

    }
}
