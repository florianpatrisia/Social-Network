package com.example.laborator.repository;

import com.example.laborator.domain.Utilizator;

import java.sql.*;
import java.util.*;

import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.repository.paging.PagingRepository;

public class UserDBPagingRepository extends UserDBRepository  implements PagingRepository<Long, Utilizator>
{
    public UserDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    private int getNumberOfElements() {
        int numberOfElements = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from users");
             ResultSet resultSet = statement.executeQuery();
        ) {
            // pas 3: process result set
            while (resultSet.next()) {
                numberOfElements = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return numberOfElements;
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {

        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        System.out.println(offset + " ?>= " + numberOfElements);
        if (offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);

        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        ) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator user = new Utilizator(firstName, lastName);
                user.setId(id);
                user.setFriends(getFriends(id));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(users, numberOfElements);
    }


/*
    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
//        Stream<Utilizator> result = StreamSupport.stream(this.findAll().spliterator(), false)
//                .skip(pageable.getPageNumber()  * pageable.getPageSize())
//                .limit(pageable.getPageSize());
//        return new PageImplementation<>(pageable, result);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
            ResultSet resultSet=statement.executeQuery();

            Set<Utilizator> users = new HashSet<>();
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                Utilizator user=new Utilizator(firstName,lastName);
                user.setId(id);
                user.setFriends(getFriends(id));
                users.add(user);

            }
            return new PageImplementation<Utilizator>(pageable, users.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}

