package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.google.gson.JsonObject;

import play.data.validation.Required;
import play.mvc.With;

import models.BannedUser;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;

@With(Secure.class)
public class BannedUsers extends CoolCRUD {
	@Before
	/**
	 * just for testing
	 */
	public static void restrictOrganizerrr() {
		// User u = (User) User.findAll().get(0);
		// Organization org = new Organization("coooolSoft", u);
		// MainEntity e = (MainEntity) MainEntity.findAll().get(0);
		// long id = e.getId();
		// System.out.println(id);
		List<Organization> organizations = Organization.findAll();
		// System.out.println(o.isEmpty() + "yaaaaaaaaaaaaaaaaaaaaaaaaaaaah");
		Organization organization = organizations.get(0);

		// System.out.println("ya raaaaaaaaaaaab" + organization.name + "  " );
		long organizationID = organizations.get(0).getId();
		// MainEntity entity = organization.entitiesList.get(1);
		// long entityId = entity.getId();
		// Topic topic = entity.topicList.get(0);
		// System.out.println(organizationID);
		// long topicId = topic.getId();

		/**
		 * testing area
		 */
		restrictOrganizer(organizationID);
		// restrictOrganizerEntityPath(entityId);
		// restrictOrganizerTopicPath(topicId);
	}

	/**
	 * just for testing
	 */

	public static void viewOrganizers() {
		// viewRestrictedOrganizersInOrganization(1);
		viewRestrictedOrganizersTopicPath(1);
	}

	/**
	 * This method renders the unauthorized page
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 */
	public static void unauthorized() {
		User user = Security.getConnected();
		render(user);
	}

	/**
	 * 
	 * renders the list of organizers in an organization
	 * 
	 * @author Nada-Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * 
	 * @param orgID
	 *            long id of the organization
	 */
	public static void restrictOrganizersList(long orgId) {

		Organization organization = Organization.findById(orgId);

		List<User> users = Users.searchOrganizer(organization);

		long organizationID = orgId;
		// boolean flag = false;
		User user = Security.getConnected();
		if (user.isAdmin || organization.creator.equals(user)) {
			render(users, organizationID, user);
		} else {
			unauthorized();
		}

	}

	/**
	 * 
	 * renders the list of organizers in an organization
	 * 
	 * @author Nada-Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * 
	 * @param orgID
	 *            long id of the organization
	 */
	public static void restrictOrganizer(long orgId) {

		Organization organization = Organization.findById(orgId);

		List<User> users = Users.searchOrganizer(organization);

		long organizationID = orgId;
		// boolean flag = false;
		User user = Security.getConnected();

		if (user.isAdmin || organization.creator.equals(user)) {
			render(users, organizationID, user);
		} else {
			unauthorized();
		}

	}

