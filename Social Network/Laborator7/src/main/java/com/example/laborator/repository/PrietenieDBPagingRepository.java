package com.example.laborator.repository;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.Prietenie;
import com.example.laborator.domain.Tuple;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.Validator;
import com.example.laborator.repository.paging.Page;
import com.example.laborator.repository.paging.Pageable;
import com.example.laborator.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrietenieDBPagingRepository extends PrietenieDBRepository implements PagingRepository<Tuple<Long, Long>, Prietenie> {
    public PrietenieDBPagingRepository(String url, String username, String password, Validator<Prietenie> prietenieValidator) {
        super(url, username, password, prietenieValidator);
    }

    private int getNumberOfElements() {
        int numberOfElements = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from friendships");
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

    public Page<Prietenie> findAllOnPage(Pageable pageable) {
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        System.out.println(offset + " ?>= " + numberOfElements);
        if (offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);

        ArrayList<Prietenie> prietenii = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ?");
        ) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");
                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> id = new Tuple<>(id1, id2);
                prietenie.setId(id);
                prietenie.setDate(date);

                prietenii.add(prietenie);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(prietenii, numberOfElements);
    }

    @Override
    public Page<Prietenie> findAll(Pageable pageable) {
        return findAllOnPage(pageable);
    }
}
/*
    public Page<Prietenie> findAllFriends(Pageable pageable, Long id_user) {
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        System.out.println(offset + " ?>= " + numberOfElements);
        if (offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);

        ArrayList<Utilizator> prieteni = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ?");
        ) {
            statement.setInt(1,limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                if(id1==id_user)
                    Utilizator u=ser
                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> id = new Tuple<>(id1, id2);
                prietenie.setId(id);
                prietenie.setDate(date);

                prietenii.add(prietenie);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(prietenii, numberOfElements);
    }
}*/

//    @Override
//    public Page<Prietenie> findAll(Pageable pageable) {
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset");
//        ) {
//            statement.setInt(1, pageable.getPageSize());
//            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
//            ResultSet resultSet=statement.executeQuery();
//
//            ArrayList<Prietenie> prietenii = new ArrayList<>();
//            while (resultSet.next()) {
//                Long id1 = resultSet.getLong("id1");
//                Long id2 = resultSet.getLong("id2");
//                String date = resultSet.getString("date");
//                Prietenie prietenie = new Prietenie();
//                Tuple<Long, Long> id = new Tuple<>(id1, id2);
//                prietenie.setId(id);
//                prietenie.setDate(date);
//
//                prietenii.add(prietenie);
//
//            }
//            return new PageImplementation<Prietenie>(pageable, prietenii.stream());
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }


