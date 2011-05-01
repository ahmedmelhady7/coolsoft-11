package controllers;

import java.util.ArrayList;
import java.util.List;

import models.BannedUser;
import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import java.lang.*;
import java.lang.reflect.*;
import java.util.*;

import controllers.CRUD.ObjectType;

import play.data.binding.*;
import play.db.*;
import play.exceptions.*;
import play.i18n.*;
import models.*;

public class Topics extends CRUD {

	/**
	 *This method first checks if the user is allowed to tag the topic,
	 *searches for the tag in the global list of tags,
	 *if found => check if it already the topic had the same tag already or add the new one to the list
	 *if not => create a new tag, save it to db, add it to the list
	 *send notifications to followers, organizers and organization lead of the tagged topic
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S2
	 * 
	 * @param topicID
	 *            : the topic that is being tagged
	 *            
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void tagTopic(long topicID, String tag) {

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = Tag.findAll();
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicID);

		if (!topic.organizers.contains(user)) {
			// user not allowed
			userNotAllowed = true;
		} else {
			for (int i = 0; i < listOfTags.size(); i++) {
				if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
					if (!topic.tags.contains(listOfTags.get(i))) {
						topic.tags.add(listOfTags.get(i));
						Notifications.sendNotification(listOfTags.get(i).followers, topic.tags.get(i).getId(), "tag", "This topic has been tagged as " + tag);
					} else {
						// tag already exists error message
						tagAlreadyExists = true;
					}
					tagExists = true;
				}
			}
			
			if (!tagExists) {
				Tag temp = new Tag(tag);
				temp.save();
				topic.tags.add(temp);
			}
			
			if (!tagAlreadyExists) {
				Notifications.sendNotification(topic.followers, topicID, "topic", "This topic has been tagged as " + tag);
				Notifications.sendNotification(topic.organizers, topicID, "topic", "This topic has been tagged as " + tag);
				List<User> list1 = new ArrayList<User>();
				list1.add(topic.entity.organization.creator);
				Notifications.sendNotification(list1, topicID, "topic", "This topic has been tagged as " + tag);
			
				}
		}
		render(tagAlreadyExists, tagExists, userNotAllowed, topic.tags);
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
	 * @param user
	 *            : the user who posted the idea
	 * 
	 * @param topic
	 *            : the topic which the idea belongs/added to
	 * 
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description of the idea
	 * 
	 * @param privacyLevel
	 *            : the level of privacy of the idea
	 * 
	 */

	public static void postIdea(User user, Topic topic, String title,
			String description) {
		// Idea idea = new Idea(title, description, user, topic);
		// idea.privacyLevel = topic.privacyLevel;
		render(title, description);
	}

