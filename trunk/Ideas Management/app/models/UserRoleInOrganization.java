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
	/*
	 * this represents the ID of the entity or the topic the enrolled belongs to
	 * or manages
	 */
	public long entityTopicID;
	/*
	 * represents the type (entity or Topic)
	 */
	public String type;

	public UserRoleInOrganization(User user, Organization org, Role role,
			long eTId, String type) {
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
		this.type = "none";

	}

	/*
	 * This method adds a new enrolled user in the organization where his role
	 * is related to a specific topic, entity .. etc
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @parm user: is the enrolled user
	 * 
	 * @parm org: is the organization the User user is enrolled in
	 * 
	 * @parm role: the role of that user in this organization
	 * 
	 * @parm entityOrTopicId : the id of the entity or topic the role of that
	 * user is related to
	 * 
	 * @parm type: the type (entity / topic)
	 * 
	 * return boolean indicating the successfulness of the operation
	 */

	public static boolean addEnrolledUser(User user, Organization org, Role role,
			long entityOrTopicId, String type) {
		new UserRoleInOrganization(user, org, role, entityOrTopicId, type)
				.save();

		return true;
	}

	/*
	 * This method adds a new enrolled user in the organization where his role
	 * is NOT related to a specific topic, entity .. etc
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @parm user: is the enrolled user
	 * 
	 * @parm org: is the organization the User user is enrolled in
	 * 
	 * @parm role: the role of that user in this organization
	 * 
	 * return boolean indicating the successfulness of the operation
	 */

	public static boolean addEnrolledUser(User user, Organization org, Role role) {
		new UserRoleInOrganization(user, org, role).save();

		return true;
	}

}
