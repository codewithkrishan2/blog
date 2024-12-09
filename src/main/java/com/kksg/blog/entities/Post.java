package com.kksg.blog.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kksg.blog.entities.enums.PostStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	
	private Date postAddedDate;
		
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category postCategory;
	
	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
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
    
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Likes> likes = new HashSet<>();  // A collection of likes for this post

}