	/*
	 * This method is used for rendering the user and the topic from the
	 * postIdea(User user, Topic topic, String title,String description)
	 */
	public static void postI() {
		User u = new User("a@gmail.com", "1234", "ah", "had", "hadi.18", 0,
				null, "", "");
		u._save();
		Topic t = new Topic("a", "s", (short) 0, u);
		t._save();
		render(u, t);
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
	 * 
	 * @return void
	 */
	public static boolean reopen(long topicId, User actor) {

		Topic targetTopic = Topic.findById(topicId);

		if (!targetTopic.organizers.contains(actor)) {
			return false;
		}

		targetTopic.openToEdit = true;
		/* TODO: buttons to be adjusted in view */

		return true;
	}

	/**
	 * This Method returns a list of all closed topics
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @return ArrayList<Topics>
	 */
	public static ArrayList<Topic> closedTopics() {
		List closedtopics = (List) new ArrayList<Topic>();
		closedtopics = (List) Topic.find("openToEdit", false).fetch();
		return (ArrayList<Topic>) closedtopics;
	}

	/**
	 * 
	 * This method gets a list of followers for a certain topic
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S29
	 * 
	 * @param id
	 *            : id of the topic
	 * 
	 * @return void
	 */
	public static void viewFollowers(long id) {
		Topic topic = Topic.findById(id);
		notFoundIfNull(topic);
		List<User> follow = topic.followers;
		render(follow);
	}

	/**
	 * This Method sends a request to post on a topic for a user to the
	 * organizer
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param topicId
	 *            : the id of the topic
	 * 
	 * @param user
	 *            : the user who request to post
	 * 
	 * @return void
	 */

	public static void requestToBost(long topicId, User user) {
		Topic t = Topic.findById(topicId);
		t.requestFromUserToPost(user);
	}

	/**
	 * This renders the RequestToPost.html to show the list of all topics where
	 * the user is not allowed to post within an organization
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param org
	 *            : The organization where the topics are
	 * 
	 * @param user
	 *            : the user who wants to request
	 * 
	 * @return void
	 */

	public static void requestTopicList(User user, Organization org) {
		List<MainEntity> e = org.entitiesList;
		List<Topic> topics = new ArrayList<Topic>();
		List<Topic> temp;
		for (int i = 0; i < e.size(); i++) {
			temp = e.get(i).topicList;
			for (int j = 0; j < temp.size(); j++) {
				if (user.topicsIOrganize.indexOf(temp.get(j)) < 0) {
					topics.add(temp.get(j));
				}
			}
		}
		render(topics, user);
	}

	/**
	 * This method searches for unblocked users who are allowed to post in a
	 * certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S13
	 * 
	 * @param topicId
	 *            : the id of the topic to search in
	 * 
	 * @return List<User>
	 */
	public static List<User> searchByTopic(long id) {

		Topic topic = Topic.findById(id);
		MainEntity entity = topic.entity;
		Organization org = entity.organization;
		ArrayList<User> searchList = (ArrayList) User.find("byIsAdmin", true)
				.fetch();
		searchList.add(org.creator);
		// searchList.add(topic.creator);

		ArrayList<User> organizer = (ArrayList) Users
				.getEntityOrganizers(entity);
		searchList.addAll(organizer);

		List<BannedUser> bannedUserT = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "topic", id).fetch(); // List of blocked users from a
												// topic
		List<BannedUser> bannedUserE = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "entity", entity.id).fetch(); // list of blocked users
														// from an entity
		List<BannedUser> bannedUserO = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "organization", org.id).fetch(); // list of blocked user
														// from an organization
		List<BannedUser> bannedUserP = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"post idea", "topic", id).fetch(); // list of users banned from
													// posting ideas in the
													// topic

		List<User> bUser = new ArrayList<User>();
		List<User> user = new ArrayList<User>();
		List<BannedUser> bannedUser = new ArrayList<BannedUser>(); // list
																	// appending
																	// all the
																	// previous
																	// banneduser
																	// lists
		bannedUser.addAll(bannedUserT);
		bannedUser.addAll(bannedUserE);
		bannedUser.addAll(bannedUserO);
		bannedUser.addAll(bannedUserP);

		for (int i = 0; i < bannedUser.size(); i++) {
			bUser.add((bannedUser.get(i)).bannedUser);
		}

		List<UserRoleInOrganization> allUser = new ArrayList<UserRoleInOrganization>();

		if ((org.privacyLevel == 0 || org.privacyLevel == 1)
				&& (topic.privacyLevel == 0 || topic.privacyLevel == 1)) {
			allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
							org, id, "idea developer", "topic");

			for (int i = 0; i < allUser.size(); i++) {
				user.add((allUser.get(i)).enrolled);
			}

			for (int i = 0; i < bUser.size(); i++) {
				if (user.contains(bUser.get(i))) {
					user.remove(bUser.get(i));

				}
			}
		} else {

			if ((org.privacyLevel == 0 || org.privacyLevel == 1)
					&& (topic.privacyLevel == 2)) {
				allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
						.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
								org, -1, "idea developer", "none");

				for (int i = 0; i < allUser.size(); i++) {
					user.add((allUser.get(i)).enrolled);
				}

				for (int i = 0; i < bUser.size(); i++) {
					if (user.contains(bUser.get(i))) {
						user.remove(bUser.get(i));

					}
				}
			}

			else {
				if ((org.privacyLevel == 2)
						&& (topic.privacyLevel == 0 || topic.privacyLevel == 1)) {
					allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
							.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
									org, id, "idea developer", "topic");

					for (int i = 0; i < allUser.size(); i++) {
						user.add((allUser.get(i)).enrolled);
					}

					for (int i = 0; i < bUser.size(); i++) {
						if (user.contains(bUser.get(i))) {
							user.remove(bUser.get(i));

						}
					}
				} else {
					if ((org.privacyLevel == 2) && (topic.privacyLevel == 2)) {

						user = User.findAll();

						for (int i = 0; i < bUser.size(); i++) {
							if (user.contains(bUser.get(i))) {
								user.remove(bUser.get(i));

							}
						}
					}
				}
			}
		}
		searchList.addAll(user);
		return searchList;

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
	 * @return boolean
	 */
	public static boolean closeTopic(long topicId, User actor) {
		Topic targetTopic = Topic.findById(topicId);

		// checks if topic is empty or the user is not an organizer
		if (targetTopic.ideas.size() == 0
				|| !targetTopic.organizers.contains(actor)) {
			return false;
		}

		// closing the topic to editing
		targetTopic.openToEdit = false;

		// Sending Notifications
		String notificationDescription = "Topic " + targetTopic.title
				+ " has been closed and promoted to execution.";

		// send notification to organizers
		Notifications.sendNotification(targetTopic.organizers, targetTopic.id,
				"Topic", notificationDescription);

		// send notification to followers
		Notifications.sendNotification(targetTopic.followers, targetTopic.id,
				"Topic", notificationDescription);

		// TODO: edit buttons in view

		return true;

	}

	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * 
	 * @description This method checks for the Validation of the info inserted
	 *              in the Add form of a Topic and if they are valid the object
	 *              is created and saved.
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void create(/*int entityid*/) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		String message = "";
		Topic tmp = (Topic) object;
		System.out.println("create() entered");
		// MainEntity topicEntity = to get connected aw kda
		MainEntity topicEntity = MainEntity.findById((long) 1);//temporary; for testing purposes
		tmp.entity = topicEntity;
		// User myUser = Security.getConnected();
		User myUser = User.findById((long) 1);//temporary; for testing purposes
		tmp.creator = myUser;

		/*
		 * if( !Users.isPermitted(tmp.creator , "post topics",
		 * topicEntity.getId(), "entity")) { message =
		 * "Sorry but you are not allowed to post topics in this entity"; } try
		 * { render( request.controller.replace( ".", "/" ) + "/blank.html",
		 * topicEntity , type, tmp.title, tmp.entity, tmp.description, message);
		 * } catch( TemplateNotFoundException e ) { render( "CRUD/blank.html",
		 * type ); }
		 */

		if (tmp.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, message);
			}
		}

		Organization topicOrganization = topicEntity.organization;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		if (!(topicEntity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			tmp.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations",
					topicEntity, topicOrganization).fetch();

		if (validation.hasErrors()) {
			if (tmp.title.equals("")) {
				message = "A Topic must have a title";
			} else if (tmp.description.equals("")) {
				message = "A Topic must have a description";

			}

			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						topicEntity, type, tmp.title, tmp.entity,
						tmp.description, tmp.followers, tmp.tags, message);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type);
			}
		}

		System.out.println("create() about to save object");
		object._save();
		System.out.println("create() object saved");
		tmp = (Topic) object;
		Calendar cal = new GregorianCalendar();
		// Logs.addLog( tmp.creator, "add", "Task", tmp.id,
		// tmp.entity.organization, cal.getTime() );
		String message2 = tmp.creator.username + " has Created the topic "
				+ tmp.title + " in " + tmp.entity;
		List users = Users.getEntityOrganizers(tmp.entity);
		users.add(tmp.entity.organization.creator);
		Notifications.sendNotification(users, tmp.id, "Topic", "A new Topic "
				+ tmp.title + " has been added in entity" + tmp.entity.name);

		// tmp.init();
		flash.success(Messages.get("crud.created", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			System.out
					.println("create() done will redirect to topics/view?topicid "
							+ message2);
			redirect("/topics/view?topicid=" + ((Topic) object).getId());

			// redirect("/topics/show?" + ((Topic) object).getId(), message2);
			// redirect( "/storys/liststoriesinproject?projectId=" +
			// tmp.taskStory.componentID.project.id + "&storyId=" +
			// tmp.taskStory.id );
		}
		if (params.get("_saveAndAddAnother") != null) {
			System.out
					.println("create() done will redirect to blank.html to add another "
							+ message2);
			redirect(request.controller + ".blank", message2);
		}
		System.out
				.println("create() done will redirect to show.html to show created"
						+ message2);
		redirect(request.controller + ".show", ((Topic) object).getId(),
				message2);
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @param entityid
	 *            : id of the entity the topic is in
	 * 
	 * @param userid
	 *            : id of the current user
	 * 
	 * @description This method renders the form for creating a topic in the
	 *              entity
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void blank(long entityid, long userid) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		MainEntity entity = MainEntity.findById(entityid);
		User user = User.findById(userid);
		System.out.println("blank() entered entity " + entityid + " and user "
				+ userid);
		// List<User> followers = entity.followers;
		// ArrayList<MainEntity> entitiesFollowed = (ArrayList<MainEntity>)
		// user.followingEntities;//display some of them on the side for quick
		// navigation

		// handle permissions, depending on the privacyLevel the user will be
		// directed to a different page

		try {
			System.out.println("blank() done about to render");
			render(type, entity, user /* , followers, entitiesFollowed */);

		} catch (TemplateNotFoundException e) {
			System.out
					.println("blank() done with exception about to render CRUD/blank.html");
			render("CRUD/blank.html", type);
		}

	}

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicid
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for editing and viewing a topic
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void show(String topicid) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicid);
		notFoundIfNull(object);
		System.out.println("entered show() for topic " + topicid);
		Topic tmp = (Topic) object;
		System.out.println(tmp.title);
		System.out.println(tmp.description);
		List<Tag> tags = tmp.tags;
		User creator = tmp.creator;
		List<User> followers = tmp.followers;
		List<Idea> ideas = tmp.ideas;
		List<Comment> comments = tmp.commentsOn;
		MainEntity entity = tmp.entity;
		Plan plan = tmp.plan;
		boolean openToEdit = tmp.openToEdit;
		short privacyLevel = tmp.privacyLevel;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = tmp.isDeletable();

		try {
			System.out.println("show() done, about to render");
			render(type, object, tags, creator, followers, ideas, comments,
					entity, plan, openToEdit, privacyLevel, deletemessage,
					deletable, topicid);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("show() done with exception, rendering to CRUD/show.html");
			render("CRUD/show.html", type, object);
		}
	}
	
	
	/**
	 * Topic view method
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicid
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for viewing a topic
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void view(String topicid) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicid);
		notFoundIfNull(object);
		System.out.println("entered view() for topic " + topicid);
		Topic tmp = (Topic) object;
		System.out.println(tmp.title);
		System.out.println(tmp.description);
		List<Tag> tags = tmp.tags;
		User creator = tmp.creator;
		List<User> followers = tmp.followers;
		List<Idea> ideas = tmp.ideas;
		List<Comment> comments = tmp.commentsOn;
		MainEntity entity = tmp.entity;
		Plan plan = tmp.plan;
		boolean openToEdit = tmp.openToEdit;
		short privacyLevel = tmp.privacyLevel;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = tmp.isDeletable();

		try {
			System.out.println("view() done, about to render");
			render(type, object, tags, creator, followers, ideas, comments,
					entity, plan, openToEdit, privacyLevel, deletemessage,
					deletable, topicid);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/topics/view.html", type, object, tags, creator, followers,
					ideas, comments, entity, plan, openToEdit, privacyLevel,
					deletemessage, deletable, topicid);
		}
	}

	/*public static void edit(String topicid) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicid);
		notFoundIfNull(object);
		System.out.println("entered edit() for topic " + topicid);
		Topic tmp = (Topic) object;
		flash.success(Messages.get("crud.edit", type.modelName,
				((Topic) object).getId()));
		System.out.println("About to redirect from edit()");
		redirect("/topics/show?topicid=" + ((Topic) object).getId(), tmp);
	}*/

	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author aliaelbolock
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
	 * @throws Exception
	 * 
	 * @return void
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
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("list() done with exceptions, will render CRUD/list.html ");
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * closedTopicsList
	 * 
	 * @author aliaelbolock
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
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void closedTopicslist(int page, String search,
			String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Topic> opentopics = Topic.find("openToEdit", false).fetch();
		Long totalCount = (long) opentopics.size();
		Long count = (long) opentopics.size();
		try {
			render(type, opentopics, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, opentopics, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicid
	 *            : id of the topic we're in
	 * 
	 * @description This method renders the form for editing a topic and saving
	 *              it
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void save(String topicid) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		System.out.println(topicid);
		Model object = Topic.findById(Long.parseLong(topicid));
		notFoundIfNull(object);
		System.out.println("entered save() for " + topicid);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic tmp = (Topic) object;
		// MainEntity topicEntity = to get connected aw kda
		MainEntity topicEntity = MainEntity.findById((long) 1); //temporary; for testing purposes
		tmp.entity = topicEntity;
		// User myUser = Security.getConnected();
		User myUser = User.findById((long) 1);// temporary; for testing purposes
		tmp.creator = myUser;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		Organization topicOrganization = topicEntity.organization;
		if (!(topicEntity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			tmp.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations",
					topicEntity, topicOrganization).fetch();
		String message = "";

		if (validation.hasErrors()) {
			if (tmp.description.equals("")) {
				message = "A Topic must have a description";

			}

			else if (tmp.privacyLevel < 0 || tmp.privacyLevel > 10) {
				message = "The privary level must be within 0 and 10";

			}

			/*
			 * else if( !Users.isPermitted(myUser, "edit topics",
			 * topicEntity.getId(), "entity")) { message =
			 * "Sorry but you are not allowed to edit topics in this entity"; }
			 */
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						topicEntity, type, tmp.title, tmp.entity,
						tmp.description, tmp.followers, tmp.tags, message,
						object, topicid);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type);
			}
		}

		System.out.println("about to save() topic");
		object._save();
		Calendar cal = new GregorianCalendar();
		// Logs.addLog( myUser, "add", "Task", tmp.id, tmp.entity.organization,
		// cal.getTime() );
		// String message3 = myUser.username + " has editted the topic " +
		List users = Users.getEntityOrganizers(tmp.entity);
		users.add(tmp.entity.organization.creator);
		Notifications.sendNotification(users, tmp.id, "Topic", "User "
				+ myUser.firstName + " has edited topic  " + tmp.title);
		System.out.println("save() done, not redirected yet");

		flash.success(Messages.get("crud.saved", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			redirect("/topics/view?topicid=" + ((Topic) object).getId());
			System.out
					.println("save() done, redirected to topics/view?topicid");
			// redirect( "/storys/liststoriesinproject?projectId=" +
			// tmp.taskStory.componentID.project.id + "&storyId=" +
			// tmp.taskStory.id );
		}
		redirect(request.controller + ".show", ((Topic) object).getId());
		System.out.println("save() done, redirected to default show.html");
	}

}
