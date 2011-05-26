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
public class Idea extends CoolModel {
	/**
	 * Idea's Tittle Required for creating an idea
	 */
	@Required
	public String title;
	/**
	 * @author Mohamed Ghanem
	 * 
	 *         Organization initialization date
	 */
	public Date intializedIn;

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
	 * the list of tags that the idea is tagged with
	 */
	@ManyToMany
	// (cascade = CascadeType.PERSIST)
	public List<Tag> tagsList;
	/**
	 * the list of comments to an idea
	 */
	@OneToMany
	// (cascade = CascadeType.PERSIST)
	public List<Comment> commentsList;
	/**
	 * a flag that represents if the idea is hidden
	 * */
	public boolean hidden;
	/**
	 * the topic that the idea belongs to
	 */
	@ManyToOne
	public Topic belongsToTopic;
	/**
	 * the Author of the idea
	 */
	@ManyToOne
	public User author;
	/**
	 * the Repoters of the idea
	 */
	@ManyToMany(mappedBy = "ideasReported")
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
	 * @author ${Loaay Alkherbawy} Each idea can have list of duplicates
	 */
	@OneToMany
	public List<Idea> duplicateIdeas;

	/**
	 * @author ${Ibrahim Safwat} Each idea can have a rating
	 */
	public String rating;

	/**
	 * @author ${Ibrahim Safwat} Must keep track of which users rated
	 */
	@ManyToMany
	public List<User> usersRated;

	/**
	 * @author ${Ibrahim Safwat} The organizer can prioritize ideas
	 */
	public String priority; // priority could equal
							// "Critical"/"High"/"Medium"/"Low
	/**
	 * @auther monica counter to check how many times this idea is viewed to be
	 *         used in sorting
	 */
	public int viewed;

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
		intializedIn = new Date();
		this.author = user;
		user.ideasCreated.add(this);
		this.belongsToTopic = topic;
		this.isDraft = false;
		this.tagsList = new ArrayList<Tag>();
		this.commentsList = new ArrayList<Comment>();
		this.reporters = new ArrayList<User>();
		this.usersRated = new ArrayList<User>();
		this.rating = "Not yet rated";
		this.priority = "Not yet prioritized";
		this.hidden = false;
		this.viewed = 0;
	}

	/**
	 * 
	 * @author ${Abdalrahman Ali}
	 * 
	 *         this constructor is used to create draft ideas
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
		intializedIn = new Date();
		this.description = body;
		this.belongsToTopic = topic;
		this.author = user;
		user.ideasCreated.add(this);
		this.isDraft = isDraft;
		this.tagsList = new ArrayList<Tag>();
		this.commentsList = new ArrayList<Comment>();
		this.reporters = new ArrayList<User>();
		this.usersRated = new ArrayList<User>();
		this.rating = "Not yet rated";
		this.priority = "Not yet prioritized";
		this.viewed=0;
		this.hidden = false;
	}

	/**
	 * 
	 * @author ${Fady Amir}
	 * 
	 * @param idea
	 *            the idea that we check its date
	 * 
	 */

	// @On("0 0 12 0 0 ?")
	/*
	 * public static void checkDate(Idea idea) {
	 * 
	 * Comment lastComment = idea.commentsList .get(idea.commentsList.size() -
	 * 1);
	 * 
	 * Date now = new Date();
	 * 
	 * Date lastCommentDate = lastComment.commentDate;
	 * 
	 * lastCommentDate.setDate(lastCommentDate.getDate() + 14);
	 * 
	 * if (lastCommentDate.after(now)) { List<User> user = new
	 * ArrayList<User>(); user.add(idea.author);
	 * 
	 * String type = "idea "; String desc = "This idea is inactive"; // Send
	 * notification
	 * 
	 * for (int i = 0; i < user.size(); i++) {
	 * Notifications.sendNotification(user.get(i).id, idea.id, type, desc); } }
	 * 
	 * }
	 * 
	 * /**
	 * 
	 * @author ${Fady Amir}
	 * 
	 * checking the dates of all ideas
	 * 
	 * 
	 * 
	 * public static void getAllIdeas() { List<Organization> listOfOrganizations
	 * = Organization.findAll(); for (int i = 0; i < listOfOrganizations.size();
	 * i++) { Organization org = listOfOrganizations.get(i); List<User> users =
	 * Users.getEnrolledUsers(org);
	 * 
	 * for (int j = 0; j < users.size(); j++) { User user = users.get(j);
	 * List<Idea> ideas = user.ideasCreated;
	 * 
	 * for (int k = 0; k < ideas.size(); k++) { Idea idea = ideas.get(k);
	 * checkDate(idea); } } } }
	 */
	
	/**
	 * The method toString for the idea
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @return String :
	 * 				The title of the description
	 * 
	 * */
	public String toString() {
		return this.title;
	}

	/**
	 * @author monica yousry this method increments the counter viewed
	 * @return:void
	 */
	public void incrmentViewed() {
		this.viewed++;
	}
}
