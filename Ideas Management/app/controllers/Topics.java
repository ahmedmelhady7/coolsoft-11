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

import com.sun.mail.iap.Response;

import controllers.CRUD.ObjectType;

import play.data.binding.*;
import play.db.*;
import play.exceptions.*;
import play.i18n.*;
import play.mvc.With;
import models.*;
import notifiers.Mail;

@With(Secure.class)
public class Topics extends CRUD {

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

		// System.out.println("2ABEL !!!" + entityId + "," + topicId);
		Topic topic = Topic.findById(topicId);
		MainEntity entity = MainEntity.findById(entityId);
		List<Topic> listOfTopics = null;

		if (entity.topicList != null) {
			listOfTopics = entity.topicList;
			listOfTopics.remove(topic);
		}
		for (int i = 0; i < listOfTopics.size(); i++) {
			if (!listOfTopics.get(i).createRelationship) {
				System.out.println(listOfTopics.get(i).createRelationship);
				listOfTopics.remove(listOfTopics.get(i));
			}
		}
		render(topic, entity, listOfTopics);
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

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		User user = (User) Security.getConnected();
		Topic topic = (Topic) Topic.findById(topicId);
		MainEntity entity = topic.entity;

		if (!tag.equals("@@")) {

			if (!Users.isPermitted(user, "tag topics", entity.id, "entity")) {
				// user not allowed
				System.out.println("user not allowed");
				userNotAllowed = true;
			} else {
				for (int i = 0; i < globalListOfTags.size(); i++) {
					if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
							|| topic.entity.organization
									.equals(globalListOfTags.get(i).createdInOrganization)) {
						listOfTags.add(globalListOfTags.get(i));
					}
				}
				for (int i = 0; i < listOfTags.size(); i++) {
					if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
						if (!topic.tags.contains(listOfTags.get(i))) {
							topic.tags.add(listOfTags.get(i));
							listOfTags.get(i).taggedTopics.add(topic);
							listOfTags.get(i).save();

							for (int j = 0; j < listOfTags.get(i).followers
									.size(); j++) {
								Notifications.sendNotification(
										listOfTags.get(i).followers.get(j).id,
										topic.tags.get(i).getId(), "tag",
										"This topic has been tagged as " + tag);
							}
						} else {
							// tag already exists error message
							System.out.println("tag already exists");
							tagAlreadyExists = true;
						}
						tagExists = true;
					}
				}

				if (!tagExists) {
					Tag temp = new Tag(tag, topic.entity.organization, user);
					topic.tags.add(temp);
					temp.taggedTopics.add(topic);
					temp.save();
					System.out.println("new tag created and saved");
				}

