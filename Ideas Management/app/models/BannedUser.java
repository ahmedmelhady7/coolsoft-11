package models;

import java.util.ArrayList;
import java.util.List;

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
public class BannedUser extends CoolModel {
	/**
	 * the organization to be banned in
	 */
	@ManyToOne
	public Organization organization;
	/**
	 * the user to be banned
	 */
	@ManyToOne
	public User bannedUser;
	/**
	 * the action that the user will be bannedFrom
	 */
	public String action;
	/**
	 * the type of a resource in the organization (entity , topic) that the user
	 * will be banned from if any
	 */
	public String resourceType;
	/**
	 * the id of a resource in the organization (entity , topic) that the user
	 * will be banned from if any
	 */
	public long resourceID;

	/**
	 * used if the action the user is banned from is not related to a certain
	 * object within the organization
	 * 
	 * @autor:Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param banned
	 *            User The banned user
	 * 
	 * @param org
	 *            Organization org at which this user will be banned from that
	 *            action
	 * @param action
	 *            String action he will be banned from
	 * 
	 * @param resourceType
	 *            String type of the object the action is related to
	 * 
	 * @param resourceId
	 *            long Id of the Object of that type
	 */

	public BannedUser(User banned, Organization org, String action,
			String resourceType, long resourceId) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
		this.resourceType = resourceType;
		resourceID = resourceId;

	}

	/**
	 * used if the action the user is banned from is related to a certain object
	 * 
	 * @author:Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @param banned
	 *            User The banned user
	 * 
	 * @param org
	 *            Organization at which this user will be banned from an action
	 * 
	 * @param action
	 *            String The action he will be banned from
	 * 
	 */

	public BannedUser(User banned, Organization org, String action) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
	}

	/**
	 * this method deletes a certain record from the table
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param toBeDeleted
	 *            BannedUser is the record to be deleted
	 */
	public static void delete(BannedUser toBeDeleted) {
		Organization organization = toBeDeleted.organization;
		User deletedUser = toBeDeleted.bannedUser;
		deletedUser.bannedUsers.remove(toBeDeleted);
		deletedUser.save();
		organization.bannedUsers.remove(toBeDeleted);
		organization.save();
		toBeDeleted.delete();
	}

	/**
	 * deleted all the records that are related to a certain entity or topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param entityTopicID
	 *            : long id of the entity or topic
	 * @param type
	 *            String that determines whether the passed value belongs to a
	 *            topic or entity
	 */
	public static void deleteEntityOrTopic(long entityTopicID, String type) {

		List<BannedUser> toBeDeleted = BannedUser
				.find("select bu from BannedUser bu where bu.resourceID = ? and bu.resourceType = ?",
						entityTopicID, type).fetch();
		if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
			for (int i = 0; i < toBeDeleted.size(); i++) {
				BannedUser record = toBeDeleted.get(i);
				BannedUser.delete(record);
			}
		}
	}

	/**
	 * block a user from a certain action within an entity and cascade that
	 * restriction to the rest of the topics within this entity if the action
	 * was topic related
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID
	 *            long ID of the User to be blocked
	 * 
	 * @param organizationID
	 *            long id that this entity belongs to
	 * 
	 * @param action
	 *            String action that the user will be banned from
	 * 
	 * @param entityID
	 *            long id the ID of that entity
	 * 
	 * @return boolean : false if found banned otherwise return true
	 * 
	 */
	public static boolean banFromActionInEntity(long userID,
			long organizationID, String action, long entityID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);

		BannedUser test = BannedUser
				.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action like ? and bu.resourceType like ? and bu.resourceID = ?",
						myBannedUser, myOrganization, action, "entity",
						entityID).first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "entity", entityID);
		newBannedUser.save();
		myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);

		ArrayList<String> topicActions = (ArrayList<String>) Roles
				.getOrganizerTopicActions();

		MainEntity mainEntity = MainEntity.findById(entityID);
		List<MainEntity> subEntities = mainEntity.subentities;

		for (int i = 0; i < subEntities.size(); i++) {
			MainEntity subEntity = subEntities.get(i);
			banFromActionInEntity(userID, organizationID, action,
					subEntity.getId());
		}

		if (topicActions.contains(action)) {
			MainEntity entity = MainEntity.findById(entityID);
			List<Topic> entityTopics = entity.topicList;
			for (int i = 0; i < entityTopics.size(); i++) {
				long topicId = entityTopics.get(i).getId();
				banFromActionInTopic(userID, organizationID, action, topicId);
			}

		}
		return true;
	}

	/**
	 * block a user from a certain action within a topic
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID
	 *            : long ID of the User to be blocked
	 * 
	 * @param organizationID
	 *            : long id that this entity belongs to
	 * 
	 * @param action
	 *            : String action that the user will be banned from
	 * 
	 * @param topicID
	 *            : long id the ID of that topic
	 * 
	 * @return boolean to indicates the successfulness of the operation
	 */
	public static boolean banFromActionInTopic(long userID,
			long organizationID, String action, long topicID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
		BannedUser test = BannedUser
				.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action like ? and bu.resourceType like ? and bu.resourceID = ?",
						myBannedUser, myOrganization, action, "topic", topicID)
				.first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "topic", topicID);
		newBannedUser.save();
		myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);
		return true;
	}

	/**
	 * de-restricts an organizer from a certain action within a specified topic
	 * and cascades the de-restriction to the rest of the entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S19
	 * 
	 * @param userID
	 *            : long user ID is the id of the organizer to be direstricted
	 * @param action
	 *            : String action to be de restricted from
	 * @param topicID
	 *            : long topicID is the id of the topic to be derestricted from
	 */

	public static void deRestrictFromTopic(long userID, String action,
			long topicID) {
		User user = User.findById(userID);
		Topic topic = Topic.findById(topicID);
		MainEntity entity = topic.entity;
		long entityID = entity.getId();
		Organization organization = entity.organization;

		BannedUser restricted = BannedUser
				.find("select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?"
						+ " and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, user, action, "topic", topicID).first();

		if (restricted != null) {
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();
		}

		BannedUser restrictedInEntity = BannedUser
				.find("select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?"
						+ " and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, user, action, "entity", entityID).first();
		if (restrictedInEntity != null) {
			deRestrictFromEntityNoCascading(userID, action, entityID);
		}

	}

	/**
	 * 
	 * de-restricts a certain user from a certain action in a certain entity
	 * without cascading the derestricton process to the topics within that
	 * entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story: C1S19
	 * 
	 * 
	 * @param userID
	 *            : long userID is the id of the user to be de-restricted
	 * @param action
	 *            :String action is the action to be de-restricted from
	 * @param entityID
	 *            : long entityID is the id of the entity to be de-restricted
	 *            from
	 */
	public static void deRestrictFromEntityNoCascading(long userID,
			String action, long entityID) {

		User user = User.findById(userID);
		MainEntity entity = MainEntity.findById(entityID);
		Organization organization = entity.organization;

		BannedUser restricted = BannedUser
				.find("select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?"
						+ " and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, user, action, "entity", entityID).first();

		if (restricted != null) {
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();

		}

	}

	/**
	 * de-restrict a user from a certain action in the whole entity and the
	 * de-restriction process is cascaded to all the topics within that entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S19
	 * 
	 * @param userID
	 *            : long userID is the id of the user to be de-restricted
	 * @param action
	 *            : String action is the action to be de-restricted from
	 * @param entityID
	 *            : long entityID is the id of the entity to be de-restricted in
	 */

	public static void deRestrictFromEntityWithCascading(long userID,
			String action, long entityID) {

		User user = User.findById(userID);
		MainEntity entity = MainEntity.findById(entityID);
		Organization organization = entity.organization;
		List<Topic> topicsList = entity.topicList;
		List<String> topicActions = Roles.getOrganizerTopicActions();

		if (topicActions.contains(action)) {
			for (int i = 0; i < topicsList.size(); i++) {
				Topic topic = topicsList.get(i);
				long topicID = topic.getId();
				deRestrictFromTopic(userID, action, topicID);

			}
		}

		BannedUser restricted = BannedUser
				.find("select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?"
						+ " and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, user, action, "entity", entityID).first();

		if (restricted != null) {
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();
		}

		List<MainEntity> subEntities = entity.subentities;
		for (int i = 0; i < subEntities.size(); i++) {
			MainEntity subEntity = subEntities.get(i);
			deRestrictFromEntityWithCascading(userID, action, subEntity.getId());
		}

	}

}
