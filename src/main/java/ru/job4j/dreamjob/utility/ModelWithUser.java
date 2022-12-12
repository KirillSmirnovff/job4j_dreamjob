package ru.job4j.dreamjob.utility;

import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public class ModelWithUser {

    private final  Model model;

    public ModelWithUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
        this.model = model;
    }

    public void addAttribute(String key, Object value) {
        model.addAttribute(key, value);
    }
}
