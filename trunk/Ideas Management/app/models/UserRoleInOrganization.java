package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class UserRoleInOrganization extends Model {

	@ManyToOne
	public User enrolled;

	@ManyToOne
	public Organization organization;

	@ManyToOne
	public Role role;
	/* this represents the ID of the entity or the topic the enrolled belongs to
	* or manages
	*/ 
	public long entityTopicID;
	/*
	 * represents the type (entity or Topic)
	 */
	public String type;

	public UserRoleInOrganization(User user, Organization org, Role role,
			long eTId , String type) {
		this.enrolled = user;
		this.organization = org;
		this.role = role;
		this.entityTopicID = eTId;
		this.type = type;

	}

	public UserRoleInOrganization(User user, Organization org, Role role) {
		this.enrolled = user;
		this.organization = org;
		this.role = role;
		this.entityTopicID = -1;
		this.type  = "none";

	}

}
