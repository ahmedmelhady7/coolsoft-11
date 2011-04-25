package models;


import java.nio.MappedByteBuffer;
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;

import play.db.jpa.*;

@Entity
public class Topic extends Model {
	@Required
	public String name;
	@Lob
	public String description;
	public short privacyLevel;
	public List<Tag> tags;
	//public List<Relationship> relationships;

	@ManyToOne
	public User creator;

	@ManyToMany
	public List<User> organizers;
	@ManyToMany
	public List<User> followers;
	@OneToMany(mappedBy = "belongsToTopic", cascade = CascadeType.ALL)
	public List<Idea> ideas;
	//public List<Comment> commentsOn;
	//public List<RequestToJoin> requestsToJoin;
	//public List<RequestOfRelationship> relationshipRequests;
	//public List<TopicInvitation> topicInvitations;
	@ManyToOne
	public MainEntity entity;
	@OneToOne
	public Plan plan;

	@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
	 List<Invitation> invitation;
	
	
	public Topic(String n, String d, short p, User c, MainEntity m) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		entity = m;
		//tags = new ArrayList<Tag>();
		//relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		invitation = new ArrayList<Invitation>();
		//commentsOn = new ArrayList<Comment>();
		//requestsToJoin = new ArrayList<RequestToJoin>();
	}
	
	public Topic(String n, String d, short p, User c) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		//tags = new ArrayList<Tag>();
		//relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		invitation = new ArrayList<Invitation>();
		//commentsOn = new ArrayList<Comment>();
		//requestsToJoin = new ArrayList<RequestToJoin>();
	}
	 
	public List<User> getOrganizer(){
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
	
	public void unfollow(User user) {
		followers.remove(user);
	}
}
