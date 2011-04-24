package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Topic extends Model {
	public String name;
	public String description;
	public short privacyLevel;// I don't know the type of this attribute
	//public ArrayList<Tag> tags;
	//public ArrayList<Relationship> relationships;

	@ManyToOne
	public User creator;// add between topic and user (post) exactly one user
						// can post many topics

	@ManyToMany
	public ArrayList<User> organizers;// topic belongs to which entity and get
										// the list of its organizers
	public ArrayList<Idea> ideas;
	//public ArrayList<Comment> commentsOn;
	//public ArrayList<RequestToJoin> requestsToJoin;
	//public ArrayList<RequestOfRelationship> relationshipRequests;
	//public ArrayList<TopicInvitation> topicInvitations;
	//public ArrayList<OrganizationEntity> entities;// change name of class Entity
													// as it leads to conflicts
													// with the Entity in java
													// in persistence
													// add this attribute
	public ArrayList<Plan> plans;// add this attribute

	public Topic(String n, String d, short p, User c) {
		name = n;
		description = d;
		privacyLevel = p;
		creator = c;
		//tags = new ArrayList<Tag>();
		//relationships = new ArrayList<Relationship>();
		organizers = new ArrayList<User>();
		ideas = new ArrayList<Idea>();
		//commentsOn = new ArrayList<Comment>();
		//requestsToJoin = new ArrayList<RequestToJoin>();
	}
	 public ArrayList<User> getOrganizer() {
		 return organizers;
		 
	 }

}
