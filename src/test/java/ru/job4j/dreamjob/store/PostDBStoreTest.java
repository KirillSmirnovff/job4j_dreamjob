package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PostDBStoreTest {

    PostDBStore store = new PostDBStore(new Main().loadPool());
    CityService service = new CityService();
    LocalDate created = LocalDate.now();

    @AfterEach
    public void wipeTable() throws SQLException {
        try (Connection connection = store.getPool().getConnection();
             PreparedStatement ps = connection.prepareStatement("delete from post")) {
            ps.execute();
        }
    }

    @Test
    public void whenCreatePost() {
        Post post = new Post(0, "Java Job", "High risk-high reward project",
                true, new City(1, "Moscow"), created);
        store.add(post);
        Post postInDb = store.findById(post.getId()).get();
        assertThat(postInDb.getName(), is(post.getName()));
    }

    @Test
    public void whenFindAll() {
        Post postOne = new Post(0, "Java Job", "High risk-high reward project",
                true, service.findById(1), created);
        Post postTwo = new Post(1, "Python Job", "Very relaxing project with omega salary",
                true, service.findById(2), created);
        store.add(postOne);
        store.add(postTwo);
        List<Post> postsInDb = store.findAll();
        assertThat(postsInDb.get(0).getName(), is(postOne.getName()));
        assertThat(postsInDb.get(1).getName(), is(postTwo.getName()));
        assertThat(postsInDb.get(0).getCity().getId(), is(postOne.getCity().getId()));
        assertThat(postsInDb.get(1).getCity().getId(), is(postTwo.getCity().getId()));
    }

    @Test
    public void whenReplace() {
        Post post = new Post(0, "Java Job", "High risk-high reward project",
                true, service.findById(1), created);
        store.add(post);
        post.setDescription("Very relaxing project with omega salary");
        post.setName("Python Job");
        boolean replace = store.replace(post);
        Post postInDb = store.findById(post.getId()).get();
        assertThat(replace, is(true));
        assertThat(post.getName(), is(postInDb.getName()));
    }
}