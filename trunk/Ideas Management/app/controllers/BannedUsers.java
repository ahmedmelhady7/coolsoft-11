package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.google.gson.JsonObject;

import play.data.validation.Required;
import play.mvc.With;

import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;

@With(Secure.class)
public class BannedUsers extends CRUD {
	@Before
	public static void restrictOrganizerrr() {
		// User u = (User) User.findAll().get(0);
		// Organization org = new Organization("coooolSoft", u);
		// MainEntity e = (MainEntity) MainEntity.findAll().get(0);
		// long id = e.getId();
		// System.out.println(id);
		List<Organization> organizations = Organization.findAll();
		// System.out.println(o.isEmpty() + "yaaaaaaaaaaaaaaaaaaaaaaaaaaaah");
		Organization organization = organizations.get(0);
		System.out.println(organizations.isEmpty());
		// System.out.println("ya raaaaaaaaaaaab" + organization.name + "  " );
		long organizationID = organizations.get(0).getId();
		MainEntity entity = organization.entitiesList.get(1);
		long entityId = entity.getId();
		// Topic topic = entity.topicList.get(0);
		// System.out.println(organizationID);
		// long topicId = topic.getId();

		/**
		 * testing area
		 */
		// restrictOrganizer(organizationID );
		restrictOrganizerEntityPath(entityId);
		// restrictOrganizerTopicPath(topicId);
	}

	/**
	 * restrict the permissions of a certain Organizer in an organization , this
	 * restriction will be in the whole entity or in a specific topic , this
	 * restriction is done by the organization lead
	 * 
	 * this method specifically renders the list of organizers in an
	 * organization
	 * 
	 * @author Nada-Ossama
	 * 
	 * @story C1S7
	 * 
	 * 
	 * @param orgID
	 *            long id of the organization
	 */
	public static void restrictOrganizer(long orgId) {

		Organization organization = Organization.findById(orgId);

		List<User> users = Users.searchOrganizer(organization);

		long organizationID = orgId;
		boolean flag = false;

		render(users, organizationID, flag);
	}

	/**
	 * Renders the set of organizers within a certain entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S17 , C1S16
	 * 
	 * @param entityId
	 *            :long entityId is the id of the entity that list of organizers
	 *            are enrolled in
	 */

	public static void restrictOrganizerEntityPath(long entityId) {

		MainEntity entity = MainEntity.findById(entityId);
		Organization organization = entity.organization;
		long organizationID = organization.getId();
		List<User> users = Users.getEntityOrganizers(entity);
		render(users, entityId, organizationID);
	}

	/**
	 * Renders the set of organizers in a certain topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S16
	 * 
	 * @param topicId
	 *            : long topicId is the Id of the topic that the organizer will
	 *            be restricted in
	 */
	public static void restrictOrganizerTopicPath(long topicId) {

		Topic topic = Topic.findById(topicId);
		MainEntity entity = topic.entity;
		Organization organization = entity.organization;
		long organizationID = organization.getId();

		List<User> users = Users.getEntityOrganizers(entity);
		render(users, organizationID, topicId);

	}

