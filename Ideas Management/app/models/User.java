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
	/**
	 * user's email
	 */
	public String email;
	
	/**
	 * user's username
	 */
	@Required
	@MinSize(3)
	@MaxSize(20)
	@Column(unique = true)
	public String username;
	
	/**
	 * user's password
	 */
	@Required
	@MinSize(3)
	public String password;
	
	/**
	 * user's first name
	 */
	@Required
	public String firstName;
	/**
	 * user's last name
	 */
	public String lastName;
	/**
	 * user's country
	 */
	public String country;
	/**
	 * user's date of birth
	 */
	public String dateofBirth;
	/**
	 * user's counter of contributions in the site
	 */
	public int communityContributionCounter;
	/**
	 * user's activation key that is needed to activate the account
	 */
	public String activationKey;
	/**
	 * 
	 */
	public String confirmPassword;
	/**
	 *  user's security question that is needed when forgetting password
	 */
	@Required
	public String securityQuestion;
	/**
	 * answer of the security question
	 */
	@Required
	public String answer;
	/**
	 *  whether a user is an Admin or not
	 */
	public boolean isAdmin;
	/**
	 * state represents the state of the user whether he is active, deleted, not
	 * active a -> active , d -> deleted , n -> not active
	 */
	public String state;
	/**
	 * user's profession
	 */
	public String profession;
	/**
	 * user's number of notifications
	 */
	public int notificationsNumber;
     
	/**
	 *  list of topics craeted by the user
	 */
	@OneToMany(mappedBy = "creator")
	public List<Topic> topicsCreated;

	/**
	 * id of the user's profile Picture
	 */
	public long profilePictureId;

	/**
	 * list of organizations created by the user
	 */
	@OneToMany(mappedBy = "creator")
	public List<Organization> createdOrganization;

	/**
	 * list of comments created by the user
	 */
	@OneToMany(mappedBy = "commenter")
	public List<Comment> hisComments;
	
	/**
	 * list of plans rated by the user
	 */
	@ManyToMany(mappedBy = "usersRated")
	public List<Plan> ratedPlans;
	/**
	 * list of topics that is followed by the user
	 */
	@ManyToMany
	public List<Topic> topicsIFollow;
	/**
	 * list of ideas created by the user
	 */
	@OneToMany(mappedBy = "author")
	public List<Idea> ideasCreated;
	/**
	 * list of ideas reported by the user
	 * 
	 **/
	@ManyToMany
	public List<Idea> ideasReported;
	/**
	 * list of ideas rated by the user
	 */
	@ManyToMany(mappedBy = "usersRated")
	public List<Idea> ideasRated;
	/**
	 * list of items assigned to the user
	 */
	@ManyToMany
	public List<Item> itemsAssigned;
	/**
	 * list of user's notifications profile
	 */
	@OneToMany(mappedBy = "user")
	public List<NotificationProfile> notificationProfiles;
	/**
	 * list of user's notifications
	 */
	@OneToMany(mappedBy = "directedTo")
	public List<Notification> notifications;

	/**
	 * list of entities followed by the user
	 */
	@ManyToMany
	public List<MainEntity> followingEntities;

	/**
	 * list of tags followed by the user
	 */
	@ManyToMany
	public List<Tag> followingTags;

	/**
	 * list of organization followed by the user
	 */
	@ManyToMany
	public List<Organization> followingOrganizations;

	/**
	 * list of requests to join organization sent by the user
	 */
	@OneToMany(mappedBy = "source")
	public List<RequestToJoin> requestsToJoin;
	/**
	 *  list of invitations sent by the user
	 */
	@OneToMany(mappedBy = "sender")
	public List<Invitation> invitation;

	/**
	 *  list of volunteer requests sent by the user
	 */
	@OneToMany(mappedBy = "sender")
	public List<VolunteerRequest> volunteerRequests;
	/**
	 * list of assigned request sent by the user
	 */
	@OneToMany(mappedBy = "sender")
	public List<AssignRequest> sentAssignRequests;
	/**
	 * list of assigned request sent to the user
	 */
	@OneToMany(mappedBy = "destination")
	public List<AssignRequest> receivedAssignRequests;
	/**
	 * list of link-duplicate request sent by the user
	 */
	@OneToMany(mappedBy = "sender")
	public List<LinkDuplicatesRequest> sentMarkingRequests;
	/**
	 * list of link-duplicate request sent tothe user
	 */
	@OneToMany(mappedBy = "idea1")
	public List<LinkDuplicatesRequest> receivedMarkingRequests;
	/**
	 * list of bannedUser that have been banned by the user
	 */
	@OneToMany(mappedBy = "bannedUser")
	public List<BannedUser> bannedUsers;
	/**
	 * list of roles that the user has in each organization
	 */
	@OneToMany(mappedBy = "enrolled")
	public List<UserRoleInOrganization> userRolesInOrganization;
	/**
	 * list of plans created by the user
	 */
	@OneToMany(mappedBy = "madeBy")
	public List<Plan> planscreated;
	/**
	 * list of labels owned by the user
	 */
	@OneToMany(mappedBy = "user")
	public List<Label> myLabels;
	/**
	 * list of topic request sent by the user
	 */
	@OneToMany(mappedBy = "requester")
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
	 * 
	 * adds an unregistered to the database 
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S10
	 * 
	 * @param email
	 *            :String , the user's email
	 *            
	 * @param username
	 *            : String ,the user's username
	 * 
	 * @param password
	 *            : String ,the user's password
	 * 
	 * 
	 * @param firstName
	 *            : String ,the user's first name
	 * 
	 * @param lastName
	 *            : String ,the user's last name
	 * 
	 * @param securityQuestion 
	 * 				String , security question of the user
	 * 
	 * @param answer
	 * 				String , the security answer of the user
	 * 
	 * 
	 * @param communityContributionCounter
	 *            : int the user's community contribution counter
	 * 
	 * @param dateofBirth
	 *            : String ,the user's date of birth
	 * 
	 * @param country
	 *            : String ,the user's country
	 * 
	 * @param profession
	 *            : String ,the user's profession
	 * 
	 */
	public User(String email, String username, String password,
			String firstName, String lastName, String securityQuestion, String answer,
			int communityContributionCounter, String dateofBirth, String country,
			String profession) {
		this.email = email;
		this.username = username;
		this.password = Codec.hexMD5(password);
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.securityQuestion = securityQuestion;
		this.answer = answer;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;
		this.ideasCreated = new ArrayList<Idea>();
		this.itemsAssigned = new ArrayList<Item>();
		this.topicsCreated = new ArrayList<Topic>();
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.sentAssignRequests = new ArrayList<AssignRequest>();
		this.receivedAssignRequests = new ArrayList<AssignRequest>();
		notificationProfiles = new ArrayList<NotificationProfile>();
		notifications = new ArrayList<Notification>();
		bannedUsers = new ArrayList<BannedUser>();
		this.state="a";
		userRolesInOrganization = new ArrayList<UserRoleInOrganization>();
		invitation = new ArrayList<Invitation>();
		this.createdOrganization = new ArrayList<Organization>();
		followingOrganizations = new ArrayList<Organization>();
		planscreated = new ArrayList<Plan>();
		this.ideasReported = new ArrayList<Idea>();
		followingEntities = new ArrayList<MainEntity>();
		topicsIFollow = new ArrayList<Topic>();
		this.ideasRated = new ArrayList<Idea>();
		this.profession = profession;
		profilePictureId = -1;
		notificationsNumber = notifications.size();
		followingTags = new ArrayList();


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
		
		organization.invitation.add(invite);
		if (topic != null)
			topic.invitations.add(invite);
		if (entity != null)
			entity.invitationList.add(invite);

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
		receivedAssignRequests.add(receivedAssignRequest);
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
	 * @story C1S9
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
	
	public int topicDraftsCount()
	{
	
		List<Topic> drafts = new ArrayList<Topic>();

		for (Topic topic : this.topicsCreated)
			if (topic.isDraft)
				drafts.add(topic);

		return drafts.size();
	}

	public int getDraftsCount() {
		return this.getDrafts().size()+topicDraftsCount();
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
		int notificationCount = 0;
		for (int i = 0; i < this.notifications.size(); i++) {
			if (!this.notifications.get(i).seen) {
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
	 * @return List<Notification>
	 * 			       the list of the user's latest notifications
	 */
	
	public List<Notification> getLatest() {		
		List<Notification> list = new ArrayList<Notification>();
		int counter = 0;
		for (int i = 0; i < this.notifications.size(); i++) {
			if (this.notifications.get(i).status.equals("New")) {
				list.add(this.notifications.get(i));
				counter++;
				if (counter == 5) {
					break;
				}
			}
		}
		List<Notification> reversedList = new ArrayList<Notification>();
		for (int i = list.size() - 1; i >= 0; i--) {
			reversedList.add(list.get(i));
		}
		return reversedList;
	}
	
	/**
	 * returns a list of the recent five logs of the user
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story CSS design
	 * 
	 * @return List<Log>
	 * 			the list of the last five logs (actions) done by the user.
	 */
	
	public List<Log> getRecentActivity() {
		List<Log> list = new ArrayList<Log>();
		int counter = 0;
		for (int i = 0; i < this.logs.size(); i++) {
			list.add(this.logs.get(i));
			counter++;
			if (counter == 5) {
				break;
			}
		}		
		List<Log> reversedList = new ArrayList<Log>();
		for (int i = list.size() - 1; i >= 0; i--) {
			reversedList.add(list.get(i));
		}
		return reversedList;		
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
			if(!organizations.contains(userRolesInOrganization.get(i).organization))
				organizations.add(userRolesInOrganization.get(i).organization);
			i++;
		}
		return organizations;
	}

}
