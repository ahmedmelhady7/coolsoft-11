package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Roles;

import play.db.jpa.Model;

/**
 * 
 * @author Nada Ossama
 * 
 */
@Entity
public class UserRoleInOrganization extends CoolModel {
	/**
	 * the user that is enrolled
	 */
	@ManyToOne
	public User enrolled;
	/**
	 * the organization where the user is enrolled in
	 */
	@ManyToOne
	public Organization organization;
	/**
	 * the role of the user in that organization
	 */
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
	 * creates an enrolled a user in a certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param user
	 *            User user to be enrolled
	 * @param org
	 *            Organization org that the user will be enrolled in
	 * @param role
	 *            Role role of that user in the organization org
	 * @param entityTopicId
	 *            long ID of the entity or the topic the user will be enrolled
	 *            in if any
	 * 
	 * @param type
	 *            String entity or topic
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
	 * creates enrolled user in a certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param user
	 *            User the user to be enrolled
	 * @param org
	 *            Organization org that the user will be enrolled in
	 * @param role
	 *            Role role of that user in the organization org
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
	 *            User user that his role is to be known
	 * @param org
	 *            Organization org i need to know the user's role in it
	 * @return Role the role of the user in that organization
	 */

	public Role userRoleInOrganization(User user, Organization org) {

		UserRoleInOrganization role = UserRoleInOrganization
				.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.organization = ?",
						user, org).first();

		if (role == null) {
			return (Roles.getRoleByName("idea developer"));
		} else
			return role.role;

	}

	/**
	 * deletes a certain record in the table
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param userRoleInOrg
	 *            UserRoleInOrganiztion record to be deleted
	 */

	public static boolean delete(UserRoleInOrganization userRoleInOrg) {
		User user = userRoleInOrg.enrolled;
		Organization organiztion = userRoleInOrg.organization;
		Role role = userRoleInOrg.role;
		try {
			user.userRolesInOrganization.remove(userRoleInOrg);
			organiztion.userRoleInOrg.remove(userRoleInOrg);
			role.userRoleInOrganization.remove(userRoleInOrg);
			userRoleInOrg.delete();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * given a certain topic or entity that will be deleted this method cascades
	 * the deletion
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param entityTopicID
	 *            long id of the entity or topic to be deleted
	 * @param type
	 *            String type that determines whether the passed id belongs to a
	 *            topic or entity
	 */

	public static void deleteEntityOrTopic(long entityTopicID, String type) {

		List<UserRoleInOrganization> toBeDeleted = UserRoleInOrganization
				.find("select uro from UserRoleInOrganization uro where uro.entityTopicID = ? and uro.type like ?",
						entityTopicID, type).fetch();
		if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
			for (int i = 0; i < toBeDeleted.size(); i++) {
				UserRoleInOrganization record = toBeDeleted.get(i);
				UserRoleInOrganization.delete(record);
			}
		}

	}

}
