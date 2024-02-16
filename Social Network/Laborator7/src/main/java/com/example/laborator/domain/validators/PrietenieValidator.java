package com.example.laborator.domain.validators;


import com.example.laborator.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String erori="";
        if(entity.getId().getLeft()==null)
            erori+="Primul Id invalid! Id-ul nu poate fi null";
        if(entity.getId().getRight()==null)
            erori+="Al doie Id invalid! Id-ul nu poate fi null";
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            erori+="Un utilizator nu poate fi prieten cu el insusi!";
        if(!erori.equals(""))
            throw new ValidationException(erori);
    }
}
