package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.With;

import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

@With(Secure.class)
public class UserRoleInOrganizations extends CRUD {

	/**
	 * adds a new enrolled user in the organization where his role
	 * is not related to a specific topic, entity .. etc
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param user: User that is enrolled 
	 * 
	 * @param org: Organization org the User user is enrolled in
	 * 
	 * @param role: the role of that user in this organization
	 * 
	 * return boolean indicating the successfulness of the operation
	 */

	public static boolean addEnrolledUser(User user, Organization org, Role role) {

		UserRoleInOrganization userRoleInOrg = 
		new UserRoleInOrganization(user, org, role).save();
		user.userRolesInOrganization.add(userRoleInOrg);
		org.userRoleInOrg.add(userRoleInOrg);
		role.userRoleInOrganization.add(userRoleInOrg);
		user.save();
		org.save();
		role.save();


         /*
		UserRoleInOrganization o = new UserRoleInOrganization(user, org, role, 1, "");
		o.save();
		*/
		return true;
	}

	/**
	 * adds a new enrolled user in the organization where his role
	 * is related to a specific topic, entity .. etc
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param user: User user the enrolled user
	 * 
	 * @param org: Organization org that the User user is enrolled in
	 * 
	 * @param role: Role role of that user in this organization
	 * 
	 * @param entityOrTopicId : long id of the entity or topic the role of that
	 * user is related to
	 * 
	 * @param type: String type (entity / topic)
	 * 
	 * @return boolean indicating the successfulness of the operation
	 */

	public static boolean addEnrolledUser(User user, Organization org,
			Role role, long entityOrTopicId, String type) {

		UserRoleInOrganization userRoleInOrg = new UserRoleInOrganization(user, org, role, entityOrTopicId, type)
				.save();
		user.userRolesInOrganization.add(userRoleInOrg);
		org.userRoleInOrg.add(userRoleInOrg);
		role.userRoleInOrganization.add(userRoleInOrg);
		user.save();
		org.save();
		role.save();

		return true;
	}

	/**
	 * checks whether this user is organizer or not given the
	 * organization ,entity or topic REGARDLESS any restrictions
	 * 
	 * @author Nada Ossama
	 * @param user User to check
	 * @param sourceID 
	 *               long sourceID to be checked in
	 * @param sourceType 
	 *               String sourceType that represents the type of the source
	 *                      
	 * @return boolean 
	 *                whether he is organizer or not
	 */

	public static boolean isOrganizer(User user, long sourceID,
			String sourceType) {

		if (sourceType.equalsIgnoreCase("Organization")) {
			Organization o = Organization.findById(sourceID);

			if ((Users.searchOrganizer(o)).contains(user)) {
				return true;
			}
		}
		if (sourceType.equalsIgnoreCase("entity")) {
			MainEntity e = MainEntity.findById(sourceID);

			if ((Users.getEntityOrganizers(e)).contains(user)) {
				return true;
			}
		}
		if (sourceType.equalsIgnoreCase("topic")) {
			Topic t = Topic.findById(sourceID);

			if ((Users.getEntityOrganizers(t.entity)).contains(user)) {
				return true;
			}

		}
		return false;

	}

}