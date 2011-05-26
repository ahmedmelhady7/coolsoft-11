package models;

/**
 @author Mostafa Ali
 */

// isdelete , state , access plan , topic , idea 
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Security;
import controllers.Topics;
import controllers.Users;
import controllers.VolunteerRequests;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.jobs.OnApplicationStart;
import play.libs.Codec;

@Entity
public class User extends CoolModel {
	@Required
	//@Email
	@Column(unique = true)
	public String email;
	@Required
	@MinSize(3)
	@MaxSize(20)
	@Column(unique = true)
	public String username;
	@Required
	@MinSize(3)
	@MaxSize(25)
	public String password;
	@Required
	public String firstName;
	public String lastName;
	public String country;
	public String dateofBirth;
	public int communityContributionCounter;
	public String activationKey;
	public String confirmPassword;
	/**
	 * Added by Ahmed Maged
	 */
	public String securityQuestion;
	public String answer;
	// added to know whether a user is an Admin or not
	public boolean isAdmin;
	/**
	 * state represents the state of the user whether he is active, deleted, not
	 * active a -> active , d -> deleted , n -> not active
	 */
	public String state;
	public String profession;
	public int notificationsNumber;

	@OneToMany(mappedBy = "creator")
	// , cascade = CascadeType.PERSIST)
	public List<Topic> topicsCreated;

	/**
	 * id of the profile Picture
	 */
	public long profilePictureId;

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>change
	// @ManyToMany(mappedBy = "enrolledUsers")
	// public List<Organization> enrolled;

	@OneToMany(mappedBy = "creator")
	// , cascade = CascadeType.ALL)
	public List<Organization> createdOrganization;

	// // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> change
	// @ManyToMany(mappedBy = "organizers")
	// // , cascade = CascadeType.PERSIST)
	// public List<Topic> topicsIOrganize;

	@ManyToMany(mappedBy = "usersRated")
	public List<Plan> ratedPlans;
	/**
	 * Added by Alia, but whoever is responsible for it please check the cascade
	 * etc.
	 */

	@ManyToMany
	// , cascade = CascadeType.PERSIST)
	public List<Topic> topicsIFollow;

	@OneToMany(mappedBy = "author")
	// , cascade = CascadeType.ALL)
	public List<Idea> ideasCreated;
	/**
	 * ideas reported by the user
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 **/
	@ManyToMany
	public List<Idea> ideasReported;

	@ManyToMany(mappedBy = "usersRated")
	public List<Idea> ideasRated;

	@ManyToMany
	// (cascade = CascadeType.PERSIST)
	public List<Item> itemsAssigned;

	@OneToMany(mappedBy = "user")
	public List<NotificationProfile> notificationProfiles;
	@OneToMany(mappedBy = "directedTo")
	public List<Notification> notifications;

	// code 3'lt
	@ManyToMany
	// (mappedBy = "followers")
	public List<MainEntity> followingEntities;

	// related to sprint 2
	@ManyToMany
	public List<Tag> followingTags;

	@ManyToMany
	// (mappedBy = "followers")
	public List<Organization> followingOrganizations;

	// @ManyToMany(mappedBy = "canAccess", cascade = CascadeType.PERSIST)
	// public List<Topic> accessedTopics;

	// List<Request> requests;
	// List<Comment> commentsPosted;
	// List<LinkDuplicates> linkDuplicates;

	@OneToMany(mappedBy = "source")
	// , cascade = CascadeType.ALL)
	public List<RequestToJoin> requestsToJoin;
	// List<RequestOfRelationship> requestRelationship;
	// List<TopicInvitation> topicInvitations;

	@OneToMany(mappedBy = "sender")
	// , cascade = CascadeType.ALL)
	public List<Invitation> invitation;

