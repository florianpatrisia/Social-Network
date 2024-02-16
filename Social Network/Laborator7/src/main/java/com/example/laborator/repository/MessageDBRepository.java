package com.example.laborator.repository;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.Utilizator;
import com.example.laborator.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class MessageDBRepository implements Repository<Long, Message>{
    protected String url;
    protected String username;
    protected String password;
    private Validator<Message> messageValidator;

    public MessageDBRepository(String url, String username, String password, Validator<Message> messageValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    public Utilizator getUtilizatorById(Long id)
    {
        String sql ="select * from users where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            //statement.setLong(1,id);
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator utilizator = new Utilizator(firstName,lastName);
                //u.setId(id1);
                return utilizator;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStringFromList(List<Utilizator> utilizator){
        String stringWithUserId = "";
        for(Utilizator u:utilizator){
            stringWithUserId=stringWithUserId + u.getId().toString() + ";";
        }
        return stringWithUserId;
    }
//    @Override
//    public Optional<Message> findOne(Long id) {
//        try(Connection connection = DriverManager.getConnection(url, username, password);
//            PreparedStatement statement = connection.prepareStatement("select * from messages " +
//                    "where id = ?");
//
//        ) {
////            statement.setInt(1, Math.toIntExact(id));
//            statement.setLong(1, id);
//            ResultSet resultSet = statement.executeQuery();
//
//            if(resultSet.next()) {
//                Long id_from=resultSet.getLong("from");
//                Long to=resultSet.getLong("to");
//                String message=resultSet.getString("message");
//                String data = resultSet.getString("data");
//                Long reply = resultSet.getLong("reply");
//
//
//                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
//                statement2.setLong(1, id);
//                resultSet = statement2.executeQuery();
//
//                ArrayList<Long> to_all = new ArrayList<>();
//                while(resultSet.next())
//                {
//                    to_all.add(resultSet.getLong("to"));
//                }
//                Message message_obj=new Message(id_from, to_all, message);
//                message_obj.setId(id);
//
//                return Optional.ofNullable(message_obj);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return Optional.empty();
//    }

    @Override
    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages where id = ?");) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long from = resultSet.getLong("from");

                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));

                String sentMessage = resultSet.getString("message");

                Timestamp timestamp = resultSet.getTimestamp("data");
                LocalDateTime date= LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.ofHours(0));

                Long reply = resultSet.getLong("reply");

                    Message message = new Message(from, to, sentMessage);
                    message.setId(id);
                    message.setData(date);
                    message.setReplyMessage(reply);

                    return Optional.of(message);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    public List<String> stringToList(String to){
        List<String> listIdUtilizator = new ArrayList<String>(Arrays.asList(to.split(";")));
        return listIdUtilizator;
    }
    public List<Utilizator> getToUtilizatorList(String list){
        List<String> listId = stringToList(list);
        List<Utilizator> listUtilizator = new ArrayList<>();
        for(String s:listId){
            listUtilizator.add(getUtilizatorById(Long.parseLong(s)));
        }
        return listUtilizator;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages=new HashSet<>();


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long id_message = resultSet.getLong("id");
                Long id_from = resultSet.getLong("from");
                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Long id_reply = resultSet.getLong("reply");

                Message message_obj = new Message(id_from, to, message);
                message_obj.setId(id_message);
                messages.add(message_obj);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        String sql;
        if(entity.getReplyMessage()==null)
            sql="insert into messages (id, \"from\", \"to\", message, data) values (?, ?, ?, ?, ?)";
        else
            sql="insert into messages (id, \"from\", \"to\", message, data, reply) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getFrom());
            statement.setArray(3, connection.createArrayOf("BIGINT", entity.getTo().toArray()));
            statement.setString(4, entity.getMesaage());
            Timestamp dataaa=Timestamp.valueOf(entity.getData());
            statement.setTimestamp(5, dataaa);
//            statement.setString(5, entity.getData());
            statement.setLong(6, entity.getReplyMessage());

            messageValidator.validate(entity);

            int response = statement.executeUpdate();

            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> delete(Long aLong) {
        return null;
    }

    public ArrayList<Message> findAllForTwoUsers(Long id1, Long id2) {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     """
                         select * from messages where "from" = ? and ? = any("to")
                         union
                         select * from messages where "from" = ? and ? = any("to")
                         order by date;
                         """);) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long from = resultSet.getLong("from");
//                Optional<Utilizator> userFrom = userDBRepository.findOne(from);

                Array arrayTo = resultSet.getArray("to");
                Long[] arrayData = (Long[]) arrayTo.getArray();
                ArrayList<Long> to = new ArrayList<>(List.of(arrayData));

                Long idMessage = resultSet.getLong("id");

                String sentMessage = resultSet.getString("message");

                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

                Long reply = resultSet.getLong("reply");

//                if (userFrom.isPresent()) {
                    Message message = new Message(from, to, sentMessage);
                    message.setId(idMessage);
                    message.setData(date);
                    message.setReplyMessage(reply);
                    messages.add(message);
//                }
            }

            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Long aLong, Message entity) {
        return Optional.empty();
    }
}
