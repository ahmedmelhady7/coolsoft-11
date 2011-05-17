package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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

	/**
	 * Default constructor for the request to rename or delete an entity
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
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
	public RenameEndRelationshipRequest(EntityRelationship entityRelationship,
			int type, String name) {
		this.entityRelationship = entityRelationship;
		this.type = type;
		this.newName = name;
	}

	/**
	 * Default constructor for the request to rename or delete a topic
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
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
	public RenameEndRelationshipRequest(TopicRelationship topicRelationship,
			int type, String name) {
		this.topicRelationship = topicRelationship;
		this.type = type;
		this.newName = name;
	}

	/**
	 * Default constructor for the request to rename or delete a tag
	 * relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
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
	public RenameEndRelationshipRequest(TagRelationship tagRelationship,
			int type, String name) {
		this.tagRelationship = tagRelationship;
		this.type = type;
		this.newName = name;
	}

}
