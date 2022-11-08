package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@ThreadSafe
@Service
public class PostService {

    private final PostDBStore store;

    public PostService(PostDBStore store) {
        this.store = store;
    }

    public Collection<Post> findAll(CityService cityService) {
        List<Post> posts = store.findAll();
        posts.forEach(
                post -> post.setCity(cityService.findById(post.getCity().getId()))
        );
        return posts;
    }

    public void add(Post post) {
        store.add(post);
    }

    public Post findById(int id) {
        return store.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public void replace(Post post) {
        store.replace(post);
    }
}
