package com.example.laborator.repository;

import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, Utilizator> {

    protected String url;
    protected String username;
    protected String password;

    private Validator<Utilizator> utilizatorValidator;


    public UserDBRepository(String url, String username, String password, Validator<Utilizator> utilizatorValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.utilizatorValidator=utilizatorValidator;
    }

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Utilizator> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator u = new Utilizator(firstName,lastName);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

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
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Optional<Utilizator> save(Utilizator entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert into users (id, first_name, last_name) values (?, ?, ?);");) {

            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());

//            utilizatorValidator.validate(entity);

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Utilizator> delete(Long id) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from users where id = ?");) {
            {
                statement.setLong(1, id);

                Utilizator utilizator=findOne(id).get();
                int response=statement.executeUpdate();
                return response == 1 ? Optional.of(utilizator) : Optional.empty();

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Utilizator> update(Utilizator entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update users set first_name = ?, last_name = ? where id = ?");) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            //validator.validate(entity);

            int response = statement.executeUpdate();
            entity.setFriends(getFriends(entity.getId()));
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Long aLong, Utilizator entity) {
        return Optional.empty();
    }
    ArrayList<Long> getFriends(Long id) {
        ArrayList<Long> friends = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     select u.id from friendships f left join users u on f.id2 = u.id where f.id1 = ?
                     union
                     select u.id from friendships f left join users u on f.id1 = u.id where f.id2 = ?;""");) {

            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id_friend = resultSet.getLong("id");
                friends.add(id_friend);
            }

            return friends;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