				if (!tagAlreadyExists) {
					for (int j = 0; j < topic.followers.size(); j++) {
						Notifications.sendNotification(
								topic.followers.get(j).id, topicId, "topic",
								"This topic has been tagged as " + tag);
					}

					for (int j = 0; j < topic.getOrganizer().size(); j++) {
						Notifications.sendNotification(topic.getOrganizer()
								.get(j).id, topicId, "topic",
								"This topic has been tagged as " + tag);
					}
					// Notifications.sendNotification(topic.followers, topicId,
					// "topic", "This topic has been tagged as " + tag);
					// Notifications.sendNotification(topic.getOrganizer(),
					// topicId,
					// "topic", "This topic has been tagged as " + tag);
					// List<User> list1 = new ArrayList<User>();
					// list1.add(topic.entity.organization.creator);
					Notifications.sendNotification(
							topic.entity.organization.creator.id, topicId,
							"topic", "This topic has been tagged as " + tag);
				}
			}

		}
		topic.save();
		List<Tag> tags = topic.tags;
		render(tagAlreadyExists, userNotAllowed, tags, topicId);
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
		for (int i = 0; i < topic.ideas.size(); i++) {
			if(topic.ideas.get(i).title.equals(title)&& topic.ideas.get(i).description.equals(description))
				return;
		}
		Idea idea = new Idea(title, description, user, topic);
		topic.ideas.add(idea);
		topic.save();
		user.communityContributionCounter++;
		user.save();
		idea.save();
	}

	/**
	 * 
	 * This method reopens a closed topic, used after its plan gets deleted
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S22
	 * 
	 * @param topicId
	 *            : the id of the topic that to be reopened
	 */
	public static void reopen(long topicId) {

		Topic targetTopic = Topic.findById(topicId);

		targetTopic.openToEdit = true;
		targetTopic.save();

		redirect("/topics/show?topicId=" + topicId);
	}

	/**
	 * This Method returns a list of all closed topics
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S21
	 * 
	 * @return ArrayList<Topics>
	 */
	public static ArrayList<Topic> closedTopics() {
		// renamed from closedtopics to closedTopics;
		List closedTopics = (List) new ArrayList<Topic>();
		closedTopics = (List) Topic.find("openToEdit", false).fetch();
		return (ArrayList<Topic>) closedTopics;
	}

	// /**
	// *
	// * This method gets a list of followers for a certain topic
	// *
	// * @author Omar Faruki
	// *
	// * @story C2S29
	// *
	// * @param id
	// * : id of the topic
	// *
	// * @return void
	// */
	// public static void viewFollowers(long id) {
	// Topic topic = Topic.findById(id);
	// notFoundIfNull(topic);
	// List<User> follow = topic.followers;
	// render(follow);
	// }

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
		// searchList.add(topic.creator);

		ArrayList<User> organizers = (ArrayList) topic.getOrganizer();
		for (int i = 0; i < organizers.size(); i++) {
			if (!searchList.contains(organizers.get(i)))
				searchList.add(organizers.get(i));
		}

		List<BannedUser> bannedUserTopic = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "topic", topicId).fetch(); // List of blocked users from
													// a
		// topic
		List<BannedUser> bannedUserEntity = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "entity", entity.id).fetch(); // list of blocked users
		// from an entity
		List<BannedUser> bannedUserOrg = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"use", "organization", org.id).fetch(); // list of blocked user
		// from an organization
		// List<BannedUser> bannedUserPlan = BannedUser.find(
		// "byOrganizationAndActionAndResourceTypeAndResourceID", org,
		// "can post ideas to a Topic", "topic", topicId).fetch(); // list
		// of
		// users
		// banned
		// from
		// posting ideas in the
		// topic

		List<User> bannedUsers = new ArrayList<User>();
		List<User> user = new ArrayList<User>();
		List<BannedUser> bannedUser = new ArrayList<BannedUser>(); // list
		// appending
		// all the
		// previous
		// banneduser
		// lists
		bannedUser.addAll(bannedUserTopic);
		bannedUser.addAll(bannedUserEntity);
		bannedUser.addAll(bannedUserOrg);
		// bannedUser.addAll(bannedUserPlan);

		for (int i = 0; i < bannedUser.size(); i++) {
			bannedUsers.add((bannedUser.get(i)).bannedUser);
		}

		List<UserRoleInOrganization> allUser = new ArrayList<UserRoleInOrganization>();
		// List<User> u = new ArrayList<User>();
		if ((org.privacyLevel == 0 || org.privacyLevel == 1)
				&& (topic.privacyLevel == 2)) {

			// allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
			// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
			// org, id, "idea developer", "topic");
			allUser = UserRoleInOrganization.find("byEntitytopicIdAndType",
					topicId, "topic").fetch();
			for (int i = 0; i < allUser.size(); i++) {
				if ((allUser.get(i).role.roleName).equals("idea developer")
						&& (allUser.get(i).organization).equals(org)
						&& !(user.contains(allUser.get(i)))) {
					user.add(allUser.get(i).enrolled);
				}
			}

			// for (int i = 0; i < allUser.size(); i++) {
			// user.add((allUser.get(i)).enrolled);
			// }

			for (int i = 0; i < bannedUsers.size(); i++) {
				if (user.contains(bannedUsers.get(i))) {
					user.remove(bannedUsers.get(i));

				}
			}
		} else {

			if ((org.privacyLevel == 0 || org.privacyLevel == 1)
					&& (topic.privacyLevel == 1)) {
				// allUser = (List<UserRoleInOrganization>)
				// UserRoleInOrganization
				// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
				// org, -1, "idea developer", "none");
				allUser = UserRoleInOrganization.find("byOrganization", org)
						.fetch();
				for (int i = 0; i < allUser.size(); i++) {
					if ((allUser.get(i).role.roleName).equals("idea developer")
							&& !(user.contains(allUser.get(i)))) {
						user.add(allUser.get(i).enrolled);
					}
				}

				// for (int i = 0; i < allUser.size(); i++) {
				// user.add((allUser.get(i)).enrolled);
				// }

				for (int i = 0; i < bannedUsers.size(); i++) {
					if (user.contains(bannedUsers.get(i))) {
						user.remove(bannedUsers.get(i));

					}
				}
			}

			else {
				if ((org.privacyLevel == 2) && (topic.privacyLevel == 2)) {
					// allUser = (List<UserRoleInOrganization>)
					// UserRoleInOrganization
					// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
					// org, id, "idea developer", "topic");

					// for (int i = 0; i < allUser.size(); i++) {
					// user.add((allUser.get(i)).enrolled);
					// }

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
					if ((org.privacyLevel == 2) && (topic.privacyLevel == 1)) {

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
		// searchList.addAll(user);

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
	 * This method closes a topic, return true if was successful, returns false
	 * if the there was ideas and doesn't close the topic
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

		System.out.println("entered closeTopic for topic:" + topicId);
		long topicIdLong = Long.parseLong(topicId);
		Topic targetTopic = Topic.findById(topicIdLong);
		User actor = User.findById(Security.getConnected().id);
		List<User> organizers = targetTopic.getOrganizer();
		List<User> followers = targetTopic.followers;

		String action = "close a topic and promote it to execution";
		String notificationDescription = "Topic " + targetTopic.title
				+ " has been closed and promoted to execution.";

		System.out.println("ideas count:" + targetTopic.getIdeas().size()
				+ " in topic" + targetTopic.getId() + "-" + targetTopic.id);

		/*
		 * checks if topic is empty if (targetTopic.getIdeas().size() == 0) {
		 * System.out.println("Topic has no ideas"); return; }
		 */

		// closing the topic to editing
		targetTopic.openToEdit = false;
		targetTopic.save();

		// Sending Notifications
		// send notification to organizers
		for (int i = 0; i < organizers.size(); i++) {
			Notifications.sendNotification(organizers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}
		// send notification to followers
		for (int i = 0; i < followers.size(); i++) {
			Notifications.sendNotification(followers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}

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
		Topic temporaryTopic = (Topic) object; // we temporarily save the object
												// created by
		// the form in temporaryTopic to validate it before
		// saving
		System.out.println("create() entered");
		MainEntity topicEntity = MainEntity.findById(entityId);
		temporaryTopic.entity = topicEntity;
		User myUser = Security.getConnected();
		temporaryTopic.creator = myUser;
		System.out.println("the idea beforew validation check"
				+ temporaryTopic.toString());

		if (temporaryTopic.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, entityId);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, message, entityId);
			}
		}

		Organization topicOrganization = topicEntity.organization;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) temporaryTopic.tags;
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
						temporaryTopic.followers, temporaryTopic.tags, message);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, entityId);
			}
		}

		if (temporaryTopic.privacyLevel < 1 || temporaryTopic.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, entityId);
			}

		}

		System.out.println("create() about to save object");
		object._save();
		((Topic) object).openToEdit = true;
		object._save();

		// myUser.communityContributionCounter =
		// myUser.communityContributionCounter +1 ;
		// myUser.communityContributionCounter++;
		// myUser.save();
		System.out.println(myUser.communityContributionCounter);
		System.out.println("create() object saved");
		temporaryTopic = (Topic) object;
		Calendar claendar = new GregorianCalendar();
		// Logs.addLog( temporaryTopic.creator, "add", "Task",
		// temporaryTopic.id,
		// temporaryTopic.entity.organization, calendar.getTime() );
		String message2 = temporaryTopic.creator.username
				+ " has Created the topic " + temporaryTopic.title + " in "
				+ temporaryTopic.entity;
		if (temporaryTopic.followers != null) {
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), temporaryTopic.id, "Topic", "A new Topic: '"
						+ temporaryTopic.title + "' has been added in entity '"
						+ temporaryTopic.entity.name + "'");
		}

		List<User> users = Users.getEntityOrganizers(temporaryTopic.entity);
		users.add(temporaryTopic.entity.organization.creator);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, temporaryTopic.id,
					"Topic", "A new Topic: '" + temporaryTopic.title
							+ "' has been added in entity '"
							+ temporaryTopic.entity.name + "'");

		// temporaryTopic.init();
		flash.success(Messages.get("crud.created", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			System.out
					.println("create() done will redirect to topics/show?topicId "
							+ message2);
			redirect("/topics/show?topicId=" + ((Topic) object).getId());

			// redirect("/topics/show?" + ((Topic) object).getId(), message2);
			// redirect( "/storys/liststoriesinproject?projectId=" +
			// temporaryTopic.taskStory.componentID.project.id + "&storyId=" +
			// temporaryTopic.taskStory.id );
		}
		if (params.get("_saveAndAddAnother") != null) {
			System.out
					.println("create() done will redirect to blank.html to add another "
							+ message2);
			render(request.controller.replace(".", "/") + "/blank.html",
					message2, entityId);

			/*
			 * render(request.controller.replace(".", "/") + "/blank.html",
			 * entityId, type, temporaryTopic.title, temporaryTopic.entity,
			 * temporaryTopic.description, temporaryTopic.followers,
			 * temporaryTopic.tags, message);
			 */
		}
		System.out
				.println("create() done will redirect to show.html to show created"
						+ message2);
		redirect(request.controller + ".view", ((Topic) object).getId(),
				message2);
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
		MainEntity topicEntity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		System.out.println("blank() entered entity " + entityId + " and user "
				+ user.toString());
		// List<User> followers = entity.followers;
		// ArrayList<MainEntity> entitiesFollowed = (ArrayList<MainEntity>)
		// user.followingEntities;//display some of them on the side for quick
		// navigation

		// handle permissions, depending on the privacyLevel the user will be
		// directed to a different page

		try {
			System.out.println("blank() done about to render");
			render(type, entityId, user /* , followers, entitiesFollowed */);

		} catch (TemplateNotFoundException exception) {
			System.out
					.println("blank() done with exception about to render CRUD/blank.html");
			render("CRUD/blank.html", type, entityId);
		}

	}

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
		System.out.println("topic is hidden? " + temporaryTopic.hidden);
		System.out.println(temporaryTopic.title);
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
		boolean topicNotClosed = targetTopic.openToEdit;
		boolean alreadyReported = false;
		boolean canDelete = Users.isPermitted(actor, "hide and delete an idea",
				topicIdLong, "topic");
		boolean alreadyReportedTopic = false;
		boolean canRequestRelationship = false;
		for (int i = 0; i < ideas.size(); i++) {
			Idea idea = ideas.get(i);

			for (int j = 0; j < idea.reporters.size()
					|| j < actor.ideasReported.size(); j++) {
				System.out.println("gowa el loop");
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
		User reporter = Security.getConnected();
		if (targetTopic.reporters != null) {
			reportersId = targetTopic.reporters.split(",");
			long reporterId = 0;
			if (!targetTopic.reporters.isEmpty()) {
				for (int i = 0; i < reportersId.length
						&& targetTopic.reporters != ""; i++) {
					reporterId = Integer.parseInt(reportersId[i]);
					if (reporterId == reporter.id) {
						reporters.add(reporter);
					}
				}
			}
		}
		for (int i = 0; i < reporters.size(); i++) {
			System.out.println(reporters.get(i).toString());
			System.out.println("gowa el loop");
			System.out.println("Ana meen ?! " + reporter.toString());
			System.out.println(alreadyReportedTopic);
			if (reporter.toString().equals(reporters.get(i).toString())) {
				alreadyReportedTopic = true;
				System.out
						.println("3mlha w 5ala el already reported b true****************************************************************************************************************************************************************");
				break;
			} else
				alreadyReportedTopic = false;
		}
		ArrayList<Idea> hiddenIdeas = new ArrayList<Idea>();
		for (int i = 0; i < ideas.size(); i++) {
			if (ideas.get(i).hidden) {
				hiddenIdeas.add(ideas.get(i));
				System.out.println("aywan aywan");
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
		if ((temporaryTopic.privacyLevel == 2)
				&& Users.isPermitted(
						actor,
						"Accept/Reject requests to post in a private topic in entities he/she manages",
						temporaryTopic.id, "topic"))
			allowed = 1;
		// Note isPermitted has a bug here!
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
		if (Users.getEntityOrganizers(entity).contains(actor) &&
				Users.isPermitted(actor, "Request to start a relationship with other items;", topicId, "Topic")) {
			canRequestRelationship = true;
		}
		try {

			render(type, object, tags, /* canUse, */alreadyReportedTopic,
					creator, followers, ideas, hiddenIdeas, numberOfIdeas,
					comments, entity, canDelete, alreadyReported, plan,
					openToEdit, privacyLevel, deleteMessage, deletable,
					topicIdLong, canClose, canPlan, targetTopic, allowed,
					permission, topicId, canPost, canNotPost, pending,
					follower, canCreateRelationship, seeRelationStatus,
					createRelationship, actor, hidden, canRestrict, check,
					canMerge, canRequestRelationship, topicIsLocked,
					organisation, check1, check2);

		} catch (TemplateNotFoundException exception) {
			render("CRUD/show.html", type, object, topicId,
					canCreateRelationship);
		}
	}

	public static void requestRelationship(long userId, long organisationId,
			long entityId, long topicId) {
		Organization organisation = Organization.findById(organisationId);
		User user = User.findById(userId);
		MainEntity entity = MainEntity.findById(entityId);
		Topic topic = Topic.findById(topicId);
		render(user, organisation, entity, topic);
	}

	public static void viewRelationships(long userId, long organisationId,
			long entityId, long topicId, boolean canRequestRelationship) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		MainEntity entity = MainEntity.findById(entityId);
		Topic topic = Topic.findById(topicId);
		render(user, organisation, entity, canRequestRelationship, topic);
	}

//	public static void deleteRequest(long userId, long relationId, int type) {
//		User user = User.findById(userId);
//		TopicRelationship relation = TopicRelationship.findById(relationId);
//		RenameEndRelationshipRequest deleteRequest = new RenameEndRelationshipRequest(
//				user, relation, type, null);
//		deleteRequest.save();
//	}
//
//	public static void renameRequest(long userId, long relationId, int type,
//			String newName) {
//		User user = User.findById(userId);
//		TopicRelationship relation = TopicRelationship.findById(relationId);
//		RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
//				user, relation, type, newName);
//		renameRequest.save();
//	}

	public static boolean topicRequestIsDuplicate(Topic topic, String source,
			String destination, String name) {
		for (CreateRelationshipRequest request : topic.relationshipRequestsSource) {
			if (request.sourceTopic.title.equalsIgnoreCase(source)
					&& request.destinationTopic.title
							.equalsIgnoreCase(destination)
					&& request.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		for (CreateRelationshipRequest request : topic.relationshipRequestsDestination) {
			if (request.sourceTopic.title.equalsIgnoreCase(source)
					&& request.destinationTopic.title
							.equalsIgnoreCase(destination)
					&& request.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		for (TopicRelationship relation : topic.relationsSource) {
			if (relation.source.title.equalsIgnoreCase(source)
					&& relation.destination.title.equalsIgnoreCase(destination)
					&& relation.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		for (TopicRelationship relation : topic.relationsDestination) {
			if (relation.source.title.equalsIgnoreCase(source)
					&& relation.destination.title.equalsIgnoreCase(destination)
					&& relation.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
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
		System.out.println("entered view() for topic " + topicId);
		Topic temporaryTopic = (Topic) object;
		System.out.println(temporaryTopic.title);
		System.out.println(temporaryTopic.description);
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

		try {
			System.out.println("view() done, about to render");
			render(type, object, tags, creator, followers, ideas, comments,
					entity, plan, openToEdit, privacyLevel, deleteMessage,
					deletable, topicId, createRelationship);
		} catch (TemplateNotFoundException exception) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/topics/view.html", type, object, tags, creator, followers,
					ideas, comments, entity, plan, openToEdit, privacyLevel,
					deleteMessage, deletable, topicId, createRelationship);
		}
	}

	/*
	 * public static void edit(String topicId) {
	 * 
	 * ObjectType type = ObjectType.get(getControllerClass());
	 * notFoundIfNull(type); Model object = type.findById(topicId);
	 * notFoundIfNull(object); System.out.println("entered edit() for topic " +
	 * topicId); Topic temporaryTopic = (Topic) object;
	 * flash.success(Messages.get("crud.edit", type.modelName, ((Topic)
	 * object).getId())); System.out.println("About to redirect from edit()");
	 * redirect("/topics/show?topicId=" + ((Topic) object).getId(),
	 * temporaryTopic); }
	 */

	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param page
	 *            : page of the list we are in
	 * 
	 * @param search
	 *            : search string
	 * 
	 * @param searchFields
	 *            : the fields we want to search
	 * 
	 * @param orderBy
	 *            : criteria to order list by
	 * 
	 * @param order
	 *            : the order of the list
	 * 
	 * @description This method renders the list of topics, with search and sort
	 *              options
	 * 
	 */
	public static void list(int page, String search, String searchFields,
			String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		System.out.println("list() entered ");
		List<Model> objects = type.findPage(page, search, searchFields,
				orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields,
				(String) request.args.get("where"));
		Long totalCount = type.count(null, null,
				(String) request.args.get("where"));
		try {
			System.out.println("list() done, will render ");
			System.out.println("list() done, will render " + type.toString());
			System.out
					.println("list() done, will render " + objects.toString());
			System.out.println("list() done, will render " + count);
			System.out.println("list() done, will render " + totalCount);
			System.out.println("list() done, will render " + page);
			System.out.println("list() done, will render " + orderBy);
			System.out.println("list() done, will render " + order);
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException exception) {
			System.out
					.println("list() done with exceptions, will render CRUD/list.html ");
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * closedTopicsList
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S21
	 * 
	 * @param page
	 *            : page of the list we are in
	 * 
	 * @param search
	 *            : search string
	 * 
	 * @param searchFields
	 *            : the fields we want to search
	 * 
	 * @param orderBy
	 *            : criteria to order list by
	 * 
	 * @param order
	 *            : the order of the list
	 * 
	 * @description This method renders the list of closed topics, with search
	 *              and sort options
	 * 
	 */
	public static void closedTopicsList(int page, String search,
			String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Topic> openTopics = Topic.find("openToEdit", false).fetch();
		Long totalCount = (long) openTopics.size();
		Long count = (long) openTopics.size();
		try {
			render(type, openTopics, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException exception) {
			render("CRUD/list.html", type, openTopics, count, totalCount, page,
					orderBy, order);
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
		System.out.println(topicId);
		Model object = Topic.findById(Long.parseLong(topicId));
		notFoundIfNull(object);
		System.out.println("entered save() for " + topicId);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic temporaryTopic = (Topic) object; // we temporarily save the object
												// edited in
		// the form in temporaryTopic to validate it before
		// saving
		MainEntity entity = temporaryTopic.entity;
		User myUser = Security.getConnected();
		boolean createRelationship = temporaryTopic.createRelationship;
		temporaryTopic.creator = myUser;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) temporaryTopic.tags;
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

			/*
			 * else if( !Users.isPermitted(myUser, "edit topics",
			 * topicEntity.getId(), "entity")) { message =
			 * "Sorry but you are not allowed to edit topics in this entity"; }
			 */
			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						object, topicId, createRelationship);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/view.html", type, topicId);
			}
		}

		if (temporaryTopic.privacyLevel < 1 || temporaryTopic.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, temporaryTopic.title,
						temporaryTopic.entity, temporaryTopic.description,
						temporaryTopic.followers, temporaryTopic.tags, message,
						object, topicId, createRelationship);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/view.html", type, topicId);
			}

		}

		System.out.println("about to save() topic");
		System.out.println(temporaryTopic.createRelationship);

		object._save();
		String message2 = "User: '" + myUser.firstName
				+ "' has edited topic  '" + temporaryTopic.title
				+ "' in entity '" + temporaryTopic.entity.name + "'";
		Log.addUserLog(message2, temporaryTopic, myUser, entity,
				entity.organization);
		List<User> users = Users.getEntityOrganizers(temporaryTopic.entity);
		if (!users.contains(temporaryTopic.entity.organization.creator))
			users.add(temporaryTopic.entity.organization.creator);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, temporaryTopic.id,
					"Topic", message2);

		System.out.println("save() done, not redirected yet");

		flash.success(Messages.get("crud.saved", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			redirect("/topics/show?topicId=" + ((Topic) object).getId());
			System.out
					.println("save() done, redirected to topics/show?topicId");

		}
		redirect(request.controller + ".view", ((Topic) object).getId());
		System.out.println("save() done, redirected to default view.html");
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
		Topic t = Topic.findById(topicId);
		if (t.followers.contains(user))
			System.out.println("You are already a follower");
		else {
			t.followers.add(user);
			t.save();
			user.topicsIFollow.add(t);
			user.save();
			redirect(request.controller + ".show", t.id,
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
		Topic topic = Topic.findById(topicId);
		if (f.equals("true"))
			followTopic(topicId);
		render(topic);
	}

	/**
	 * Overrides CRUD's delete() and deletes a topic
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
		System.out.println("entered my delete");
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Topic temporaryTopic = (Topic) object;
		MainEntity entity = temporaryTopic.entity;
		Plan plan = temporaryTopic.plan;
		System.out.println("entering try");
		try {
			System.out.println("entered try");
			Calendar calendar = new GregorianCalendar();
			User myUser = Security.getConnected();
			// Logs.addLog( myUser, "delete", "Task", temporaryTopic.id,
			// temporaryTopic.taskStory.componentID.project, calendar.getTime()
			// );
			String message = myUser.username + " has deleted the topic "
					+ temporaryTopic.title;
			List<User> users = Users.getEntityOrganizers(entity);
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", message);
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), entity.getId(), "entity", message);
			Notifications.sendNotification(temporaryTopic.creator.getId(),
					entity.getId(), "entity", justification);
			System.out.println(justification);

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

			Log.addUserLog(message, temporaryTopic, myUser, entity,
					entity.organization);
			object._delete();
			System.out.println("deleted");
			System.out.println("leaving try");

		} catch (Exception exception) {
			System.out.println("entered catch");
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		System.out.println("flash.success");
		// redirect(request.controller + ".list");
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
		User myUser = Security.getConnected();

		try {
			System.out.println("entered try");
			Calendar calendar = new GregorianCalendar();
			// Logs.addLog( myUser, "delete", "Task", temporaryTopic.id,
			// temporaryTopic.taskStory.componentID.project, calendar.getTime()
			// );
			String message = myUser.username + " has hidden the topic "
					+ temporaryTopic.title;
			List<User> users = Users.getEntityOrganizers(entity);
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", message);
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), entity.getId(), "entity", message);
			Notifications.sendNotification(temporaryTopic.creator.getId(),
					entity.getId(), "entity", justification);
			Log.addUserLog(message, temporaryTopic, myUser, entity,
					entity.organization);
			System.out.println(justification);
			temporaryTopic.hidden = true;
			temporaryTopic.hider = myUser;
			temporaryTopic.save();
			System.out.println("test " + temporaryTopic.hidden);
			System.out.println("leaving try");

		} catch (Exception exception) {
			System.out.println("entered catch");
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		System.out.println("flash.success");
		// redirect(request.controller + ".list");
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
		User myUser = Security.getConnected();

		try {
			System.out.println("entered try");
			Calendar calendar = new GregorianCalendar();
			// Logs.addLog( myUser, "delete", "Task", temporaryTopic.id,
			// temporaryTopic.taskStory.componentID.project, calendar.getTime()
			// );
			String message = myUser.username + " has unhidden the topic "
					+ temporaryTopic.title;
			List<User> users = Users.getEntityOrganizers(entity);
			for (int i = 0; i < users.size(); i++)
				Notifications.sendNotification(users.get(i).id,
						temporaryTopic.id, "Topic", message);
			for (int i = 0; i < temporaryTopic.followers.size(); i++)
				Notifications.sendNotification(temporaryTopic.followers.get(i)
						.getId(), entity.getId(), "entity", message);

			Log.addUserLog(message, temporaryTopic, myUser, entity,
					entity.organization);

			temporaryTopic.hidden = false;
			temporaryTopic.save();
			System.out.println("test " + temporaryTopic.hidden);
			System.out.println("leaving try");

		} catch (Exception exception) {
			System.out.println("entered catch");
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		System.out.println("flash.success");
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
		// String topiccId = topicId + "";
		redirect("Topics.show", topic.id);
		// Topics.show(topiccId);
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
	public static void deleteIdea(long ideaId) {
		Idea idea = Idea.findById(ideaId);
		try {
			idea.delete();
		} catch (Exception e) {
			redirect(request.controller + ".show");
		}
		flash.success(Messages.get("crud.deleted"));
		redirect(request.controller + ".list");
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
		idea.hidden=false;
		idea.save();
		
		String message =  "you idea "+ idea.title +" is now visible";
		
		Notifications.sendNotification(idea.author.id, idea.id, "Idea", message);
		
		System.out.println("abo hadiiiiiiiiiiiiiiiiii");
		
		redirect("topics/show?topicId="+idea.belongsToTopic.id);
		
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
	 */
	public static void hideIdea(Long ideaId,String justification) {
		Idea idea = Idea.findById(ideaId);
		Topic topic = idea.belongsToTopic;
		User user = Security.getConnected();
		try {
			// Logs.addLog( myUser, "delete", "Task", temporaryTopic.id,
			// temporaryTopic.taskStory.componentID.project, cal.getTime() );
			String message = user.username + " has hidden the topic "
					+ idea.title;
			List<User> users = Users.getEntityOrganizers(topic.entity);
			// for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(idea.author.id/*
															 * users.get(i).id
															 */, idea.id,
					"Idea", message);
			// for (int i = 0; i < topic.followers.size(); i++)
			// Notifications.sendNotification(topic.followers.get(i).getId(),
			// topic.getId(), "entity", message);
			idea.hidden = true;
			idea.save();
			System.out.println("hidden");
			System.out.println("leaving try");

		} catch (Exception e) {
			System.out.println("entered catch");
		}
		System.out.println("flash.success");
		// redirect(request.controller + ".list");
		redirect("topics.show", topic.id);
	}

	/**
	 * This method renders get a topic ID, and the renders the ideas to a new
	 * page
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

		Long topicIdLong = Long.parseLong(topicId);
		Topic targetTopic = Topic.findById(topicIdLong);
		List ideas = new ArrayList<Idea>();
		List allIdeas = targetTopic.getIdeas();

		for (int i = 0; i < allIdeas.size(); i++) {
			Idea currentIdea = (Idea) allIdeas.get(i);

			if (currentIdea.hidden == false && currentIdea.isDraft == false) {
				ideas.add(currentIdea);
			}
		}

		render(topicId, ideas);
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
	 * @param entityId
	 *            : the id
	 */
	public static void createDraft(String title, String description,
			int privacyLevel, boolean createRelationship, long entityId) {

		User user = Security.getConnected();
		MainEntity targetEntity = MainEntity.findById(entityId);
		boolean isDraft = true;

		Topic draftTopic = new Topic(title, description, privacyLevel, user,
				targetEntity, createRelationship, isDraft);

		draftTopic.save();

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


	public static void saveDraft(long topicId, String title, String description,
			int privacyLevel, boolean createRelationship) {
		
		Topic targetTopic = Topic.findById(topicId);

		targetTopic.title = title;
		targetTopic.description = description;
		targetTopic.privacyLevel = privacyLevel;
		targetTopic.createRelationship = createRelationship;
		targetTopic.intializedIn = new Date();

		targetTopic.save();
		
		System.out.println(targetTopic.title + targetTopic.description + targetTopic.privacyLevel +
				targetTopic.createRelationship + targetTopic.intializedIn);
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

	public static void postDraftTopic(long topicIdId, String title,
			String description, int privacyLevel, boolean createRelationship) {

		Topic targetTopic = Topic.findById(topicIdId);
		User user = Security.getConnected();
		MainEntity entity = targetTopic.entity;
		
		saveDraft(topicIdId, title, description,
				 privacyLevel, createRelationship);

		targetTopic.isDraft = false;
		targetTopic.save();
		user.save();
		entity.save();
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


		User user = Security.getConnected();
	//	System.out.println("Topic title:" + targetTopic.title);
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

		// User actor = Security.getConnected();

		List<Topic> draftTopics = new ArrayList<Topic>();
		List<Topic> allTopics = new ArrayList<Topic>();

		for (int i = 0; i < allTopics.size(); i++) {
			Topic currentTopic = allTopics.get(i);

			if (currentTopic.isDraft) {
				draftTopics.add(currentTopic);
			}
		}

		render(draftTopics);
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
		//MainEntity entity = targetTopic.entity;
		
		targetTopic.delete();
		user.save();
		//entity.save();
		
	}
}