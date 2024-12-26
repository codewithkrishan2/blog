package com.kksg.blog.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(
	    name = "tag",
	    indexes = {
	        @Index(name = "idx_tag_name", columnList = "tagName", unique = true)
	    }
	)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tagId;

    private String tagName;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();
	
}
