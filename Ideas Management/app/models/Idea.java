package models;

import java.util.ArrayList;

import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.Model;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */

public class Idea extends Model {
	@Required
	String title;
	@Required
	String description;
	int spamCounter;
	@Required
	int privacyLevel;
	@ManyToMany(cascade = CascadeType.PERSIST)
	// ArrayList<Tag> tagsList;
	@OneToMany(cascade = CascadeType.PERSIST)
	// ArrayList<Comment> commentsList;
	@Required
	@ManyToOne
	// Topic belongsTo;
	Boolean Active;

	// @Required
	@ManyToOne
	public User author;

	public Idea(String t, String d, User user) {
		this.title = t;
		this.description = d;
		this.Active = false;
		this.author = user;
		// this.tagsList = new ArrayList<Tag>();
		// this.commentsList = new ArrayList<Comment>();

	}
	
	

}
