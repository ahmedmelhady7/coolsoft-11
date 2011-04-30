package models;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

public class LinkDuplicatesRequest {
	/**
	 * The Ideas declared as duplicates
	 */
	@Required
	@ManyToMany
	public Idea idea1;
	
	@Required
	@ManyToMany
	public Idea idea2;

	/**
	 * The user sending the request to the organizaer
	 */
	@Required
	@ManyToOne
	public User sender;

	/**
	 * A description that a user adds when sending the request
	 */
	public String description;

	/**
	 * RequestToJoin Class Constructor
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S10
	 * 
	 */
	public LinkDuplicatesRequest(User sender, Idea idea1, Idea idea2,
			String des) {
		this.sender = sender;
		this.idea1 = idea1;
		this.idea2 = idea2;
		this.description = des;
	}
}
