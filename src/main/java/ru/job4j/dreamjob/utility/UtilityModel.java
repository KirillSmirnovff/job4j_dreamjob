package ru.job4j.dreamjob.utility;

import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public final class UtilityModel {

    private UtilityModel() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static  void addUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
    }

}
