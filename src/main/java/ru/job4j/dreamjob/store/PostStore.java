package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PostStore {

    private static final PostStore INST = new PostStore();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger();

    private PostStore() {
    }

    public static PostStore instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public void add(Post post) {
        post.setId(counter.getAndIncrement());
        posts.put(post.getId(), post);
    }

    public Post findById(int id) {
        Post post = posts.get(id);
        int postId = post.getId();
        String postName = post.getName();
        String postDesc = post.getDescription();
        return new Post(postId, postName, postDesc);
    }

    public void update(Post post) {
        System.out.println(post.getId());
        System.out.println(post.getName());
        System.out.println(post.getDescription());
        posts.put(post.getId(), post);
    }
}