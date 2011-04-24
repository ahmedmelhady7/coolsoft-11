package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	public String email;
	public String password;
	public String firstName;
	public String lastName;
	public String username;
	public String country;
	public Date dateofBirth;
	public int communityContributionCounter;
    ArrayList<Role> roles;
	
    @OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
	public ArrayList<Topic> topicsCreated;
	@ManyToMany(mappedBy = "organizers", cascade = CascadeType.PERSIST)
	public ArrayList<Topic> topicsIOrganize;
	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	public ArrayList<Idea> ideasCreated;
	@ManyToMany(mappedBy = "assignee", cascade = CascadeType.PERSIST)
	public ArrayList<Item> itemsAssigned;

	// ArrayList<AssignRequest> assignRequests;
	// @OneToMany (mappedBy = user)
	// ArrayList<NotificationProfile> noficationProfiles;
	// @OneToMany (mappedBy = directedTo)
	// ArrayList<Notification> notifications ;
	// ArrayList<Request> requests;
	// ArrayList<Comment> commentsPosted;
	// ArrayList<LinkDuplicates> linkDuplicates;
	// ArrayList<volunteerRequests> volunteerRequests;
	// ArrayList<Invitation> invitations;
	// ArrayList<RequestToJoin> requestsToJoin;
	// ArrayList<RequestOfRelationship> requestRelationship;
	// ArrayList<TopicInvitation> topicInvitations;

	public User(String email, String password, String firstName,
			String lastName, String username, int communityContributionCounter,
			Date dateofBirth, String country) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;
		roles = new ArrayList<Role>();
		ideasCreated = new ArrayList<Idea>();
		topicsIOrganize = new ArrayList<Topic>();
		topicsCreated = new ArrayList<Topic>();
		// notification= new ArrayList<NotificationProfile>();
		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();
		// assignRequests = new ArrayList<AssignRequests>();
		// volunteerRequests = new ArrayList<volunteerRequests>();
		// invitations = new ArrayList<Invitation>();
		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>() ;

	}

}
