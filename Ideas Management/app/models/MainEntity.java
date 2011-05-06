package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Notifications;
import controllers.Users;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Noha Khater
 * 
 *         The entities and sub-entities in an organization
 */

@Entity
public class MainEntity extends Model {

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
	 * The list of related entities
	 */
	// public List<MainEntity> relatedEntities;

	/**
	 * The list of tags that the entity is tagged by
	 */
	@ManyToMany
	public List<Tag> tagList;

	@OneToMany(mappedBy = "entity")
	public List<Invitation> invitationList;

	// ArrayList<Relationship> relationshipList;
	// ArrayList<Request> requestList;
	// Arraylist<RequestOfRelationship>

	/**
	 * Default constructor for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @stroy C2S2
	 * 
	 * @param n
	 *            : the name of the entity being created
	 * 
	 * @param d
	 *            : the description of the entity
	 * 
	 * @param org
	 *            : the organization that the entity is created in
	 * 
	 */

	public MainEntity(String n, String d, Organization org) {
		this.name = n;
		intializedIn = new Date();
		this.description = d;
		this.parent = null;
		this.organization = org;
		org.entitiesList.add(this);
		invitationList = new ArrayList<Invitation>();
		subentities = new ArrayList<MainEntity>();
		followers = new ArrayList<User>();
		topicList = new ArrayList<Topic>();
		tagList = new ArrayList<Tag>();
		int size = org.followers.size();
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(org.followers.get(i).id, org.id,
					"organization", "A new entity (" + n
							+ ") has been created in " + org.name);
		}
	}

	/**
	 * Default constructor for a sub-entity for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @stroy C2S20
	 * 
	 * @param n
	 *            : the name of the entity being created
	 * 
	 * @param d
	 *            : the description of the entity
	 * 
	 * @param parent
	 *            : the parent entity of the sub-entity being created
	 * 
	 * @param org
	 *            : the organization that the entity is created in
	 * 
	 */

	public MainEntity(String n, String d, MainEntity parent, Organization org) {
		this.name = n;
		intializedIn = new Date();
		this.description = d;
		this.parent = parent;
		parent.subentities.add(this);
		this.organization = org;
		org.entitiesList.add(this);
		invitationList = new ArrayList<Invitation>();
		subentities = new ArrayList<MainEntity>();
		followers = new ArrayList<User>();
		topicList = new ArrayList<Topic>();
		tagList = new ArrayList<Tag>();
		List<User> receivers = Users.getEntityOrganizers(parent);
		int size = receivers.size();
		for (int j = 0; j < size; j++) {
			Users.getEntityOrganizers(this).add(receivers.get(j));
		}
		receivers.add(org.creator);
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(receivers.get(i).id, parent.id,
					"entity", "A new subentity (" + n
							+ ") has been created for the entity ("
							+ parent.name + ")");
		}
		size = org.followers.size();
		for (int i = 0; i < size; i++) {
			Notifications.sendNotification(org.followers.get(i).id, org.id,
					"organization", "A new entity (" + n
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
