package models;

// isdelete , state , access plan , topic , idea 
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import controllers.Topics;
import controllers.VolunteerRequests;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	@Required
	@Email
	public String email;
	@Required
	@Column(unique = true)
	@MaxSize(10)
	public String username;
	@Required
	@MaxSize(25)
	@MinSize(4)
	public String password;
	@Required
	public String firstName;
	public String lastName;
	public String country;
	public Date dateofBirth;
	public int communityContributionCounter;
	// added to know whether a user is an Admin or not
	public boolean isAdmin;
	/**
	 * state represents the state of the user whether he is active, deleted, not
	 * active a -> active , d -> deleted , n -> not active
	 */
	public char state;
	public String profession;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
	public List<Topic> topicsCreated;

	// to be removed
	@ManyToMany
	public List<Organization> enrolled;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	public List<Organization> createdOrganization;

	// to be removed
	@ManyToMany(mappedBy = "organizers", cascade = CascadeType.PERSIST)
	public List<Topic> topicsIOrganize;

	/**
	 * Added by Alia, but whoever is responsible for it please check the cascade
	 * etc.
	 */

	@ManyToMany(mappedBy = "followers", cascade = CascadeType.PERSIST)
	public List<Topic> topicsIFollow;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	public List<Idea> ideasCreated;

	@ManyToMany(mappedBy = "reporters", cascade = CascadeType.ALL)
	public List<Idea> ideasReported;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public List<Item> itemsAssigned;

	@OneToMany(mappedBy = "user")
	public List<NotificationProfile> notificationProfiles;
	@OneToMany(mappedBy = "directedTo")
	public List<Notification> notifications;
	@ManyToMany
	public List<MainEntity> followingEntities;

	// related to sprint 2
	@ManyToMany
	public List<Tag> followingTags;

	@ManyToMany
	public List<Organization> followingOrganizations;

	// to be removed
	@ManyToMany
	public List<MainEntity> entitiesIOrganize;

	// @ManyToMany(mappedBy = "canAccess", cascade = CascadeType.PERSIST)
	// public List<Topic> accessedTopics;

	// List<Request> requests;
	// List<Comment> commentsPosted;
	// List<LinkDuplicates> linkDuplicates;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<RequestToJoin> requestsToJoin;
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

	@OneToMany(mappedBy = "madeBy", cascade = CascadeType.PERSIST)
	public List<Plan> planscreated;

	public User(String email, String password, String firstName,
			String lastName, String username, int communityContributionCounter,
			Date dateofBirth, String country, String profession) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;
		// this.roles = new ArrayList<Role>();
		this.ideasCreated = new ArrayList<Idea>();
		this.itemsAssigned = new ArrayList<Item>();
		this.topicsIOrganize = new ArrayList<Topic>();
		this.topicsCreated = new ArrayList<Topic>();
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.sentAssignRequests = new ArrayList<AssignRequest>();
		this.receivedAssignRequests = new ArrayList<AssignRequest>();
		// this.accessedTopics = new ArrayList<Topic>();
		notificationProfiles = new ArrayList<NotificationProfile>();
		notifications = new ArrayList<Notification>();
		bannedUsers = new ArrayList<BannedUser>();
		// added
		userRolesInOrganization = new ArrayList<UserRoleInOrganization>();
		invitation = new ArrayList<Invitation>();
		this.enrolled = new ArrayList<Organization>();
		this.createdOrganization = new ArrayList<Organization>();
		followingOrganizations = new ArrayList<Organization>();
		planscreated = new ArrayList<Plan>();

		followingEntities = new ArrayList<MainEntity>();

		this.state = 'a';
		this.profession = profession;

		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();

		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>();

	}

	/**
	 * 
	 * this method posts an Idea in a certain topic
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S14
	 * 
	 * @param topic
	 *            : the topic which the idea is being post under
	 * @param title
	 *            : the title of the idea
	 * @param description
	 *            : description/content of the idea
	 * 
	 * @return void
	 */

	public void postIdea(Topic topic, String title, String description) {
		Idea idea = new Idea(title, description, this, topic);
		idea.privacyLevel = topic.privacyLevel;
		ideasCreated.add(idea);
		_save();
	}

	public void addInvitation(String email, String role,
			Organization organization, MainEntity entity) {

		Invitation invite = new Invitation(email, entity, organization, role,
				this).save();
		this.invitation.add(invite);
		organization.invitation.add(invite);
		entity.invitationList.add(invite);
		this.save();
	}

	/**
	 * 
	 * This Method adds a volunteer request sent by the user to work on a
	 * certain item in a plan to the list of volunteer requests sent by the user
	 * given the volunteer request
	 * 
	 * @author salma.qayed
	 * 
	 * @story C5S10
	 * 
	 * @param volunteerRequest
	 *            : the VolunteerRequest that needs to be added to the list of
	 *            volunteer requests of the user
	 * 
	 * @return void
	 */

	public void addVolunteerRequest(VolunteerRequest volunteerRequest) {
		volunteerRequests.add(volunteerRequest);
		this.save();

	}

	/**
	 * 
	 * This Method adds an assign request sent by the user to another user
	 * assigning him to a certain item in a plan to the list of assign requests
	 * sent by the user given the assign request
	 * 
	 * @author salma.qayed
	 * 
	 * @story C5S4
	 * 
	 * @param sentAssignRequest
	 *            : the AssignRequest that needs to be added to the list of
	 *            assign requests sent by the user
	 * 
	 * @return void
	 */
	public void addSentAssignRequest(AssignRequest sentAssignRequest) {
		sentAssignRequests.add(sentAssignRequest);
		this.save();
	}

	/**
	 * 
	 * This Method adds an assign request received by the user to work on a
	 * certain item in a plan to the list of assign requests received by the
	 * user given the assign request
	 * 
	 * @author salma.qayed
	 * 
	 * @story C5S4
	 * 
	 * @param receivedAssignRequest
	 *            : the AssignRequest that needs to be added to the list of
	 *            assign requests received by the user
	 * 
	 * @return void
	 */
	public void addReceivedAssignRequest(AssignRequest receivedAssignRequest) {
		sentAssignRequests.add(receivedAssignRequest);
		this.save();
	}

	/**
	 * This Method adds a tag to the list of tags followed
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S11
	 * 
	 * @param tag
	 *            : the tag that the user wants to follow
	 * 
	 * @return void
	 */

	public void follow(Tag tag) {
		followingTags.add(tag);
		_save();
	}

	/**
	 * This Method removes a tag from the followers list
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param t
	 *            : the tag that the user is following
	 * 
	 * @return void
	 */

	public void unfollow(Tag t) {
		followingTags.remove(t);
		_save();
	}

	/**
	 * This Method removes a topic from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param t
	 *            : the topic that the user is following
	 * 
	 * @return void
	 */

	public void unfollow(Topic t) {
		topicsIFollow.remove(t);
		_save();
	}

	/**
	 * This Method removes a mainEntity from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param m
	 *            : the MainEntity that the user is following
	 * 
	 * @return void
	 */
	public void unfollow(MainEntity m) {
		followingEntities.remove(m);
		_save();
	}

	/**
	 * This Method removes a organization from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param o
	 *            : the Organization that the user is following
	 * 
	 * @return void
	 */
	public void unfollow(Organization o) {
		followingOrganizations.remove(o);
		_save();
	}

	/**
	 * This Method returns all the users notifications
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @return List of notifications
	 */

	public List<Notification> openNotifications() {
		return notifications;
	}

	/**
	 * This Method returns all the users notification profiles
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @return List of notification profiles
	 */

	public List<NotificationProfile> openNotificationProfile() {
		return notificationProfiles;
	}

	/**
	 * This Method overrides method equals that compares two users by comparing
	 * ther ids
	 * 
	 * @author fadwa sakr
	 * 
	 * @param o
	 *            : the object to be compared with
	 */

	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			return id == user.id;
		}

		return false;

	}

	/**
	 * Override the toString method to see the name & the username
	 * 
	 * @author Mostafa Ali
	 * 
	 * @return String
	 */
	public String toString() {
		return this.firstName + " " + this.lastName + "\t" + "username "
				+ this.username;
	}

	/**
	 * 
	 * This Method checks if the user can send a volunteer request to work on
	 * the item given the item id
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param itemId
	 *            : the id of the item that the user is checking if he can
	 *            volunteer to work on
	 * @return boolean
	 */
	public boolean canVolunteer(long itemId) {

		Item item = Item.findById(itemId);
		if (!Topics.searchByTopic(item.plan.topic.id).contains(this)) {
			return false;
		}
		if (item.assignees.contains(this)) {

			return false;
		} else {

			for (int i = 0; i < item.volunteerRequests.size(); i++) {
				if (this.id == item.volunteerRequests.get(i).sender.id) {
					return false;
				}
			}

			for (int i = 0; i < item.assignRequests.size(); i++) {
				if (this.id == item.assignRequests.get(i).destination.id) {
					return false;
				}
			}
		}
		return true;

	}

}
