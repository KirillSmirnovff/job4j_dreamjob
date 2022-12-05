package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.store.UserDBStore;

import java.util.Objects;
import java.util.Optional;

@ThreadSafe
@Service
public class UserService {

    private final UserDBStore store;

    public UserService(UserDBStore store) {
        this.store = store;
    }

    private boolean isValid(User user) {
        return (user.getEmail().length() > 5 && user.getPassword().length() > 7);
    }

    public Optional<User> add(User user) {
        Optional<User> result = Optional.empty();
        if (isValid(user)) {
            result = store.add(user);
        }
        return result;
    }

    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        Optional<User> result = store.findUserByEmailAndPassword(email);
        if (result.isPresent()) {
            String userPassword = result.get().getPassword();
            if (!Objects.equals(password, userPassword)) {
                result = Optional.empty();
            }
        }
        return result;
    }
}
