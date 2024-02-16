package com.example.laborator.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long>{
    private Long from;
    private ArrayList<Long> to;
    private String mesaage;
    private LocalDateTime data;

    private Long replyMessage=0l;
    private String color;
    private static Long contor = 1L;

    public Message(Long from, ArrayList<Long> to, String mesaage, LocalDateTime data, Long reply) {
        this.setId(contor++);
        this.from = from;
        this.to = to;
        this.mesaage = mesaage;
        this.data =data;
        this.replyMessage=reply;
    }
    public Message(Long from, ArrayList<Long> to, String mesaage) {
        this.setId(contor++);
        this.from = from;
        this.to = to;
        this.mesaage = mesaage;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.data = LocalDateTime.now();
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from.getId();
    }

    public ArrayList<Long> getTo() {
        return to;
    }

    public void setTo(ArrayList<Long> to) {
        this.to = to;
    }

    public String getMesaage() {
        return mesaage;
    }

    public void setMesaage(String mesaage) {
        this.mesaage = mesaage;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(Long replyMessage) {
        this.replyMessage = replyMessage;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", message=" + mesaage + '\'' +
                ", data=" + data +
                ", replyMessage=" + replyMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}

