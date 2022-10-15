package ru.job4j.dreamjob.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.PostService;

@Controller
public class PostController {

    private final PostService service = PostService.instOf();

    @GetMapping("/posts")
    public String posts(Model model) {
        model.addAttribute("posts", service.findAll());
        return "posts";
    }

    @GetMapping("/formAddPost")
    public String addPost(Model model) {
        return "addPost";
    }

    @PostMapping("/createPost")
    public String createPost(@ModelAttribute Post post) {
        service.add(post);
        System.out.println(post.getId());
        System.out.println(post.getName());
        System.out.println(post.getDescription());
        return "redirect:/posts";
    }

    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute Post post) {
        service.replace(post);
        return "redirect:/posts";
    }

    @GetMapping("/formUpdatePost/{postId}")
    public String formUpdatePost(Model model, @PathVariable("postId") int id) {
        model.addAttribute("post", service.findById(id));
        return "updatePost";
    }
}