package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class RequestToJoin extends Model {

	/**
	 * The user requesting to join a specific organization or posting in a topic
	 */
	@Required
	@ManyToOne
	public User source;

	/**
	 * The topic that the user is requesting to post in
	 */
	@Required
	@ManyToOne
	public Topic topic;

	/**
	 * The organization that the user is requesting to join
	 */
	@Required
	@ManyToOne
	public Organization organization;

	/**
	 * A description that a user adds when sending the request
	 */
	public String description;

	/**
	 * RequestToJoin Class Constructor
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S15
	 * 
	 * @param source
	 *            : The user requesting
	 * 
	 * @param topic
	 *            : The topic that the user is requesting to post in
	 * 
	 * @param organization
	 *            : The organization that the user wants to join
	 * 
	 * @param description
	 *            : The message sent along with the request
	 */
	public RequestToJoin(User source, Topic topic, Organization organization,
			String description) {
		this.source = source;
		this.topic = topic;
		this.organization = organization;
		this.description = description;
	}

	/**
	 * Overrides the toString method to get the description of a request
	 * 
	 * @author Omar Faruki
	 * 
	 * @return String
	 */
	public String toString() {
		return this.description;
	}
}
