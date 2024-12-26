package com.kksg.blog.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.kksg.blog.entities.enums.PostStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(
	    name = "post",
	    indexes = {
	        @Index(name = "idx_post_title", columnList = "postTitle"),
	        @Index(name = "idx_post_status", columnList = "status"),
	        @Index(name = "idx_post_created_on", columnList = "createdOn"),
	        @Index(name = "idx_post_slug", columnList = "slug")
	    }
	)
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer postId;
	
	@Column(length = 250, nullable = false)
	private String postTitle;
	
	@Lob
	@Column(columnDefinition = "TEXT")
	private String postContent;
	private String postImage;
	
	@Column(updatable = false)
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
	private Boolean isDeleted;
		
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category postCategory;
	
	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Comments> comments = new HashSet<>();
	
	@Enumerated(EnumType.STRING)
	private PostStatus status;
	
	// SEO Fields
	@Column(length = 255)
    private String metaTitle;
	
	@Column(length = 255)
    private String metaDescription;
	
	@Column(length = 500)
    private String metaKeywords;
    private String slug;  // Custom URL Slug
    
    @Column(nullable = false)
    private long likeCount = 0;

    @Column(nullable = false)
    private long viewCount = 0;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(  name = "post_tags", joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @PrePersist
    public void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = createdOn;
    }

    @PreUpdate
    public void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

}
