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

@Entity
@Data
public class FlaggedComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comments comment; // The comment being flagged

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)	
    private User user; // The user who flagged the comment

    @Column(nullable = false)
    private String reason; // Reason for flagging (e.g., "Offensive", "Spam", etc.)

    @Column(nullable = false)
    private LocalDateTime flaggedAt; // Time when the comment was flagged

}
