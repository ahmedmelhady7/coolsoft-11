package models;

import java.nio.MappedByteBuffer;
import java.util.*;

import javax.persistence.*;

import play.data.validation.*;

import play.db.jpa.*;

@Entity
public class Topic extends Model {

	/**
	 * the name of the topic
	 */
	@Required
	public String title;

	/**
	 * the description of the topic
	 */
	@Lob
	@Required
	@MaxSize(10000)
	public String description;

	/**
	 * the privacy level of the topic, restricts who can do what with it
	 */
	public int privacyLevel;

	/**
	 * the list of tags the topic is tagged with
	 */
	@ManyToMany
	public List<Tag> tags;

	/**
	 * the list of relationships the topic has
	 */
	// public List<Relationship> relationships;

	/**
	 * the creator of the topic
	 */
	// @Required
	@ManyToOne
	public User creator;

	/**
	 * the list of topic organizers
	 */

	// // to be removed
	@ManyToMany
	public List<User> organizers;

	/**
	 * the list of followers of the topic
	 */
	@ManyToMany(mappedBy = "topicsIFollow")
	public List<User> followers;

	/*
	 * the list of users that can access the topic
	 */
	// @ManyToMany
	// public List<User> canAccess;

	/**
	 * the list of ideas in the topic
	 */
	@OneToMany(mappedBy = "belongsToTopic")
	// , cascade = CascadeType.ALL)
	public List<Idea> ideas;

	/**
	 * the list of comments on the topic
	 */
	@OneToMany(mappedBy = "commentedTopic")
	// , cascade = CascadeType.ALL)
	public List<Comment> commentsOn;

	/**
	 * the list of requests to join the topic
	 */
	@OneToMany(mappedBy = "topic")
	// , cascade = CascadeType.ALL)
	public List<RequestToJoin> requestsToJoin;

	/**
	 * the list of relationship requests with the topic
	 */
	// public List<RequestOfRelationship> relationshipRequests;

	/**
	 * the list of invitations to the topic?
	 */
	// public List<TopicInvitation> topicInvitations;

	/**
	 * the entity the topic is in
	 */
	// @Required
	@ManyToOne
	public MainEntity entity;

	/**
	 * the plan that the topic is promoted to
	 */
	@OneToOne
	public Plan plan;

	/*
	 * boolean flag to determine if the topic is closed or not
	 * 
	 * @author Mostafa Aboul Atta
	 */
	public boolean openToEdit;

	/**
	 * Default constructor that creates a topic with name, description,privacy
	 * level, creator and entity
	 * 
	 * @author Alia el Bolock
	 * 
	 * @param title
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param i
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 * @param entity
	 *            Entity that the topic belongs/added to
	 */
	public Topic(String title, String description, int i, User creator,
			MainEntity entity) {
		this.title = title;
		this.description = description;
		this.privacyLevel = i;
		this.creator = creator;
		this.entity = entity;
		tags = new ArrayList<Tag>();
		// relationships = new ArrayList<Relationship>();
		// organizers = new ArrayList<User>();
		followers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		commentsOn = new ArrayList<Comment>();
		// canAccess = new ArrayList<User>();
		requestsToJoin = new ArrayList<RequestToJoin>();

		openToEdit = true;
	}

	/**
	 * Creates a topic with name, description,privacy level and creator
	 * 
	 * @author Alia el Bolock
	 * 
	 * @param title
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param privacyLevel
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 */
	public Topic(String title, String description, short privacyLevel,
			User creator) {
		this.title = title;
		this.description = description;
		this.privacyLevel = privacyLevel;
		this.creator = creator;
		tags = new ArrayList<Tag>();
		// relationships = new ArrayList<Relationship>();
		// organizers = new ArrayList<User>();
		followers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		commentsOn = new ArrayList<Comment>();
		// requestsToJoin = new ArrayList<RequestToJoin>();

		openToEdit = true;
	}

	/**
	 * This Method returns a list of organizers in a certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S12
	 * 
	 * @return ArrayList<User>
	 */

	public List<User> getOrganizer() {
	//  List<UserRoleInOrganization> o = (List<UserRoleInOrganization>) UserRoleInOrganization
//	    .find("select uro.enrolled from UserRoleInOrganization uro,Role r where  uro.Role = r and uro.resourceID = ? and r.roleName like ? and uro.resourceType = ? ",
//	      this.id, "organizer", "topic");
	//
	  List<User> organizer = new ArrayList<User>();
	//  for (int i = 0; i < o.size(); i++) {
	//   organizer.add((o.get(i).enrolled));
	//  }
	//  return organizer;
	  
	  List<UserRoleInOrganization> o = UserRoleInOrganization.find("byEntityTopicIDAndType", this.entity.id,"entity").fetch();
	  for(int i= 0; i <o.size(); i++) {
	   if((o.get(i).role.roleName).equals("organizer")){
	    organizer.add(o.get(i).enrolled);
	   }
	  }
	  return organizer;
	  
	  
	 }

	/**
	 * This Method returns the list of ideas in a certain topic
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S20
	 * 
	 * @return ArrayList<Idea>
	 */
	public List<Idea> getIdeas() {
		return (List<Idea>) ideas;
	}

	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public void unfollow(User user) {
		followers.remove(user);
		_save();
	}

	// /**
	// * This Method returns the list of followers in a certain topic
	// *
	// * @author Omar Faruki
	// *
	// * @story C2S29
	// *
	// * @return ArrayList<User>
	// */
	// public List<User> getFollowers() {
	// return (List<User>) this.followers;
	// }

	/**
	 * This Method overrides the toString method
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @return String
	 */
	public String toString() {
		return title;
	}

	/**
	 * This method posts an Idea in a certain topic
	 * 
	 * @author m.hisham.sa
	 * 
	 * @story C2S14
	 * 
	 * @param user
	 *            : the user who posts the idea
	 * @param title
	 *            : the title of the idea
	 * @param description
	 *            : description/content of the idea
	 * 
	 * @return void
	 */

	public void postIdea(User user, String title, String description) {
		Idea idea = new Idea(title, description, user, this);
		idea.privacyLevel = this.privacyLevel;
		this.ideas.add(idea);

	}

	/**
	 * This Method sends a request to the topic organizer and add the request to
	 * the list of requests
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param u
	 *            : the user who request to post
	 * 
	 * @return void
	 */

	public void requestFromUserToPost(User u) {
		if (requestsToJoin.indexOf(u) < 0) {
			User o = getOrganizer().get(0);
			RequestToJoin r = new RequestToJoin(u, this, null, o.email);
			// send the request
			requestsToJoin.add(r);
			_save();
		}
	}

	public boolean isDeletable() {
		// TODO Auto-generated method stub
		if (openToEdit == false)
			return false;
		if (ideas.size() > 0)
			return false;
		return true;
	}

	public boolean isHideable() {
		// TODO Auto-generated method stub
		if (openToEdit == false)
			return false;

		return true;
	}
}
