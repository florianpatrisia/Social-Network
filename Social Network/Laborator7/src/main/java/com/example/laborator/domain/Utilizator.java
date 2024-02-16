package com.example.laborator.domain;

import java.util.ArrayList;
import java.util.Objects;


public class Utilizator extends Entity<Long>{
    protected String firstName;
    protected String lastName;
    private ArrayList<Long> friends;
    private static Long contor = 1L;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends=new ArrayList<>();
        this.setId(contor++);
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Long> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Long> friends){this.friends=friends;}

    @Override
    public String toString() {
        return super.toString()  + "first name: " + firstName + ", last name: " + lastName + ", friends: " + friends + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    public  void adaugaPrieten(Utilizator utilizator)
    {
        this.friends.add(utilizator.getId());
    }

    public void stergePrieten(Utilizator utilizator)
    {
        this.friends.remove(utilizator.getId());
    }


    public boolean estePrieten(Utilizator other)
    {
        //Function<Long, Boolean> func=prieten-> other.getId().equals(prieten);
        for(Long prieten: friends)
            if(other.getId().equals(prieten))
                return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

}