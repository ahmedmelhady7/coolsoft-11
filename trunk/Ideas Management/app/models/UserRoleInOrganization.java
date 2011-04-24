package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class UserRoleInOrganization extends Model {

	@OneToMany
	List<User> enrolled;

	@OneToMany
	List<Organization> organization;

	@OneToMany
	List<Role> roles;
	// this represents the ID of the entity or the topic that an organizer
	// manages
	int entityTopicID;

	boolean blocked;

	public UserRoleInOrganization(List<User> enrolled,
			List<Organization> organization, List<Role> role, int eTID,
			boolean blocked) {
		this.enrolled = enrolled;
		this.organization = organization;
		this.roles = role;
		entityTopicID = eTID;
		this.blocked = blocked;

	}
	

}
