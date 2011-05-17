package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

/**
 * @author Noha Khater
 * 
 *         The Relationship Requests to start a relationship between entities,
 *         topics or tags.
 * 
 */

@Entity
public class CreateRelationshipRequest extends CoolModel {

	/**
	 * The user who made the relationship request
	 */
	@Required
	@ManyToOne
	public User requester;

	/**
	 * The name of the relationship
	 */
	@Required
	public String name;

	/**
	 * The source of the entity relationship
	 */
	@Required
	@ManyToOne
	public MainEntity sourceEntity;

	/**
	 * The destination of the entity relationship
	 */
	@Required
	@ManyToOne
	public MainEntity destinationEntity;

	/**
	 * The source of the topic relationship
	 */
	@Required
	@ManyToOne
	public Topic sourceTopic;

	/**
	 * The destination of the topic relationship
	 */
	@Required
	@ManyToOne
	public Topic destinationTopic;

	/**
	 * The source of the tag relationship
	 */
	@Required
	@ManyToOne
	public Tag sourceTag;

	/**
	 * The destination of the tag relationship
	 */
	@Required
	@ManyToOne
	public Tag destinationTag;

	/**
	 * Default constructor for creating an entity relationship creation request
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the relationship request
	 * 
	 * @param sourceEntity
	 *            the source of the entity relationship
	 * 
	 * @param destinationEntity
	 *            the destination for the entity relationship
	 * 
	 * @param name
	 *            the name of the relationship to be created
	 * 
	 */
	public CreateRelationshipRequest(User requester, MainEntity sourceEntity,
			MainEntity destinationEntity, String name) {
		this.requester = requester;
		this.sourceEntity = sourceEntity;
		this.destinationEntity = destinationEntity;
		this.name = name;
		this.sourceEntity.relationshipRequestsSource.add(this);
		this.destinationEntity.relationshipRequestsDestination.add(this);
	}

	/**
	 * Default constructor for creating a topic relationship creation request
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the relationship request
	 * 
	 * @param sourceTopic
	 *            the source of the topic relationship
	 * 
	 * @param destinationTopic
	 *            the destination for the topic relationship
	 * 
	 * @param name
	 *            the name of the relationship to be created
	 * 
	 */
	public CreateRelationshipRequest(User requester, Topic sourceTopic,
			Topic destinationTopic, String name) {
		this.requester = requester;
		this.sourceTopic = sourceTopic;
		this.destinationTopic = destinationTopic;
		this.name = name;
		this.sourceTopic.relationshipRequestsSource.add(this);
		this.destinationTopic.relationshipRequestsDestination.add(this);
	}

	/**
	 * Default constructor for creating a tag relationship creation request
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param requester
	 *            the user who made the relationship request
	 * 
	 * @param sourceTag
	 *            the source of the tag relationship
	 * 
	 * @param destinationTag
	 *            the destination for the tag relationship
	 * 
	 * @param name
	 *            the name of the relationship to be created
	 * 
	 */
	public CreateRelationshipRequest(User requester, Tag sourceTag,
			Tag destinationTag, String name) {
		this.requester = requester;
		this.sourceTag = sourceTag;
		this.destinationTag = destinationTag;
		this.name = name;
		this.sourceTag.relationshipRequestsSource.add(this);
		this.destinationTag.relationshipRequestsDestination.add(this);
	}

}
