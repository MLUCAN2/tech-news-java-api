package com.technews.controller;

import com.technews.model.Comment;
import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.CommentRepository;
import com.technews.repository.PostRepository;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
public class HomePageController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;

    private static final Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            return "redirect:/";
        }

        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/users/logout")
    public String logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/")
    public String homepageSetup(Model model, HttpServletRequest request) {
        try {
            logger.info("Entering homepageSetup method");

            User sessionUser = new User();

            if (request.getSession(false) != null) {
                sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
                model.addAttribute("loggedIn", sessionUser.isLoggedIn());
                logger.info("User is logged in");
            } else {
                model.addAttribute("loggedIn", false);
                logger.info("User is not logged in");
            }

            List<Post> postList = postRepository.findAll();
            logger.info("Fetched post list of size: {}", postList.size());

            for (Post p : postList) {
                p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));

                Optional<User> userOptional = userRepository.findById(p.getUserId());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    p.setUserName(user.getUsername());
                } else {
                    p.setUserName("Unknown"); // Handle the case where the user is not found
                }
            }

            model.addAttribute("postList", postList);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());

            // "point" and "points" attributes refer to upvotes.
            model.addAttribute("point", "point");
            model.addAttribute("points", "points");

            logger.info("Exiting homepageSetup method");
            return "homepage";
        } catch (Exception e) {
            logger.error("Exception in homepageSetup: ", e);
            return "error"; // Return an error view
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPageSetup(Model model, HttpServletRequest request) throws Exception {
        if (request.getSession(false) != null) {
            setupDashboardPage(model, request);
            return "dashboard";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @GetMapping("/dashboardEmptyTitleAndLink")
    public String dashboardEmptyTitleAndLinkHandler(Model model, HttpServletRequest request) throws Exception {
        setupDashboardPage(model, request);
        model.addAttribute("notice", "To create a post the Title and Link must be populated!");
        return "dashboard";
    }

    @GetMapping("/singlePostEmptyComment/{id}")
    public String singlePostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        model.addAttribute("notice", "To add a comment you must enter the comment in the comment text area!");
        return "single-post";
    }

    @GetMapping("/post/{id}")
    public String singlePostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        setupSinglePostPage(id, model, request);
        return "single-post";
    }

    @GetMapping("/editPostEmptyComment/{id}")
    public String editPostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            model.addAttribute("notice", "To add a comment you must enter the comment in the comment text area!");
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @GetMapping("/dashboard/edit/{id}")
    public String editPostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            setupEditPostPage(id, model, request);
            return "edit-post";
        } else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    public Model setupDashboardPage(Model model, HttpServletRequest request) throws Exception {
        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

        Integer userId = sessionUser.getId();

        List<Post> postList = postRepository.findAllPostsByUserId(userId);
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));

            Optional<User> userOptional = userRepository.findById(p.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                p.setUserName(user.getUsername());
            } else {
                p.setUserName("Unknown");
            }
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("postList", postList);
        model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        model.addAttribute("post", new Post());

        return model;
    }

    public Model setupSinglePostPage(int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
        }

        Post post = postRepository.getById(id);
        post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));

        Optional<User> userOptional = userRepository.findById(post.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            post.setUserName(user.getUsername());
        } else {
            post.setUserName("Unknown");
        }

        List<Comment> commentList = commentRepository.findAllCommentsByPostId(post.getId());

        model.addAttribute("post", post);

        model.addAttribute("commentList", commentList);
        model.addAttribute("comment", new Comment());

        return model;
    }

    public Model setupEditPostPage(int id, Model model, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

            Post returnPost = postRepository.getById(id);
            Optional<User> userOptional = userRepository.findById(returnPost.getUserId());
            if (userOptional.isPresent()) {
                User tempUser = userOptional.get();
                returnPost.setUserName(tempUser.getUsername());
            } else {
                returnPost.setUserName("Unknown");
            }
            returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

            List<Comment> commentList = commentRepository.findAllCommentsByPostId(returnPost.getId());

            model.addAttribute("post", returnPost);
            model.addAttribute("loggedIn", sessionUser.isLoggedIn());
            model.addAttribute("commentList", commentList);
            model.addAttribute("comment", new Comment());
        }

        return model;
    }
}