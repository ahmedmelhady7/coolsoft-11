package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Roles;

import play.db.jpa.Model;

@Entity
public class UserRoleInOrganization extends Model {

	@ManyToOne
	public User enrolled;

	@ManyToOne
	public Organization organization;

	@ManyToOne
	public Role role;
	/**
	 * this represents the ID of the entity or the topic the enrolled belongs to
	 * or manages
	 */
	public long entityTopicID;
	/**
	 * represents the type (entity or Topic)
	 */
	public String type;

	/**
	 * this organizer creates enrolls a user in a certain organization
	 *
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param user
	 *            is the user to be enrolled
	 * @param org
	 *            is the organization that the user will be enrolled in
	 * @param role
	 *            is the role of that user in the organization org
	 * @param entityTopicId
	 *            is the ID of the entity or the topic the user will be inrolled
	 *            in if any
	 * 
	 * @param type
	 *            is entity or topic
	 * 
	 */

	public UserRoleInOrganization(User user, Organization org, Role role,
			long entityTopicId, String type) {
		this.enrolled = user;
		this.organization = org;
		this.role = role;
		this.entityTopicID = entityTopicId;
		this.type = type;

	}

	/**
	 * this organizer creates enrolls a user in a certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 *
	 * @param user
	 *            is the user to be enrolled
	 * @param org
	 *            is the organization that the user will be enrolled in
	 * @param role
	 *            is the role of that user in the organization org
	 * 
	 */

	public UserRoleInOrganization(User user, Organization org, Role role) {
		this.enrolled = user;
		this.organization = org;
		this.role = role;
		this.entityTopicID = -1;
		this.type = "none";

	}

	/**
	 * returns the role of a user in the organization
	 * 
	 * @author Nada.Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param user
	 *            the user i need to know has role
	 * @param org
	 *            the organization i need to know the user's role in it
	 * @return the role of the user in that organization
	 */

	public Role userRoleInOrganization(User user, Organization org) {

		UserRoleInOrganization role = UserRoleInOrganization
				.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.organization = ?",
						user, org).first();
		
	    if (role == null ){
	    	return (Roles.getRoleByName("idea developer"));
	    }
	    else
	    return role.role;
						
	}
	//
	//
	// /*
	// * This method adds a new enrolled user in the organization where his role
	// * is related to a specific topic, entity .. etc
	// *
	// * @author Nada Ossama
	// *
	// * @story :C1S7
	// *
	// * @parm user: is the enrolled user
	// *
	// * @parm org: is the organization the User user is enrolled in
	// *
	// * @parm role: the role of that user in this organization
	// *
	// * @parm entityOrTopicId : the id of the entity or topic the role of that
	// * user is related to
	// *
	// * @parm type: the type (entity / topic)
	// *
	// * return boolean indicating the successfulness of the operation
	// */
	//
	// public static boolean addEnrolledUser(User user, Organization org,
	// Role role, long entityOrTopicId, String type) {
	// new UserRoleInOrganization(user, org, role, entityOrTopicId, type)
	// .save();
	//
	// return true;
	// }
	//
	// /*
	// * This method adds a new enrolled user in the organization where his role
	// * is NOT related to a specific topic, entity .. etc
	// *
	// * @author Nada Ossama
	// *
	// * @story :C1S7
	// *
	// * @param user: is the enrolled user
	// *
	// * @param org: is the organization the User user is enrolled in
	// *
	// * @param role: the role of that user in this organization
	// *
	// * return boolean indicating the successfulness of the operation
	// */
	//
	// public static boolean addEnrolledUser(User user, Organization org, Role
	// role) {
	// new UserRoleInOrganization(user, org, role).save();
	//
	// return true;
	// }
	//
	// /*
	// * gets the list of organizers of a certain entity
	// *
	// * @author: Nada Ossama
	// *
	// * @param e: is the entity that i want to retrieve all the organizers that
	// * are enrolled in it
	// *
	// * @return List of Organizers in that entity
	// */
	//
	// public static List<User> getEntityOrganizers(MainEntity e) {
	// List<User> organizers = null;
	// if (e != null) {
	// Organization o = e.organization;
	// organizers = (List<User>) UserRoleInOrganization
	// .find("select uro.enrolled from UserRoleInOrganization uro and Role r "
	// + "where uro.organization = ? and"
	// + " uro.Role = r and r.roleName like ? and uro.entityTopicID = ? "
	// + "and uro.type like ?", o, "organizer", e.getId(),
	// "entity");
	//
	// }
	// return organizers;
	// }
	//
	//
	// /*
	// * gets all the users enrolled in an organization these users are: 1- the
	// * Organization lead 2- the organizers (even if blocked) 3- Idea
	// Developers
	// * in secret or private topics
	// *
	// * @author : Nada Ossama
	// *
	// * @param: the organization that the users are enrolled in
	// *
	// * @return :List of enrolled users
	// */
	// public static List<User> getEnrolledUsers(Organization o) {
	// List<User> enrolled = null;
	// if (o != null) {
	//
	// enrolled = (List<User>) UserRoleInOrganization.find(
	// "select uro.enrolled from UserRoleInOrganization uro "
	// + "where uro.organization = ? ", o);
	//
	// }
	// return enrolled;
	// }

}