	/**
	 * takes the Id of the organization (the same as the previous method) and
	 * the ID of the User to be restricted,and renders the list of entities the
	 * selected user is enrolled in and it will give the organization lead to
	 * restrict him from the whole selected entity or from a specific topic
	 * 
	 * @story C1S7
	 * 
	 * @author Nada Ossama
	 * 
	 * @param userID
	 *            long id of the user to be restricted
	 * @param organizationId
	 *            long id of the organization he will be restricted in
	 */
	public static void entitiesEnrolledIn(@Required long userId,
			long organizationId) {
		JsonObject json = new JsonObject();

		List<MainEntity> entities = new ArrayList<MainEntity>();
		if (validation.hasErrors() || userId == 0) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		} else {

			// System.out.println(organizationId);
			Organization organization = Organization.findById(organizationId);
			User user = User.findById(userId);
			entities = Users.getEntitiesOfOrganizer(organization, user);
			// System.out.println("++++" + entities.isEmpty());

			// render(user, organizationId, entities);
			// MainEntity x = new MainEntity("MET", "fjfkj", organization);
			// entities.add(x);
		}
		// String nada = "nadaz";
		// return nada;
		String entity = "";
		String entityIds = "";
		// System.out.println(entities.size() + "FFFFFFFFFFFF");
		for (int i = 0; i < entities.size(); i++) {
			entity += entities.get(i).name + ",";
			entityIds += entities.get(i).getId() + ",";
		}
		json.addProperty("entityIds", entityIds);
		json.addProperty("entityList", entity);
		renderJSON(json.toString());
	}

	/**
	 * checks if the organization lead has chosen to restrict the organizer from
	 * the whole selected entity or from a topic in it and accordingly it will
	 * render the list of actions related or it will redirect to other page to
	 * select the topic
	 * 
	 * @author Nada ossama
	 * 
	 * @story C1S7
	 * @param entityId
	 *            : the selected entity id
	 * @param topic
	 *            : String flag if it is equal to true then restrict from a
	 *            certain topic o.w.from the whole entity
	 * @param organizationId
	 *            :long id of the organization entered as before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void topicsEnrolledInOrRedirect(@Required long entityId,
			@Required String topic, long organizationId, long userId) {

		if (validation.hasErrors() || entityId == 0) {
			flash.error("Oops, please select atleast one choise ");
			entitiesEnrolledIn(userId, organizationId);
		}
		if (topic.equalsIgnoreCase("true")) {
			MainEntity entity = MainEntity.findById(entityId);
			List<Topic> entityTopics = entity.topicList;
			render(entityTopics, organizationId, userId);
		} else {
			entityActions(entityId, organizationId, userId);
		}

	}

	public static void topicsEnrolledIn(@Required long entityId,
			long organizationId, long userId) {

		JsonObject json = new JsonObject();

		if (validation.hasErrors() || entityId == 0) {
			flash.error("Oops, please select atleast one choise ");
			entitiesEnrolledIn(userId, organizationId);
		}

		MainEntity entity = MainEntity.findById(entityId);
		List<Topic> entityTopics = entity.topicList;
		String topicsNames = "";
		String topicsIds = "";
		for (int i = 0; i < entityTopics.size(); i++) {
			topicsNames += entityTopics.get(i).title + ",";
			topicsIds += entityTopics.get(i).getId() + ",";
		}
		System.out.println(topicsNames);
		json.addProperty("topicsIds", topicsIds);
		json.addProperty("topicsNames", topicsNames);
		renderJSON(json.toString());

	}

	/**
	 * block the organizer from the whole entity so the method will render the
	 * set of actions
	 * 
	 * 
	 * @author Nada Ossama
	 * @story C1S7
	 * 
	 * @param entityId
	 *            : long entityId to be restricted in
	 * @param organizationId
	 *            : long Id of the organization passed before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void entityActions(long entityId, long organizationId,
			long userId) {

		// System.out.println("NADAaaaAAA" + entityId + "org" + organizationId +
		// "user" + userId);
		JsonObject json = new JsonObject();
		List<String> entityActions = Roles.getRoleActions("organizer");
		MainEntity entity = MainEntity.findById(entityId);
		Organization organization = entity.organization;
		User user = User.findById(userId);

		List<BannedUser> restrictedUser = BannedUser
				.find("select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "entity", entityId).fetch();
		List<String> restricted = new ArrayList<String>();
		for (int k = 0; k < restrictedUser.size(); k++) {
			restricted.add(restrictedUser.get(k).action);
		}

		for (int i = 0; i < entityActions.size(); i++) {
			if (restricted.contains(entityActions.get(i))) {
				System.out.println(entityActions.get(i));
				entityActions.remove(i);
				i--;
			}
		}
		String restrictedActions = "";
		for (int i = 0; i < restricted.size(); i++) {
			restrictedActions += restricted.get(i) + ",";
		}
		String stringActions = "";
		for (int i = 0; i < entityActions.size(); i++) {
			stringActions += entityActions.get(i) + ",";
			System.out.println("entity Action:" + entityActions.get(i));

		}
		json.addProperty("restrictedActions", restrictedActions);
		json.addProperty("actions", stringActions);

		renderJSON(json.toString());

	}

	/**
	 * render the list of actions the organizer is allowed to do in that topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * 
	 * @param topicId
	 *            : long Id of the topic to be restricted in
	 * @param userId
	 *            :long Id of the user to be restricted
	 */

	public static void topicActions(@Required long topicId, long userId) {

		JsonObject json = new JsonObject();

		List<String> topicActions = Roles.getOrganizerTopicActions();
		Topic topic = (Topic.findById(topicId));
		MainEntity entity = topic.entity;
		Organization organization = entity.organization;
		User user = User.findById(userId);

		if (validation.hasErrors() || topicId == 0) {
			flash.error("Oops, please select atleast one choise ");
			topicsEnrolledInOrRedirect(entity.getId(), "topic",
					organization.getId(), user.getId());
		}

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "topic", topicId).fetch();

		int topicActionsSize = topicActions.size();
		for (int i = 0; i < topicActionsSize; i++) {
			for (int j = 0; j < restricted.size(); j++) {
				if (restricted.get(j).equalsIgnoreCase(topicActions.get(i))) {
					topicActions.remove(i);
					i--;
					topicActionsSize--;
					continue;
				}
			}

			// if (restricted.contains(topicActions.get(i))){
			//
			// System.out.println("removed :" + topicActions.remove(i));
			// }
		}
		String topicsActions = "";
		for (int i = 0; i < topicActions.size(); i++) {
			topicsActions += topicActions.get(i) + ",";
		}
		String restrictedTopicActions = "";
		for (int i = 0; i < restricted.size(); i++) {
			restrictedTopicActions += restricted.get(i) + ",";
		}

		System.out.println(topicsActions.length());
		json.addProperty("topicsActions", topicsActions);
		json.addProperty("restrictedTopicActions", restrictedTopicActions);

		renderJSON(json.toString());

	}

	/**
	 * restricts the user from a certain action according to the selection
	 * 
	 * @story C1S7
	 * @author Nada Ossama
	 * @param action
	 *            : String to be restricted from
	 * @param type
	 *            : String topic or entity
	 * @param entityTopicId
	 *            : long id of the topic or entity
	 * @param userId
	 *            : long Id of the user to be restricted
	 */

	public static boolean restrictFinal(@Required String[] actionToDo,
			String type, long entityTopicId, long userId) {

		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();

			if (validation.hasErrors() || actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				topicActions(entityTopicId, userId);
			}

			for (int i = 0; i < actionToDo.length; i++) {
				changed = BannedUser.banFromActionInTopic(userId,
						organizationId, actionToDo[i], entityTopicId);
			}

			Notifications.sendNotification(userId, Security.getConnected()
					.getId(), "user",
					"you have been restricted from the following action :"
							+ actionToDo + " In organization  : " + org
							+ " In Entity :" + entity + " In Topic :" + topic);

		}

		else {

			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();

			if (validation.hasErrors() || actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				entityActions(entityTopicId, organizationId, userId);
			}

			for (int i = 0; i < actionToDo.length; i++) {
				changed = BannedUser.banFromActionInEntity(userId,
						organizationId, actionToDo[i], entityTopicId);
			}

			Notifications.sendNotification(userId, Security.getConnected()
					.getId(), "user",
					"you have been restricted from the following action :"
							+ actionToDo + " In organization  : " + org
							+ " In Entity :" + entity);

		}

		return true;

	}

	public static boolean deRestrict(long userId, String[] actionsRestricted,
			long entityTopicID, String type) {

		if (type.equals("topic")) {

			for (int i = 0; i < actionsRestricted.length; i++) {
				System.out.println("In BannedUsers : Topic deRest."
						+ actionsRestricted[i]);
				BannedUser.deRestrictFromTopic(userId, actionsRestricted[i],
						entityTopicID);

			}
		} else {
			for (int i = 0; i < actionsRestricted.length; i++) {
				System.out.println("In bannedUsers: Entity de-Restriction"
						+ actionsRestricted[i]);
				BannedUser.deRestrictFromEntityWithCascading(userId,
						actionsRestricted[i], entityTopicID);
			}
		}

		return true;
	}

	/**
	 * render the list of idea developers to be blocked/unblocked
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S16
	 * 
	 * @param topId
	 *            long Id of the topic/entity that ll view the ideadevelopers
	 * 
	 * @param num
	 *            int num as 1 if the source is topic or 0 if entity
	 * 
	 */

	public static void viewUsers(long topId, int num) {

		if (num == 0) {

			List<User> users = Users.getIdeaDevelopers(topId, "entity");
			MainEntity topic = MainEntity.findById(topId);
			List<Integer> count = new ArrayList<Integer>();
			for (int i = 0; i < users.size(); i++) {
				BannedUser banned1 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "view", "entity", topic.id).first();
				BannedUser banned2 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "use", "entity", topic.id).first();
				if (banned1 == null && banned2 == null)
					count.add(0);
				else if (banned1 != null && banned2 != null)
					count.add(3);
				else if (banned2 != null)
					count.add(2);
				else if (banned1 != null)
					count.add(1);

			}

			render(users, count, topic, num);
		} else {
			List<User> users = Users.getIdeaDevelopers(topId, "topic");
			Topic topic = Topic.findById(topId);
			List<Integer> count = new ArrayList<Integer>();
			for (int i = 0; i < users.size(); i++) {
				BannedUser banned1 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "view", "topic", topic.id).first();
				BannedUser banned2 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "entity", "topic", topic.id).first();
				if (banned1 == null && banned2 == null)
					count.add(0);
				else if (banned1 != null && banned2 != null)
					count.add(3);
				else if (banned2 != null)
					count.add(2);
				else if (banned1 != null)
					count.add(1);

			}

			render(users, count, topic, num);

		}

	}

	/**
	 * block/unblock an idea developer from viewing/using a certain topic calls
	 * doBlock() to block from entity
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S16
	 * 
	 * @param userId
	 *            long Id of user that ll be blocked/unblocked
	 * @param type
	 *            int type 0 if view and 1 if use
	 * @param topId
	 *            long Id of the topic/entity that user ll be blocked/unblocked
	 *            from
	 * @param id
	 *            int id , 0 if entity and 1 if topic
	 * @param m
	 *            String m that is the text on the button to the action if
	 *            (block or unblock)
	 * 
	 */

	public static void block(long userId, int type, long topId, int id,
			String text, String message) {

		System.out.println(message);
		System.out.println("here");
		System.out.println(text);
		System.out.println(userId);
		System.out.println(type);
		System.out.println(topId);
		User user = User.findById(userId);
		List<User> organizers = new ArrayList<User>();
		MainEntity entity = null;
		Topic topic = null;

		if (id == 0) {
			entity = MainEntity.findById(topId);
			organizers = Users.getEntityOrganizers(entity);

			if (type == 0) {

				if (text.equals("Block from viewing")) {
					doBlock(userId, topId, 0, 0);
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from viewing Entity "
												+ entity.name);
					Notifications.sendNotification(userId, topId, "entity",
							"You have been blocked from viewing entity "
									+ entity.name + " because " + message);

				} else {

					doBlock(userId, topId, 1, 0);
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from viewing Entity "
												+ entity.name);
					Notifications.sendNotification(userId, topId, "entity",
							"You have been unblocked from viewing entity "
									+ entity.name);

				}
			} else {
				if (text.equals("Block from using")) {
					doBlock(userId, topId, 0, 1);
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from performing any action within entity "
												+ entity.name);
					Notifications.sendNotification(userId, topId, "entity",
							"You have been blocked from performing any action within entity "
									+ entity.name + " because " + message);
				} else {
					doBlock(userId, topId, 1, 1);
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from performing any action within entity "
												+ entity.name);
					Notifications.sendNotification(userId, topId, "entity",
							"You have been ublocked from performing any action within entity "
									+ entity.name + " because " + message);
				}
			}
		} else {
			topic = Topic.findById(topId);
			entity = topic.entity;
            organizers = Users.getEntityOrganizers(entity);
			if (type == 0) {
				if (text.equals("Block from viewing")) {
					BannedUser block = new BannedUser(user,
							topic.entity.organization, "view topic", "topic",
							topId);
					// unblock
					block.save();
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from viewing topiic "
												+ topic.title);
					Notifications.sendNotification(userId, topId, "topic",
							"You have been blocked from viewing topic "
									+ topic.title + " because " + message);

				}
			} else {
				if (text.equals("Block from using")) {
					BannedUser block = new BannedUser(user,
							topic.entity.organization, "use", "topic", topId);
					// unblock
					block.save();
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										topId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from performing any action within topic "
												+ topic.title);
					Notifications.sendNotification(userId, topId, "topic",
							"You have been blocked from performing any action within topic "
									+ topic.title + " because " + message);

				}
			}
		}

	}

	/**
	 * block/unblock an idea developer from viewing/using this entity and its
	 * all sub-entities and topics
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S16
	 * 
	 * @param userId
	 *            long Id of user that ll be blocked/unblocked
	 * 
	 * @param entId
	 *            long Id of entity that user ll be blocked/unblocked from
	 * 
	 * @param choice
	 *            int choice as 0 if view or 1 if use
	 * 
	 * @param type
	 *            int type view of 0 or use if 1 ,that user ll be
	 *            blocked/unblocked from
	 * 
	 */

	public static void doBlock(long userId, long entId, int choice, int type) {

		String action = "";
		if (type == 0)
			action = "view";
		else
			action = "use";

		MainEntity entity = MainEntity.findById(entId);
		User user = User.findById(userId);

		if (choice == 0) {
			BannedUser block = new BannedUser(user, entity.organization,
					action, "entity", entId);
			block.save();
			for (int j = 0; j < entity.topicList.size(); j++) {
				BannedUser block1 = new BannedUser(user, entity.organization,
						action, "topic", entity.topicList.get(j).id);
				block1.save();
			}

			List<MainEntity> subentity = entity.subentities;
			for (int j = 0; j < subentity.size(); j++) {
				BannedUser block1 = new BannedUser(user, entity.organization,
						action, "entity", subentity.get(j).id);
				block1.save();

				for (int k = 0; k < subentity.get(j).topicList.size(); k++) {
					BannedUser block2 = new BannedUser(user,
							entity.organization, action, "topic",
							subentity.get(j).topicList.get(k).id);
					block2.save();
				}
			}

		} else {

			BannedUser unblock = BannedUser.find(
					"byBannedUserAndActionAndResourceTypeAndResourceID", user,
					action, "entity", entity.id).first();
			unblock.delete();

			for (int j = 0; j < entity.topicList.size(); j++) {
				BannedUser unblock1 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						user, action, "topic", entity.topicList.get(j).id)
						.first();
				unblock1.delete();
			}

			List<MainEntity> subentity = entity.subentities;
			for (int j = 0; j < subentity.size(); j++) {
				BannedUser unblock2 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						user, action, "entity", subentity.get(j).id).first();

				unblock2.delete();

				for (int k = 0; k < subentity.get(j).topicList.size(); k++) {
					BannedUser unblock1 = BannedUser
							.find("byBannedUserAndActionAndResourceTypeAndResourceID",
									user, action, "topic",
									subentity.get(j).topicList.get(k).id)
							.first();

					unblock1.delete();
				}
			}

		}
	}

}
