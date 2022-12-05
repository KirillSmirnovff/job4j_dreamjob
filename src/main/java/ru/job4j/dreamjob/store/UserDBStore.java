package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());
    private static final String INSERT_USER = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
    private static final String SELECT_USER = "SELECT * FROM users WHERE email = ?";

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password")
        );
    }

    public Optional<User> add(User user) {
        Optional<User> result = Optional.empty();
        try (Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    result = Optional.of(user);
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return result;
    }

    public Optional<User> findUserByEmailAndPassword(String email) {
        Optional<User> result = Optional.empty();
        try (Connection connection = pool.getConnection();
            PreparedStatement statement =  connection.prepareStatement(SELECT_USER)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = Optional.of(getUser(rs));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return result;
    }
}
