package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.User;
import com.technews.model.Vote;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserRepository repository;

    //Autowired means to instantiate or creating an instance of a class (inserts dependencies)
    @Autowired
    VoteRepository voteRepository;

    //Handles HTTP Get requests for all the users
    @GetMapping("/api/users")
    //Return a list of users
    public List<User> getAllUsers(){
        //fetches all users from the DB
        List<User> userList=repository.findAll();
        //The "for" iterates over each user of the userList
        for (User u: userList){
            //Retrieves a list of posts associated with the user
            List<Post> postList= u.getPosts();
            //The "for iterates over each post of the postList"
            for (Post p: postList){
                //counts the number of votes for the specific post
                p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
            }
        }
        return userList;
    }

    //Gets a single user by their id
    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        User returnUser = repository.getById(id);
        List<Post> postList = returnUser.getPosts();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }
        return returnUser;
    }

    @PostMapping("/api/users")
    public User addUser(@RequestBody User user) {
        // Encrypt password
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        repository.save(user);
        return user;
    }

    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        User tempUser = repository.getById(id);
        if (!tempUser.equals(null)) {
            user.setId(tempUser.getId());
            repository.save(user);
        }
        return user;
    }
    @DeleteMapping("/api/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        repository.deleteById(id);
    }
}
