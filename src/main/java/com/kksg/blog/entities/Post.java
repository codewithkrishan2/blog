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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer postId;
	
	@Column(length = 5000, nullable = false)
	private String postTitle;
	
	@Column(length = 1000000000)
	private String postContent;
	
	private String postImage;
	
	private LocalDateTime postAddedDate;
		
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
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String slug;  // Custom URL Slug
    
    @Column(nullable = false)
    private long likeCount = 0;

    @Column(nullable = false)
    private long viewCount = 0;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

}
