package com.example.laborator.repository;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.validators.Validator;
//import com.example.laborator.repository.paging.PageImplementation;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MessageDBPagingRepository extends MessageDBRepository implements PagingRepository<Long, Message> {
    public MessageDBPagingRepository(String url, String username, String password, Validator<Message> messageValidator) {
        super(url, username, password, messageValidator);
    }
    private int getNumberOfElements(){
        int numberOfElements = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from movies");
             ResultSet resultSet = statement.executeQuery();
        ) {
            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return numberOfElements;
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        System.out.println(offset + " ?>= "+numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);

        Set<Message> messages=new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendrequests");
        ) {

            statement.setInt(2, offset);
            statement.setInt(1, limit);
            ResultSet resultSet=statement.executeQuery();

            while (resultSet.next()) {
                Long id_message = resultSet.getLong("id");
                Long id_from = resultSet.getLong("from");
                String message = resultSet.getString("message");
                String data = resultSet.getString("data");
                Long id_reply = resultSet.getLong("reply_message");

                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
                statement2.setLong(1, id_message);
                ResultSet resultSet2 = statement2.executeQuery();

                ArrayList<Long> to_all = new ArrayList<>();
                while(resultSet2.next())
                {
                    to_all.add(resultSet.getLong("to"));
                }

                Message message_obj = new Message(id_from, to_all, message);
                message_obj.setId(id_message);
                messages.add(message_obj);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(messages, numberOfElements);

    }

        /*
    @Override
    public Page<Message> findAll(Pageable pageable) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendrequests");
        ) {

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
            ResultSet resultSet=statement.executeQuery();

            Set<Message> messages=new HashSet<>();

            while (resultSet.next()) {
                Long id_message = resultSet.getLong("id");
                Long id_from = resultSet.getLong("from");
                String message = resultSet.getString("message");
                String data = resultSet.getString("data");
                Long id_reply = resultSet.getLong("reply_message");

                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
                statement2.setLong(1, id_message);
                ResultSet resultSet2 = statement2.executeQuery();

                ArrayList<Long> to_all = new ArrayList<>();
                while(resultSet2.next())
                {
                    to_all.add(resultSet.getLong("to"));
                }


                Message message_obj = new Message(id_from, to_all, message, data, id_reply);
                message_obj.setId(id_message);
                messages.add(message_obj);
            }
            return new PageImplementation<Message>(pageable, messages.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
