package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;

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
	public List<Role> roles;
	// added to know whether a user is an Admin or not
	boolean isAdmin;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
	public List<Topic> topicsCreated;

	@ManyToMany(mappedBy = "organizers", cascade = CascadeType.PERSIST)
	public List<Topic> topicsIOrganize;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	public List<Idea> ideasCreated;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public List<Item> itemsAssigned;

	@OneToMany(mappedBy = "user")
	public List<NotificationProfile> notificationProfiles;
	@OneToMany(mappedBy = "directedTo")
	public List<Notification> notifications;
	@ManyToMany
	List<MainEntity> followingEntities;
	@ManyToMany
	public List<Tag> followingTags;
	@ManyToMany
	public List<Organization> followingOrganizations;

	// List<Request> requests;
	// List<Comment> commentsPosted;
	// List<LinkDuplicates> linkDuplicates;
	
	// List<RequestToJoin> requestsToJoin;
	// List<RequestOfRelationship> requestRelationship;
	// List<TopicInvitation> topicInvitations;
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<Invitation> invitation;
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<AssignRequest> sentAssignRequests;

	@OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
	public List<AssignRequest> receivedAssignRequests;

	@OneToMany(mappedBy = "bannedUser", cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;

	@OneToMany(mappedBy = "enrolled", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRolesInOrganization;

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
		this.itemsAssigned = new ArrayList<Item>();
		this.topicsIOrganize = new ArrayList<Topic>();
		this.topicsCreated = new ArrayList<Topic>();
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.sentAssignRequests = new ArrayList<AssignRequest>();
		this.receivedAssignRequests = new ArrayList<AssignRequest>();
		notificationProfiles = new ArrayList<NotificationProfile>();
		notifications = new ArrayList<Notification>();
		bannedUsers = new ArrayList<BannedUser>();
		invitation = new ArrayList<Invitation>();
		
		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();
		
		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>();

	}
}
