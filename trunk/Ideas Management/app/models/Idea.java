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
	@Lob
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
	 * the Repoters of the idea
	 */
	@ManyToMany
	public List<User> reporters;
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

	/**
	 * @author ${Ibrahim Safwat}
	 * Each idea can have a rating
	 */
	public int rating;
	
	/**
	 * @author ${Ibrahim Safwat}
	 * Must keep track of which users rated
	 */
	//@OneToOne (mappedBy = "idea", cascade = CascadeType.ALL)
	//public List<User> usersRated;

	/**
	 * @author ${Ibrahim Safwat}
	 * The organizer can prioritize ideas
	 */
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
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param UserToShare
	 * 				User that wants to share the idea
	 * @param UserToShareWith
	 * 				User the will be sent the notification with the ideaID
	 * @param ideaID
	 * 				ID of the idea to be shared
	 */
	public void shareIdea(User UserToShare, User UserToShareWith, int ideaID)
	{
		// send a notification to UserToShareWith with the ideaID from UserToShare
	}
	
	public void setPriority(String priority, int ideaID)
	{
		//user must be organizer
		//ideaID.priority = priority
	}
	
	public void rate(int rate, int ideaID)
	{
		//user must be organizer in the organization to rate an idea
		//if user already rated -> error "already rated"
		
//		if(rate>=0 && rate<=5)
//		{
//			float oldRating;
//			float tempRating;
//			tempRating = (oldRating + rate)/2;
//			ideaID.rating = tempRating;
//		}
//		else
//		{
//			error"Number must be between 0 and 5"
//		}
	}

}
