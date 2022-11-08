package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());
    private static final String SELECT_ALL = "SELECT * FROM post";
    private static final String INSERT_POST = "INSERT INTO post(name, city_id, description, created, visible) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_POST = "UPDATE post SET name = ?, description = ?, visible = ?, city_id = ? where id = ?";

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    private Post createPost(ResultSet it) throws SQLException {
        return new Post(it.getInt("id"), it.getString("name"),
                it.getString("description"), it.getBoolean("visible"),
                new City(it.getInt("city_id")),
                it.getTimestamp("created").toLocalDateTime().toLocalDate());
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(createPost(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return posts;
    }


    public Post add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(INSERT_POST,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getCity().getId());
            ps.setString(3, post.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated().atStartOfDay()));
            ps.setBoolean(5, post.isVisible());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return post;
    }

    public boolean replace(Post post) {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_POST)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setBoolean(3, post.isVisible());
            ps.setInt(4, post.getCity().getId());
            ps.setInt(5, post.getId());
            int affected =  ps.executeUpdate();
            rsl = affected > 0;
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public Optional<Post> findById(int id) {
        Optional<Post> rsl = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM post WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    rsl = Optional.of(createPost(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }
}