	/**
	 * renders a sorted lists of BannedUser to be viewed
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param organizationID
	 *            long id of the organization that the viewed bannedUser belongs
	 *            to
	 */
	public static void viewRestrictedOrganizersTopicPath(long organizationID) {
		Organization organization = Organization.findById(organizationID);
		User user = Security.getConnected();
		List<BannedUser> bannedUsers = BannedUser.find(
				"select bu from BannedUser bu where bu.organization = ?",
				organization).fetch();
		List<BannedUser> finalBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < bannedUsers.size(); i++) {
			User banned = bannedUsers.get(i).bannedUser;
			List<User> searchResult = Users.searchOrganizer(organization);
			if (searchResult.contains(banned)) {
				finalBannedUsers.add(bannedUsers.get(i));
			}
		}
		List<BannedUser> entityBannedUsers = new ArrayList<BannedUser>();
		List<BannedUser> topicBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < finalBannedUsers.size(); i++) {
			BannedUser bannedUser = finalBannedUsers.get(i);
			if (bannedUser.resourceType.equalsIgnoreCase("topic")) {
				topicBannedUsers.add(bannedUser);
			}
			if (bannedUser.resourceType.equalsIgnoreCase("entity")) {
				entityBannedUsers.add(bannedUser);
			}
		}

		// List<BannedUser> sortedByEntity = sortByID(entityBannedUsers);
		// List<BannedUser> sortedByTopic = sortByID(topicBannedUsers);
		// List<BannedUser> sortByUserInEntity = sortByUser(entityBannedUsers);
		// List<BannedUser> sortByUserInTopic = sortByUser(topicBannedUsers);
		// List<BannedUser> sortByActionInEntity =
		// sortByAction(entityBannedUsers);
		// List<BannedUser> sortByActionInTopic =
		// sortByAction(topicBannedUsers);
		//
		// String ids = "";
		// String names = "";
		// for(int i = 0 ; i < sortedByEntity.size() ; i++){
		// ids = ids + sortedByEntity.get(i).resourceID + ",";
		//
		// MainEntity entity =
		// MainEntity.findById(sortedByEntity.get(i).resourceID);
		// names = names + (entity.name) + ",";
		//
		// }
		// boolean Flag = false; if(user == null ){
		// Flag = true;
		// }
		// System.out.println(Flag + "Yarubbbbbbbbbbbbbbbb");

		if (user.isAdmin || organization.creator.equals(user)) {
			render(user, organizationID, topicBannedUsers);
		} else {
			unauthorized();
		}

	}

	/**
	 * Renders the set of organizers within a certain entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S17 , C1S19
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
		User user = Security.getConnected();

		if (user.isAdmin || organization.creator.equals(user)) {
			render(user, users, entityId, organizationID);
		} else {
			unauthorized();
		}

	}

	/**
	 * Renders the set of organizers in a certain topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
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
		User user = Security.getConnected();

		if (user.isAdmin || organization.creator.equals(user)) {
			render(user, users, organizationID, topicId);
		} else {
			unauthorized();
		}

	}

	/**
	 * takes the Id of the organization and the list of ID of the User to be
	 * restricted,and renders the list of common entities the selected users are
	 * enrolled in and it will give the organization lead to restrict him from
	 * the whole selected entity or from a specific topic
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @author Nada Ossama
	 * 
	 * @param userID
	 *            long id of the user to be restricted
	 * @param organizationId
	 *            long id of the organization he will be restricted in
	 */

	public static void entitiesEnrolledInGroup(@Required long[] userId,
			long organizationId) {
		JsonObject json = new JsonObject();

		List<MainEntity> entities = new ArrayList<MainEntity>();
		List<MainEntity> finalEntities = new ArrayList<MainEntity>();
		if (validation.hasErrors() || userId.length == 0) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		} else {

			Organization organization = Organization.findById(organizationId);
			User user = User.findById(userId[0]);
			finalEntities = Users.getEntitiesOfOrganizer(organization, user);

			for (int i = 1; i < userId.length; i++) {
				user = User.findById(userId[i]);
				entities = Users.getEntitiesOfOrganizer(organization, user);
				for (int j = 0; j < finalEntities.size(); j++) {
					if (!entities.contains(finalEntities.get(j))) {
						finalEntities.remove(j);
					}
				}
			}

		}

		String entity = "";
		String entityIds = "";

		for (int i = 0; i < finalEntities.size(); i++) {
			entity += finalEntities.get(i).name + ",";
			entityIds += finalEntities.get(i).getId() + ",";
		}

		json.addProperty("entityIds", entityIds);
		json.addProperty("entityList", entity);
		renderJSON(json.toString());
	}

	/**
	 * takes the Id of the organization (the same as the previous method) and
	 * the ID of the User to be restricted,and renders the list of entities the
	 * selected user is enrolled in and it will give the organization lead to
	 * restrict him from the whole selected entity or from a specific topic
	 * 
	 * @story C1S7 , C1S19
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

		String entity = "";
		String entityIds = "";

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
	 * @story C1S7 , C1S19
	 * 
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

	/**
	 * Renders the List of Topics within the specified entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param entityId
	 *            : long entityId is the id of the entity that the list of
	 *            topics belongs to
	 * @param organizationId
	 *            : long organizationId is the id of the organization that the
	 *            topic belongs to
	 * @param userId
	 *            : long userId is the id of the user to be restricted
	 */

	public static void topicsEnrolledIn(@Required long entityId,
			long organizationId, long userId) {

		JsonObject json = new JsonObject();

		// if (validation.hasErrors() || entityId == 0) {
		// flash.error("Oops, please select atleast one choise ");
		// entitiesEnrolledIn(userId,organizationId);
		// }

		MainEntity entity = MainEntity.findById(entityId);
		List<Topic> entityTopics = entity.topicList;
		String topicsNames = "";
		String topicsIds = "";
		for (int i = 0; i < entityTopics.size(); i++) {
			topicsNames += entityTopics.get(i).title + ",";
			topicsIds += entityTopics.get(i).getId() + ",";
		}

		json.addProperty("topicsIds", topicsIds);
		json.addProperty("topicsNames", topicsNames);
		renderJSON(json.toString());

	}

	/**
	 * gets the list of topics in a certain entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param entityId
	 *            :long entityId the id of the entity that the topics belongs to
	 */

	public static void getEntityTopics(long entityId) {

		JsonObject json = new JsonObject();

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
	 * gets all the actions of the organizer
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 */
	public static void getAllEntityActions() {

		JsonObject json = new JsonObject();
		List<String> entityActions = Roles.getRoleActions("organizer");

		String stringActions = "";
		for (int i = 0; i < entityActions.size(); i++) {
			stringActions += entityActions.get(i) + ",";

		}

		json.addProperty("actions", stringActions);

		renderJSON(json.toString());

	}

	/**
	 * gets all the actions of the organizer role
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 */

	public static void getTopicActions() {

		JsonObject json = new JsonObject();

		List<String> topicActions = Roles.getOrganizerTopicActions();

		String topicsActions = "";
		for (int i = 0; i < topicActions.size(); i++) {
			topicsActions += topicActions.get(i) + ",";
		}

		json.addProperty("topicsActions", topicsActions);

		renderJSON(json.toString());

	}

	/**
	 * block the organizer from the whole entity so the method will render two
	 * lists one for the list of actions the organizer is allowed to do and
	 * another a list of actions the organizer is not allowed to do
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7, C1S19
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

		}
		json.addProperty("restrictedActions", restrictedActions);
		json.addProperty("actions", stringActions);

		renderJSON(json.toString());

	}

	/**
	 * render a list of actions the organizer is allowed to do in that topic and
	 * another list of actions the organizers is not allowed to do
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7 , C1S19
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

		for (int i = 0; i < topicActions.size(); i++) {

			if (restricted.contains(topicActions.get(i))) {
				topicActions.remove(i);
				i--;

			}
		}
		String topicsActions = "";
		for (int i = 0; i < topicActions.size(); i++) {
			topicsActions += topicActions.get(i) + ",";
		}
		String restrictedTopicActions = "";
		for (int i = 0; i < restricted.size(); i++) {
			restrictedTopicActions += restricted.get(i) + ",";
		}

		json.addProperty("topicsActions", topicsActions);
		json.addProperty("restrictedTopicActions", restrictedTopicActions);

		renderJSON(json.toString());

	}

	/**
	 * restricts the user from a list of actions according to the selection from
	 * the whole entity or from a certain topic according to the type
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param actionToDo
	 *            :String [] that represents the actions to be restricted from
	 * @param type
	 *            : String the type is either topic or entity
	 * @param entityTopicId
	 *            : long id of the topic or entity to be restricted in
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
				User restricted = User.findById(userId);
				User restricter = Security.getConnected();
				Log.addUserLog(
						"User " + restricter.firstName + " "
								+ restricter.lastName
								+ " has restricted the Organizer: "
								+ restricted.firstName + " "
								+ restricted.lastName + " from "
								+ actionToDo[i] + " In organization  : " + org
								+ " In Entity :" + entity + " In Topic :"
								+ topic, org, entity, topic);
				Notifications.sendNotification(userId, Security.getConnected()
						.getId(), "user",
						"you have been restricted from the following action :"
								+ actionToDo[i] + " In organization  : " + org
								+ " In Entity :" + entity + " In Topic :"
								+ topic);

			}

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

				User restricted = User.findById(userId);
				User restricter = Security.getConnected();
				Log.addUserLog("User " + restricter.firstName + " "
						+ restricter.lastName
						+ " has restricted the Organizer: "
						+ restricted.firstName + " " + restricted.lastName
						+ " from " + actionToDo[i] + " In organization  : "
						+ org + " In Entity :" + entity, org, entity);

				Notifications.sendNotification(userId, restricter.getId(),
						"user",
						"you have been restricted from the following action :"
								+ actionToDo[i] + " In organization  : " + org
								+ " In Entity :" + entity);

			}

		}

		return true;

	}

	/**
	 * restricts a list of users in a certain entity or topic from a list of
	 * actions according to the selection from the whole entity or from a
	 * certain topic according to the type
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param actionToDo
	 *            :String [] that represents the actions to be restricted from
	 * @param type
	 *            : String the type is either topic or entity
	 * @param entityTopicId
	 *            : long id of the topic or entity to be restricted in
	 */

	public static boolean restrictGroupFinal(@Required String[] actionToDo,
			String type, long entityTopicId) {

		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();
			List<User> organizers = Users.getEntityOrganizers(entity);

			for (int j = 0; j < organizers.size(); j++) {
				long userId = organizers.get(j).getId();
				for (int i = 0; i < actionToDo.length; i++) {
					changed = BannedUser.banFromActionInTopic(userId,
							organizationId, actionToDo[i], entityTopicId);

					User restricted = User.findById(userId);
					User restricter = Security.getConnected();
					Log.addUserLog("User " + restricter.firstName + " "
							+ restricter.lastName
							+ " has restricted the Organizer: "
							+ restricted.firstName + " " + restricted.lastName
							+ " from " + actionToDo[i] + " In organization  : "
							+ org + " In Entity :" + entity + " In Topic :"
							+ topic, org, entity, topic);

					Notifications.sendNotification(userId, Security
							.getConnected().getId(), "user",
							"you have been restricted from the following action :"
									+ actionToDo[i] + " In organization  : "
									+ org + " In Entity :" + entity
									+ " In Topic :" + topic);
				}

			}

		}

		else {

			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();
			List<User> organizers = Users.getEntityOrganizers(entity);

			for (int j = 0; j < organizers.size(); j++) {
				long userId = organizers.get(j).getId();
				for (int i = 0; i < actionToDo.length; i++) {
					changed = BannedUser.banFromActionInEntity(userId,
							organizationId, actionToDo[i], entityTopicId);

					User restricted = User.findById(userId);
					User restricter = Security.getConnected();
					Log.addUserLog("User " + restricter.firstName + " "
							+ restricter.lastName
							+ " has restricted the Organizer: "
							+ restricted.firstName + " " + restricted.lastName
							+ " from " + actionToDo[i] + " In organization  : "
							+ org + " In Entity :" + entity, org, entity);

					Notifications.sendNotification(userId, Security
							.getConnected().getId(), "user",
							"you have been restricted from the following action :"
									+ actionToDo[i] + " In organization  : "
									+ org + " In Entity :" + entity);

				}

			}

		}

		return changed;

	}

	/**
	 * De-Restricts a certain user from a list of actions according to the
	 * selection in the whole entity or in a certain topic according to the type
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S19
	 * 
	 * @param userId
	 *            : long userId is the id of the user to be de-restricted
	 * 
	 * @param actionsRestricted
	 *            : String [] that represents the list of actions to be
	 *            de-restricted from
	 * @param entityTopicID
	 *            : long id of the entity or topic to be de-restricted from
	 * @param type
	 *            : String type that specifies whether to de-restrict the user
	 *            in an entity or topic
	 */

	public static void deRestrict(long userId, String[] actionsRestricted,
			long entityTopicID, String type) {

		if (type.equals("topic")) {

			Topic topic = Topic.findById(entityTopicID);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;

			for (int i = 0; i < actionsRestricted.length; i++) {
				System.out.println("In BannedUsers : Topic deRest."
						+ actionsRestricted[i]);
				BannedUser.deRestrictFromTopic(userId, actionsRestricted[i],
						entityTopicID);

				User restricted = User.findById(userId);
				User restricter = Security.getConnected();
				Log.addUserLog("User " + restricter.firstName + " "
						+ restricter.lastName
						+ " has de-restricted the Organizer: "
						+ restricted.firstName + " " + restricted.lastName
						+ " from " + actionsRestricted[i]
						+ " In organization  : " + org + " In Entity :"
						+ entity + " In Topic :" + topic, org, entity, topic);

				Notifications.sendNotification(userId, Security.getConnected()
						.getId(), "user",
						"you have been de-restricted from the following action :"
								+ actionsRestricted[i] + " In organization  : "
								+ org + " In Entity :" + entity + " In Topic :"
								+ topic);

			}
		} else {

			MainEntity entity = MainEntity.findById(entityTopicID);
			Organization org = entity.organization;

			for (int i = 0; i < actionsRestricted.length; i++) {
				System.out.println("In bannedUsers: Entity de-Restriction"
						+ actionsRestricted[i]);
				BannedUser.deRestrictFromEntityWithCascading(userId,
						actionsRestricted[i], entityTopicID);

				User restricted = User.findById(userId);
				User restricter = Security.getConnected();
				Log.addUserLog("User " + restricter.firstName + " "
						+ restricter.lastName
						+ " has de-restricted the Organizer: "
						+ restricted.firstName + " " + restricted.lastName
						+ " from " + actionsRestricted[i]
						+ " In organization  : " + org + " In Entity :"
						+ entity, org, entity);

				Notifications.sendNotification(userId, Security.getConnected()
						.getId(), "user",
						"you have been de-restricted from the following action :"
								+ actionsRestricted[i] + " In organization  : "
								+ org + " In Entity :" + entity);

			}
		}

	}

	/**
	 * De-Restricts a group of users from a list of actions according to the
	 * selection in the whole entity or in a certain topic according to the type
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S19
	 * 
	 * @param userId
	 *            : long userId is the id of the user to be de-restricted
	 * 
	 * @param actionsRestricted
	 *            : String [] that represents the list of actions to be
	 *            de-restricted from
	 * @param entityTopicID
	 *            : long id of the entity or topic to be de-restricted from
	 * @param type
	 *            : String type that specifies whether to de-restrict the user
	 *            in an entity or topic
	 */

	public static void deRestrictGroup(String[] actionsRestricted,
			long entityTopicID, String type) {

		if (type.equals("topic")) {

			Topic topic = Topic.findById(entityTopicID);
			MainEntity entity = topic.entity;
			List<User> organizers = Users.getEntityOrganizers(entity);
			Organization org = entity.organization;

			for (int j = 0; j < organizers.size(); j++) {
				long userId = organizers.get(j).getId();
				for (int i = 0; i < actionsRestricted.length; i++) {
					System.out.println("In BannedUsers : Topic deRest."
							+ actionsRestricted[i]);
					BannedUser.deRestrictFromTopic(userId,
							actionsRestricted[i], entityTopicID);

					User restricted = User.findById(userId);
					User restricter = Security.getConnected();
					Log.addUserLog("User " + restricter.firstName + " "
							+ restricter.lastName
							+ " has de-restricted the Organizer: "
							+ restricted.firstName + " " + restricted.lastName
							+ " from " + actionsRestricted[i]
							+ " In organization  : " + org + " In Entity :"
							+ entity + " In Topic :" + topic, org, entity,
							topic);

					Notifications.sendNotification(userId, Security
							.getConnected().getId(), "user",
							"you have been de-restricted from the following action :"
									+ actionsRestricted[i]
									+ " In organization  : " + org
									+ " In Entity :" + entity + " In Topic :"
									+ topic);

				}

			}
		} else {
			MainEntity entity = MainEntity.findById(entityTopicID);
			List<User> organizers = Users.getEntityOrganizers(entity);
			Organization org = entity.organization;

			for (int j = 0; j < organizers.size(); j++) {
				long userId = organizers.get(j).getId();
				for (int i = 0; i < actionsRestricted.length; i++) {
					System.out.println("In bannedUsers: Entity de-Restriction"
							+ actionsRestricted[i]);
					BannedUser.deRestrictFromEntityWithCascading(userId,
							actionsRestricted[i], entityTopicID);
					User restricted = User.findById(userId);
					User restricter = Security.getConnected();
					Log.addUserLog("User " + restricter.firstName + " "
							+ restricter.lastName
							+ " has de-restricted the Organizer: "
							+ restricted.firstName + " " + restricted.lastName
							+ " from " + actionsRestricted[i]
							+ " In organization  : " + org + " In Entity :"
							+ entity, org, entity);

					Notifications.sendNotification(userId, Security
							.getConnected().getId(), "user",
							"you have been de-restricted from the following action :"
									+ actionsRestricted[i]
									+ " In organization  : " + org
									+ " In Entity :" + entity);

				}

			}

		}

	}

	/**
	 * Displays the list of organizers that are restricted from certain actions
	 * in a certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param organizationID
	 *            long id of the organization that the organizer is restricted
	 *            in
	 */

	public static void viewRestrictedOrganizersInOrganization(
			long organizationID) {
		Organization organization = Organization.findById(organizationID);
		User user = Security.getConnected();
		List<BannedUser> bannedUsers = BannedUser.find(
				"select bu from BannedUser bu where bu.organization = ?",
				organization).fetch();
		List<BannedUser> finalBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < bannedUsers.size(); i++) {
			User banned = bannedUsers.get(i).bannedUser;
			List<User> searchResult = Users.searchOrganizer(organization);
			if (searchResult.contains(banned)) {
				finalBannedUsers.add(bannedUsers.get(i));
			}
		}

		List<BannedUser> entityBannedUsers = new ArrayList<BannedUser>();
		List<BannedUser> topicBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < finalBannedUsers.size(); i++) {
			BannedUser bannedUser = finalBannedUsers.get(i);

			if (bannedUser.resourceType.equalsIgnoreCase("topic")) {
				topicBannedUsers.add(bannedUser);

			}
			if (bannedUser.resourceType.equalsIgnoreCase("entity")) {
				entityBannedUsers.add(bannedUser);

			}
		}

		// List<BannedUser> sortedByEntity = sortByID(entityBannedUsers);
		// List<BannedUser> sortedByTopic = sortByID(topicBannedUsers);
		// List<BannedUser> sortByUserInEntity = sortByUser(entityBannedUsers);
		// List<BannedUser> sortByUserInTopic = sortByUser(topicBannedUsers);
		// List<BannedUser> sortByActionInEntity =
		// sortByAction(entityBannedUsers);
		// List<BannedUser> sortByActionInTopic =
		// sortByAction(topicBannedUsers);
		//
		// String ids = "";
		// String names = "";
		// for(int i = 0 ; i < sortedByEntity.size() ; i++){
		// ids = ids + sortedByEntity.get(i).resourceID + ",";
		//
		// MainEntity entity =
		// MainEntity.findById(sortedByEntity.get(i).resourceID);
		// names = names + (entity.name) + ",";
		//
		// }

		if (user.isAdmin || organization.creator.equals(user)) {
			render(user, organizationID, entityBannedUsers);
		} else {
			unauthorized();
		}

	}

	/**
	 * Sorts the list of banned users by the resource id
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param toBeSorted
	 *            :List<BannedUser> that requires sorting
	 * @return List<BannedUser> :sorted list
	 */
	public static List<BannedUser> sortByID(List<BannedUser> toBeSorted) {
		List<BannedUser> sorted = new ArrayList<BannedUser>();
		String type = "";
		for (int i = 0; i < toBeSorted.size(); i++) {
			BannedUser banned = toBeSorted.get(i);
			type = toBeSorted.get(0).resourceType;
			if (sorted.isEmpty()) {
				sorted.add(banned);
			} else {
				boolean added = false;
				for (int j = 0; j < sorted.size(); j++) {
					if (type.equalsIgnoreCase("entity")) {
						MainEntity entityOne = MainEntity
								.findById(banned.resourceID);
						System.out.println("test" + banned.resourceID);
						MainEntity entityTwo = MainEntity.findById(sorted
								.get(j).resourceID);
						if (entityOne.name.compareToIgnoreCase(entityTwo.name) < 0) {
							added = true;
							sorted.add(j, banned);
							break;
						}
					} else {

						Topic topicOne = Topic.findById(banned.resourceID);
						System.out.println("test" + banned.resourceID);
						Topic topicTwo = Topic
								.findById(sorted.get(j).resourceID);

						if (topicOne.title.compareToIgnoreCase(topicTwo.title) < 0) {
							added = true;
							sorted.add(j, banned);
							break;
						}
					}

				}
				if (!added) {
					sorted.add(banned);
				}
			}

		}
		return sorted;
	}

	/**
	 * maps the id of an entity or a topic to it's name or title
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param idd
	 *            , String idd is the of the object to be mapped
	 * 
	 * @param type
	 *            , is the type of the object to be mapped
	 */

	public static String maps(String idd, String type) {

		long id = Long.parseLong(idd);
		String name = "";
		// JsonObject json = new JsonObject();
		if (type.equalsIgnoreCase("entity")) {
			MainEntity entity = MainEntity.findById(id);
			name = entity.name;

		} else {
			Topic topic = Topic.findById(id);
			name = topic.title;
		}

		// json.addProperty("namee", name);
		// System.out.println(name);
		// renderJSON(json.toString());
		return (name);
	}

	/**
	 * maps the action , banned user name , resource name given a certain type
	 * (entity/topic) number to know which list to map from and order is the
	 * index in that list
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param order
	 *            int order is the index in the list to map from
	 * 
	 * @param organizationID
	 *            long organization id that the viewed organizers are restricted
	 *            in
	 * @param number
	 *            int number to know which list to map from
	 * @param type
	 *            is the type of the source the organizer is restricted from
	 *            (entity/topic)
	 */
	public static void go(int order, long organizationID, int number,
			String type) {

		Organization organization = Organization.findById(organizationID);

		List<BannedUser> bannedUsers = BannedUser.find(
				"select bu from BannedUser bu where bu.organization = ?",
				organization).fetch();
		List<BannedUser> finalBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < bannedUsers.size(); i++) {
			User banned = bannedUsers.get(i).bannedUser;
			List<User> searchResult = Users.searchOrganizer(organization);
			if (searchResult.contains(banned)) {
				finalBannedUsers.add(bannedUsers.get(i));
			}
		}
		List<BannedUser> entityBannedUsers = new ArrayList<BannedUser>();
		List<BannedUser> topicBannedUsers = new ArrayList<BannedUser>();

		for (int i = 0; i < finalBannedUsers.size(); i++) {
			BannedUser bannedUser = finalBannedUsers.get(i);
			if (bannedUser.resourceType.equalsIgnoreCase("topic")) {
				topicBannedUsers.add(bannedUser);
			}
			if (bannedUser.resourceType.equalsIgnoreCase("entity")) {
				entityBannedUsers.add(bannedUser);
			}
		}

		List<BannedUser> sortedByEntity = sortByID(entityBannedUsers);
		List<BannedUser> sortedByTopic = sortByID(topicBannedUsers);
		List<BannedUser> sortByUserInEntity = sortByUser(entityBannedUsers);
		List<BannedUser> sortByUserInTopic = sortByUser(topicBannedUsers);
		List<BannedUser> sortByActionInEntity = sortByAction(entityBannedUsers);
		List<BannedUser> sortByActionInTopic = sortByAction(topicBannedUsers);

		// String ids = "";
		// String names = "";
		// for(int i = 0 ; i < sortedByEntity.size() ; i++){
		// ids = ids + sortedByEntity.get(i).resourceID + ",";
		//
		// MainEntity entity =
		// MainEntity.findById(sortedByEntity.get(i).resourceID);
		// names = names + (entity.name) + ",";
		//
		// }
		// System.out.println(ids);
		//

		if (number == 4) {
			JsonObject json = new JsonObject();
			String myname = sortByActionInTopic.get(order).bannedUser.firstName
					+ " " + sortByActionInTopic.get(order).bannedUser.lastName;
			String myaction = sortByActionInTopic.get(order).action;
			String myresourceId = sortByActionInTopic.get(order).resourceID
					+ "";
			json.addProperty("name", myname);
			json.addProperty("action", myaction);
			json.addProperty("resourceId", myresourceId);
			String name = maps(myresourceId, type);
			json.addProperty("namee", name);
			renderJSON(json.toString());
		}
		if (number == 5) {

			JsonObject json = new JsonObject();
			String myname = sortedByTopic.get(order).bannedUser.firstName + " "
					+ sortedByTopic.get(order).bannedUser.lastName;
			String myaction = sortedByTopic.get(order).action;
			String myresourceId = sortedByTopic.get(order).resourceID + "";
			json.addProperty("name", myname);
			json.addProperty("action", myaction);
			json.addProperty("resourceId", myresourceId);
			String name = maps(myresourceId, type);
			json.addProperty("namee", name);
			System.out.println(myresourceId);
			renderJSON(json.toString());
		}
		if (number == 6) {
			JsonObject json = new JsonObject();
			String myname = sortByUserInTopic.get(order).bannedUser.firstName
					+ " " + sortByUserInTopic.get(order).bannedUser.lastName;
			String myaction = sortByUserInTopic.get(order).action;
			String myresourceId = sortByUserInTopic.get(order).resourceID + "";
			json.addProperty("name", myname);
			json.addProperty("action", myaction);
			json.addProperty("resourceId", myresourceId);
			String name = maps(myresourceId, type);
			json.addProperty("namee", name);
			renderJSON(json.toString());
		}
		if (number == 1) {
			JsonObject json = new JsonObject();
			String myname = sortedByEntity.get(order).bannedUser.firstName
					+ " " + sortedByEntity.get(order).bannedUser.lastName;
			String myaction = sortedByEntity.get(order).action;
			String myresourceId = sortedByEntity.get(order).resourceID + "";
			json.addProperty("name", myname);
			json.addProperty("action", myaction);
			json.addProperty("resourceId", myresourceId);
			String name = maps(myresourceId, type);
			json.addProperty("namee", name);
			renderJSON(json.toString());
		} else {
			if (number == 2) {
				JsonObject json = new JsonObject();
				String myname = sortByUserInEntity.get(order).bannedUser.firstName
						+ " "
						+ sortByUserInEntity.get(order).bannedUser.lastName;
				String myaction = sortByUserInEntity.get(order).action;
				String myresourceId = sortByUserInEntity.get(order).resourceID
						+ "";
				json.addProperty("name", myname);
				json.addProperty("action", myaction);
				json.addProperty("resourceId", myresourceId);
				String name = maps(myresourceId, type);
				json.addProperty("namee", name);
				renderJSON(json.toString());
			} else {

				JsonObject json = new JsonObject();
				String myname = sortByActionInEntity.get(order).bannedUser.firstName
						+ " "
						+ sortByActionInEntity.get(order).bannedUser.lastName;
				String myaction = sortByActionInEntity.get(order).action;
				String myresourceId = sortByActionInEntity.get(order).resourceID
						+ "";
				json.addProperty("name", myname);
				json.addProperty("action", myaction);
				json.addProperty("resourceId", myresourceId);
				String name = maps(myresourceId, type);
				json.addProperty("namee", name);

				renderJSON(json.toString());

			}

		}

	}

	/**
	 * sorts the BannedUser list by the first Name of the user
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param toBeSorted
	 *            :List<BannedUser> that requires sorting
	 * @return List<BannedUser> : sorted List
	 */
	public static List<BannedUser> sortByUser(List<BannedUser> toBeSorted) {
		List<BannedUser> sorted = new ArrayList<BannedUser>();
		for (int i = 0; i < toBeSorted.size(); i++) {
			BannedUser banned = toBeSorted.get(i);
			if (sorted.isEmpty()) {
				sorted.add(banned);
			} else {
				boolean added = false;
				for (int j = 0; j < sorted.size(); j++) {
					if (banned.bannedUser.firstName
							.compareTo(sorted.get(j).bannedUser.firstName) < 0) {
						added = true;
						sorted.add(j, banned);
						break;
					}
				}
				if (!added) {
					sorted.add(banned);
				}
			}

		}
		return sorted;
	}

	/**
	 * sorted a list of BannedUser by Action
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param toBeSorted
	 *            : List<BannedUser> that requires sorting
	 * 
	 * @return List<BannedUser> sorted List
	 */

	public static List<BannedUser> sortByAction(List<BannedUser> toBeSorted) {
		List<BannedUser> sorted = new ArrayList<BannedUser>();

		for (int i = 0; i < toBeSorted.size(); i++) {
			BannedUser banned = toBeSorted.get(i);
			if (sorted.isEmpty()) {
				sorted.add(banned);
			} else {
				boolean added = false;
				for (int j = 0; j < sorted.size(); j++) {
					if (banned.action.compareToIgnoreCase(sorted.get(j).action) < 0) {
						added = true;
						sorted.add(j, banned);

						break;

					}
				}
				if (!added) {
					sorted.add(banned);
				}
			}

		}
		return sorted;
	}

	/**
	 * renders the list of entities in a certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S16
	 * 
	 * @param organizationID
	 *            : long id of the organization
	 */
	public static void dummy() {
		restrictGroupInSelectedEntity(1);
	}

	public static void restrictGroupInSelectedEntity(long organizationID) {

		Organization organization = Organization.findById(organizationID);
		List<MainEntity> organizationEntities = organization.entitiesList;
		User user = Security.getConnected();
		if (user.isAdmin || organization.creator.equals(user)) {
			render(user, organizationEntities, organizationID);
		} else {
			unauthorized();
		}

	}

	/**
	 * gets the list of organizers of a certain entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7 , C1S19
	 * 
	 * @param entityId
	 *            : long id of the entity that the organizers are enrolled in
	 */
	public static void getOrganizersInEntity(long entityId) {

		JsonObject json = new JsonObject();

		MainEntity entity = MainEntity.findById(entityId);
		List<User> organizers = Users.getEntityOrganizers(entity);
		String organizersNames = "";
		String organizersIDs = "";

		for (int i = 0; i < organizers.size(); i++) {
			organizersNames += organizers.get(i).firstName + " "
					+ organizers.get(i).lastName + ",";
			organizersIDs += organizers.get(i).getId() + ",";
		}
		json.addProperty("organizersNames", organizersNames);
		json.addProperty("organizersIDs", organizersIDs);

		renderJSON(json.toString());

	}

	/**
	 * Renders the list of idea developers enrolled in the entity/topic to the
	 * organizer/lead/admin to be blocked/unblocked
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S16
	 * 
	 * @param numId
	 *            long Id of the topic/entity that has the ideadevelopers
	 * 
	 * @param num
	 *            int num , 1 if the button in topic page / 0 if entity page
	 * 
	 */

	public static void viewUsers(long numId, int num) {
          User user=Security.getConnected();
		
		if (num == 0) {
			int check = 0;
			if (Users.isPermitted(user,
					"block a user from viewing or using a certain entity",
					numId, "entity"))
				check = 1;
			
			List<User> users = Users.getIdeaDevelopers(numId, "entity");
			MainEntity object = MainEntity.findById(numId);
			List<Integer> count = new ArrayList<Integer>();
			for (int i = 0; i < users.size(); i++) {
				BannedUser banned1 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "view", "entity", object.id).first();
				BannedUser banned2 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "use", "entity", object.id).first();
				if (banned1 == null && banned2 == null)
					count.add(0);
				else if (banned1 != null && banned2 != null)
					count.add(3);
				else if (banned2 != null)
					count.add(2);
				else if (banned1 != null)
					count.add(1);

			}

			render(users, count, object, num,user,check);
		} else {
			
			int check = 0;
			if (Users.isPermitted(user,
					"block a user from viewing or using a certain entity",
					numId, "entity"))
				check = 1;
			List<User> users = Users.getIdeaDevelopers(numId, "topic");
			Topic object = Topic.findById(numId);
			List<Integer> count = new ArrayList<Integer>();
			for (int i = 0; i < users.size(); i++) {
				BannedUser banned1 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "view", "topic", object.id).first();
				BannedUser banned2 = BannedUser.find(
						"byBannedUserAndActionAndResourceTypeAndResourceID",
						users.get(i), "use", "topic", object.id).first();
				if (banned1 == null && banned2 == null)
					count.add(0);
				else if (banned1 != null && banned2 != null)
					count.add(3);
				else if (banned2 != null)
					count.add(2);
				else if (banned1 != null)
					count.add(1);

			}

			render(users, count, object, num,user,check);

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
	 * 
	 * @param type
	 *            int type, 0 if view/1 if use
	 * 
	 * @param numId
	 *            long Id of the topic/entity that the user ll be
	 *            blocked/unblocked from
	 * 
	 * @param id
	 *            int id , 0 if entity/1 if topic
	 * 
	 * @param text
	 *            String text that is the text on the button represents the
	 *            action if (block or unblock)
	 * 
	 * @param message
	 *            String message that is the reason why this person has been
	 *            blocked
	 * 
	 */

	public static void block(long userId, int type, long numId, int id,
			String text, String message) {
		System.out.println("OK");
		System.out.println(message);
		System.out.println("here");
		System.out.println(text);
		System.out.println(userId);
		System.out.println(type);
		System.out.println(numId);
		System.out.println(id);

		User user = User.findById(userId);
		User organizer = Security.getConnected();

		List<User> organizers = new ArrayList<User>();
		MainEntity entity = null;
		Topic topic = null;

		if (id == 0) {
			entity = MainEntity.findById(numId);
			// organizers = Users.getEntityOrganizers(entity);

			if (type == 0) {

				if (text.equals("Block from viewing")) {
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " blocked " + user.firstName
							+ " " + user.lastName + " from viewing Entity "
							+ entity.name, user, organizer, entity,
							entity.organization);
					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from viewing Entity "
												+ entity.name + " because "
												+ message);
					Notifications.sendNotification(userId, numId, "entity",
							"You have been blocked from viewing entity "
									+ entity.name + " because " + message);
					doBlock(userId, numId, 0, 0);

				} else {

					System.out.println("LAMA");
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " unblocked "
							+ user.firstName + " " + user.lastName
							+ " from viewing Entity " + entity.name, user,
							organizer, entity, entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from viewing Entity "
												+ entity.name);
					Notifications.sendNotification(userId, numId, "entity",
							"You have been unblocked from viewing entity "
									+ entity.name);
					doBlock(userId, numId, 1, 0);
					System.out.println("HEREEEEEEEEEEE LAMA");

				}
			} else {
				if (text.equals("Block from using")) {
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " blocked " + user.firstName
							+ " " + user.lastName + " from using Entity "
							+ entity.name, user, organizer, entity,
							entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from performing any action within Entity "
												+ entity.name + " because "
												+ message);
					Notifications.sendNotification(userId, numId, "entity",
							"You have been blocked from performing any action within entity "
									+ entity.name + " because " + message);
					doBlock(userId, numId, 0, 1);

				} else {
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " unblocked "
							+ user.firstName + " " + user.lastName
							+ " from using Entity " + entity.name, user,
							organizer, entity, entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from performing any action within Entity "
												+ entity.name);
					Notifications.sendNotification(userId, numId, "entity",
							"You have been ublocked from performing any action within entity "
									+ entity.name + " because " + message);
					doBlock(userId, numId, 1, 1);

				}
			}
		} else {

			System.out.println("YES");
			topic = Topic.findById(numId);
			entity = topic.entity;
			organizers = Users.getEntityOrganizers(entity);
			organizers.remove(user);

			if (type == 0) {
				System.out.println("view");
				if (text.equals("Block from viewing")) {
					System.out.println("vvvvvvvvv");
					BannedUser block = new BannedUser(user,
							topic.entity.organization, "view", "topic", numId);
					block.save();
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " blocked " + user.firstName
							+ " " + user.lastName + " from viewing Topic "
							+ topic.title, user, organizer, topic,
							entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from viewing topic "
												+ topic.title + " because "
												+ message);
					Notifications.sendNotification(userId, numId, "topic",
							"You have been blocked from viewing topic "
									+ topic.title + " because " + message);

				} else {
					System.out.println("LAMALEEEM");
					BannedUser unblock = BannedUser
							.find("byBannedUserAndActionAndResourceTypeAndResourceID",
									user, "view", "topic", numId).first();
					unblock.delete();

					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " unblocked "
							+ user.firstName + " " + user.lastName
							+ " from viewing Topic " + topic.title, user,
							organizer, topic, entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from performing any action within topic "
												+ topic.title);
					Notifications.sendNotification(userId, numId, "topic",
							"You have been unblocked from viewing topic "
									+ topic.title);
				}
			} else {
				if (text.equals("Block from using")) {

					System.out.println("use");
					BannedUser block = new BannedUser(user,
							topic.entity.organization, "use", "topic", numId);
					block.save();
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " blocked " + user.firstName
							+ " " + user.lastName + " from using Topic "
							+ topic.title, user, organizer, topic,
							entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been blocked from performing any action within topic "
												+ topic.title + " because "
												+ message);
					Notifications.sendNotification(userId, numId, "topic",
							"You have been blocked from performing any action within topic "
									+ topic.title + " because " + message);

				} else {
					System.out.println("useeeeeeeee");
					BannedUser unblock = BannedUser
							.find("byBannedUserAndActionAndResourceTypeAndResourceID",
									user, "use", "topic", numId).first();
					unblock.delete();
					Log.addUserLog(organizer.firstName + " "
							+ organizer.lastName + " unblocked "
							+ user.firstName + " " + user.lastName
							+ " from using Topic " + topic.title, user,
							organizer, topic, entity.organization);

					for (int i = 0; i < organizers.size(); i++)
						Notifications
								.sendNotification(
										organizers.get(i).id,
										numId,
										"topic",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ " has been unblocked from performing any action within topic "
												+ topic.title);
					Notifications.sendNotification(userId, numId, "topic",
							"You have been unblocked from performing any action within topic "
									+ topic.title);
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
	 *            long Id of entity that the user ll be blocked/unblocked from
	 * 
	 * @param choice
	 *            int choice, 0 if block/1 if unblock
	 * 
	 * @param type
	 *            int type (0 if view/1 if use) represnts the action the user ll
	 *            be blocked/unblocked from
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
