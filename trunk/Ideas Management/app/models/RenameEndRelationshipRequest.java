package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import controllers.Users;

import play.data.validation.Required;

/**
 * @author Noha Khater
 * 
 *         The Relationship Requests to rename or delete a relationship between
 *         entities, topics or tags.
 */

@Entity
public class RenameEndRelationshipRequest extends CoolModel {

	/**
	 * The user who made the relationship request
	 */
	@Required
	@ManyToOne
	public User requester;
	
	@Required
	@ManyToOne
	public Organization organisation;

	/**
	 * The entity relationship to be renamed or deleted
	 */
	@Required
	@ManyToOne
	public EntityRelationship entityRelationship;

	/**
	 * The topic relationship to be renamed or deleted
	 */
	@Required
	@ManyToOne
	public TopicRelationship topicRelationship;

//	/**
//	 * The tag relationship to be renamed or deleted
//	 */
//	@Required
//	@ManyToOne
//	public TagRelationship tagRelationship;

	/**
	 * The new name for renaming the relationship
	 */
	@Required
	public String newName;

	/**
	 * The type of the request (0=delete, 1=rename)
	 */
	@Required
	public int requestType;
	
	/**
	 * The type of the request (0=entity, 1=topic)
	 */
	@Required
	public int type;

//	@ManyToOne
//	public MainEntity destinationEntity;
//
//	@ManyToOne
//	public Topic destinationTopic;
//
//	@ManyToOne
//	public Tag destinationTag;

	/**
	 * Default constructor for the request to rename or delete an entity
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the request
	 * 
	 * @param entityRelationship
	 *            the entity relationship to be renamed or deleted
	 * 
	 * @param requestType
	 *            the type of the request (rename or delete)
	 * 
	 * @param name
	 *            the new name for renaming
	 * 
	 */
	public RenameEndRelationshipRequest(User requester, Organization organisation,
			EntityRelationship entityRelationship, int type, 
			int requestType, String name) {
		this.requester = requester;
		this.organisation = organisation;
		this.entityRelationship = entityRelationship;
		this.type = type;
		this.requestType = requestType;
		this.newName = name;
		this.organisation.renameEndRelationshipRequest.add(this);
		this.organisation.save();
	}

	/**
	 * Default constructor for the request to rename or delete a topic
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the request
	 * 
	 * @param topicRelationship
	 *            the topic relationship to be renamed or deleted
	 * 
	 * @param requestType
	 *            the type of the request (rename or delete)
	 * 
	 * @param name
	 *            the new name for renaming
	 * 
	 */
	public RenameEndRelationshipRequest(User requester, Organization organisation,
			TopicRelationship topicRelationship, int type,
			int requestType, String name) {
		this.requester = requester;
		this.organisation = organisation;
		this.topicRelationship = topicRelationship;
		this.type = type;
		this.requestType = requestType;
		this.newName = name;
		this.organisation.renameEndRelationshipRequest.add(this);
		this.organisation.save();
	}

//	/**
//	 * Default constructor for the request to rename or delete a tag
//	 * relationship
//	 * 
//	 * @author Noha Khater
//	 * 
//	 * @Story C2S18
//	 * 
//	 * @param requester
//	 *            the user who made the request
//	 * 
//	 * @param tagRelationship
//	 *            the tag relationship to be renamed or deleted
//	 * 
//	 * @param type
//	 *            the type of the request (rename or delete)
//	 * 
//	 * @param name
//	 *            the new name for renaming
//	 * 
//	 */
//	public RenameEndRelationshipRequest(User requester,
//			TagRelationship tagRelationship, int type, String name) {
//		this.requester = requester;
//		this.tagRelationship = tagRelationship;
//		this.type = type;
//		this.newName = name;
//	}

}
