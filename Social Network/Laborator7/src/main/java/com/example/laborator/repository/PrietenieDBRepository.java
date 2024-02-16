package com.example.laborator.repository;

import com.example.laborator.domain.Prietenie;
import com.example.laborator.domain.Tuple;
import com.example.laborator.domain.validators.PrietenieValidator;
import com.example.laborator.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class PrietenieDBRepository implements Repository<Tuple<Long, Long>, Prietenie> {

    protected String url;
    protected String username;
    protected String password;
    protected Validator<Prietenie> prietenieValidator;


    public PrietenieDBRepository(String url, String username, String password, Validator<Prietenie> prietenieValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.prietenieValidator = new PrietenieValidator();
    }
    /*public PrietenieDBRepository(String url, String username, String password, PrietenieValidator prietenieValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.prietenieValidator=prietenieValidator;
    }*/

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> ID) {
        //Prietenie prietenie=null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                     "where id1 = ? and id2 = ?");) {
            statement.setLong(1, ID.getLeft());
            statement.setLong(2, ID.getRight());
            //statement.setLong(1, ID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long idUser1 = resultSet.getLong("id1");
                Long idUser2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");

                //Timestamp dateDB=resultSet.getTimestamp("date");
                //LocalDateTime date=new Timestamp(dateDB.getTime()).toLocalDateTime();

                Prietenie prietenie = new Prietenie();
                ID.setLeft(idUser1);
                ID.setRight(idUser2);
                prietenie.setId(ID);
                prietenie.setDate(date);
                return Optional.of(prietenie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        ArrayList<Prietenie> prietenii = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");

                //Timestamp dateDB= resultSet.getTimestamp("date");
                //LocalDateTime date=new Timestamp(dateDB.getTime()).toLocalDateTime();

                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> id = new Tuple<>(id1, id2);
                prietenie.setId(id);
                prietenie.setDate(date);

                prietenii.add(prietenie);

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prietenii;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        prietenieValidator.validate(entity);

        String sql = "insert into friendships (id1, id2, date) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setString(3, entity.getDate());

            //Timestamp dateDB= Timestamp.valueOf(entity.getDate());
            //statement.setTimestamp(3, dateDB);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> id) {
        Optional<Prietenie> optionalFriendship = findOne(id);

        if (optionalFriendship.isEmpty()) {
            throw new IllegalArgumentException("Friendship with id " + id + " does not exist!");
        }

        String sql = "delete from friendships where id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            Prietenie prietenie = findOne(id).get();
            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(prietenie) : Optional.empty();

            /*int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return optionalFriendship;
            }*/

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Prietenie> update(Tuple<Long, Long> longLongTuple, Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        prietenieValidator.validate(entity);
        if (findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE friendship SET \"id1\"=?, \"id2\"=?, WHERE \"date\"=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setString(3, entity.getDate());
            //Timestamp date = Timestamp.valueOf(entity.getDate());
            //statement.setTimestamp(3, date);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }


    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        prietenieValidator.validate(entity);
        if (findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE friendship SET \"id1\"=?, \"id2\"=?, WHERE \"date\"=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(1, entity.getId().getRight());
            Timestamp date = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(3, date);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }
}
