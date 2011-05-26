package models;

import java.nio.MappedByteBuffer;
import java.util.*;

import javax.persistence.*;

import controllers.Notifications;
import controllers.Users;

import play.data.validation.*;

import play.db.jpa.*;

@Entity
public class Topic extends CoolModel {

	/**
	 * the name of the topic
	 */
	@Required
	public String title;

	/**
	 * @author Mohamed Ghanem
	 * 
	 *         Topic initialization date
	 */
	public Date intializedIn;

	/**
	 * the description of the topic
	 */
	@Lob
	@Required
	@MaxSize(10000)
	public String description;

	/**
	 * the privacy level of the topic, restricts who can do what with it
	 */
	@Required
	public int privacyLevel;

	/**
	 * The list of relations where the topic is the source
	 */
	@OneToMany(mappedBy = "source")
	public List<TopicRelationship> relationsSource;

	/**
	 * The list of relations where the topic is the destination
	 */
	@OneToMany(mappedBy = "destination")
	public List<TopicRelationship> relationsDestination;

	/**
	 * the list of tags the topic is tagged with
	 */
	@ManyToMany
	public List<Tag> tags;

	/**
	 * the list of relationships the topic has
	 */
	// public List<Relationship> relationships;

	/**
	 * the creator of the topic
	 */
	// @Required
	@ManyToOne
	public User creator;

	/**
	 * the list of topic organizers
	 */

	// // to be removed >>>>>>>>>>>>>> change
	// @ManyToMany
	// public List<User> organizers;

	/**
	 * the list of followers of the topic
	 */
	@ManyToMany(mappedBy = "topicsIFollow")
	public List<User> followers;

	/**
	 * The list of related topics
	 */
	// no mapping
	// public List<Topic> relatedTopics;

	/*
	 * the list of users that can access the topic
	 */
	// @ManyToMany
	// public List<User> canAccess;

	/**
	 * the list of ideas in the topic
	 */
	@OneToMany(mappedBy = "belongsToTopic")
	// , cascade = CascadeType.ALL)
	public List<Idea> ideas;

	// /**
	// * the list of reporters of the topic
	// */
	// @ManyToMany(mappedBy = "topicsReported")
	// public List<User> reporters;

	/**
	 * the reporters of the topic
	 */

	public String reporters;

	/**
	 * the list of comments on the topic
	 */
	@OneToMany(mappedBy = "commentedTopic")
	// , cascade = CascadeType.ALL)
	public List<Comment> commentsOn;

	/**
	 * the list of requests to join the topic
	 */
	@OneToMany(mappedBy = "topic")
	// , cascade = CascadeType.ALL)
	public List<RequestToJoin> requestsToJoin;

	/**
	 * the list of invitations to join the topic
	 */
	@OneToMany(mappedBy = "topic")
	public List<Invitation> invitations;

	/**
	 * List of relationship requests for the Topic where it's a source.
	 */
	@OneToMany(mappedBy = "sourceTopic")
	public List<CreateRelationshipRequest> relationshipRequestsSource;

	/**
	 * List of relationship requests for the Topic where it's a destination.
	 */
	@OneToMany(mappedBy = "destinationTopic")
	public List<CreateRelationshipRequest> relationshipRequestsDestination;

	// @OneToMany(mappedBy = "destinationTopic")
	// public List<RenameEndRelationshipRequest> renameEndRelationshipRequest;

	/**
	 * the list of invitations to the topic?
	 */
	// public List<TopicInvitation> topicInvitations;

	/**
	 * the entity the topic is in
	 */
	// @Required
	@ManyToOne
	public MainEntity entity;

	/**
	 * the plan that the topic is promoted to
	 */
	@OneToOne
	public Plan plan;

	/*
	 * boolean flag to determine if the topic is closed or not
	 * 
	 * @author Mostafa Aboul Atta
	 */
	public boolean openToEdit;

	/**
	 * This variable checks whether this entity allows the creation of
	 * relationships in it
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S31
	 */
	public boolean createRelationship;

	/**
	 * shows whether the topic is visible or hidden
	 */
	public boolean hidden;

	/**
	 * the user who hid the topic
	 */
	public User hider;

