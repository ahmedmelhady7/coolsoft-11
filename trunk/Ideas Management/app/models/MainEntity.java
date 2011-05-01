package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Noha Khater
 * 
 * The entities and sub-entities in an organization
 */

@Entity
public class MainEntity extends Model {

	/**
	 * Name of the Entity
	 */
	@Required
	public String name;

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
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	public List<MainEntity> subentities;
	
	/**
	 * The followers of the entity
	 */
	@ManyToMany(mappedBy = "followingEntities", cascade = CascadeType.ALL)
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
	@OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
	public List<Topic> topicList;
	
	/**
	 * The list of tags that the entity is tagged by
	 */
	@ManyToMany(mappedBy = "entities", cascade = CascadeType.ALL)
	public List<Tag> tagList;
	
	//Entity does not need an invitation!! Should be removed (Noha)
	@OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
	public List<Invitation> invitationList;
	
	//to be removed
	@ManyToMany(mappedBy = "entitiesIOrganize", cascade = CascadeType.ALL)
	public List<User> organizers;

	// ArrayList<Relationship> relationshipList;
	// ArrayList<Request> requestList;
	// Arraylist<RequestOfRelationship>

	/**
	 * Default constructor for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @stroy  C2S2
	 * 
	 * @param  n: the name of the entity being created
	 * 
	 * @param  d: the description of the entity 
	 * 
	 * @param  org: the organization that the entity is created in
	 * 
	 */
	
	public MainEntity(String n, String d, Organization org) {
		this.name = n;
		this.description = d;
		this.parent = null;
		this.organization = org;
		org.entitiesList.add(this);
		invitationList = new ArrayList<Invitation>();
		subentities = new ArrayList<MainEntity>();
		followers = new ArrayList<User>();
		topicList = new ArrayList<Topic>();
		tagList = new ArrayList<Tag>();
		organizers = new ArrayList<User>();

	}

	/**
	 * Default constructor for a sub-entity for an entity within an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @stroy  C2S20
	 * 
	 * @param  n: the name of the entity being created
	 * 
	 * @param  d: the description of the entity 
	 *  
	 * @param  parent: the parent entity of the sub-entity being created
	 * 
	 * @param  org: the organization that the entity is created in
	 * 
	 */
	
	public MainEntity(String n, String d, 
			MainEntity parent, Organization org) {
		this.name = n;
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
		organizers = new ArrayList<User>();
	}

	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */
	public void unfollow(User user) {
		followers.remove(user);
		_save();
	}
}
