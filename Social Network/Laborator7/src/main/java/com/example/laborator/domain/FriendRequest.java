package com.example.laborator.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long, Long>>{
//    Utilizator from;
 //   Utilizator to;
    private Tuple<Long, Long> id_request;
    FriendRequestStatus status;
    String date;
    Long to;
    Long from;

/*
    public FriendRequest(Utilizator from, Utilizator to) {
        //this.from = from;
        //this.to = to;
        this.status = FriendRequestStatus.PENDING;
    }*/

    public FriendRequest(Tuple<Long, Long> request) {
        this.id_request=request;
        this.from=id_request.getLeft();
        this.to=id_request.getRight();
        this.status = FriendRequestStatus.PENDING;
        this.date = Timestamp.valueOf(LocalDateTime.now()).toString();
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from.getId();
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Utilizator to) {
        this.to = to.getId();
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "utilizator1=" + from +
                ", utilizator2=" + to +
                ", status='" + status +
                ", date=" + date +
                '}';
    }
}