	/**
	 * flag to determine if the topic is a draft or not
	 */
	public boolean isDraft;

	/**
	 * @auther monica counter to check how many times this topic is viewed to be
	 *         used in sorting
	 */
	public int viewed;

	/**
	 * Default constructor that creates a topic with name, description,privacy
	 * level, creator and entity
	 * 
	 * @author Alia el Bolock
	 * 
	 * @param title
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param i
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 * @param entity
	 *            Entity that the topic belongs/added to
	 */
	public Topic(String title, String description, int privacyLevel,
			User creator, MainEntity entity, boolean createRelationship) {
		this.title = title;
		intializedIn = new Date();
		this.description = description;
		this.privacyLevel = privacyLevel;
		this.creator = creator;
		this.entity = entity;
		this.relationsSource = new ArrayList<TopicRelationship>();
		this.relationsDestination = new ArrayList<TopicRelationship>();
		this.relationshipRequestsSource = new ArrayList<CreateRelationshipRequest>();
		this.relationshipRequestsDestination = new ArrayList<CreateRelationshipRequest>();
		this.tags = new ArrayList<Tag>();
		// relationships = new ArrayList<Relationship>();
		// organizers = new ArrayList<User>();
		this.followers = new ArrayList<User>();
		this.ideas = new ArrayList<Idea>();
		// this.reporters = new ArrayList<User>();
		this.commentsOn = new ArrayList<Comment>();
		// canAccess = new ArrayList<User>();
		this.reporters = "";
		this.requestsToJoin = new ArrayList<RequestToJoin>();
		this.createRelationship = createRelationship;
		this.openToEdit = true;
		this.isDraft = false;
		this.viewed = 0;
	}

	/**
	 * Creates a topic with name, description,privacy level and creator
	 * 
	 * @author Alia el Bolock
	 * 
	 * @param title
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param privacyLevel
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 */
	public Topic(String title, String description, short privacyLevel,
			User creator, boolean createRelationship) {
		this.title = title;
		intializedIn = new Date();
		this.description = description;
		this.privacyLevel = privacyLevel;
		this.creator = creator;
		this.relationsSource = new ArrayList<TopicRelationship>();
		this.relationsDestination = new ArrayList<TopicRelationship>();
		this.relationshipRequestsSource = new ArrayList<CreateRelationshipRequest>();
		this.relationshipRequestsDestination = new ArrayList<CreateRelationshipRequest>();
		this.tags = new ArrayList<Tag>();
		// organizers = new ArrayList<User>();
		this.followers = new ArrayList<User>();
		this.ideas = new ArrayList<Idea>();
		this.commentsOn = new ArrayList<Comment>();
		// requestsToJoin = new ArrayList<RequestToJoin>();
		this.hidden = false;
		this.reporters = "";
		this.createRelationship = createRelationship;
		this.openToEdit = true;
		this.isDraft = false;
	}

	/**
	 * @Description Constructor for drafts that creates a topic with name,
	 *              description,privacylevel, creator and entity and sets
	 *              isDraft flag to true
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @param title
	 *            title of the topic
	 * @param description
	 *            description of the topic
	 * @param privacyLevel
	 *            the privacy level of the topic
	 * @param creator
	 *            Author of the topic
	 * @param entity
	 *            Entity that the topic belongs/added to
	 * @param createRelationship
	 *            If it's allowed to have relationships
	 */
	public Topic(String title, String description, int privacyLevel,
			User creator, MainEntity entity, boolean createRelationship,
			boolean isDraft) {

		this(title, description, privacyLevel, creator, entity,
				createRelationship);
		this.isDraft = true;
	}

	/**
	 * returns a list of organizers in a certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S12
	 * 
	 * @return ArrayList<User>
	 */

