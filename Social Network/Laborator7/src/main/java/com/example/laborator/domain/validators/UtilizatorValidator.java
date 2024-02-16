package com.example.laborator.domain.validators;

import com.example.laborator.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String erori="";
        if(entity.getId()==null)
            erori+="Id-ul nu poate fi null!";
        if(entity.getId()<0)
            erori+="Id-ul trebuie sa fie numar pozitiv natural!";
        if(entity.getFirstName().isEmpty())
            erori+="Numele nu poate fi gol!";
        if(entity.getLastName().isEmpty())
            erori+="Prenumele nu poate fi gol!";
        for(int i=0;i<entity.getFirstName().length();i++)
            if(!Character.isLetter(entity.getFirstName().charAt(i)))
            {
                erori+="Nume invalid. Numele trebuie sa contina doar litere!";
                break;
            }
        for(int i=0;i<entity.getLastName().length();i++)
            if(!Character.isLetter(entity.getLastName().charAt(i)))
            {
                erori+="Prenume invalid. Prenuumele trebuie sa contina doar litere!";
                break;
            }
        if(erori.length()!=0)
            throw new ValidationException(erori);

    }
}

