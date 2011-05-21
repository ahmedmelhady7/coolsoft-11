package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * This class is responsible for the creation of a certain tag
 * 
 * @author Omar Faruki
 */
@Entity
public class Tag extends CoolModel {

	/**
	 * Tag name
	 */
	@Required
	public String name;
	// @ManyToMany(mappedBy = "relatedTags") Still waiting for the Relationship
	// Class
	// public List<Tag> relatedTags; Sprint 2

	// String creator; We might still need this attribute;

	/**
	 * The list of related tags
	 */

	// public List<Tag> relatedTags;

	/**
	 * List of users following the tag
	 */
	@ManyToMany(mappedBy = "followingTags")
	// , cascade = CascadeType.ALL)
	public List<User> followers;

	/**
	 * Added by Alia, but whoever is responsible for it please check the cascade
	 * etc.
	 */
	@ManyToMany(mappedBy = "tags")
	public List<Topic> taggedTopics;

	/**
	 * List of organizations tagged by the tag
	 */
	@ManyToMany(mappedBy = "relatedTags")
	public List<Organization> organizations;

	/**
	 * List of entities tagged by the tag
	 */
	@ManyToMany(mappedBy = "tagList")
	public List<MainEntity> entities;

	/**
	 * The list of relations where the tag is the source
	 */
	@OneToMany(mappedBy = "source")
	public List<TagRelationship> relationsSource;
	
	@OneToMany(mappedBy = "destination")
	public List<TagRelationship> relationsDestination;
	
	/**
	 * List of items tagged by the tag
	 */
	@ManyToMany(mappedBy = "tags")
	public List<Item> taggedItems;

	@ManyToMany(mappedBy = "tagsList")
	public List<Idea> taggedIdeas;

	/**
	 * List of relationship requests for the Tag
	 */
	@OneToMany(mappedBy = "destinationTag")
	public List<CreateRelationshipRequest> relationshipRequestsDestination;

	@OneToMany(mappedBy = "sourceTag")
	public List<CreateRelationshipRequest> relationshipRequestsSource;
	
	@OneToMany(mappedBy = "destinationTag")
	public List<RenameEndRelationshipRequest> renameEndRelationshipRequest;

	// ERD change : organization can create many tags, a tag can be created in a
	// single org
	/**
	 * The Organization in which the tag was created
	 */
	@ManyToOne
	public Organization createdInOrganization;
	
	/**
	 * The User that created the tag
	 */
	@ManyToOne
	public User creator;

	/**
	 * Tag attributes
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S9
	 * 
	 * @param name
	 *            The name of the created tag
	 * 
	 * @param org
	 *            The organization in which the tag was created
	 */
	public Tag(String name, Organization org, User creator) {
		this.setName(name);
		this.followers = new ArrayList<User>();
		this.organizations = new ArrayList<Organization>();
		this.taggedItems = new ArrayList<Item>();
		this.taggedTopics = new ArrayList<Topic>();
		this.relationsSource = new ArrayList<TagRelationship>();
		this.relationsDestination = new ArrayList<TagRelationship>();
		this.relationshipRequestsSource = new ArrayList<CreateRelationshipRequest>();
		this.relationshipRequestsDestination = new ArrayList<CreateRelationshipRequest>();
		this.taggedIdeas = new ArrayList<Idea>();
		// this.relatedTags = new ArrayList<Tag>();
		// ERD change
		this.createdInOrganization = org;
		this.creator = creator;
//		org.relatedTags.add(this);
	}

	/**
	 * This Method adds a user to the list of followers
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S11
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */
	public void follow(User user) {
		followers.add(user);
		_save();
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Overrides the method toString to return the name of the tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @return String
	 */
	public String toString() {
		return this.name;
	}
}
