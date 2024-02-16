package com.example.laborator.repository;

import com.example.laborator.domain.*;
import com.example.laborator.domain.validators.FriendRequestValidator;
import com.example.laborator.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class FriendRequestDBRepository implements Repository<Tuple<Long, Long>, FriendRequest>{
    protected String url;
    protected String username;
    protected String password;
    protected Validator<FriendRequest> friendRequestValidator;
    private UserDBRepository utilizatorRepo;
    protected PrietenieDBRepository prietenieRepo;

    public FriendRequestDBRepository(String url, String username, String password, UserDBRepository repo1, PrietenieDBRepository repo2) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.utilizatorRepo=repo1;
        this.prietenieRepo=repo2;
        this.friendRequestValidator = new  FriendRequestValidator();
    }

    /*
    @Override
    public Optional<FriendRequest> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from requests" + "where id = ?");) {

            statement.setLong(1, id);
            ResultSet resultSet=statement.executeQuery();

            if(resultSet.next()) {
                Long from=resultSet.getLong("from");
                Optional<Utilizator> utilizatorFrom=utilizatorRepo.findOne(from);

                Long to=resultSet.getLong("to");
                Optional<Utilizator> utilizatorTo=utilizatorRepo.findOne(to);

                FriendRequestStatus status= FriendRequestStatus.valueOf(resultSet.getString("status"));

                if(utilizatorFrom.isPresent() && utilizatorTo.isPresent())
                {
                    FriendRequest newFriendRequest= new FriendRequest(utilizatorFrom.get(), utilizatorTo.get());
                    newFriendRequest.setStatus(status);
                    newFriendRequest.setId(id);

                    return Optional.of(newFriendRequest);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }*/

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from requests where \"from\" = ? and \"to\" = ?");) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            ResultSet resultSet=statement.executeQuery();

            if(resultSet.next()) {
                Long from=resultSet.getLong("from");
                Optional<Utilizator> utilizatorFrom=utilizatorRepo.findOne(from);

                Long to=resultSet.getLong("to");
                Optional<Utilizator> utilizatorTo=utilizatorRepo.findOne(to);

                FriendRequestStatus status= FriendRequestStatus.valueOf(resultSet.getString("status"));

                String date=resultSet.getString("data");

                if(utilizatorFrom.isPresent() && utilizatorTo.isPresent()) {
                    Tuple<Long, Long> id_request = new Tuple<>(from, to);
                    FriendRequest newFriendRequest = new FriendRequest(id_request);
                    newFriendRequest.setStatus(status);
                    newFriendRequest.setDate(date);
                    newFriendRequest.setId(id_request);
                    return Optional.of(newFriendRequest);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        ArrayList<FriendRequest> list_friendRequests=new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long from=resultSet.getLong("from");
                Optional<Utilizator> utilizatorFrom=utilizatorRepo.findOne(from);

                Long to=resultSet.getLong("to");
                Optional<Utilizator> utilizatorTo=utilizatorRepo.findOne(to);

                FriendRequestStatus status= FriendRequestStatus.valueOf(resultSet.getString("status"));

                String date=resultSet.getString("data");

                if(utilizatorFrom.isPresent() && utilizatorTo.isPresent())
                {
                    Tuple<Long, Long> id_request=new Tuple<>(from,to);
                    FriendRequest newFriendRequest= new FriendRequest(id_request);
                    newFriendRequest.setStatus(status);
                    newFriendRequest.setDate(date);
                    newFriendRequest.setId(id_request);
                    list_friendRequests.add(newFriendRequest);
                }

            }
            return list_friendRequests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        friendRequestValidator.validate(entity);

        String sql = "insert into requests (\"from\", \"to\", \"status\", \"data\") values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setLong(1, entity.getFrom());
            statement.setLong(2, entity.getTo());
            statement.setString(3, entity.getStatus().toString());
            statement.setString(4, entity.getDate());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        String sql = "delete from requests where \"from\" = ? and \"to\" =?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            FriendRequest friendRequest=findOne(id).get();
            friendRequest.setId(id);

            int response = statement.executeUpdate();
            return response == 1 ? Optional.of(friendRequest) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public Optional<FriendRequest> delete(Long id) {
//
//
//        String sql = "delete from request where id = ?";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(sql);) {
//
//            statement.setLong(1, id);
//            FriendRequest friendRequest=findOne(id).get();
//
//            int response = statement.executeUpdate();
//            return response == 1 ? Optional.of(friendRequest) : Optional.empty();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
/*
    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update requests set status = ? where id = ?");) {

            statement.setString(1, entity.getStatus().toString());
            statement.setLong(2, entity.getId());

            friendRequestValidator.validate(entity);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public Optional<FriendRequest> update(Tuple<Long, Long> longLongTuple, FriendRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update requests set \"status\" = ? and \"data\" = ? where \"from\" = ? and \"to\" = ?");) {

            statement.setString(1, entity.getStatus().toString());
            statement.setLong(2, entity.getFrom());
            statement.setLong(3, entity.getTo());

            friendRequestValidator.validate(entity);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update requests set \"status\" = ?, \"data\" = ? where \"from\" = ? and \"to\" = ?");) {

            statement.setString(1, entity.getStatus().toString());
            statement.setString(2, entity.getDate());
            statement.setLong(3, entity.getFrom());
            statement.setLong(4, entity.getTo());

            friendRequestValidator.validate(entity);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public Optional<FriendRequest> update(Long aLong, FriendRequest entity) {
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("update requests set status = ? where id = ?");) {
//
//            statement.setString(1, entity.getStatus().toString());
//            statement.setLong(2, entity.getId());
//
//            friendRequestValidator.validate(entity);
//
//            int response = statement.executeUpdate();
//            return response == 0 ? Optional.of(entity) : Optional.empty();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
