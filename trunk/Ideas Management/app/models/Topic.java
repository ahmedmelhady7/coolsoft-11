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
	public String name;
	
	/**
	 * the description of the topic
	 */
	@Lob
	public String description;
	
	/**
	 * the privacy level of the topic, restricts who can do what with it
	 */
	public short privacyLevel;
	
	/**
	 * the list of tags the topic is tagged with
	 */
	@ManyToMany
	public List<Tag> tags;
	
	/**
	 * the list of relationships the topic has
	 */
	//public List<Relationship> relationships;
	
	/**
	 * the creator of the topic
	 */
	@ManyToOne
	public User creator;
	
	/**
	 * the list of topic organizers
	 */
	@ManyToMany
	public List<User> organizers;
	
	/**
	 * the list of followers of the topic
	 */
	@ManyToMany
	public List<User> followers;
	
	/**
	 * the list of ideas in the topic
	 */
	@OneToMany(mappedBy = "belongsToTopic", cascade = CascadeType.ALL)
	public List<Idea> ideas;
	
	/**
	 * the list of comments on the topic
	 */
	@OneToMany
	public List<Comment> commentsOn;
	
	/**
	 * the list of requests to join the topic
	 */
	//public List<RequestToJoin> requestsToJoin;
	
	/**
	 * the list of relationship requests with the topic
	 */
	//public List<RequestOfRelationship> relationshipRequests;
	
	/**
	 * the list of invitations to the topic?
	 */
	//public List<TopicInvitation> topicInvitations;
	
	/**
	 * the entity the topic is in
	 */
	@ManyToOne
	public MainEntity entity;
	
	/**
	 * the plan that the topic is promoted to
	 */
	@OneToOne
	public Plan plan;
	
	/**
	 * the list of invitations to the topic?
	 */
	@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
	 List<Invitation> invitation;
	
	/**
	 * Default constructor that creates a topic with name, description,privacy level, creator and entity
	 * 
	 * @author ${aliaelbolock}
	 * 
	 * @param name
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param privacyLevel           
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 * @param entity
	 *            Entity that the topic belongs/added to
	 */
	public Topic(String n, String d, short p, User c, MainEntity m) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		entity = m;
		tags = new ArrayList<Tag>();
		//relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		invitation = new ArrayList<Invitation>();
		commentsOn = new ArrayList<Comment>();
		//requestsToJoin = new ArrayList<RequestToJoin>();
	}
	
	/**
	 * Creates a topic with name, description,privacy level and creator
	 * 
	 * @author ${aliaelbolock}
	 * 
	 * @param name
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param privacyLevel           
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 */
	public Topic(String n, String d, short p, User c) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		tags = new ArrayList<Tag>();
		//relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		invitation = new ArrayList<Invitation>();
		commentsOn = new ArrayList<Comment>();
		//requestsToJoin = new ArrayList<RequestToJoin>();
	}
	 
	/**
	 * This Method returns a list of organizers in a certain topic
	 *
	 * @author 	lama.ashraf
	 *
	 * @story 	C1S12
	 *
	 * @return	ArrayList<User>
	 */
	public List<User> getOrganizer(){
		 return organizers;
	}
	
	/**
	 * This Method returns the list of ideas in a certain topic
	 *
	 * @author 	aliaelbolock
	 *
	 * @story 	C3S20
	 *
	 * @return	ArrayList<Idea>
	 */
	public ArrayList<Idea> getIdeas(){
		 return (ArrayList<Idea>) ideas;
	}
	
	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	
	public void unfollow(User user) {
		followers.remove(user);
	}
}
