package com.kksg.blog.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // The user who liked the post/comment

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;  // The post that was liked (nullable if the like is for a comment)

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = true)
    private Comments comment;  // The comment that was liked (nullable if the like is for a post)

    @Column(nullable = false)
    private LocalDateTime likeDate;  // Date when the like was added

}
