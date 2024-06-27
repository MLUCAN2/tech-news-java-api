package com.technews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

//Persistable, so the post class can specify and map to a database table
@Entity
//Ignore JSON properties during serialization to avoid issues during lazy loading or hibernation
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//Specifies the table name
@Table(name = "post")

//This formats our table and inputs properties of our class
public class Post implements Serializable {

    //Denotes the primary key
    @Id
    //Strategy for generating the id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    private String postUrl;
    //The username does not persist or remain stored in the database
    @Transient
    private String userName;
    //The voteCount does not persist or remain stored in the database
    @Transient
    private int voteCount;
    private Integer userId;

    //The date cannot be null
    @NotNull
    //Is an Enum or fixed constant in java. Specifies the type of value (Temporal can have 3 Date, Time, and Timestamp)
    //Date is year, month, day
    @Temporal(TemporalType.DATE)
    //Specifies column name
    @Column(name = "posted_at")
    private Date postedAt = new Date();

    //Required updated date
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    //Specifies the relationship between comments
    //Lazy loading fetches info from the db only when it is first accessed rather than when the parent is loaded
    // Need to use FetchType.LAZY to resolve multiple bags exception
    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    //A constructor that allows for an empty post object
    public Post() {
    }

    //A parameterized constructor that creates the post object
    public Post(Integer id, String title, String postUrl, int voteCount, Integer userId) {
        this.id = id;
        this.title = title;
        this.postUrl = postUrl;
        this.voteCount = voteCount;
        this.userId = userId;
    }

    //Getters and setters, allow us to access and modify private fields
    //They are like the read and update part of CRUD
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    //Checks if two Post objects are true by comparing fields then returns the data
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return getVoteCount() == post.getVoteCount() &&
                Objects.equals(getId(), post.getId()) &&
                Objects.equals(getTitle(), post.getTitle()) &&
                Objects.equals(getPostUrl(), post.getPostUrl()) &&
                Objects.equals(getUserName(), post.getUserName()) &&
                Objects.equals(getUserId(), post.getUserId()) &&
                Objects.equals(getPostedAt(), post.getPostedAt()) &&
                Objects.equals(getUpdatedAt(), post.getUpdatedAt()) &&
                Objects.equals(getComments(), post.getComments());
    }

    //Returns a hash code value for the post object
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getPostUrl(), getUserName(), getVoteCount(), getUserId(), getPostedAt(), getUpdatedAt(), getComments());
    }

    //Returns a readable string of the post object
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", voteCount=" + voteCount +
                ", userId=" + userId +
                ", postedAt=" + postedAt +
                ", updatedAt=" + updatedAt +
                ", comments=" + comments +
                '}';
    }
}