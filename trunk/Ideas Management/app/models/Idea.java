package models;

import java.util.*;

import javax.persistence.*;

import controllers.Notifications;
import controllers.Users;

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
	 * @author ${Ibrahim Safwat} Each idea can have a rating
	 */
	public int rating;

	/**
	 * @author ${Ibrahim Safwat} Must keep track of which users rated
	 */
	@OneToMany
	public List<User> usersRated;

	/**
	 * @author ${Ibrahim Safwat} The organizer can prioritize ideas
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
		this.commentsList = new ArrayList<Comment>();
		this.rating = 0;
		this.priority = null;

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
	 *            //user must be organizer Author of the idea
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
		this.usersRated = new ArrayList<User>();
		// this.commentsList = new ArrayList<Comment>();
	}

	public String toString() {
		return this.title;
	}

	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param UserToShare
	 *            User that wants to share the idea
	 * @param UserToShareWith
	 *            User the will be sent the notification with the ideaID
	 * @param ideaID
	 *            ID of the idea to be shared
	 */
	public void shareIdea(ArrayList<User> UserToShare, User UserToShareWith,
			long ideaID) {
		String type = "idea";
		String desc = "userLoggedIn shared an Idea with you";
		UserToShare = new ArrayList<User>();
		long notId = ideaID;
		Notifications.sendNotification(UserToShare, notId, type, desc);
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param priority
	 *            the priority to be set
	 * @param ideaID
	 *            the ID of the idea to prioritize
	 */
	public void setPriority(String priority, int ideaID) {
		// ideaID.priority = priority
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param userToCheck
	 *            User to be checked if he/she is in the list usersRated
	 * @return
	 */
	public boolean checkRated(User userToCheck) {
		for (int i = 0; i < usersRated.size(); i++) {
			if (userToCheck == usersRated.get(i))
				return true;
		}
		return false;
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rate
	 *            rating taken from the user
	 * @param ideaID
	 *            idea that the user wants to rate
	 */
	public void rate(int rate, int ideaID) {
		// User userLoggedIn = new User();
		//
		// if(!checkRated(userLoggedIn))
		// {
		// if(rate>=0 && rate<=5)
		// {
		// float oldRating;
		// float tempRating;
		// tempRating = (oldRating + rate)/2;
		// ideaID.rating = tempRating;
		// }
		// }
	}

}
