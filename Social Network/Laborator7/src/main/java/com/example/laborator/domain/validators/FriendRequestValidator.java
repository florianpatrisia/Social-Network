package com.example.laborator.domain.validators;

import com.example.laborator.domain.FriendRequest;

public class FriendRequestValidator implements Validator<FriendRequest>{

    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        String erori="";
        if(entity.getId()==null)
            erori+="Id invalid! Id-ul nu poate fi null!";
        if(entity.getFrom()==null)
            erori+="Id-ul primul utilizator este invalid! Id-ul nu poate fi null!";
        if(entity.getTo()==null)
            erori+="Id-ul celui de-al doilea utilizator este invalid! Id-ul nu poate fi null!";
        if (entity.getStatus()==null)
            erori+="Status invalid! Statusul nu trebuie sa fie null!";
        if (!erori.equals(""))
            throw new ValidationException(erori);
    }
}
