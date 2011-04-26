package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.Model;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */

/**
 * Ideas to be posted to Topics
 */

@Entity
public class Idea extends Model {
	/**
	 * Idea's Tittle Required for creating an idea
	 */
	@Required
	public String title;
	/**
	 * Idea's description Required for creating an idea
	 */
	@Required
	public String description;
	/**
	 * Counter for how many times the idea got reported as a spam
	 */
	public int spamCounter;
	/**
	 * The level of privacy of the idea (public,private,secret)
	 */
	public int privacyLevel;
	/**
	 * the list of tags that the idea is tagged with
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	public List<Tag> tagsList;
	/**
	 * the list of comments to an idea
	 */
	@OneToMany(cascade = CascadeType.PERSIST)
	public List<Comment> commentsList;
	/**
	 * the topic that the idea belongs to
	 */
	@Required
	@ManyToOne
	public Topic belongsToTopic;
	/**
	 * the Author of the idea
	 */
	@Required
	@ManyToOne
	public User author;
	/**
	 * the plan that the idea is marked in
	 */
	@ManyToOne
	public Plan plan;
	/*
	 * true if the idea is not to be posted (saved as a draft) , false if it's
	 * to be posted normally
	 */

	public boolean isDraft;

	public int rating;

	public String priority; // priority could equal
							// "Critical"/"High"/"Medium"/"Low

	/**
	 * Default constructor
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @param title
	 *            title of the idea
	 * @param description
	 *            description of the idea
	 * @param user
	 *            Author of the idea
	 * @param topic
	 *            Topic that the idea belongs/added to
	 */

	public Idea(String title, String description, User user, Topic topic) {
		this.title = title;
		this.description = description;
		this.author = user;
		user.ideasCreated.add(this);
		this.belongsToTopic = topic;
		this.isDraft = false;
		this.tagsList = new ArrayList<Tag>();
		// this.commentsList = new ArrayList<Comment>();

	}

	/**
	 * 
	 * @author ${Abdalrahman Ali}
	 * 
	 * @param title
	 *            title of the idea
	 * @param body
	 *            the body of the idea
	 * @param user
	 *            Author of the idea
	 * @param topic
	 *            Topic that the idea belongs/added to
	 * @param isDraft
	 *            True if this idea will be saved as a draft
	 * 
	 */

	public Idea(String title, String body, User user, Topic topic,
			boolean isDraft) {
		this.title = title;
		this.description = body;
		this.belongsToTopic = topic;
		this.author = user;
		user.ideasCreated.add(this);
		this.isDraft = isDraft;
		this.tagsList = new ArrayList<Tag>();
		// this.commentsList = new ArrayList<Comment>();
	}

}
