package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ManyToAny;

import controllers.Notifications;
import controllers.Users;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.jobs.OnApplicationStart;

/**
 * @author Noha Khater
 * 
 *         The entities and sub-entities in an organization
 */

@Entity
public class MainEntity extends CoolModel {

	/**
	 * Name of the Entity
	 */
	@Required
	public String name;

	/**
	 * @author Mohamed Ghanem
	 * 
	 *         Entity initialization date
	 */
	public Date intializedIn;

	/**
	 * The parent entity in case of a sub-entity
	 */
	@ManyToOne
	public MainEntity parent;

	/**
	 * The description of the entity
	 */
	@Lob
	@Required
	public String description;

	/**
	 * list of relations where the entity is the source
	 */
	@OneToMany(mappedBy = "source")
	public List<EntityRelationship> relationsSource;

	/**
	 * list of relations where the entity is the destination
	 */
	@OneToMany(mappedBy = "destination")
	public List<EntityRelationship> relationsDestination;

	/**
	 * A list of sub-entities of this entity
	 */
	@OneToMany(mappedBy = "parent")
	public List<MainEntity> subentities;

	/**
	 * The followers of the entity
	 */
	@ManyToMany(mappedBy = "followingEntities")
	public List<User> followers;

	/**
	 * The organization of the entity
	 */
	@Required
	@ManyToOne
	public Organization organization;

	/**
	 * The list of topics available in that entity
	 */
	@OneToMany(mappedBy = "entity")
	public List<Topic> topicList;

	/**
	 * The list of tags that the entity is tagged by
	 */
	@ManyToMany
	public List<Tag> tagList;

	@OneToMany(mappedBy = "entity")
	public List<Invitation> invitationList;

	@OneToMany(mappedBy = "entity")
	public List<TopicRequest> topicRequests;

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
	 * List of relationship requests for the Entity
	 */
	@OneToMany(mappedBy = "sourceEntity")
	public List<CreateRelationshipRequest> relationshipRequestsSource;

	@OneToMany(mappedBy = "destinationEntity")
	public List<CreateRelationshipRequest> relationshipRequestsDestination;
	
	@OneToMany(mappedBy = "destinationEntity")
	public List<RenameEndRelationshipRequest> renameEndRelationshipRequest;
	
	/**
	 * The list of items related to this entity
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S17
	 */
	@OneToMany(mappedBy = "relatedEntity")
	public List<Item> relatedItems;

	/**
	 * Default constructor for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S2
	 * 
	 * @param name
	 *            : the name of the entity being created
	 * 
	 * @param description
	 *            : the description of the entity
	 * 
	 * @param org
	 *            : the organization that the entity is created in
	 * 
	 */

	public MainEntity(String name, String description, Organization org,
			boolean createRelationship) {
		this.name = name;
		this.intializedIn = new Date();
		this.description = description;
		this.parent = null;
		this.organization = org;
		org.entitiesList.add(this);
		this.invitationList = new ArrayList<Invitation>();
		this.subentities = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relationsSource = new ArrayList<EntityRelationship>();
		this.relationsDestination = new ArrayList<EntityRelationship>();
		this.relationshipRequestsSource = new ArrayList<CreateRelationshipRequest>();
		this.relationshipRequestsDestination = new ArrayList<CreateRelationshipRequest>();
		this.topicList = new ArrayList<Topic>();
		this.tagList = new ArrayList<Tag>();
		int size = org.followers.size();
		this.createRelationship = createRelationship;
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(org.followers.get(i).id, org.id,
					"organization", "A new entity (" + name
							+ ") has been created in " + org.name);
		}
	}

	/**
	 * Default constructor for a sub-entity for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S20
	 * 
	 * @param name
	 *            : the name of the entity being created
	 * 
	 * @param description
	 *            : the description of the entity
	 * 
	 * @param parent
	 *            : the parent entity of the sub-entity being created
	 * 
	 * @param org
	 *            : the organization that the entity is created in
	 * 
	 */

	public MainEntity(String name, String description, MainEntity parent,
			Organization org, boolean createRelationship) {
		this.name = name;
		this.intializedIn = new Date();
		this.description = description;
		this.parent = parent;
		parent.subentities.add(this);
		this.organization = org;
		org.entitiesList.add(this);
		this.invitationList = new ArrayList<Invitation>();
		this.subentities = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.topicList = new ArrayList<Topic>();
		this.tagList = new ArrayList<Tag>();
		this.relationsSource = new ArrayList<EntityRelationship>();
		this.relationsDestination = new ArrayList<EntityRelationship>();
		this.relationshipRequestsSource = new ArrayList<CreateRelationshipRequest>();
		this.relationshipRequestsDestination = new ArrayList<CreateRelationshipRequest>();
		List<User> receivers = Users.getEntityOrganizers(parent);
		int size = receivers.size();
		this.createRelationship = createRelationship;
		for (int j = 0; j < size; j++) {
			Users.getEntityOrganizers(this).add(receivers.get(j));
		}
		receivers.add(org.creator);
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(receivers.get(i).id, parent.id,
					"entity", "A new subentity (" + name
							+ ") has been created for the entity ("
							+ parent.name + ")");
		}
		size = org.followers.size();
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(org.followers.get(i).id, org.id,
					"organization", "A new entity (" + name
							+ ") has been created in " + org.name);
		}
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
	 * @return void
	 */
	public void unfollow(User user) {
		followers.remove(user);
		_save();
	}

	public String toString() {
		return name;
	}
}
