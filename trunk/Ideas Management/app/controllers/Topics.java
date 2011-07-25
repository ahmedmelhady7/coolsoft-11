package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import models.BannedUser;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.Idea;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.RenameEndRelationshipRequest;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import com.google.gson.JsonObject;
import com.sun.mail.iap.Response;

import controllers.CRUD.ObjectType;
import play.data.binding.*;
import play.db.*;
import play.exceptions.*;
import play.i18n.*;
import play.mvc.With;
import models.*;
import notifiers.Mail;

/**
 * @author ${Alia El-Bolock}
 * 
 */
@With(Secure.class)
public class Topics extends CoolCRUD {
	static List<User> joinedUsersInTopic = new ArrayList<User>();

	/**
	 * renders the related topic, entity the topic belongs to and the list of
	 * other topics in the organization to the view
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param topicId
	 *            : id of the topic to be related
	 * 
	 * @param entityId
	 *            : id of the entity the topic belongs to
	 */
	public static void createRelation(long topicId, long entityId) {

		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		MainEntity entity = MainEntity.findById(entityId);
		List<Topic> listOfTopics = null;
		if (entity.topicList != null) {
			listOfTopics = entity.topicList;
			listOfTopics.remove(topic);
		}
		for (int i = 0; i < listOfTopics.size(); i++) {
			if (!listOfTopics.get(i).createRelationship) {
				listOfTopics.remove(listOfTopics.get(i));
			}
		}
		render(topic, entity, listOfTopics, user);
	}

	/**
	 * This method first checks if the user is allowed to tag the topic,
	 * searches for the tag in the global list of tags, if found => check if it
	 * already the topic had the same tag already or add the new one to the list
	 * if not => create a new tag, save it to db, add it to the list send
	 * notifications to followers, organizers and organization lead of the
	 * tagged topic
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S2
	 * 
	 * @param topicId
	 *            : the topic that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void tagTopic(long topicId, String tag) {
		Tag tempTag = null;
		List<Tag> listOfTags = getAvailableTags(topicId);
		boolean tagAlreadyExists = false;
		boolean tagExists = false;
		User user = (User) Security.getConnected();
		Topic topic = (Topic) Topic.findById(topicId);
		notFoundIfNull(topic);

		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				tempTag = listOfTags.get(i);
				if (!topic.tags.contains(tempTag)) {
					topic.tags.add(tempTag);
					tempTag.taggedTopics.add(topic);
					tempTag.save();

					for (int j = 0; j < tempTag.followers.size(); j++) {
						Notifications.sendNotification(
								tempTag.followers.get(j).id, tempTag.getId(),
								"tag", "This topic has been tagged as " + tag);
					}
				} else {
					tagAlreadyExists = true;
				}
				tagExists = true;
			}
		}

		if (!tagExists && topic.entity.organization.createTag) {
			tempTag = new Tag(tag, topic.entity.organization, user);
			topic.tags.add(tempTag);
			tempTag.taggedTopics.add(topic);
			tempTag.save();
		}

		if (!tagAlreadyExists) {
			for (int j = 0; j < topic.followers.size(); j++) {
				Notifications.sendNotification(topic.followers.get(j).id,
						topicId, "topic", "This topic has been tagged as "
								+ tag);
			}

			for (int j = 0; j < topic.getOrganizer().size(); j++) {
				Notifications.sendNotification(topic.getOrganizer().get(j).id,
						topicId, "topic", "This topic has been tagged as "
								+ tag);
			}
			Notifications.sendNotification(
					topic.entity.organization.creator.id, topicId, "topic",
					"This topic has been tagged as " + tag);

			String logDescription = "<a href=\"/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>"
					+ " added the tag " + "<a href=\"/tags/mainpage?tagId="
					+ tempTag.id + "\">" + tempTag.name + "</a>"
					+ " to the topic " + "<a href=\"/topics/show?topicId="
					+ topic.id + "\">" + topic.title + "</a>";
			Log.addUserLog(logDescription, topic);

		}
		topic.save();
		JsonObject json = new JsonObject();
		json.addProperty("tagName", tempTag.name);
		json.addProperty("tagId", tempTag.id);
		renderJSON(json.toString());
	}

	/**
	 * 
	 * @description this method checks if the given tag is already in the list
	 *              of tags related to this topic to avoid duplication
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S2
	 * 
	 * @param topicId
	 *            : the topic that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void checkIfTagAlreadyExists(long topicId, String tag) {
		Topic topic = (Topic) Topic.findById(topicId);
		notFoundIfNull(topic);
		boolean tagAlreadyExists = false;
		List<Tag> listOfTags = topic.tags;
		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				tagAlreadyExists = true;
				break;
			} else {
				tagAlreadyExists = false;
			}
		}
		JsonObject json = new JsonObject();
		json.addProperty("tagAlreadyExists", tagAlreadyExists);
		renderJSON(json.toString());
	}

	/**
	 * 
	 * @description this method returns the list of tags that are available for
	 *              that topic to be tagged with
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S2
	 * 
	 * @param topicId
	 *            : the topic that is being tagged
	 * 
	 * @return list of tags
	 * 
	 */

