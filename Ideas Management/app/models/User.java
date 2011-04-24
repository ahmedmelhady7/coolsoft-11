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
	public ArrayList<Role> roles;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
	public ArrayList<Topic> topicsCreated;
	@ManyToMany(mappedBy = "organizers", cascade = CascadeType.PERSIST)
	public ArrayList<Topic> topicsIOrganize;
	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	public ArrayList<Idea> ideasCreated;
	@ManyToMany(mappedBy = "assignee", cascade = CascadeType.PERSIST)
	public ArrayList<Item> itemsAssigned;

	public ArrayList<AssignRequest> assignRequests;

	@OneToMany(mappedBy = "user")
	ArrayList<NotificationProfile> notificationProfiles;
	@OneToMany(mappedBy = "directedTo")
	ArrayList<Notification> notifications;
	// ArrayList<Request> requests;
	// ArrayList<Comment> commentsPosted;
	// ArrayList<LinkDuplicates> linkDuplicates;
	// ArrayList<Invitation> invitations;
	// ArrayList<RequestToJoin> requestsToJoin;
	// ArrayList<RequestOfRelationship> requestRelationship;
	// ArrayList<TopicInvitation> topicInvitations;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	public ArrayList<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	public ArrayList<AssignRequest> sentAssignRequests;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	public ArrayList<AssignRequest> receivedAssignRequests;

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
		this.roles = new ArrayList<Role>();
		this.ideasCreated = new ArrayList<Idea>();
		this.topicsIOrganize = new ArrayList<Topic>();
		this.topicsCreated = new ArrayList<Topic>();
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.sentAssignRequests = new ArrayList<AssignRequest>();
		this.receivedAssignRequests = new ArrayList<AssignRequest>();
		notificationProfiles = new ArrayList<NotificationProfile>();
		notifications = new ArrayList<Notification>();
		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();
		// invitations = new ArrayList<Invitation>();
		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>() ;

	}
}
