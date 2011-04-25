package models;

import java.nio.MappedByteBuffer;
import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Topic extends Model {
	public String name;
	public String description;
	public short privacyLevel;// I don't know the type of this attribute
	public ArrayList<Tag> tags;
	// public ArrayList<Relationship> relationships;

	@ManyToOne
	public User creator;// add between topic and user (post) exactly one user
						// can post many topics

	@ManyToMany
	public ArrayList<User> organizers;// topic belongs to which entity and get
	// the list of its organizers
	@OneToMany(mappedBy = "belongsToTopic", cascade = CascadeType.ALL)
	public ArrayList<Idea> ideas;

	@ManyToOne
	public MainEntity entity;
	// public ArrayList<Comment> commentsOn;
	// public ArrayList<RequestToJoin> requestsToJoin;
	// public ArrayList<RequestOfRelationship> relationshipRequests;
	// public ArrayList<TopicInvitation> topicInvitations;
	// public ArrayList<OrganizationEntity> entities;// change name of class
	// Entity
	// as it leads to conflicts
	// with the Entity in java
	// in persistence
	// add this attribute
	public ArrayList<Plan> plans;// add this attribute

	@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
	 List<Invitation> invitation;
	
	
	public Topic(String n, String d, short p, User c) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		// tags = new ArrayList<Tag>();
		// relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		invitation = new ArrayList<Invitation>();
		// commentsOn = new ArrayList<Comment>();
		// requestsToJoin = new ArrayList<RequestToJoin>();
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
	public ArrayList<User> getOrganizer() {
		return organizers;

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
	
	/*
	public void unfollow(User user) {
		followers.remove(user);
	}
*/
}
