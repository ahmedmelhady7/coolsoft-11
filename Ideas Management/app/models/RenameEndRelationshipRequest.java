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

	/**
	 * The tag relationship to be renamed or deleted
	 */
	@Required
	@ManyToOne
	public TagRelationship tagRelationship;

	/**
	 * The new name for renaming the relationship
	 */
	@Required
	public String newName;

	/**
	 * The type of the request (0=delete, 1=rename)
	 */
	@Required
	public int type;

	@ManyToOne
	public MainEntity destinationEntity;

	@ManyToOne
	public Topic destinationTopic;

	@ManyToOne
	public Tag destinationTag;

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
	 * @param type
	 *            the type of the request (rename or delete)
	 * 
	 * @param name
	 *            the new name for renaming
	 * 
	 */
	public RenameEndRelationshipRequest(User requester,
			EntityRelationship entityRelationship, int type, String name) {
		this.requester = requester;
		this.entityRelationship = entityRelationship;
		this.type = type;
		this.newName = name;
		if (Users.getEntityOrganizers(entityRelationship.source).contains(
				requester)) {
			entityRelationship.destination.renameEndRelationshipRequest
					.add(this);
			entityRelationship.destination.save();
		} else {
			entityRelationship.source.renameEndRelationshipRequest.add(this);
			entityRelationship.source.save();
		}
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
	 * @param type
	 *            the type of the request (rename or delete)
	 * 
	 * @param name
	 *            the new name for renaming
	 * 
	 */
	public RenameEndRelationshipRequest(User requester,
			TopicRelationship topicRelationship, int type, String name) {
		this.requester = requester;
		this.topicRelationship = topicRelationship;
		this.type = type;
		this.newName = name;
		if (topicRelationship.source.getOrganizer().contains(requester)) {
			topicRelationship.destination.renameEndRelationshipRequest
					.add(this);
			topicRelationship.destination.save();
		} else {
			topicRelationship.source.renameEndRelationshipRequest.add(this);
			topicRelationship.source.save();
		}
	}

	/**
	 * Default constructor for the request to rename or delete a tag
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the request
	 * 
	 * @param tagRelationship
	 *            the tag relationship to be renamed or deleted
	 * 
	 * @param type
	 *            the type of the request (rename or delete)
	 * 
	 * @param name
	 *            the new name for renaming
	 * 
	 */
	public RenameEndRelationshipRequest(User requester,
			TagRelationship tagRelationship, int type, String name) {
		this.requester = requester;
		this.tagRelationship = tagRelationship;
		this.type = type;
		this.newName = name;
	}

}