	public List<User> getOrganizer() {
		// List<UserRoleInOrganization> o = (List<UserRoleInOrganization>)
		// UserRoleInOrganization
		// .find("select uro.enrolled from UserRoleInOrganization uro,Role r where  uro.Role = r and uro.resourceID = ? and r.roleName like ? and uro.resourceType = ? ",
		// this.id, "organizer", "topic");
		//
		List<User> organizer = new ArrayList<User>();
		// for (int i = 0; i < o.size(); i++) {
		// organizer.add((o.get(i).enrolled));
		// }
		// return organizer;

		List<UserRoleInOrganization> o = UserRoleInOrganization.find(
				"byEntityTopicIDAndType", this.entity.id, "entity").fetch();
		for (int i = 0; i < o.size(); i++) {
			if ((o.get(i).role.roleName).equals("organizer")) {
				organizer.add(o.get(i).enrolled);
			}
		}
		return organizer;

	}

	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            the user who follows
	 * 
	 */

	public void unfollow(User user) {
		followers.remove(user);
		_save();
	}

	// /**
	// * This Method returns the list of followers in a certain topic
	// *
	// * @author Omar Faruki
	// *
	// * @story C2S29
	// *
	// * @return ArrayList<User>
	// */
	// public List<User> getFollowers() {
	// return (List<User>) this.followers;
	// }

	/**
	 * This Method overrides the toString method
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S1
	 * 
	 * @return String
	 */
	public String toString() {
		return title;
	}

	/**
	 * This method posts an Idea in a certain topic
	 * 
	 * @author m.hisham.sa
	 * 
	 * @story C2S14
	 * 
	 * @param user
	 *            : the user who posts the idea
	 * @param title
	 *            : the title of the idea
	 * @param description
	 *            : description/content of the idea
	 * 
	 * @return void
	 */

	public void postIdea(User user, String title, String description) {
		Idea idea = new Idea(title, description, user, this);
		this.ideas.add(idea);

	}

	/**
	 * This Method sends a request to the topic organizer and add the request to
	 * the list of requests
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S13
	 * 
	 * @param userId
	 *            the id of the user who request to post
	 */

	public void requestFromUserToPost(long userId) {
		User user = User.findById(userId);
		if (!hasRequest(userId)) {
			RequestToJoin r;
			List<User> organizaers = getOrganizer();
			if (organizaers.size() > 0) {
				r = new RequestToJoin(user, this, this.entity.organization,
						"I would like to post").save();
				for (int i = 0; i > organizaers.size(); i++)
					Notifications.sendNotification(organizaers.get(i).id, id,
							"topic", user.username
									+ " has requested to post on topic "
									+ title);
				Notifications.sendNotification(entity.organization.creator.id,
						id, "topic", user.username
								+ " has requested to post on topic " + title);
			} else {
				r = new RequestToJoin(user, this, this.entity.organization,
						"I would like to post").save();
				Notifications.sendNotification(entity.organization.creator.id,
						id, "topic", user.username
								+ " has requested to post on topic " + title);
			}
			requestsToJoin.add(r);
			_save();
		}
	}

	/**
	 * This Method return true if a user have requested to join this topic
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S13
	 * 
	 * @param userId
	 *            the id of the user
	 * 
	 * @return boolean
	 */

	public boolean hasRequest(long userId) {
		RequestToJoin req;
		for (int i = 0; i < requestsToJoin.size(); i++) {
			req = requestsToJoin.get(i);
			if (req.source.id == userId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a topic can be deleted
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @return boolean
	 */
	public boolean isDeletable() {
		// TODO Auto-generated method stub
		// if (openToEdit == false)
		// return false;
		if (ideas.size() > 0)
			return false;
		return true;
	}

	/*
	 * 
	 * Checks whether a topic can be hidden
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @return boolean
	 * 
	 * public boolean isHideable() { // TODO Auto-generated method stub if
	 * (openToEdit == false) return false;
	 * 
	 * return true; }
	 */

	/**
	 * Checks whether a certain user can view this topic
	 * 
	 * @author Alia El Bolock
	 * 
	 * @param user
	 *            : the user upon which the ckeck should be done
	 * 
	 * @return boolean
	 */
	public boolean canView(User user) {
		boolean canView = false;
		if (Users.isPermitted(user, "view", this.id, "topic"))
			canView = true;
		return canView;
	}

	/**
	 * @author monica yousry this method increments the counter viewed
	 * @return:void
	 */
	public void incrmentViewed() {
		this.viewed++;
	}
}