	/**
	 *  list of invitations that have been sent by that user
	 */
	@OneToMany(mappedBy = "sender")
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "sender")
	// , cascade = CascadeType.ALL)
	public List<AssignRequest> sentAssignRequests;

	@OneToMany(mappedBy = "destination")
	// , cascade = CascadeType.ALL)
	public List<AssignRequest> receivedAssignRequests;

	@OneToMany(mappedBy = "sender")
	// , cascade = CascadeType.ALL)
	public List<LinkDuplicatesRequest> sentMarkingRequests;

	@OneToMany(mappedBy = "idea1")
	// , cascade = CascadeType.ALL)
	public List<LinkDuplicatesRequest> receivedMarkingRequests;

	@OneToMany(mappedBy = "bannedUser")
	// , cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;

	@OneToMany(mappedBy = "enrolled")
	// , cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRolesInOrganization;

	@OneToMany(mappedBy = "madeBy")
	// , cascade = CascadeType.PERSIST)
	public List<Plan> planscreated;

	@OneToMany(mappedBy = "user")
	public List<Label> myLabels;

	@OneToMany(mappedBy = "requester")
	// , cascade = CascadeType.ALL)
	public List<TopicRequest> topicRequests;

	/**
	 * List of creation relationship requests the user made
	 */
	@OneToMany(mappedBy = "requester")
	public List<CreateRelationshipRequest> myRelationshipRequests;

	/**
	 * List of renaming and ending relationship requests the user made
	 */
	@OneToMany(mappedBy = "requester")
	public List<RenameEndRelationshipRequest> myRenameEndRelationshipRequests;

	/**
	 * The tags that the user created
	 */
	@OneToMany(mappedBy = "creator")
	public List<Tag> createdTags;

	/**
	 * @author Mostafa Ali
	 */
	public User(String email, String username, String password,
			String firstName, String lastName, String securityQuestion, String answer,
			int communityContributionCounter, String dateofBirth, String country,
			String profession) {
		this.email = email;
		this.username = username;
		this.password = Codec.hexMD5(password);
		//this.password =password;
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.securityQuestion = securityQuestion;
		this.answer = answer;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;
		// this.roles = new ArrayList<Role>();
		this.ideasCreated = new ArrayList<Idea>();
		this.itemsAssigned = new ArrayList<Item>();
		// this.topicsIOrganize = new ArrayList<Topic>();
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
		// this.enrolled = new ArrayList<Organization>();
		this.createdOrganization = new ArrayList<Organization>();
		followingOrganizations = new ArrayList<Organization>();
		planscreated = new ArrayList<Plan>();
		this.ideasReported = new ArrayList<Idea>();
//		this.topicsReported = new ArrayList<Topic>();
		followingEntities = new ArrayList<MainEntity>();
		topicsIFollow = new ArrayList<Topic>();
		this.ideasRated = new ArrayList<Idea>();
		this.state = "a";
		this.profession = profession;
		profilePictureId = -1;
		notificationsNumber = notifications.size();
		followingTags = new ArrayList();
		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();

		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>();

	}

	/**
	 * 
	 * Creates a new invitation and adds it to the list of invitations
	 *  of organization/entity/topic
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * @param email
	 *            String email the destination of the invitation
	 * 
	 * @param role
	 *            String role that will be assigned to the user if accept
	 * 
	 * @param organization
	 *            Organization organization that sends the invitation
	 * 
	 * @param entity
	 *            MainEntity entity that sends the invitation
	 * 
	 * @param topic
	 *            Topic topic that sends the invitation
	 * 
	 */

	public void addInvitation(String email, String role,
			Organization organization, MainEntity entity, Topic topic) {

		Invitation invite = new Invitation(email, entity, organization, role,
				this, topic).save();
		this.invitation.add(invite);
		if (topic != null)
			topic.invitations.add(invite);
		else {
			organization.invitation.add(invite);
			if (entity != null)
				entity.invitationList.add(invite);

		}
		this.save();
	}

	/**
	 * 
	 * This Method adds a volunteer request sent by the user to work on a
	 * certain item in a plan to the list of volunteer requests sent by the user
	 * given the volunteer request
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param volunteerRequest
	 *            : the VolunteerRequest that needs to be added to the list of
	 *            volunteer requests of the user
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
	 * @author Salma Osama
	 * 
	 * @story C5S4
	 * 
	 * @param sentAssignRequest
	 *            : the AssignRequest that needs to be added to the list of
	 *            assign requests sent by the user
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
	 * @author Salma Osama
	 * 
	 * @story C5S4
	 * 
	 * @param receivedAssignRequest
	 *            : the AssignRequest that needs to be added to the list of
	 *            assign requests received by the user
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
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param tag
	 *            the tag that the user is following
	 * 
	 */

	public void unfollow(Tag tag) {
		followingTags.remove(tag);
		_save();
	}

	/**
	 * This Method removes a topic from the followers
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param topic
	 *            the topic that the user is following
	 * 
	 */

	public void unfollow(Topic topic) {
		topicsIFollow.remove(topic);
		_save();
	}

	/**
	 * This Method removes a mainEntity from the followers
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param entity
	 *            the MainEntity that the user is following
	 * 
	 */
	public void unfollow(MainEntity entity) {
		followingEntities.remove(entity);
		_save();
	}

	/**
	 * This Method removes a organization from the followers
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param organization
	 *            the Organization that the user is following
	 */
	public void unfollow(Organization organization) {
		followingOrganizations.remove(organization);
		_save();
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
		if (Users
				.isPermitted(
						this,
						"accept/Reject user request to volunteer to work on action item in a plan",
						item.plan.topic.id, "topic")) {
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

	/**
	 * 
	 * This Method checks if the user can directly assign himself to work on an
	 * item given the item id
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param itemId
	 *            : the id of the item that the user is checking if he can
	 *            assign himself to to work on it
	 * @return boolean
	 */
	public boolean canWork(long itemId) {
		Item item = Item.findById(itemId);

		if (Users
				.isPermitted(
						this,
						"accept/Reject user request to volunteer to work on action item in a plan",
						item.plan.topic.id, "topic")) {
			// if (!Topics.searchByTopic(item.plan.topic.id).contains(this)) {
			// return false;
			// }
			if (item.assignees.contains(this)) {

				return false;
			} else {

				for (int i = 0; i < item.assignRequests.size(); i++) {
					if (this.id == item.assignRequests.get(i).destination.id) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * This Method checks if the user has sent a volunteer request and it's
	 * still pending
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param itemId
	 *            : the id of the item that the user is checking if he can
	 *            assign himself to to work on it
	 * @return boolean
	 */
	public boolean pendingVolunteerRequest(long itemId) {

		for (VolunteerRequest volunteerRequest : volunteerRequests) {
			if (volunteerRequest.destination.id == itemId) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Idea> getDrafts() {
		List<Idea> drafts = new ArrayList<Idea>();

		for (Idea idea : this.ideasCreated)
			if (idea.isDraft)
				drafts.add(idea);

		return (ArrayList<Idea>) drafts;
	}

	public int getDraftsCount() {
		return this.getDrafts().size();
	}

	/**
	 * returns the number of new notifications that the user has.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * 
	 * @return int
	 * 			the number of notifications that the user has
	 */
	
	public int getNotificationNumber() {
		User user = Security.getConnected();
		int notificationCount = 0;
		for (int i = 0; i < user.notifications.size(); i++) {
			if (!user.notifications.get(i).seen) {
				notificationCount++;
			}
		}
		return notificationCount;
	}

	/**
	 * Returns the latest notifications
	 * i.e. having their status as new
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * @return List
	 * 			List<Notification> the list of the latest notifications
	 */
	
	public List<Notification> getLatest() {
		User user = Security.getConnected();
		List<Notification> list = new ArrayList<Notification>();
		for (int i = 0; i < user.notifications.size(); i++) {
			if (user.notifications.get(i).status.equals("New")) {
				list.add(user.notifications.get(i));
			}
		}
		return list;
	}		

	
	/**
	 * gets the list of documents owned by the user 
	 * 
	 * @author Ibrahim Al-Kahayat
	 * 
	 * @story C2S28
	 * 
	 * @return List<Document>
	 * 
	 */
	public List<Document> getDocuments() {
		List<Document> documents = Document.find("byUserOrganizationId", id).fetch();
		for (int i = 0; i < documents.size(); i++) {
			if (documents.get(i).isOrganization) {
				documents.remove(i);
			}
		}
		return documents;
	}

	/**
	 * This method gets all Organizations that the user is enrolled in
	 * 
	 * @author Omar Faruki
	 * 
	 * @return List<Organization>
	 */
	public List<Organization> getOrganizations() {
		List<Organization> organizations = new ArrayList<Organization>();
		int i = 0;
		while (i < this.userRolesInOrganization.size()) {
				organizations.add(userRolesInOrganization.get(i).organization);
			i++;
		}
		return organizations;
	}

}
