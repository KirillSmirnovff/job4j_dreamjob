package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class PostService {

    private final PostDBStore store;

    public PostService(PostDBStore store) {
        this.store = store;
    }

    public Collection<Post> findAll(CityService cityService) {
        return store.findAll(cityService);
    }

    public void add(Post post) {
        store.add(post);
    }

    public Post findById(int id) {
        Post rsl = new Post();
        Optional<Post> post = store.findById(id);
        if (post.isPresent()) {
            rsl = post.get();
        }
        return rsl;
    }

    public void replace(Post post) {
        store.replace(post);
    }
}
