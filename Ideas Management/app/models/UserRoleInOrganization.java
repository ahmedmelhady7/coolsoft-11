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
	// this represents the ID of the entity or the topic that an organizer
	// manages
	public int entityTopicID;
	// modified
	//public boolean isBlocked;

	public UserRoleInOrganization(User user, Organization org, Role role,
			int eTId, boolean isBlocked) {
		this.enrolled = user;
		this.organization = org;
		this.role = role;
		this.entityTopicID = eTId;
		//this.isBlocked = isBlocked;

	}

	public UserRoleInOrganization() {
		// this.enrolled = new Aray;
		// this.organization = organization;
		// this.roles = role;
		// entityTopicID = eTID;
		// this.blocked = blocked;

	}

}