	public static List<Tag> getAvailableTags(long topicId) {
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		notFoundIfNull(globalListOfTags);
		Topic topic = Topic.findById(topicId);
		notFoundIfNull(topic);
		for (int i = 0; i < globalListOfTags.size(); i++) {
			if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
					|| topic.entity.organization
							.equals(globalListOfTags.get(i).createdInOrganization)) {
				listOfTags.add(globalListOfTags.get(i));
			}
		}
		return listOfTags;
	}

	/**
	 * 
	 * This method is responsible for posting an idea to a topic by a certain
	 * user
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param topicId
	 *            : the id of topic which the idea belongs/added to
	 * 
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description of the idea
	 * 
	 * 
	 */

	public static void postIdea(long topicId, String title, String description) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		if (title.equals(null) || description.equals(null)
				|| title.trim().equals("") || description.trim().equals(""))
			return;
		for (int i = 0; i < topic.ideas.size(); i++) {
			if (topic.ideas.get(i).isDraft
					&& topic.ideas.get(i).title.equals(title)
					&& topic.ideas.get(i).author.username.equals(user.username)) {
				Idea idea = Idea.findById(topic.ideas.get(i).id);
				idea.delete();
			}
		}
		Idea idea = new Idea(title, description, user, topic).save();
		topic.ideas.add(idea);
		topic.save();
		user.communityContributionCounter++;
		user.save();
		idea.save();
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>" + "posted an  "
				+ "<a href=\"/ideas/show?ideaId=" + idea.id + "\">"
				+ idea.title + "</a>" + " to topic "
				+ "<a href=\"/topics/show?topicId=" + topic.id + "\">"
				+ topic.title + "</a>";
		Log.addUserLog(logDescription, user, idea, topic, topic.entity,
				topic.entity.organization);
		redirect("/topics/show?topicId=" + topicId);
	}

	/**
	 * 
	 * @description This method reopens a closed topic, used after its plan gets
	 *              deleted
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S22
	 * 
	 * @param topicId
	 *            : the id of the topic that to be reopened
	 */
	public static void reopen(long topicId) {

		User user = Security.getConnected();
		Topic targetTopic = Topic.findById(topicId);
		notFoundIfNull(targetTopic);
		targetTopic.openToEdit = true;
		targetTopic.save();

		String log = "<a href=\"/users/viewprofile?userId=" + user.id + "\">"
				+ user.username + "</a>" + " reopened Topic "
				+ "<a href=\"/topics/show?topicId=" + targetTopic.id + "\">"
				+ targetTopic.title + "</a>";

		MainEntity entity = targetTopic.entity;
		Organization organization = entity.organization;

		Log.addUserLog(log, user, targetTopic, entity, organization);

		redirect("/topics/show?topicId=" + topicId);

	}

	/**
	 * This Method sends a request to post on a topic for a user to the
	 * organizer
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S13
	 * 
	 * @param topicId
	 *            the id of the topic
	 * 
	 */

	public static void addRequest(long topicId) {
		User user = Security.getConnected();
		Topic topict = Topic.findById(topicId);
		topict.requestFromUserToPost(user.id);
	}

	/**
	 * searches for unblocked users who are allowed to post in a certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S13
	 * 
	 * @param topicId
	 *            : a long id of the topic to search in
	 * 
	 * @return List<User>
	 */

	public static List<User> searchByTopic(long topicId) {

		Topic topic = Topic.findById(topicId);
		MainEntity entity = topic.entity;
		Organization org = entity.organization;

		ArrayList<User> searchList = (ArrayList) User.find("byIsAdmin", true)
				.fetch();
		if (!searchList.contains(org.creator))
			searchList.add(org.creator);
		ArrayList<User> organizers = (ArrayList) topic.getOrganizer();
		for (int i = 0; i < organizers.size(); i++) {
			if (!searchList.contains(organizers.get(i)))
				searchList.add(organizers.get(i));
		}

		List<BannedUser> bannedUserTopic = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "topic", topicId).fetch(); // List of blocked users from
													// a topic
		List<BannedUser> bannedUserEntity = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "entity", entity.id).fetch(); // list of blocked users
		// from an entity
		List<BannedUser> bannedUserOrg = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "organization", org.id).fetch(); // list of blocked user
		// from an organization

		List<User> bannedUsers = new ArrayList<User>();
		List<User> user = new ArrayList<User>();
		List<BannedUser> bannedUser = new ArrayList<BannedUser>(); // list
		// appending all the previous banned user lists
		bannedUser.addAll(bannedUserTopic);
		bannedUser.addAll(bannedUserEntity);
		bannedUser.addAll(bannedUserOrg);
		for (int i = 0; i < bannedUser.size(); i++) {
			bannedUsers.add((bannedUser.get(i)).bannedUser);
		}

		List<UserRoleInOrganization> allUser = new ArrayList<UserRoleInOrganization>();
		if ((org.privacyLevel == 0 || org.privacyLevel == 1)
				&& (topic.privacyLevel == 1)) {
			allUser = UserRoleInOrganization.find("byEntitytopicIdAndType",
					topicId, "topic").fetch();
			for (int i = 0; i < allUser.size(); i++) {
				if ((allUser.get(i).role.roleName).equals("idea developer")
						&& (allUser.get(i).organization).equals(org)
						&& !(user.contains(allUser.get(i)))) {
					user.add(allUser.get(i).enrolled);
				}
			}
			for (int i = 0; i < bannedUsers.size(); i++) {
				if (user.contains(bannedUsers.get(i))) {
					user.remove(bannedUsers.get(i));

				}
			}
		} else {

			if ((org.privacyLevel == 0 || org.privacyLevel == 1)
					&& (topic.privacyLevel == 2)) {
				allUser = UserRoleInOrganization.find("byOrganization", org)
						.fetch();
				for (int i = 0; i < allUser.size(); i++) {
					if ((allUser.get(i).role.roleName).equals("idea developer")
							&& !(user.contains(allUser.get(i)))) {
						user.add(allUser.get(i).enrolled);
					}
				}
				for (int i = 0; i < bannedUsers.size(); i++) {
					if (user.contains(bannedUsers.get(i))) {
						user.remove(bannedUsers.get(i));

					}
				}
			}

			else {
				if ((org.privacyLevel == 2) && (topic.privacyLevel == 1)) {
					allUser = UserRoleInOrganization.find(
							"byEntitytopicIdAndType", topicId, "topic").fetch();
					for (int i = 0; i < allUser.size(); i++) {
						if ((allUser.get(i).role.roleName)
								.equals("idea developer")
								&& (allUser.get(i).organization).equals(org)
								&& !(user.contains(allUser.get(i)))) {
							user.add(allUser.get(i).enrolled);
						}
					}

					for (int i = 0; i < bannedUsers.size(); i++) {
						if (user.contains(bannedUsers.get(i))) {
							user.remove(bannedUsers.get(i));

						}
					}
				} else {
					if ((org.privacyLevel == 2) && (topic.privacyLevel == 2)) {

						user = User.findAll();

						for (int i = 0; i < bannedUsers.size(); i++) {
							if (user.contains(bannedUsers.get(i))) {
								user.remove(bannedUsers.get(i));

							}
						}
					}
				}
			}
		}

		for (int i = 0; i < user.size(); i++) {
			if (!searchList.contains(user.get(i)))
				searchList.add(user.get(i));
		}
		int size = searchList.size();
		ArrayList<User> searchListActive = new ArrayList<User>();
		for (int i = 0; i < size; i++) {
			if (searchList.get(i).state.equals("a")) {
				searchListActive.add(searchList.get(i));
			}
		}

		return searchListActive;

	}

	/**
	 * @ description This method closes a topic, return true if was successful,
	 * returns false if the there was ideas and doesn't close the topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S4
	 * 
	 * @param topicId
	 *            : the id of the topic to be closed
	 * 
	 */
	public static void closeTopic(String topicId) {

		long topicIdLong = Long.parseLong(topicId);
		Topic targetTopic = Topic.findById(topicIdLong);
		notFoundIfNull(targetTopic);

		User actor = User.findById(Security.getConnected().id);
		List<User> organizers = targetTopic.getOrganizer();
		List<User> followers = targetTopic.followers;

		String action = "close a topic and promote it to execution";
		String notificationDescription = "Topic " + targetTopic.title
				+ " has been closed and promoted to execution.";
		targetTopic.openToEdit = false;
		targetTopic.save();
		for (int i = 0; i < organizers.size(); i++) {
			Notifications.sendNotification(organizers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}
		for (int i = 0; i < followers.size(); i++) {
			Notifications.sendNotification(followers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}

		String log = "<a href=\"/users/viewprofile?userId=" + actor.id + "\">"
				+ actor.username + "</a>" + " closed Topic "
				+ "<a href=\"/topics/show?topicId=" + targetTopic.id + "\">"
				+ targetTopic.title + "</a>";

		MainEntity entity = targetTopic.entity;
		Organization organization = entity.organization;

		Log.addUserLog(log, actor, targetTopic, entity, organization);

		redirect("/topics/show?topicId=" + topicId);
	}

	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * 
	 * @description This method checks for the Validation of the info inserted
	 *              in the Add form of a Topic and if they are valid the object
	 *              is created and saved and the community contribution counter
	 *              of the author is incremented.
	 * 
	 * @param entityId
	 *            : Id of the entity where the topic will be created
	 * 
	 * @throws Exception
	 * 
	 */
	public static void create(long entityId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		String message = "";
		User user = Security.getConnected();
		Topic temporaryTopic = (Topic) object; // we temporarily save the object
												// created by the form in
												// temporaryTopic to validate it
												// before saving
		List<MainEntity> listOfEntities = MainEntity.findAll();
		temporaryTopic.creator = user;
		MainEntity topicEntity = MainEntity.findById(entityId);
		MainEntity entity = MainEntity.findById(entityId);
		if (!user.isAdmin) {

			temporaryTopic.entity = topicEntity;
		} else {
			if (entity == null)
				topicEntity = temporaryTopic.entity;
			else
				temporaryTopic.entity = topicEntity;
		}
		if (temporaryTopic.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, entityId, user, listOfEntities);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, message, entityId, user);
			}
		}

		Organization topicOrganization = topicEntity.organization;
		if (!(topicEntity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			temporaryTopic.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations",
					topicEntity, topicOrganization).fetch();

		if (validation.hasErrors()) {
			if (temporaryTopic.title.equals("")) {
				message = "A Topic must have a title";
			} else if (temporaryTopic.description.equals("")) {
				message = "A Topic must have a description";

			}

			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						user);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, entityId, user);
			}
		}

		if (temporaryTopic.privacyLevel < 1 || temporaryTopic.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						user);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, entityId, user);
			}

		}
		object._save();
		((Topic) object).openToEdit = true;
		object._save();
		temporaryTopic = (Topic) object;

		if (temporaryTopic.entity != temporaryTopic.entity.organization.entitiesList
				.get(0)) {
			if (temporaryTopic.followers != null) {
				for (int i = 0; i < temporaryTopic.followers.size(); i++)
					Notifications.sendNotification(temporaryTopic.followers
							.get(i).getId(), temporaryTopic.id, "Topic",
							"A new Topic: '" + temporaryTopic.title
									+ "' has been added in entity '"
									+ temporaryTopic.entity.name + "'");
			}
		} else {
			if (temporaryTopic.followers != null) {
				for (int i = 0; i < temporaryTopic.followers.size(); i++)
					Notifications.sendNotification(temporaryTopic.followers
							.get(i).getId(), temporaryTopic.id, "Topic",
							"A new Topic: '" + temporaryTopic.title
									+ "' has been added in the organization '"
									+ temporaryTopic.entity.organization.name
									+ "'");
			}
		}

		List<User> users = Users.getEntityOrganizers(temporaryTopic.entity);
		users.add(temporaryTopic.entity.organization.creator);

		if (temporaryTopic.entity != temporaryTopic.entity.organization.entitiesList
				.get(0)) {
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", "A new Topic: '"
								+ temporaryTopic.title
								+ "' has been added in entity '"
								+ temporaryTopic.entity.name + "'");
		} else {
			for (int i = 0; i < users.size(); i++) {
				Notifications
						.sendNotification(
								users.get(i).id,
								temporaryTopic.id,
								"Topic",
								"A new Topic: '"
										+ temporaryTopic.title
										+ "' has been added in the organization '"
										+ temporaryTopic.entity.organization.name
										+ "'");
			}
		}

		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " created the topic " + "<a href=\"/topics/show?topicId="
				+ temporaryTopic.id + "\">" + temporaryTopic.title + "</a>"
				+ " from entity " + "<a href=\"/mainentitys/viewentity?id="
				+ topicEntity.id + "\">" + topicEntity.name + "</a>";
		Log.addUserLog(logDescription, temporaryTopic, user, topicEntity,
				topicEntity.organization);

		flash.success(Messages.get("crud.created", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			redirect("/topics/show?topicId=" + ((Topic) object).getId());
		}
		if (params.get("_saveAndAddAnother") != null) {
			render(request.controller.replace(".", "/") + "/blank.html",
					entityId, user, entity, listOfEntities);
		}
		redirect(request.controller + ".view", ((Topic) object).getId());
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param entityId
	 *            : id of the entity the topic is in
	 * 
	 * @param userId
	 *            : id of the current user
	 * 
	 * @description This method renders the form for creating a topic in the
	 *              entity
	 * 
	 */
	public static void blank(long entityId, long userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		MainEntity entity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		// handle permissions, depending on the privacyLevel the user will be
		// directed to a different page
		List<MainEntity> listOfEntities = MainEntity.findAll();
		if (entity != null) {
			Organization organization = entity.organization;
			long organizationId = organization.id;
			String organizationName = organization.name;
			boolean isDefaultEntity;
			if (entity.equals(organization.entitiesList.get(0))) {
				isDefaultEntity = true;
			} else {
				isDefaultEntity = false;
			}
			int permission = 1;
			if (!Users.isPermitted(user, "post topics", entity.id, "entity"))
				permission = 0;

			if (permission == 1) {
				try {
					render("Topics/blank.html", type, entityId, user, entity,
							isDefaultEntity, organizationId, organizationName);

				} catch (TemplateNotFoundException exception) {
					render("CRUD/blank.html", type, entityId, user, entity,
							isDefaultEntity, organizationName);
				}
			} else {
				BannedUsers.unauthorized();
			}
		} else {
			if (user.isAdmin) {
				try {
					render("CoolCRUD/blank.html", type, user, listOfEntities);

				} catch (Exception exception) {
					render("CoolCRUD/blank.html", type, user, listOfEntities);
				}
			}

			else {
				BannedUsers.unauthorized();
			}
		}
	}

	/*
	 * public static void blank() throws Exception { ObjectType type =
	 * ObjectType.get(getControllerClass()); notFoundIfNull(type);
	 * Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	 * constructor.setAccessible(true); Model object = (Model)
	 * constructor.newInstance(); User user = Security.getConnected();
	 * List<MainEntity> listOfEntities = MainEntity.findAll(); try {
	 * render(type, object, user, listOfEntities); } catch
	 * (TemplateNotFoundException e) { render("CRUD/blank.html", type, object,
	 * user, listOfEntities); } }
	 * 
	 * public static void create() throws Exception { ObjectType type =
	 * ObjectType.get(getControllerClass()); notFoundIfNull(type);
	 * Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	 * constructor.setAccessible(true); Model object = (Model)
	 * constructor.newInstance(); Binder.bind(object, "object", params.all());
	 * validation.valid(object); if (validation.hasErrors()) {
	 * renderArgs.put("error", Messages.get("crud.hasErrors")); try {
	 * render(request.controller.replace(".", "/") + "/blank.html", type,
	 * object); } catch (TemplateNotFoundException e) {
	 * render("CRUD/blank.html", type, object); } } object._save();
	 * flash.success(Messages.get("crud.created", type.modelName)); if
	 * (params.get("_save") != null) { redirect(request.controller + ".list"); }
	 * if (params.get("_saveAndAddAnother") != null) {
	 * redirect(request.controller + ".blank"); } redirect(request.controller +
	 * ".show", object._key()); }
	 */

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for editing and viewing a topic
	 * 
	 */
	public static void show(long topicId) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicId);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		User user = Security.getConnected();
		List<Tag> tags = temporaryTopic.tags;
		User creator = temporaryTopic.creator;
		List<User> followers = temporaryTopic.followers;
		List<Idea> ideas = temporaryTopic.ideas;
		List<Comment> comments = temporaryTopic.commentsOn;
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		boolean openToEdit = temporaryTopic.openToEdit;
		int privacyLevel = temporaryTopic.privacyLevel;
		boolean createRelationship = temporaryTopic.createRelationship;
		String deleteMessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = temporaryTopic.isDeletable();
		boolean hidden = temporaryTopic.hidden;
		int canClose = 0;
		int canPlan = 0;
		int canMerge = 0;
		int canRestrict = 0;
		long topicIdLong = topicId;
		User actor = Security.getConnected();
		String actionClose = "close a topic and promote it to execution";
		String actionPlan = "create an action plan to execute an idea";
		String actionMerge = "merge ideas";
		Topic targetTopic = Topic.findById(topicIdLong);
		boolean canReport = Users
				.isPermitted(user, "use", topicIdLong, "topic");
		boolean alreadyReported = false;
		boolean canDelete = Users.isPermitted(actor, "hide and delete an idea",
				topicIdLong, "topic");
		boolean alreadyReportedTopic = false;
		boolean canRequestRelationship = false;
		boolean topicNotClosed = targetTopic.openToEdit;
		temporaryTopic.incrmentViewed();
		temporaryTopic.save();
		long userId = user.id;
		for (int i = 0; i < ideas.size(); i++) {
			Idea idea = ideas.get(i);

			for (int j = 0; j < idea.reporters.size()
					|| j < actor.ideasReported.size(); j++) {
				if (idea.reporters.size() > 0
						&& (actor.toString().equals(
								idea.reporters.get(j).toString()) || idea
								.toString().equals(
										idea.reporters.get(j).toString())))
					alreadyReported = true;
			}
		}
		// begin New Hadi
		// for public topic
		System.out.println("joined users " + searchByTopic(temporaryTopic.id));
		boolean userJoinedPublic = false;
		boolean userJoinedPrivate = false;
		boolean privateTopic = false;
		if (user.username.equals(temporaryTopic.creator.username)
				|| user.isAdmin
				|| user.username
						.equals(temporaryTopic.entity.organization.creator.username))
			userJoinedPublic = true;
		if (temporaryTopic.privacyLevel == 2) {
			for (int i = 0; i < user.userRolesInOrganization.size(); i++) {
				System.out.println(user.userRolesInOrganization.get(i).role);
				if (user.userRolesInOrganization.get(i).role.equals(new Role(
						"idea developer", "use")))
					userJoinedPublic = true;
			}
		}
		// for private topic
		if (temporaryTopic.privacyLevel == 1) {
			privateTopic = true;
			for (int i = 0; i < user.userRolesInOrganization.size(); i++) {
				if (user.userRolesInOrganization.get(i).role.equals(new Role(
						"organizer", "use")))
					userJoinedPrivate = true;
			}
			if (user.username.equals(temporaryTopic.creator.username)
					|| user.isAdmin
					|| user.username
							.equals(temporaryTopic.entity.organization.creator.username))
				userJoinedPrivate = true;
			else {
				for (int i = 0; i < user.userRolesInOrganization.size(); i++) {
					if (user.userRolesInOrganization.get(i).role
							.equals(new Role("idea developer", "use")))
						userJoinedPrivate = true;
				}
			}
		}
		// End New Hadi
		ArrayList<User> topicReporters = new ArrayList<User>();
		String[] topicReportersId = { "0" };
		User reporter = Security.getConnected();
		if (targetTopic.reporters != null) {
			topicReportersId = targetTopic.reporters.split(",");
			long reporterId = 0;
			if (!targetTopic.reporters.isEmpty()) {
				for (int i = 0; i < topicReportersId.length
						&& targetTopic.reporters != ""; i++) {
					reporterId = Integer.parseInt(topicReportersId[i]);
					if (reporterId == reporter.id) {
						topicReporters.add(reporter);
					}
				}
			}
		}
		for (int i = 0; i < topicReporters.size(); i++) {
			if (reporter.toString().equals(topicReporters.get(i).toString())) {
				alreadyReportedTopic = true;
				break;
			} else
				alreadyReportedTopic = false;
		}
		ArrayList<Idea> hiddenIdeas = new ArrayList<Idea>();
		for (int i = 0; i < ideas.size(); i++) {
			if (ideas.get(i).hidden) {
				hiddenIdeas.add(ideas.get(i));
			}
		}
		int allowed = 0;
		for (int k = 0; k < ideas.size(); k++) {
			if (ideas.get(k).hidden)
				ideas.remove(k);
			else if (ideas.get(k).isDraft)
				ideas.remove(k);
		}
		int numberOfIdeas = ideas.size();
		if ((temporaryTopic.privacyLevel == 1)
				&& Users.isPermitted(
						actor,
						"Accept/Reject requests to post in a private topic in entities he/she manages",
						temporaryTopic.id, "topic"))
			allowed = 1;
		boolean canPost = Users.isPermitted(Security.getConnected(), "use",
				temporaryTopic.id, "topic");

		int check = 0;
		if (Users.isPermitted(Security.getConnected(),
				"block a user from viewing or using a certain entity",
				topicIdLong, "topic"))
			check = 1;

		boolean allowedToTag = Users.isPermitted(user, "tag topics", topicId,
				"topic") || Users.isPermitted(user, "use", topicId, "topic");

		int check1 = 0;
		if (Users.isPermitted(Security.getConnected(), "view", topicIdLong,
				"topic"))
			check1 = 1;

		int check2 = 0;
		if (Users.isPermitted(Security.getConnected(), "use", topicIdLong,
				"topic"))
			check2 = 1;

		if (Users.isPermitted(actor, actionClose, topicIdLong, "topic")) {
			canClose = 1;
		}

		if (Users.isPermitted(actor, actionPlan, topicIdLong, "topic")) {
			canPlan = 1;
		}

		if (Users.isPermitted(actor, actionMerge, topicIdLong, "topic")) {
			canMerge = 1;
		}

		int permission = 1;

		if (!Users.isPermitted(actor, "post topics", entity.id, "entity")) {
			permission = 0;
		}
		boolean isIdeaDeveloper = false;
		List<UserRoleInOrganization> roles = UserRoleInOrganization.find(
				"byEnrolled", actor).fetch();
		for (int k = 0; k < roles.size(); k++) {
			if (roles.get(k).type.equalsIgnoreCase("topic")
					&& roles.get(k).entityTopicID == topicIdLong) {
				isIdeaDeveloper = true;
				break;
			}
		}
		boolean seeRelationStatus = false;
		if (actor.isAdmin || entity.organization.creator.equals(actor)
				|| creator.equals(actor)) {
			seeRelationStatus = true;
		}
		boolean isMemeber = Users.getEnrolledUsers(
				targetTopic.entity.organization).contains(actor);
		boolean pending = targetTopic.hasRequest(actor.id);
		boolean canNotPost = (targetTopic.creator.id != actor.id
				&& !actor.isAdmin && !pending)
				&& !isIdeaDeveloper && isMemeber;
		boolean follower = actor.topicsIFollow.contains(targetTopic);
		boolean canCreateRelationship = TopicRelationships
				.isAllowedTo(topicIdLong);
		boolean topicIsLocked = targetTopic.createRelationship;
		Organization organisation = targetTopic.entity.organization;
		boolean defaultEntity = false;
		if (targetTopic.entity.equals(organisation.entitiesList.get(0))) {
			defaultEntity = true;
		}
		if (actor.isAdmin || entity.organization.creator.equals(actor)) {
			canRestrict = 1;
		}
		if (Users.getEntityOrganizers(entity).contains(actor)
				&& Users.isPermitted(actor,
						"Request to start a relationship with other items",
						topicId, "topic")) {
			canRequestRelationship = true;
		}

		boolean joined = false;
		joinedUsersInTopic = searchByTopic(topicId);
		for (int i = 0; i < joinedUsersInTopic.size(); i++) {
			if (joinedUsersInTopic.get(i).username.equals(user.username))
				;
			joined = true;
		}
		boolean banned = true;

		if (temporaryTopic.hidden == true) {
			if (temporaryTopic.hider.id.compareTo(user.id) == 0) {
				banned = false;
			}
		} else {
			if (temporaryTopic.canView(user)) {
				banned = false;
			}
		}
		List<Topic> listOfTopics = null;
		if (entity.topicList != null) {
			listOfTopics = entity.topicList;
			listOfTopics.remove(temporaryTopic);
		}
		for (int i = 0; i < listOfTopics.size(); i++) {
			if (!listOfTopics.get(i).createRelationship) {
				listOfTopics.remove(listOfTopics.get(i));
			}
		}

		if (banned == false) {
			try {
				render(type, object, tags, joined, alreadyReportedTopic,
						creator, followers, ideas, userJoinedPublic,
						userJoinedPrivate, privateTopic, canReport, userId,
						topicNotClosed, hiddenIdeas, numberOfIdeas, comments,
						entity, canDelete, alreadyReported, plan, openToEdit,
						privacyLevel, deleteMessage, deletable, topicIdLong,
						canClose, canPlan, targetTopic, allowed, permission,
						topicId, canPost, canNotPost, pending, follower,
						canCreateRelationship, seeRelationStatus,
						createRelationship, actor, hidden, canRestrict, check,
						canMerge, canRequestRelationship, topicIsLocked,
						organisation, check1, check2, user, listOfTopics,
						allowedToTag, temporaryTopic, defaultEntity);

			} catch (TemplateNotFoundException exception) {
				render("CRUD/show.html", type, object, topicId,
						canCreateRelationship, user);
			}
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * renders the page for requesting topic relationship creation.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request.
	 * 
	 * @param organisationId
	 *            the id of the organisation from which the request is made.
	 * 
	 * @param entityId
	 *            the id of the topic's entity.
	 * 
	 * @param topicId
	 *            the id of the topic from which the request page is accessed.
	 * 
	 */
	public static void requestRelationship(long userId, long organisationId,
			long entityId, long topicId) {
		Organization organisation = Organization.findById(organisationId);
		User user = User.findById(userId);
		MainEntity entity = MainEntity.findById(entityId);
		Topic topic = Topic.findById(topicId);
		render(user, organisation, entity, topic);
	}

	/**
	 * renders the page for viewing the relationships for a topic.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user viewing the requests.
	 * 
	 * @param organisationId
	 *            the id of the organisation.
	 * 
	 * @param entityId
	 *            the id of the topic's entity.
	 * 
	 * @param topicId
	 *            the id of the topic whose relationships are viewed.
	 * 
	 * @param canRequestRelationship
	 *            boolean variable whether this user is allowed to make
	 *            relationship requests or not.
	 * 
	 */
	public static void viewRelationships(long userId, long organisationId,
			long entityId, long topicId, boolean canRequestRelationship) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		MainEntity entity = MainEntity.findById(entityId);
		Topic topic = Topic.findById(topicId);
		render(user, organisation, entity, canRequestRelationship, topic);
	}

	/**
	 * Topic view method
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for viewing a topic
	 * 
	 */
	public static void view(String topicId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicId);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		User user = Security.getConnected();
		;
		List<Tag> tags = temporaryTopic.tags;
		User creator = temporaryTopic.creator;
		List<User> followers = temporaryTopic.followers;
		List<Idea> ideas = temporaryTopic.ideas;
		List<Comment> comments = temporaryTopic.commentsOn;
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		boolean createRelationship = temporaryTopic.createRelationship;
		boolean openToEdit = temporaryTopic.openToEdit;
		int privacyLevel = temporaryTopic.privacyLevel;
		String deleteMessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = temporaryTopic.isDeletable();
		int canClose = 0;
		int canPlan = 0;
		int canMerge = 0;
		int canRestrict = 0;
		long topicIdLong = Long.parseLong(topicId);
		User actor = Security.getConnected();
		String actionClose = "close a topic and promote it to execution";
		String actionPlan = "create an action plan to execute an idea";
		String actionMerge = "merge ideas";
		Topic targetTopic = Topic.findById(topicIdLong);
		boolean canReport = Users
				.isPermitted(user, "use", topicIdLong, "topic");
		boolean alreadyReported = false;
		boolean canDelete = Users.isPermitted(actor, "hide and delete an idea",
				topicIdLong, "topic");
		boolean alreadyReportedTopic = false;
		boolean canRequestRelationship = false;
		for (int i = 0; i < ideas.size(); i++) {
			Idea idea = ideas.get(i);

			for (int j = 0; j < idea.reporters.size()
					|| j < actor.ideasReported.size(); j++) {
				if (idea.reporters.size() > 0
						&& (actor.toString().equals(
								idea.reporters.get(j).toString()) || idea
								.toString().equals(
										idea.reporters.get(j).toString())))
					alreadyReported = true;
			}
		}
		ArrayList<User> reporters = new ArrayList<User>();
		String[] reportersId = { "0" };
		if (targetTopic.reporters != null) {
			reportersId = targetTopic.reporters.split(",");
			long reporterId = 0;
			if (!targetTopic.reporters.isEmpty()) {
				for (int i = 0; i < reportersId.length
						&& targetTopic.reporters != ""; i++) {
					reporterId = Integer.parseInt(reportersId[i]);
					if (reporterId == user.id) {
						reporters.add(user);
					}
				}
			}
		}
		for (int i = 0; i < reporters.size(); i++) {
			if (user.toString().equals(reporters.get(i).toString())) {
				alreadyReportedTopic = true;
				break;
			} else
				alreadyReportedTopic = false;
		}
		ArrayList<Idea> hiddenIdeas = new ArrayList<Idea>();
		for (int i = 0; i < ideas.size(); i++) {
			if (ideas.get(i).hidden) {
				hiddenIdeas.add(ideas.get(i));
			}
		}
		int allowed = 0;
		for (int k = 0; k < ideas.size(); k++) {
			if (ideas.get(k).hidden)
				ideas.remove(k);
			else if (ideas.get(k).isDraft)
				ideas.remove(k);
		}
		int numberOfIdeas = ideas.size();
		if ((temporaryTopic.privacyLevel == 1)
				&& Users.isPermitted(
						actor,
						"Accept/Reject requests to post in a private topic in entities he/she manages",
						temporaryTopic.id, "topic"))
			allowed = 1;
		boolean canPost = Users.isPermitted(Security.getConnected(), "use",
				temporaryTopic.id, "topic");
		int check = 0;
		if (Users.isPermitted(Security.getConnected(),
				"block a user from viewing or using a certain entity",
				topicIdLong, "topic"))
			check = 1;
		if (Users.isPermitted(actor, actionClose, topicIdLong, "topic")) {
			canClose = 1;
		}
		if (Users.isPermitted(actor, actionPlan, topicIdLong, "topic")) {
			canPlan = 1;
		}
		if (Users.isPermitted(actor, actionMerge, topicIdLong, "topic")) {
			canMerge = 1;
		}
		int permission = 1;
		if (!Users.isPermitted(actor, "post topics", entity.id, "entity")) {
			permission = 0;
		}
		boolean isIdeaDeveloper = false;
		List<UserRoleInOrganization> roles = UserRoleInOrganization.find(
				"byEnrolled", actor).fetch();
		for (int k = 0; k < roles.size(); k++) {
			if (roles.get(k).type.equalsIgnoreCase("topic")
					&& roles.get(k).entityTopicID == topicIdLong) {
				isIdeaDeveloper = true;
				break;
			}
		}
		boolean isMemeber = Users.getEnrolledUsers(
				targetTopic.entity.organization).contains(actor);
		boolean pending = targetTopic.hasRequest(actor.id);
		boolean canNotPost = (targetTopic.creator.id != actor.id
				&& !actor.isAdmin && !pending)
				&& !isIdeaDeveloper && isMemeber;
		boolean follower = actor.topicsIFollow.contains(targetTopic);
		boolean canCreateRelationship = TopicRelationships
				.isAllowedTo(topicIdLong);
		boolean topicIsLocked = targetTopic.createRelationship;
		Organization organization = targetTopic.entity.organization;
		if (actor.isAdmin || entity.organization.creator.equals(actor)) {
			canRestrict = 1;
		}
		if (Users.getEntityOrganizers(entity).contains(actor)
				&& Users.isPermitted(actor,
						"Request to start a relationship with other items;",
						topicIdLong, "Topic")) {
			canRequestRelationship = true;
		}
		boolean defaultEntity = false;
		if (targetTopic.entity.equals(organization.entitiesList.get(0))) {
			defaultEntity = true;
		}

		if (permission == 1) {
			try {
				render(type, object, tags, creator, followers, ideas, comments,
						entity, plan, openToEdit, privacyLevel, deleteMessage,
						deletable, topicId, createRelationship, user, allowed,
						alreadyReportedTopic, canReport, numberOfIdeas,
						canClose, canMerge, canPlan, check,
						canCreateRelationship, follower, canNotPost, pending,
						canRestrict, organization, canRequestRelationship,
						defaultEntity);
			} catch (TemplateNotFoundException exception) {
				render("/topics/view.html", type, object, tags, creator,
						followers, ideas, comments, entity, plan, openToEdit,
						privacyLevel, deleteMessage, deletable, topicId,
						createRelationship, user, allowed,
						alreadyReportedTopic, canReport, numberOfIdeas,
						canClose, canMerge, canPlan, check,
						canCreateRelationship, follower, canNotPost, pending,
						canRestrict, canRequestRelationship, organization,
						defaultEntity);
			}
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we're in
	 * 
	 * @description This method renders the form for editing a topic and saving
	 *              it
	 * 
	 * @throws Exception
	 * 
	 */
	public static void save(String topicId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = Topic.findById(Long.parseLong(topicId));
		notFoundIfNull(object);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic temporaryTopic = (Topic) object; // we temporarily save the object
												// edited in the form in
												// temporaryTopic
												// to validate it before saving
		MainEntity entity = temporaryTopic.entity;
		User user = Security.getConnected();
		boolean createRelationship = temporaryTopic.createRelationship;
		temporaryTopic.creator = user;
		Organization topicOrganization = entity.organization;
		if (!(entity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			temporaryTopic.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations", entity,
					topicOrganization).fetch();
		String message = "";

		if (validation.hasErrors()) {
			if (temporaryTopic.description.equals("")) {
				message = "A Topic must have a description";

			}

			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						object, topicId, createRelationship, user);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/view.html", type, topicId, user);
			}
		}

		if (temporaryTopic.privacyLevel < 1 || temporaryTopic.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						object, topicId, createRelationship, user);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/view.html", type, topicId, user);
			}

		}

		object._save();
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " edited the topic " + "<a href=\"/topics/show?topicId="
				+ temporaryTopic.id + "\">" + temporaryTopic.title + "</a>"
				+ " in entity " + "<a href=\"/mainentitys/viewentity?id="
				+ entity.id + "\">" + entity.name + "</a>";
		Log.addUserLog(logDescription, temporaryTopic, user, entity,
				entity.organization);
		String notification = user.username + " edited the topic "
				+ temporaryTopic.title + " in entity " + entity.name;
		List<User> users = Users.getEntityOrganizers(temporaryTopic.entity);
		if (!users.contains(temporaryTopic.entity.organization.creator))
			users.add(temporaryTopic.entity.organization.creator);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, temporaryTopic.id,
					"Topic", notification);

		flash.success(Messages.get("crud.saved", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			redirect("/topics/show?topicId=" + ((Topic) object).getId());

		}
		redirect(request.controller + ".view", ((Topic) object).getId());

	}

	/**
	 * The method that allows a user to follow a certain topic
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param topicId
	 *            : The id of the topic that the user wants to follow
	 * 
	 */
	public static void followTopic(long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		if (topic.followers.contains(user)) {

		} else {
			topic.followers.add(user);
			topic.save();
			user.topicsIFollow.add(topic);
			user.save();
			Log.addUserLog("<a href=\"/users/viewprofile?userId=" + user.id
					+ "\">" + user.username + "</a>"
					+ " has followed the topic ("
					+ "<a href=\"/topics/show?topicId=" + topic.id + "\">"
					+ topic.title + "</a>" + ")", topic.entity.organization);
			redirect(request.controller + ".show", topic.id,
					"You are now a follower");
		}
	}

	/**
	 * The method that renders the page for viewing the followers of a topic
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param topicId
	 *            : The id of the topic that the user wants to view its
	 *            followers
	 * 
	 * @param f
	 *            : The String that is used as a variable for checking
	 * 
	 */
	public static void viewFollowers(long topicId, String f) {
		Topic temporaryTopic = Topic.findById(topicId);
		notFoundIfNull(temporaryTopic);
		User user = Security.getConnected();
		List<Tag> tags = temporaryTopic.tags;
		User creator = temporaryTopic.creator;
		List<User> followers = temporaryTopic.followers;
		List<Idea> ideas = temporaryTopic.ideas;
		List<Comment> comments = temporaryTopic.commentsOn;
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		boolean openToEdit = temporaryTopic.openToEdit;
		int privacyLevel = temporaryTopic.privacyLevel;
		boolean createRelationship = temporaryTopic.createRelationship;
		String deleteMessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = temporaryTopic.isDeletable();
		boolean hidden = temporaryTopic.hidden;
		int canClose = 0;
		int canPlan = 0;
		int canMerge = 0;
		int canRestrict = 0;
		long topicIdLong = topicId;
		User actor = Security.getConnected();
		String actionClose = "close a topic and promote it to execution";
		String actionPlan = "create an action plan to execute an idea";
		String actionMerge = "merge ideas";
		Topic targetTopic = Topic.findById(topicIdLong);
		boolean canReport = Users
				.isPermitted(user, "use", topicIdLong, "topic");
		boolean alreadyReported = false;
		boolean canDelete = Users.isPermitted(actor, "hide and delete an idea",
				topicIdLong, "topic");
		boolean alreadyReportedTopic = false;
		boolean canRequestRelationship = false;
		boolean topicNotClosed = targetTopic.openToEdit;
		temporaryTopic.incrmentViewed();
		temporaryTopic.save();
		long userId = user.id;
		for (int i = 0; i < ideas.size(); i++) {
			Idea idea = ideas.get(i);

			for (int j = 0; j < idea.reporters.size()
					|| j < actor.ideasReported.size(); j++) {
				if (idea.reporters.size() > 0
						&& (actor.toString().equals(
								idea.reporters.get(j).toString()) || idea
								.toString().equals(
										idea.reporters.get(j).toString())))
					alreadyReported = true;
			}
		}
		ArrayList<User> topicReporters = new ArrayList<User>();
		String[] topicReportersId = { "0" };
		User reporter = Security.getConnected();
		if (targetTopic.reporters != null) {
			topicReportersId = targetTopic.reporters.split(",");
			long reporterId = 0;
			if (!targetTopic.reporters.isEmpty()) {
				for (int i = 0; i < topicReportersId.length
						&& targetTopic.reporters != ""; i++) {
					reporterId = Integer.parseInt(topicReportersId[i]);
					if (reporterId == reporter.id) {
						topicReporters.add(reporter);
					}
				}
			}
		}
		for (int i = 0; i < topicReporters.size(); i++) {
			if (reporter.toString().equals(topicReporters.get(i).toString())) {
				alreadyReportedTopic = true;
				break;
			} else
				alreadyReportedTopic = false;
		}
		ArrayList<Idea> hiddenIdeas = new ArrayList<Idea>();
		for (int i = 0; i < ideas.size(); i++) {
			if (ideas.get(i).hidden) {
				hiddenIdeas.add(ideas.get(i));
			}
		}
		int allowed = 0;
		for (int k = 0; k < ideas.size(); k++) {
			if (ideas.get(k).hidden)
				ideas.remove(k);
			else if (ideas.get(k).isDraft)
				ideas.remove(k);
		}
		int numberOfIdeas = ideas.size();
		if ((temporaryTopic.privacyLevel == 1)
				&& Users.isPermitted(
						actor,
						"Accept/Reject requests to post in a private topic in entities he/she manages",
						temporaryTopic.id, "topic"))
			allowed = 1;
		boolean canPost = Users.isPermitted(Security.getConnected(), "use",
				temporaryTopic.id, "topic");

		int check = 0;
		if (Users.isPermitted(Security.getConnected(),
				"block a user from viewing or using a certain entity",
				topicIdLong, "topic"))
			check = 1;
		int check1 = 0;
		if (Users.isPermitted(Security.getConnected(), "view", topicIdLong,
				"topic"))
			check1 = 1;

		int check2 = 0;
		if (Users.isPermitted(Security.getConnected(), "use", topicIdLong,
				"topic"))
			check2 = 1;

		if (Users.isPermitted(actor, actionClose, topicIdLong, "topic")) {
			canClose = 1;
		}

		if (Users.isPermitted(actor, actionPlan, topicIdLong, "topic")) {
			canPlan = 1;
		}

		if (Users.isPermitted(actor, actionMerge, topicIdLong, "topic")) {
			canMerge = 1;
		}

		int permission = 1;

		if (!Users.isPermitted(actor, "post topics", entity.id, "entity")) {
			permission = 0;
		}
		boolean isIdeaDeveloper = false;
		List<UserRoleInOrganization> roles = UserRoleInOrganization.find(
				"byEnrolled", actor).fetch();
		for (int k = 0; k < roles.size(); k++) {
			if (roles.get(k).type.equalsIgnoreCase("topic")
					&& roles.get(k).entityTopicID == topicIdLong) {
				isIdeaDeveloper = true;
				break;
			}
		}
		boolean seeRelationStatus = false;
		if (actor.isAdmin || entity.organization.creator.equals(actor)
				|| creator.equals(actor)) {
			seeRelationStatus = true;
		}
		boolean isMemeber = Users.getEnrolledUsers(
				targetTopic.entity.organization).contains(actor);
		boolean pending = targetTopic.hasRequest(actor.id);
		boolean canNotPost = (targetTopic.creator.id != actor.id
				&& !actor.isAdmin && !pending)
				&& !isIdeaDeveloper && isMemeber;
		boolean follower = actor.topicsIFollow.contains(targetTopic);
		boolean canCreateRelationship = TopicRelationships
				.isAllowedTo(topicIdLong);
		boolean topicIsLocked = targetTopic.createRelationship;
		Organization organisation = targetTopic.entity.organization;
		if (actor.isAdmin || entity.organization.creator.equals(actor)) {
			canRestrict = 1;
		}
		if (Users.getEntityOrganizers(entity).contains(actor)
				&& Users.isPermitted(actor,
						"Request to start a relationship with other items",
						topicId, "topic")) {
			canRequestRelationship = true;
		}

		boolean joined = false;
		joinedUsersInTopic = searchByTopic(topicId);
		for (int i = 0; i < joinedUsersInTopic.size(); i++) {
			if (joinedUsersInTopic.get(i).username.equals(user.username))
				;
			joined = true;
		}
		boolean banned = true;

		if (temporaryTopic.hidden == true) {
			if (temporaryTopic.hider.id.compareTo(user.id) == 0) {
				banned = false;
			}
		} else {
			if (temporaryTopic.canView(user)) {
				banned = false;
			}
		}
		if (f.equals("true"))
			followTopic(topicId);
		if (banned == false) {
			try {

				render(tags, joined, alreadyReportedTopic, creator, followers,
						ideas, canReport, userId, topicNotClosed, hiddenIdeas,
						numberOfIdeas, comments, entity, canDelete,
						alreadyReported, plan, openToEdit, privacyLevel,
						deleteMessage, deletable, topicIdLong, canClose,
						canPlan, targetTopic, allowed, permission, topicId,
						canPost, canNotPost, pending, follower,
						canCreateRelationship, seeRelationStatus,
						createRelationship, actor, hidden, canRestrict, check,
						canMerge, canRequestRelationship, topicIsLocked,
						organisation, check1, check2, user, temporaryTopic);

			} catch (TemplateNotFoundException exception) {

			}
		}
	}

	/**
	 * Deletes a topic and returns whether the topic was successfully deleted
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @param id
	 *            : the id of the topic to be deleted
	 * 
	 * @param justification
	 *            : the justification message that is sent by the deleter to the
	 *            creator of the topic
	 * 
	 * @return boolean
	 */
	public static boolean deleteTopic(String id, String justification) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		User user = Security.getConnected();
		String message = user.username + " has deleted the topic "
				+ temporaryTopic.title;
		List<User> users = Users.getEntityOrganizers(entity);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, temporaryTopic.id,
					"Topic", message);
		for (int i = 0; i < temporaryTopic.followers.size(); i++)
			Notifications.sendNotification(temporaryTopic.followers.get(i)
					.getId(), entity.getId(), "entity", message);
		Notifications.sendNotification(temporaryTopic.creator.getId(),
				entity.getId(), "entity", "Your Topic was deleted because "
						+ justification);

		// added by Mohamed Hisham to delete the topic's relationships whenever
		// its deleted
		for (int i = 0; i < temporaryTopic.relationsSource.size(); i++) {
			TopicRelationships.delete(temporaryTopic.relationsSource.get(i).id);
		}
		for (int i = 0; i < temporaryTopic.relationsDestination.size(); i++) {
			TopicRelationships.delete(temporaryTopic.relationsDestination
					.get(i).id);
		}
		// }

		// >>>>added by Salma Osama to delete the topic's plan if the topic
		// is deleted
		if (plan != null) {
			Plans.deletePlan(plan.id);
		}
		// fadwa
		for (int i = 0; i < temporaryTopic.requestsToJoin.size(); i++)
			temporaryTopic.requestsToJoin.get(i).delete();
		for (int i = 0; i < temporaryTopic.invitations.size(); i++)
			temporaryTopic.invitations.get(i).delete();
		// fadwa

		UserRoleInOrganization.deleteEntityOrTopic(temporaryTopic.id, "topic");

		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " deleted the topic " + "<a href=\"/topics/show?topicId="
				+ temporaryTopic.id + "\">" + temporaryTopic.title + "</a>"
				+ " from entity " + "<a href=\"/mainentitys/viewentity?id="
				+ entity.id + "\">" + entity.name + "</a>";
		Log.addUserLog(logDescription, temporaryTopic, user, entity,
				entity.organization);
		object._delete();
		return true;
	}

	/**
	 * Deletes a topic and returns whether the topic was successfully deleted
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @param id
	 *            : the id of the topic to be deleted
	 * 
	 * @param justification
	 *            : the justification message that is sent by the deleter to the
	 *            creator of the topic
	 * 
	 * @return boolean
	 */
	public static boolean deleteTopicInternally(String id) {
		long temporaryTopicId = Long.parseLong(id);
		List<Tag> tags = Tag.findAll();
		List<User> allUsers = User.findAll();
		Topic temporaryTopic = Topic.findById(temporaryTopicId);
		List<User> followers = temporaryTopic.followers;
		notFoundIfNull(temporaryTopic);
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		String message = "Topic " + temporaryTopic.title + " has been deleted";
		List<User> users = Users.getEntityOrganizers(entity);
		List<MainEntity> allEntities = MainEntity.findAll();
		List<Idea> allIdeas = Idea.findAll();

		// added by Mohamed Hisham to delete the topic's relationships whenever
		// its deleted
		// {
		for (int i = 0; i < temporaryTopic.relationsSource.size(); i++) {
			TopicRelationships.delete(temporaryTopic.relationsSource.get(i).id);
		}
		for (int i = 0; i < temporaryTopic.relationsDestination.size(); i++) {
			TopicRelationships.delete(temporaryTopic.relationsDestination
					.get(i).id);
		}
		// }

		// >>>>added by Salma Osama to delete the topic's plan if the topic
		// is deleted
		if (plan != null) {
			Plans.deletePlan(plan.id);
		}
		// fadwa
		for (int i = 0; i < temporaryTopic.requestsToJoin.size(); i++)
			temporaryTopic.requestsToJoin.get(i).delete();
		for (int i = 0; i < temporaryTopic.invitations.size(); i++)
			temporaryTopic.invitations.get(i).delete();
		// fadwa

		for (int i = 0; i < tags.size(); i++) {
			if (tags.get(i).taggedTopics.contains(temporaryTopic)) {
				tags.get(i).taggedTopics.remove(temporaryTopic);
				tags.get(i).save();
			}
		}

		for (int i = 0; i < allUsers.size(); i++) {
			if (allUsers.get(i).topicsCreated.contains(temporaryTopic)) {
				allUsers.get(i).topicsCreated.remove(temporaryTopic);
				allUsers.get(i).save();
			}
		}

		for (int i = 0; i < followers.size(); i++) {
			if (followers.get(i).topicsIFollow.contains(temporaryTopic)) {
				followers.get(i).topicsIFollow.remove(temporaryTopic);
				followers.get(i).save();
			}
		}
		for (int i = 0; i < temporaryTopic.ideas.size(); i++) {
			Ideas.delete(temporaryTopic.ideas.get(i).id,
					"The Topic has been deleted");
		}
		for (int i = 0; i < temporaryTopic.commentsOn.size(); i++) {
			Comments.deleteComment(temporaryTopic.commentsOn.get(i).id);
		}
		UserRoleInOrganization.deleteEntityOrTopic(temporaryTopic.id, "topic");

		String justification = "Your Topic was deleted because "
				+ "The organization " + temporaryTopic.entity.organization
				+ " where this topic " + temporaryTopic + " was, was deleted!";

		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, temporaryTopic.id,
					"Topic", message);
		for (int i = 0; i < temporaryTopic.followers.size(); i++)
			Notifications.sendNotification(temporaryTopic.followers.get(i)
					.getId(), entity.getId(), "entity", message);
		for (int i = 0; i < allEntities.size(); i++) {
			if (allEntities.get(i).topicList.contains(temporaryTopic)) {
				allEntities.get(i).topicList.remove(temporaryTopic);
				allEntities.get(i).save();
			}
		}
		for (int j = 0; j < temporaryTopic.relationshipRequestsSource.size(); j++) {
			CreateRelationshipRequests
					.delete(temporaryTopic.relationshipRequestsSource.get(j).id);
		}
		for (int j = 0; j < temporaryTopic.relationshipRequestsDestination
				.size(); j++) {
			CreateRelationshipRequests
					.delete(temporaryTopic.relationshipRequestsDestination
							.get(j).id);
		}
		Notifications.sendNotification(temporaryTopic.creator.getId(),
				entity.getId(), "entity", justification);
		String logDescription = "The topic " + temporaryTopic.title
				+ " in entity " + "<a href=\"/mainentitys/viewentity?id="
				+ entity.id + "\">" + entity.name + "</a>" + "was deleted";
		Log.addUserLog(logDescription, temporaryTopic, entity,
				entity.organization);
		temporaryTopic.delete();
		return true;
	}

	/**
	 * Ovverides CRUD's delete and deletes a topic
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @param id
	 *            : the id of the topic to be deleted
	 * 
	 * @param justification
	 *            : the justification message that is sent by the deleter to the
	 *            creator of the topic
	 */
	public static void delete(String id, String justification) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		MainEntity entity = temporaryTopic.entity;
		try {

			deleteTopic(id, justification);
		}

		catch (Exception exception) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		redirect("mainentitys.viewentity", entity.id);
	}

	/**
	 * Hides a topic but keeps it in the database
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @param id
	 *            : the id of the topic to be hidden
	 * 
	 * @param justification
	 *            : the justification message that is sent by the deleter to the
	 *            creator of the topic
	 */
	public static void hide(String id, String justification) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		MainEntity entity = temporaryTopic.entity;
		User user = Security.getConnected();

		try {
			String message = user.username + " has hidden the topic "
					+ temporaryTopic.title;
			List<User> users = Users.getEntityOrganizers(entity);
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", message);
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), entity.getId(), "entity", message);
			Notifications.sendNotification(temporaryTopic.creator.getId(),
					entity.getId(), "entity", "Your Topic was hidden because "
							+ justification);

			String logDescription = "<a href=\"/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>"
					+ " hid the topic " + "<a href=\"/topics/show?topicId="
					+ temporaryTopic.id + "\">" + temporaryTopic.title + "</a>"
					+ " from entity " + "<a href=\"/mainentitys/viewentity?id="
					+ entity.id + "\">" + entity.name + "</a>";
			Log.addUserLog(logDescription, temporaryTopic, user, entity,
					entity.organization);
			temporaryTopic.hidden = true;
			temporaryTopic.hider = user;
			temporaryTopic.save();

		} catch (Exception exception) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		redirect("mainentitys.viewentity", entity.id);
	}

	/**
	 * Unhides a topic but keeps it in the database
	 * 
	 * @author Alia El Bolock
	 * 
	 * @story C3S9
	 * 
	 * @param id
	 *            : the id of the topic to be unhidden
	 */
	public static void unhide(String id) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		MainEntity entity = temporaryTopic.entity;
		User user = Security.getConnected();

		try {
			String message = user.username + " has unhidden the topic "
					+ temporaryTopic.title;
			List<User> users = Users.getEntityOrganizers(entity);
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", message);
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), entity.getId(), "entity", message);

			String logDescription = "<a href=\"/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>"
					+ " unhid the topic " + "<a href=\"/topics/show?topicId="
					+ temporaryTopic.id + "\">" + temporaryTopic.title + "</a>"
					+ " from entity " + "<a href=\"/mainentitys/viewentity?id="
					+ entity.id + "\">" + entity.name + "</a>";
			Log.addUserLog(logDescription, temporaryTopic, user, entity,
					entity.organization);

			temporaryTopic.hidden = false;
			temporaryTopic.save();

		} catch (Exception exception) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		// redirect(request.controller + ".list");
		redirect("mainentitys.viewentity", entity.id);
	}

	/**
	 * This method changes the relationship status of a topic
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S31
	 * 
	 * @param topicId
	 *            The id of the topic
	 * 
	 * @param createRelationship
	 *            Specifies whether a relationship can be created with that
	 *            topic
	 */

	public static void changeRelationStatus(long topicId,
			boolean createRelationship) {
		Topic topic = Topic.findById(topicId);
		topic.createRelationship = createRelationship;
		topic.save();
		redirect("Topics.show", topic.id);
	}

	/**
	 * 
	 * @author ${Ahmed EL-Hadi}
	 * 
	 * 
	 * @param ideaId
	 *            : id of the idea to be deleted
	 * 
	 * @description This method deletes and idea from the database
	 * 
	 * 
	 */
	public static void deleteIdea(long ideaId, String justification) {
		Idea idea = Idea.findById(ideaId);
		String message = "your idea " + idea.title + " has been deleted by "
				+ Security.getConnected().username + " Justification : "
				+ justification;
		System.out.println("ssssssssssssss lesa");
		List<Comment> commentslist = Comment.find("byCommentedIdea", idea)
				.fetch();
		System.out.println("aywaaaaaaaaaa");
		try {
			for (int i = 0; i < commentslist.size(); i++) {
				Comments.deleteComment(commentslist.get(i).getId());
				System.out.println("deleted comment " + i);
			}
			System.out.println("3ml delete lel comments");
			// printing
			System.out.println(idea.belongsToTopic);
			System.out.println(idea.commentsList);
			System.out.println(idea.plan);
			System.out.println(idea.delete().toString());

			String logDescription = "<a href=\"/users/viewprofile?userId="
					+ Security.getConnected().id + "\">"
					+ Security.getConnected().username + "</a>"
					+ " deleted the idea  " + idea.title;
			Log.addUserLog(logDescription, Security.getConnected(), idea,
					idea.belongsToTopic, idea.belongsToTopic.entity,
					idea.belongsToTopic.entity.organization);

			idea.save();
			idea.delete();
			Notifications.sendNotification(idea.author.id, idea.id, "Idea",
					message);

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			redirect(request.controller + ".view");
		}

		redirect("/topics/show?topicId=" + idea.belongsToTopic.id);
	}

	/**
	 * Unhide an idea
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S8
	 * 
	 * @param ideaId
	 *            : the id of the idea to be unhidden
	 */

	public static void unhideIdea(Long ideaId) {
		Idea idea = Idea.findById(ideaId);
		idea.hidden = false;
		idea.save();

		String message = "your idea " + idea.title + " is now visible";

		Notifications
				.sendNotification(idea.author.id, idea.id, "Idea", message);

		redirect("topics/show?topicId=" + idea.belongsToTopic.id);

	}

	/**
	 * Hide an idea
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S8
	 * 
	 * @param ideaId
	 *            : the id of the idea to be hidden
	 * @param justification
	 *            : the justification note sent to the user
	 */
	public static void hideIdea(Long ideaId, String justification) {
		Idea idea = Idea.findById(ideaId);
		Topic topic = idea.belongsToTopic;
		User user = Security.getConnected();
		try {
			String message = user.username + " has hidden the idea "
					+ idea.title + " Justification : " + justification;
			Notifications.sendNotification(idea.author.id, idea.id, "Idea",
					message);
			idea.hidden = true;
			idea.save();

		} catch (Exception e) {
		}
		redirect("topics.show", topic.id);
	}

	/**
	 * @description This method renders get a topic ID, and the renders the
	 *              ideas to a new page
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S7
	 * 
	 * @param topicId
	 *            : the id of the topic to be closed
	 * 
	 */
	public static void getIdeasToMerge(String topicId) {

		User user = Security.getConnected();
		Long topicIdLong = Long.parseLong(topicId);
		Topic targetTopic = Topic.findById(topicIdLong);
		notFoundIfNull(targetTopic);
		List ideas = new ArrayList<Idea>();
		List allIdeas = targetTopic.ideas;

		for (int i = 0; i < allIdeas.size(); i++) {
			Idea currentIdea = (Idea) allIdeas.get(i);

			if (currentIdea.hidden == false && currentIdea.isDraft == false) {
				ideas.add(currentIdea);
			}
		}

		render(topicId, ideas, user);
	}

	/**
	 * @description This method created a draft
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @param title
	 *            : The title of the topic
	 * @param description
	 *            : the description of the topic
	 * @param privacyLevel
	 *            : privacy level of the topic
	 * @param createRelationship
	 *            : can the topic create relationship
	 * @param entityId
	 *            : the id
	 */
	public static long createDraft(String title, String description,
			int privacyLevel, boolean createRelationship, long entityId) {

		User user = Security.getConnected();
		MainEntity targetEntity = MainEntity.findById(entityId);
		boolean isDraft = true;

		Topic draftTopic = new Topic(title, description, privacyLevel, user,
				targetEntity, createRelationship, isDraft);

		draftTopic.save();
		return draftTopic.id;
	}

	/**
	 * @description save a topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S23
	 * 
	 * @param topicId
	 *            the id of the topic
	 * 
	 * @param title
	 *            the title of the topic
	 * 
	 * @param description
	 *            the description of the topic
	 * 
	 * @param privacyLevel
	 *            the privacy level of the topic
	 * 
	 * @param createRelationship
	 *            can the relationships be created
	 */

	public static void saveDraft(long topicId, String title,
			String description, int privacyLevel, boolean createRelationship) {
		Topic targetTopic = Topic.findById(topicId);
		targetTopic.title = title;
		targetTopic.description = description;
		targetTopic.privacyLevel = privacyLevel;
		targetTopic.createRelationship = createRelationship;
		targetTopic.intializedIn = new Date();
		targetTopic.save();
	}

	/**
	 * @description publish a topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S23
	 * 
	 * @param topicId
	 *            the id of the topic
	 * 
	 * @param title
	 *            the title of the topic
	 * 
	 * @param description
	 *            the description of the topic
	 * 
	 * @param privacyLevel
	 *            the privacy level of the topic
	 * 
	 * @param createRelationship
	 *            can the relationships be created
	 */

	public static void postDraftTopic(long topicId, String title,
			String description, int privacyLevel, boolean createRelationship) {

		Topic targetTopic = Topic.findById(topicId);
		User user = Security.getConnected();
		targetTopic.title = title;
		targetTopic.description = description;
		targetTopic.privacyLevel = privacyLevel;
		targetTopic.createRelationship = createRelationship;
		targetTopic.intializedIn = new Date();
		targetTopic.isDraft = false;
		targetTopic.save();
		user.save();
		MainEntity entity = targetTopic.entity;
		Organization organization = entity.organization;
		String log = "<a href=\"/users/viewprofile?userId=" + user.id + "\">"
				+ user.username + "</a>" + " posted Topic "
				+ "<a href=\"/topics/show?topicId=" + targetTopic.id + "\">"
				+ targetTopic.title + "</a>" + " to the entity "
				+ "<a href=\"/mainentitys/viewentity?id=" + entity.id + "\">"
				+ entity.name + "</a>";
		Log.addUserLog(log, user, targetTopic, entity, organization);

	}

	/**
	 * @description This method renders to editing a draft page
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S23
	 * 
	 * @param topicId
	 *            Id of the draft topic to be edited
	 */

	public static void editDraft(long topicId) {
		Topic targetTopic = Topic.findById(topicId);
		notFoundIfNull(targetTopic);
		User user = Security.getConnected();
		render(targetTopic, user);
	}

	/**
	 * @description This method shows the draft Topics of the connected user
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S23
	 * 
	 */

	public static void getDraftTopics() {

		User user = Security.getConnected();
		List<Topic> draftTopics = new ArrayList<Topic>();
		List<Topic> allTopics = new ArrayList<Topic>();

		for (int i = 0; i < allTopics.size(); i++) {
			Topic currentTopic = allTopics.get(i);

			if (currentTopic.isDraft) {
				draftTopics.add(currentTopic);
			}
		}

		render(draftTopics, user);
	}

	/**
	 * @Description This method discards a draft
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @param topicId
	 *            The id of the draft that to be deleted
	 * 
	 */
	public static void discardDraftTopic(long topicId) {

		Topic targetTopic = Topic.findById(topicId);
		User user = Security.getConnected();
		targetTopic.delete();
		user.save();
		redirect("/ideas/getdrafts");

	}

	/**
	 * @Description This method discard a draft topic before publishing it using
	 *              crud
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @param topicId
	 *            The id of the draft that to be deleted
	 */

	public static void discardForCrud(long topicId) {

		Topic targetTopic = Topic.findById(topicId);
		User user = Security.getConnected();
		targetTopic.delete();
		user.save();

	}

	/**
	 * @Description: this method post a draft topic directly without editing
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @param topicId
	 *            : the ID of the topic to be posted
	 */
	public static void postWithoutEdit(long topicId) {
		Topic targetTopic = Topic.findById(topicId);

		targetTopic.isDraft = false;
		targetTopic.intializedIn = new Date();
		targetTopic.save();
		User user = Security.getConnected();
		MainEntity entity = targetTopic.entity;
		Organization organiztion = entity.organization;
		String log = "<a href=\"/users/viewprofile?userId=" + user.id + "\">"
				+ user.username + "</a>" + " posted Topic "
				+ "<a href=\"/show?topicId=" + targetTopic.id + "\">"
				+ targetTopic.title + "</a>";
		Log.addUserLog(log, user, targetTopic, organiztion, entity);

		redirect("/ideas/getdrafts");
	}

	/**
	 * @author Ahmed El-Hadi
	 * 
	 * @param userId
	 *            : the user who wants to join the topic to post
	 * 
	 * @description : the method which let the user join a topic to post in it
	 */
	public static void joinToPost(long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Role role = new Role("idea developer", "use").save();
		UserRoleInOrganizations.addEnrolledUser(user,
				topic.entity.organization, role, topicId, "topic");
		topic.save();
		redirect("/topics/show?topicId=" + topicId);
	}
}