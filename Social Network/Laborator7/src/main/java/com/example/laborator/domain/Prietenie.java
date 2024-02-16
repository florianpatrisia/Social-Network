package com.example.laborator.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Prietenie extends Entity<Tuple<Long,Long>> {
    private Tuple<Long, Long> idPrietenie;
    public String date;

    public Prietenie(Tuple<Long, Long> idPrietenie) {
        this.idPrietenie=idPrietenie;
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //this.date = LocalDateTime.now().format(formatter);
    }

    public Prietenie() {
        this.idPrietenie=idPrietenie;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date=date;

    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idPrietenie); //, date);
    }

    @Override
    public String toString() {
        return super.toString()+ "{ date: "+date+"}";
    }
}
