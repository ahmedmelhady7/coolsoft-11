package controllers;

/**
 @author Mostafa Ali
 */
// list of organizers in entity 

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import controllers.CoolCRUD.ObjectType;
import notifiers.Mail;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.GenericModel.JPAQuery;
import play.db.jpa.JPA;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import models.*;
import java.util.Arrays;

@With(Secure.class)
public class Users extends CoolCRUD {

	/**
	 * 
	 * overrides the CRUD list method ,renders the list of users
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param page
	 *            : int page of the list we are in
	 * 
	 * @param search
	 *            : String search string
	 * 
	 * @param searchFields
	 *            : String the fields we want to search
	 * 
	 * @param orderBy
	 *            : String criteria to order list by
	 * 
	 * @param order
	 *            : String the order of the list (asc /desc)
	 * 
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
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("list() done with exceptions, will render CRUD/list.html ");
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * overrides the CRUD view method and renders the form for viewing a user
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param userId
	 *            :String id of the user we want to show
	 * 
	 * 
	 * @throws Exception
	 * 
	 */
	public static void view(String userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		long userID = Long.parseLong(userId);
		User object = User.findById(userID);
		notFoundIfNull(object);
		System.out.println("entered view() for User " + object.username);
		System.out.println(object.email);
		System.out.println(object.username);
		int communityContributionCounter = object.communityContributionCounter;
		String name = object.firstName + " " + object.lastName;
		String profession = object.profession;
		String username = object.username;
		String birthDate = "" + object.dateofBirth;

		int adminFlag = 0;
		if (Security.getConnected().isAdmin) {
			adminFlag = 1;
		}
		try {

			System.out.println("view() done, about to render");
			render(type, object, username, name, communityContributionCounter,
					profession, birthDate, userId, adminFlag);

		} catch (TemplateNotFoundException e) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/users/view.html");
		}
	}

	/**
	 * overrides the CRUD show method,renders the form for editing and viewing a
	 * user
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param String
	 *            userId : id of the user we want to show
	 * 
	 * 
	 * @throws Exception
	 * 
	 */
	// public static void show(String userId) {
	// ObjectType type = ObjectType.get(getControllerClass());
	// notFoundIfNull(type);
	// long userID = Long.parseLong(userId);
	// User object = User.findById(userID);
	// notFoundIfNull(object);
	// System.out.println("entered view() for User " + object.username);
	// System.out.println(object.email);
	// System.out.println(object.username);
	// int communityContributionCounter = object.communityContributionCounter;
	// String name = object.firstName + " " + object.lastName;
	// String profession = object.profession;
	// String username = object.username;
	// String birthDate = "" + object.dateofBirth;
	// try {
	// System.out.println("show() done, about to render");
	// render(type, object, username, name, communityContributionCounter,
	// profession, birthDate, userId);
	// } catch (TemplateNotFoundException e) {
	// System.out
	// .println("show() done with exception, rendering to CRUD/show.html");
	// render("CRUD/show.html", type, object);
	// }
	// }

	public static void show(String userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(userId);
		notFoundIfNull(object);
		System.out.println("entered show() for user " + userId);
		User tmp = (User) object;
		int communityContributionCounter = tmp.communityContributionCounter;
		String name = tmp.firstName + " " + tmp.lastName;
		String profession = tmp.profession;
		String username = tmp.username;
		String dateofBirth = "" + tmp.dateofBirth;
		try {
			render(type, object, username, name, communityContributionCounter,
					profession, dateofBirth, userId);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
	}

	/**
	 * renders the form for editing and viewing a user
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S1-2
	 * 
	 * @param String
	 *            userId : id of the user we want to show
	 * 
	 */
	public static void viewMyProfile(String userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		long userID = Long.parseLong(userId);
		User object = User.findById(userID);
		notFoundIfNull(object);
		System.out.println("entered view() for User " + object.username);
		System.out.println(object.email);
		System.out.println(object.username);
		int communityContributionCounter = object.communityContributionCounter;
		String name = object.firstName + " " + object.lastName;
		String profession = object.profession;
		String username = object.username;
		String birthDate = "" + object.dateofBirth;
		String password = object.password;
		String email = object.email;

		int adminFlag = 0;
		if (Security.getConnected().isAdmin) {
			adminFlag = 1;
		}
		try {

			System.out.println("view() done, about to render");
			render(type, object, username, name, communityContributionCounter,
					profession, birthDate, userId, adminFlag, email, password);

		} catch (TemplateNotFoundException e) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/users/view.html");
		}
	}

	/**
	 * passes the user's Id to viewMyProfile method
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S2-1
	 * 
	 * @param String
	 * 
	 */
	public static void editProfile() {
		User user = Security.getConnected();
		viewMyProfile(user.id + "");
	}

	/**
	 * Renders the profile page of the user.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S1
	 * 
	 * @param userId
	 *            long the Id of the user who's profile will be displayed
	 * 
	 */

	public static void viewProfile(long userId) {
		User userConnected = Security.getConnected();
		User user = User.findById(userId);
		boolean flag = true;
		if (user.id == userConnected.id) {
			render(user, flag);
		} else {
			flag = false;
			render(user, flag);
		}
	}

	/**
	 * Overrides the CRUD method blank, renders the form for creating a user
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * 
	 * @throws Exception
	 * 
	 */
	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(Users.class);
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		User user = Security.getConnected();
		int adminFlag = 0;
		int unregisteredUser = 0;
		try {
			if (user.isAdmin) {
				adminFlag = 1;
			}
		} catch (NullPointerException e) {
			unregisteredUser = 1;
		}
		try {
			render(type, object, adminFlag, unregisteredUser);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type, object);
		}
	}

	/**
	 * This Method adds a user to the list of followers in a given tag
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S11
	 * 
	 * @param tagId
	 *            : the id of the tag that the user is following
	 * 
	 * @param userId
	 *            : the id of the user who follows
	 * 
	 */
	public static void followTag(long tagId, long userId) {
		Tag tag = Tag.findById(tagId);
		User user = User.findById(userId);
		tag.follow(user);
		user.follow(tag);
	}

	/**
	 * This Method removes a user from the list of followers in a given Topic
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param topicId
	 *            the id of the topic that the user is following
	 */

	public static void unfollowTopic(long topicId) {
		Topic topic = Topic.findById(topicId);
		User user = Security.getConnected();
		topic.unfollow(user);
		user.unfollow(topic);
	}

	/**
	 * This Method removes a user from the list of followers in a given Tag
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param tagId
	 *            the id of the tag that the user is following
	 */
	public static void unfollowTag(long tagId) {
		Tag tag = Tag.findById(tagId);
		User user = Security.getConnected();
		tag.unfollow(user);
		user.unfollow(tag);
	}

	/**
	 * This Method removes a user from the list of followers in an organization
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param organizationId
	 *            the id of the organization the user is following
	 * 
	 * @param userId
	 *            the id of the user who follows
	 */

	public static void unfollowOrganization(long organizationId) {
		Organization organization = Organization.findById(organizationId);
		User user = Security.getConnected();
		organization.unfollow(user);
		user.unfollow(organization);
	}

	/**
	 * This Method removes a user from the list of followers in an entity
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param entityId
	 *            the id of the entity the user is following
	 */

	public static void unfollowEntity(long entityId) {
		MainEntity entity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		entity.unfollow(user);
		user.unfollow(entity);
	}

	/**
	 * this Method is responsible for reporting an idea as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param ideaId
	 *            : the ID of the idea to be reported
	 * 
	 * 
	 */
	public static void reportIdeaAsSpam(long ideaId) {
		System.out.println("hadiiiiiiiiiiiiiiii");
		Idea idea = Idea.findById(ideaId);
		User reporter = Security.getConnected();
		idea.spamCounter++;
		reporter.ideasReported.add(idea);
		idea.reporters.add(reporter);
		List<User> organizers = Users
				.getEntityOrganizers(idea.belongsToTopic.entity);
		System.out.println(organizers);
		System.out.println(idea.reporters.get(0).toString());
		for (int j = 0; j < organizers.size(); j++) {
			Mail.reportAsSpamMail(organizers.get(j), reporter, idea,
					idea.description, idea.title);
		}
		idea.save();
		reporter.save();
		System.out.println(reporter.ideasReported.get(0).toString());
		ideaSpamView(ideaId);

	}

	/**
	 * this Method is responsible for the view of reporting the idea as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param ideaId
	 *            : the ID of the idea to be reported
	 * 
	 */

	public static void ideaSpamView(long ideaId) {
		boolean alreadyReported = false;
		Idea idea = Idea.findById(ideaId);
		User reporter = Security.getConnected();
		for (int i = 0; i < idea.reporters.size(); i++) {
			if (reporter.username.equals(idea.reporters.get(i).username))
				alreadyReported = true;
		}
		redirect("/ideas/show?ideaId=" + idea.getId(), alreadyReported);
		// render(alreadyReported);
	}

	/**
	 * this Method is responsible for reporting a Topic as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param topicId
	 *            : the ID of the topic to be reported
	 * 
	 * 
	 */
	public static void reportTopicAsSpam(long topicId) {
		System.out.println("hadiiiiiiiiiiiiiiii");
		Topic topic = Topic.findById(topicId);
		User reporter = Security.getConnected();
		if (topic.reporters == null)
			topic.reporters = "";
		topic.reporters += reporter.id + ",";
		System.out.println(topic.reporters + " howa da ");
		List<User> organizers = Users.getEntityOrganizers(topic.entity);
		for (int j = 0; j < organizers.size(); j++) {
			Mail.reportTopicMail(topic.creator, reporter, topic,
					topic.description, topic.title);
		}
		topic.save();
		reporter.save();
		// System.out.println(reporter.ideasReported.get(0).toString());
		TopicSpamView(topicId);

	}

	/**
	 * this Method is responsible for the view of reporting the idea as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param topicId
	 *            : the ID of the topic to be reported
	 * 
	 */

	public static void TopicSpamView(long topicId) {
		boolean alreadyReported = false;
		Topic topic = Topic.findById(topicId);
		User reporter = Security.getConnected();
		// for (int i = 0; i < topic.reporters.size(); i++) {
		// if (reporter.username.equals(topic.reporters.get(i).username))
		// alreadyReported = true;
		// }
		redirect("/topics/show?topicId=" + topic.getId(), alreadyReported);
		// render(alreadyReported);
	}

	/**
	 * returns the list of users that are banned from a certain action in a
	 * certain organization from a certain source in a certain type such that
	 * the returned users are all active
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param organization
	 *            Organization organization where this user is banned from
	 * @param action
	 *            String action those users are banned form
	 * @param sourceID
	 *            long id of the source that user is banned from
	 * @param type
	 *            String type of the source
	 * @return List<User> is the list of he banned users
	 */

	public static List<User> getBannedUser(Organization organization,
			String action, long sourceID, String type) {
		List<User> users = BannedUser
				.find("select bu.bannedUser from BannedUser where bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, action, type, sourceID).fetch();

		for (int i = 0; i < users.size(); i++) {

			if (!users.get(i).state.equals("a")) {
				users.remove(i);
				i--;
			}
		}

		return (users);
	}

	/**
	 * 
	 * responsible for searching for users using specific criteria
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param keyword
	 *            :a String keyword the user enters for searching
	 * 
	 * @return List<User>
	 */
	public static ArrayList<User> searchUser(String keyword) {

		List<User> searchResultByName = new ArrayList<User>();
		List<User> searchResultByProfession = new ArrayList<User>();
		List<User> searchResultByEmail = new ArrayList<User>();

		List<User> searchResultByNameActive = new ArrayList<User>();
		List<User> searchResultByProfessionActive = new ArrayList<User>();
		List<User> searchResultByEmailActive = new ArrayList<User>();

		if (keyword != null) {

			searchResultByName = User.find("byUsernameLike",
					"%" + keyword + "%").<User> fetch();
			searchResultByProfession = User.find("byProfessionLike",
					"%" + keyword + "%").<User> fetch();
			searchResultByEmail = User.find("byEmailLike", "%" + keyword + "%")
					.<User> fetch();
		}

		int nameSize = searchResultByName.size();
		int professionSize = searchResultByProfession.size();
		int emailSize = searchResultByEmail.size();
		for (int i = 0; i < nameSize; i++) {
			if (searchResultByName.get(i).state == "a") {
				searchResultByNameActive.add(searchResultByName.get(i));
			}
		}
		for (int i = 0; i < professionSize; i++) {
			if (searchResultByProfession.get(i).state == "a") {
				searchResultByProfessionActive.add(searchResultByProfession
						.get(i));
			}
		}

		for (int i = 0; i < emailSize; i++) {
			if (searchResultByEmail.get(i).state == "a") {
				searchResultByEmailActive.add(searchResultByEmail.get(i));
			}
		}
		ArrayList<User> search = new ArrayList<User>();
		search.addAll(searchResultByEmailActive);
		search.addAll(searchResultByNameActive);
		search.addAll(searchResultByProfessionActive);

		// searchResultByName.addAll(searchResultByProfession);
		// searchResultByName.addAll(searchResultByEmail);
		// render(searchResultByName, searchResultByProfession,
		// searchResultByEmail);
		return search;

	}

	/**
	 * 
	 * responsible for searching for organizers in a certain organization
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param o
	 *            : the organization
	 * 
	 * @return List<User>
	 */
	public static List<User> searchOrganizer(Organization o) {
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		List<User> user = new ArrayList<User>();
		List<User> userActive = new ArrayList<User>();
		if (o != null) {
			// organizers = (List<UserRoleInOrganization>)
			// UserRoleInOrganization
			// .find("select uro.enrolled from UserRoleInOrganization uro,Role r where  uro.Role = r and uro.organization = ? and r.roleName like ? ",
			// o, "organizer");
			organizers = UserRoleInOrganization.find("byOrganization", o)
					.fetch();
			for (int i = 0; i < organizers.size(); i++) {
				if ((organizers.get(i).role.roleName).equals("organizer")) {
					user.add(organizers.get(i).enrolled);
				}
			}

		}
		// List<User> finalOrganizers = new ArrayList<User>();
		// for (int i = 0; i < organizers.size(); i++) {
		// finalOrganizers.add((organizers.get(i)).enrolled);
		// }
		int size = user.size();
		for (int i = 0; i < size; i++) {
			if (user.get(i).state == "a") {
				userActive.add(user.get(i));
			}
		}
		return userActive;
	}

	/**
	 * 
	 * responsible for telling whether a user is allowed to do a specific action
	 * in an organization/entity/topic
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S15
	 * 
	 * @param user
	 *            : a User user who is going to perform the action
	 * 
	 * @param action
	 *            :a String action performed
	 * 
	 * @param placeId
	 *            : a long id of the organization/ entity/ topic
	 * 
	 * @param placeType
	 *            : a String type whether an organization/ entity/ topic
	 * 
	 * @return boolean
	 */
	public static boolean isPermitted(User user, String action, long placeId,
			String placeType) {
		// User banned =
		// BannedUser.find("select b.bannedUser from BannedUser b where b.bannedUser = ? and b.resourceID = ? and b.resourceType = ? and b.action =  ",
		// user);

		BannedUser bannedView = BannedUser.find(
				"byBannedUserAndActionAndResourceTypeAndResourceID", user,
				"view", placeType, placeId).first();

		if (bannedView != null) {
			return false;
		}

		BannedUser banned = BannedUser.find(
				"byBannedUserAndActionAndResourceTypeAndResourceID", user,
				action, placeType, placeId).first();

		String role;
		if (user.isAdmin) {
			return true;
		}

		if (banned != null) {
			return false;
		}
		if (UserRoleInOrganizations.isOrganizer(user, placeId, placeType)) {
			System.out.println("aloooooooooo2");
			List<String> r = Roles.getRoleActions("organizer");
			if (r.contains(action)) {
				System.out.println("aloooooooooo3");
				return true;
			} else {
				if (Roles.getRoleActions("idea developer").contains(action)) {
					System.out.println("aloooooooooo4");
					return true;
				} else {
					System.out.println("aloooooooooo5");
					return false;
				}
			}
		}

		if (placeType.equalsIgnoreCase("organization")) {
			Organization org = Organization.findById(placeId);
			// List<UserRoleInOrganization> l =
			// UserRoleInOrganization.find("byOrganizationAnd")
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organizationLead").contains(action)) {
					return true;
				} else {
					if (Roles.getRoleActions("organizer").contains(action)) {
						return true;
					} else {
						if (Roles.getRoleActions("idea developer").contains(
								action)) {
							return true;
						} else {
							return false;
						}
					}
				}

			}

			if (org.privacyLevel == 0 || org.privacyLevel == 1) {

				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndOrganization", user, org).fetch();
				if (allowed.size() == 0) {
					return false;
				} else {
					if (Roles.getRoleActions("idea developer").contains(action)) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (action.equals("view")) {
					return true;
				}
				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndOrganization", user, org).fetch();
				if (allowed.size() == 0) {
					return false;
				} else
					return true;
			}

		}
		if (placeType.equalsIgnoreCase("topic")) {
			System.out.println("aloooooooooo1");
			Topic topic = Topic.findById(placeId);
			MainEntity m = topic.entity;
			Organization org = m.organization;
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organizationLead").contains(action)) {
					return true;
				} else {
					if (Roles.getRoleActions("organizer").contains(action)) {
						return true;
					} else {
						if (Roles.getRoleActions("idea developer").contains(
								action)) {
							return true;
						} else {
							return false;
						}
					}
				}

			}
			if (topic.privacyLevel == 2) {
				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndEntityTopicIDAndType", user,
								topic.id, "topic").fetch();
				if (allowed.size() == 0) {
					return false;
				} else {
					if (Roles.getRoleActions("idea developer").contains(action)) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (org.privacyLevel == 0 || org.privacyLevel == 1) {

					List<UserRoleInOrganization> allowed = UserRoleInOrganization
							.find("byEnrolledAndOrganization", user, org)
							.fetch();
					if (allowed.size() == 0) {
						return false;
					} else {
						if (Roles.getRoleActions("idea developer").contains(
								action)) {
							return true;
						} else {
							return false;
						}
					}
				}
				if (org.privacyLevel == 2) {
					if (action.equals("view")) {
						return true;
					}
					List<UserRoleInOrganization> allowed = UserRoleInOrganization
							.find("byEnrolledAndOrganization", user, topic)
							.fetch();
					if (allowed.size() == 0) {
						return false;
					} else
						return true;
				}
			}
		}

		if (placeType.equalsIgnoreCase("entity")) {
			MainEntity entity = MainEntity.findById(placeId);
			Organization org = entity.organization;
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organizationLead").contains(action)) {
					return true;
				} else {
					if (Roles.getRoleActions("organizer").contains(action)) {
						return true;
					} else {
						if (Roles.getRoleActions("idea developer").contains(
								action)) {
							return true;
						} else {
							return false;
						}
					}
				}

			}
			if (org.privacyLevel == 0 || org.privacyLevel == 1) {

				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndOrganization", user, org).fetch();
				if (allowed.size() == 0) {
					return false;
				} else {
					if (Roles.getRoleActions("idea developer").contains(action)) {
						return true;
					} else {
						return false;
					}
				}
			}
			if (org.privacyLevel == 2) {
				if (Roles.getRoleActions("idea developer").contains(action)) {
					return true;
				} else {
					return false;
				}
			}
		}

		System.out.println("you entered an invalid type");
		return false;

	}

	public static void r(long i) {
		Topic t = Topic.findById(i);
		t.title = "done";
		t._save();
	}

	/**
	 * gets the list of organizers of a certain topic such that all the users in
	 * the returned list are active
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param topic
	 *            : Topic topic that i want to retrieve all the organizers that
	 *            are enrolled in it
	 * 
	 * @return List<User> of Organizers in that topic
	 */

	public List<User> getTopicOrganizers(Topic topic) {
		List<User> enrolled = new ArrayList<User>();
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		if (topic != null) {
			Organization organization = topic.entity.organization;
			organizers = UserRoleInOrganization

					.find("select uro from UserRoleInOrganization uro  where uro.organization = ? and uro.entityTopicID = ? and uro.type like ?",
							organization, topic.getId(), "topic").fetch();
			for (int i = 0; i < organizers.size(); i++) {

				if ((organizers.get(i).role.roleName.equals("organizer"))
						&& organizers.get(i).enrolled.state.equals("a")) {
					enrolled.add(organizers.get(i).enrolled);
				}

			}

		}
		return enrolled;
	}

	/**
	 * gets the list of organizers of a certain entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param entity
	 *            : MainEntity entity that i want to retrieve all the organizers
	 *            that are enrolled in it
	 * 
	 * @return List <User> Organizers in that entity
	 */
	public static List<User> getEntityOrganizers(MainEntity entity) {
		List<User> enrolled = new ArrayList<User>();
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		if (entity != null) {
			Organization organization = entity.organization;

			long entityId = entity.getId();
			organizers = UserRoleInOrganization
					.find("select uro from UserRoleInOrganization uro where uro.organization = ? and uro.entityTopicID = ? and uro.type like ?",
							organization, entityId, "entity").fetch();
			for (int i = 0; i < organizers.size(); i++) {
				if (((organizers.get(i).role.roleName).equals("organizer"))
						&& (organizers.get(i).enrolled.state.equals("a"))) {

					enrolled.add(organizers.get(i).enrolled);
				}
			}

		}
		return enrolled;
	}

	/**
	 * gets the list of users who are allowed to delete an idea or a comment
	 * 
	 * @author: Lama Ashraf
	 * 
	 * @story C1S15
	 * 
	 * @param user
	 *            : User user is the idea developer performing this action
	 * 
	 * @param action
	 *            :a String action performed
	 * 
	 * @param placeId
	 *            : a long id of the idea/comment
	 * 
	 * @param placeType
	 *            : a String type whether an idea/comment
	 * @param topicId
	 *            : a long id of the topic
	 * 
	 * @return boolean
	 **/
	public static boolean canDelete(User user, String action, long placeId,
			String placeType, long topicId) {

		BannedUser bannedView = BannedUser.find(
				"byBannedUserAndActionAndResourceTypeAndResourceID", user,
				"view", placeType, placeId).first();

		if (bannedView != null) {
			return false;
		}

		BannedUser banned = BannedUser.find(
				"byBannedUserAndActionAndResourceTypeAndResourceID", user,
				action, placeType, placeId).first();

		if (banned != null) {
			return false;
		}
		if (placeType == "idea") {
			Idea idea = Idea.findById(placeId);
			if (user.equals(idea.author)) {
				return true;
			}
			return false;
		}

		if (placeType == "comment") {
			Comment comment = Comment.findById(placeId);
			if (user.equals(comment.commenter)) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * gets all the users enrolled in an organization these users are: 1- the
	 * Organization lead 2- the organizers (even if blocked) 3- Idea Developers
	 * in secret or private topics and the state of all the returned users is
	 * active
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param: organization Organization organization that the users are
	 *         enrolled in
	 * 
	 * @return :List<User> enrolled users
	 */

	public static List<User> getEnrolledUsers(Organization organization) {
		List<User> enrolled = new ArrayList<User>();
		List<User> finalEnrolled = new ArrayList<User>();
		if (organization != null) {

			enrolled = UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = ? ",
							organization).fetch();

			for (int i = 0; i < enrolled.size(); i++) {
				if (enrolled.get(i).state.equals("a")) {
					finalEnrolled.add(enrolled.get(i));
				}
			}

		}
		return finalEnrolled;
	}

	/**
	 * Returns a list of all idea developers within a certain organization in a
	 * certain entity or in a specific topic according to the "type" passed such
	 * that the state of all the returned users is active
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param entityTopicId
	 *            : long entityTopicId is the id of the entity or topic
	 * 
	 * @param type
	 *            : String type is the parameter that specifies whether to fetch
	 *            all the idea developers in the required entity or in a certain
	 *            topic
	 * 
	 * @return List<User> : is the list of idea developers
	 */

	public static List<User> getIdeaDevelopers(long entityTopicId, String type) {
		List<User> enrolled = new ArrayList<User>();
		List<User> finalEnrolled = new ArrayList<User>();
		Role role = Roles.getRoleByName("idea developer");
		MainEntity entity = MainEntity.findById(entityTopicId);
		Organization organization = entity.organization;
		if (type.equalsIgnoreCase("topic")) {
			Topic topic = Topic.findById(entityTopicId);
			entity = topic.entity;
			organization = entity.organization;
			enrolled = UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = ? and uro.role = ? and uro.entityTopicID = ? and type = ?",
							organization, role, entityTopicId, type).fetch();
		} else {
			enrolled = UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = ? and uro.role = ? ",
							organization, role).fetch();
		}

		for (int i = 0; i < enrolled.size(); i++) {
			if (enrolled.get(i).state.equals("a")) {

				finalEnrolled.add(enrolled.get(i));
			}
		}

		return finalEnrolled;

	}

	/**
	 * return all the entities that a certain organizer is enrolled in within a
	 * certain organization
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param org
	 *            Organization org that contains the entities
	 * @param user
	 *            User the intended user
	 * @return list <MainEntities> of entities
	 */
	public static List<MainEntity> getEntitiesOfOrganizer(Organization org,
			User user) {
		List<MainEntity> entities = new ArrayList<MainEntity>();

		List<UserRoleInOrganization> userRoleInOrg = UserRoleInOrganization
				.find("byOrganizationAndEnrolled", org, user).fetch();

		for (int i = 0; i < userRoleInOrg.size(); i++) {
			if (userRoleInOrg.get(i).role.roleName.equals("organizer")) {
				entities.add((MainEntity) MainEntity.findById(userRoleInOrg
						.get(i).entityTopicID));
			}
		}
		return entities;
	}

	/**
	 * overrides the CRUD create method that is used to create a new user and
	 * make sure that this user is valid, and then it renders a message
	 * mentioning whether the operation was successful or not.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * 
	 */

	// public static void create() throws Exception {
	// // if(Security.getConnected().isAdmin);
	// ObjectType type = ObjectType.get(Users.class);
	// notFoundIfNull(type);
	// Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	// constructor.setAccessible(true);
	// Model object = type.entityClass.newInstance();
	// User user = (User) object;
	// Binder.bind(object, "object", params.all());
	// validation.valid(object);
	// System.out.println(object.toString());
	// String errorMessage = "";
	// try {
	// if (user.username.length() >= 20) {
	// errorMessage = "Username cannot exceed 20 characters";
	// }
	// if (user.find("ByUsername", user.username) != null) {
	// errorMessage += "This username already exists !";
	// }
	// } catch (NullPointerException e) {
	// errorMessage = "Username field is required, u must have a username !";
	// }
	//
	// try {
	// if (user.password.length() >= 20) {
	// errorMessage += "Password cannot exceed 20 characters";
	// }
	//
	// } catch (NullPointerException e) {
	// errorMessage += "Password field is required, u must have a username !";
	// }
	//
	// try {
	// if (user.password.length() >= 20) {
	// errorMessage += "First name cannot exceed 20 characters";
	// }
	//
	// } catch (NullPointerException e) {
	// errorMessage += "First name field is required, u must have a username !";
	// }
	//
	// try {
	//
	// if (user.find("ByEmail", user.email) != null) {
	// errorMessage += "This mail already exists !";
	// }
	//
	// } catch (NullPointerException e) {
	// errorMessage += "Email field is required, u must have a username !";
	// }
	// /*if (validation.hasErrors()) {
	// renderArgs.put("error", Messages.get("crud.hasErrors"));
	// try {
	// System.out.println(object.toString() + "!1try");
	// render(request.controller.replace(".", "/") + "/blank.html",
	// type, object);
	// } catch (TemplateNotFoundException e) {
	// System.out.println(object.toString() + "catch");
	// render("CRUD/blank.html", type, object);
	// }
	// }*/
	// System.out.println("");
	//
	// try {
	// render(request.controller.replace(".", "/") + "/blank.html",
	// user.email, user.username, user.password, user.firstName,
	// user.lastName, user.communityContributionCounter,
	// user.dateofBirth, user.country, user.profession,
	// errorMessage);
	// } catch (TemplateNotFoundException e) {
	// render("CRUD/blank.html", type);
	// }
	// System.out.println(object.toString() + "before the save");
	// object._save();
	// flash.success(Messages.get("crud.created", type.modelName));
	// if (params.get("_save") != null) {
	// System.out.println(object.toString() + "save condition");
	// redirect(request.controller + ".list");
	// }
	// if (params.get("_saveAndAddAnother") != null) {
	// redirect(request.controller + ".blank");
	// }
	// redirect(request.controller + ".show", object._key());
	// }
	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		String message = "";
		User tmp = (User) object;
		System.out.println("create() entered");
		tmp.email = tmp.email.trim().toLowerCase();
		tmp.username = tmp.username.trim();
		tmp.password = tmp.password.trim();
		tmp.firstName = tmp.firstName.trim();
		boolean flag = false;
		String communityCounterString = tmp.communityContributionCounter + "";
		boolean communityCounterFlag = false;
		try {
			Integer.parseInt(communityCounterString);
		} catch (NumberFormatException e) {
			communityCounterFlag = true;
		}
		/*
		 * if (User.find("ByEmail", tmp.email) != null ||
		 * User.find("ByUsername", tmp.username) != null) { flag = true; }
		 * System.out.println(flag );
		 */
		if (validation.hasErrors()) {
			System.out.println("lol");
			if (tmp.email.equals("")) {
				message = "A User must have an email";
				System.out.println(message);
			} else if (tmp.username.equals("")) {
				message = "A User must have a username";
				System.out.println(message);
			} else if (tmp.password.equals("")) {
				message = "A User must have a password";
				System.out.println(message);
			} else if (tmp.firstName.trim().equals("")) {
				message = "A User must have a first name";
				System.out.println(message);
			} else if (tmp.username.length() >= 20) {
				message = "Username cannot exceed 20 characters";
			} else if (tmp.password.length() >= 25) {
				message = "First name cannot exceed 25 characters";
			} else if (communityCounterFlag) {
				message = "Community contributuion counter must be a number";
			} /*
			 * else if (User.find("ByEmail", tmp.email) != null) { message =
			 * "This Email already exists !"; } else if (User.find("ByUsername",
			 * tmp.username) != null) { message =
			 * "This username already exists !"; }
			 */

			try {
				System.out.println("show user try ");
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message);
			} catch (TemplateNotFoundException e) {
				System.out.println("show user catch ");
				render("CRUD/blank.html", type);
			}
		}

		System.out.println("create() about to save object");
		tmp.state = "w";
		object._save();
		System.out.println("create() object saved");
		tmp = (User) object;
		Mail.welcome(tmp);
		// Calendar cal = new GregorianCalendar();
		// Logs.addLog( user.getConnected, "add", "User", tmp.username.
		// cal.getTime() );
		String message2 = tmp.username + " has been added to users ";
		System.out.println("id " + tmp.getId());

		flash.success(Messages.get("crud.created", type.modelName,
				((User) object).getId()));
		if (params.get("_save") != null) {
			System.out
					.println("create() done will redirect to users/show?topicid "
							+ message2);
			redirect("/users/show?userId=" + tmp.getId());

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
		redirect(request.controller + ".view", ((User) object).getId(),
				message2);
	}

	/**
	 * overrides the CRUD save method ,used to submit the edit, to make sure
	 * that the edits are acceptable, and then it renders a message mentioning
	 * whether the operation was successful or not.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            :String the user's id
	 * 
	 */

	public static void save(String id) throws Exception {
		// Security.check(Security.getConnected().isAdmin||);
		ObjectType type = ObjectType.get(Users.class);
		notFoundIfNull(type);
		Model object = type.findById(id);
		User oldUser = (User) object;
		String oldEmail = oldUser.email;
		char[] oldemailArray = oldEmail.toCharArray();
		String oldFirstName = oldUser.firstName;
		char[] oldFirstNameArray = oldFirstName.toCharArray();
		String oldLastName = oldUser.lastName;
		char[] oldLastNameArray = oldLastName.toCharArray();
		int oldCommunityContributionCounter = oldUser.communityContributionCounter;
		String oldCountry = oldUser.country;
		char[] oldCountryArray = oldCountry.toCharArray();
		String oldProfession = oldUser.profession;
		char[] oldProfessionArray = oldProfession.toCharArray();
		Binder.bind(object, "object", params.all());
		System.out.println(object.toString() + "begin");
		validation.valid(object);
		String editedMessage = "User : " + oldUser.username + "\n";
		String message = "";
		User tmp = (User) object;
		System.out.println("create() entered");
		tmp.email = tmp.email.trim();
		tmp.email = tmp.email.toLowerCase();
		tmp.username = tmp.username.trim();
		tmp.password = tmp.password.trim();
		tmp.firstName = tmp.firstName.trim();
		tmp.lastName = tmp.lastName.trim();
		tmp.profession = tmp.profession.trim();
		String communityCounterString = tmp.communityContributionCounter + "";
		boolean communityCounterFlag = false;
		try {
			Integer.parseInt(communityCounterString);
		} catch (NumberFormatException e) {
			communityCounterFlag = true;
		}

		if (validation.hasErrors() || communityCounterFlag) {
			System.out.println("lol");
			if (tmp.email.equals("")) {
				message = "A User must have an email";
				System.out.println(message);
			} /*
			 * else if (tmp.username.trim().equals("")) { message =
			 * "A User must have a username"; System.out.println(message); }
			 * else if (tmp.password.trim().equals("")) { message =
			 * "A User must have a password"; System.out.println(message); }
			 */else if (tmp.username.length() >= 20) {
				message = "Username cannot exceed 20 characters";
			} else if (tmp.password.length() >= 25) {
				message = "First name cannot exceed 25 characters";
			} else if (communityCounterFlag) {
				message = "Community contributuion counter must be a number";
			}
			try {
				System.out.println("show user try ");
				render(request.controller.replace(".", "/") + "/view.html",
						object, type, message);

			} catch (TemplateNotFoundException e) {
				System.out.println("show user catch ");
				render("CRUD/blank.html", type);
			}
		}

		System.out.println(object.toString() + "before save");
		object._save();
		if (Security.getConnected().isAdmin
				&& Security.getConnected().id != tmp.id) {
			Notifications.sendNotification(tmp.id, Security.getConnected().id,
					"User", "The admin has edited your profile");
		}
		if (!(Arrays.equals(oldemailArray, tmp.email.toCharArray()))) {
			editedMessage += "Email changed to -->  " + tmp.email + "\n";
		}
		if (!(Arrays.equals(oldFirstNameArray, tmp.firstName.toCharArray()))) {
			editedMessage += oldFirstName + " First Name changed to -->  "
					+ tmp.firstName + "\n";
		}
		if (!(Arrays.equals(oldLastNameArray, tmp.lastName.toCharArray()))) {
			editedMessage += oldLastName + "  changed to -->  " + tmp.lastName
					+ "\n";
		}
		if (oldUser.communityContributionCounter != tmp.communityContributionCounter) {
			editedMessage += oldCommunityContributionCounter
					+ "  changed to -->  " + tmp.communityContributionCounter
					+ "\n";
		}
		/*
		 * if(oldUser.dateofBirth !=(tmp.dateofBirth)); { editedMessage +=
		 * dateofBirth + "  changed to -->  " + tmp.dateofBirth +"\n"; }
		 */
		if (!(Arrays.equals(oldCountryArray, tmp.country.toCharArray()))) {
			editedMessage += oldUser.country + "  changed to -->  "
					+ tmp.country + "\n";
		}
		if (!(Arrays.equals(oldProfessionArray, tmp.profession.toCharArray()))) {
			editedMessage += oldProfession + " changed to --> "
					+ tmp.profession + "\n";
		}
		System.out.println("edited" + editedMessage + " all");
		Log.addUserLog("Admin " + Security.getConnected().firstName + " "
				+ Security.getConnected().lastName + " has edited "
				+ tmp.username + "'s profile" + "\n" + editedMessage, tmp);
		System.out.println(object.toString() + "after the save");
		flash.success(Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			if (Security.getConnected().isAdmin) {
				redirect(request.controller + ".list");
			} else {
				Log.addUserLog("User" + Security.getConnected().firstName + " "
						+ Security.getConnected().lastName
						+ " has edites his/her profile",
						Security.getConnected());
				redirect("/Users/viewProfile?userId=" + id); // was showProfile
			}
		}
		redirect(request.controller + ".show", object._key());

	}

	// /**
	// * overrides the CRUD view method ,is responsible for deleting a user by
	// the
	// * System admin after specifying that user's id , and then it renders a
	// * message confirming whether the delete was successful or not
	// *
	// * @author Mostafa Ali
	// *
	// * @story C1S9
	// *
	// * @param id
	// * :String the user's id
	// *
	// *
	// * */
	// public static void delete(String id) {
	// long userId = Long.parseLong(id);
	// User user = User.findById(userId);
	// String x = "";
	// try {
	//
	// if (!(user.state.equals("n"))) {
	// user.state = "d";
	// user._save();
	// x = "deletion successful";
	// System.out.println(x + "  first if");
	// Mail.deletion(user, "");
	// } else {
	// x = "You can not delete a user who's deactivated his account !";
	// System.out.println(x + "else");
	// }
	// render(request.controller.replace(".", "/") + "/index.html", x);
	// } catch (NullPointerException e) {
	// x = "No such User !!";
	// System.out.println(x + "catch");
	// render(request.controller.replace(".", "/") + "/index.html");
	//
	// }
	//
	// }

	/**
	 * deletes a user after the system admin ( the one who's requesting the
	 * delete) specifies the user's id and the reason for the deletion , then it
	 * sends a mail to the deleted user notifying him of both the event and the
	 * reason
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            :String the user's id
	 * 
	 * @param deletionMessage
	 *            :String the reason the user was deleted for
	 * 
	 * */
	public static void delete(String id, String deletionMessage) {
		long userId = Long.parseLong(id);
		User user = User.findById(userId);
		String x = "";
		try {

			if (!(user.state.equals("n"))) {
				user.state = "d";
				user._save();
				x = "deletion successful";
				/*
				 * >>>>> Added by Salma Osama to: delete the volunteerRequests
				 * and the receivedAssignRequests of the user : remove the user
				 * from the list of assignees of the items he's assigned to
				 */
				for (int i = 0; i < user.volunteerRequests.size(); i++) {
					user.volunteerRequests.get(i).delete();
				}
				for (int i = 0; i < user.receivedAssignRequests.size(); i++) {
					user.receivedAssignRequests.get(i).delete();
				}
				Item item;
				while (!user.itemsAssigned.isEmpty()) {
					item = user.itemsAssigned.remove(0);
					item.assignees.remove(user);
					item.save();
					user.save();
				}
				// >>>>>> end of Salma's part :)
				System.out.println(x + "  first if");
				System.out.println("message" + deletionMessage);
				Mail.deletion(user, deletionMessage);
			} else {
				x = "You can not delete a user who's deactivated his account !";
				System.out.println(x + "else");
			}
			for (int i = 0; i < user.invitation.size(); i++)
				user.invitation.get(i).delete();
			for (int i = 0; i < user.requestsToJoin.size(); i++)
				user.requestsToJoin.get(i).delete();
			render(request.controller.replace(".", "/") + "/index.html", x);
		} catch (NullPointerException e) {
			x = "No such User !!";
			System.out.println(x + "catch");
			render(request.controller.replace(".", "/") + "/index.html");

		}

	}

	/**
	 * Renders the list of notifications of the user, to the view to display the
	 * notifications.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 */

	public static void viewNotifications() {
		User user = Security.getConnected();
		for (int i = 0; i < user.notifications.size(); i++) {
			if (user.notifications.get(i).seen) {
				Notification notification = user.notifications.get(i);
				notification.status = "Old";
				notification.save();
			} else {
				Notification notification = user.notifications.get(i);
				notification.status = "*New";
				notification.seen = true;
				notification.save();
			}
		}
		user.save();
		List<Notification> temp = user.notifications;
		List<Notification> nList = new ArrayList<Notification>();
		for (int i = temp.size() - 1; i > -1; i--) {
			nList.add(temp.get(i));
		}
		render(user, nList);
	}

	/**
	 * Changes the status of the notifications when the user views the latest
	 * notifications.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 */

	public static void showNotifications() {
		User user = Security.getConnected();
		for (int i = 0; i < user.notifications.size(); i++) {
			if (user.notifications.get(i).seen) {
				Notification notification = user.notifications.get(i);
				notification.status = "Old";
				notification.save();
			} else {
				Notification notification = user.notifications.get(i);
				notification.status = "*New";
				notification.seen = true;
				notification.save();
			}
		}
		user.save();
	}

	/**
	 * Renders the list of notification profiles for the user to view and edit
	 * his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 */

	public static void viewNotificationProfile() {
		User user = Security.getConnected();
		List<NotificationProfile> npList = user.notificationProfiles;
		render(user, npList);
	}

	/**
	 * Deletes the notifications of the users which he checked from the
	 * notifiactions list.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param a
	 *            long[] the list of notification IDs to be deleted
	 * 
	 */

	public static void deleteNotifications(long[] a) {
		for (int i = 0; i < a.length; i++) {
			Notification notification = Notification.findById(a[i]);
			notification.delete();
		}
	}

	public static void notifications() {
		User user = Security.getConnected();
		// List<NotificationProfile> npList = user.notificationProfiles;
		List<ArrayList> list = new ArrayList<ArrayList>();
		// List<String> titles = new ArrayList<String>();
		// List<Long> ids = new ArrayList<Long>();
		// List<String> types = new ArrayList<String>();
		for (int i = 0; i < user.notifications.size(); i++) {
			boolean flag = true;
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).get(0).equals(user.notifications.get(i).title)
						&& list.get(j).get(1)
								.equals(user.notifications.get(i).type)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(user.notifications.get(i).title);
				// temp.add(user.notifications.get(i).sourceID);
				temp.add(user.notifications.get(i).type);
				list.add(temp);
				// titles.add(user.notifications.get(i).title);
				// ids.add(user.notifications.get(i).sourceID);
				// types.add(user.notifications.get(i).type);
			}
		}
		render(user, list);
	}

	/**
	 * Ends the session of the current user and logs out
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 * 
	 */

	public static void logout() {
		try {
			session.remove("user_id");
			Secure.logout();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * renders the deactivation page
	 * 
	 * @story C1S5
	 * 
	 * @author Mai Magdy
	 * 
	 */
	public static void confirmDeactivation() {
		User user=Security.getConnected();
		render(user);

	}

	/**
	 * Deactivate the account of that user by setting the state to "n"
	 * 
	 * @story C1S5
	 * 
	 * @author Mai Magdy
	 * 
	 */
	public static void deactivate() {
		User user = Security.getConnected();
		user.state = "n";
		user.save();
		Mail.deactivate();
		logout();
	}

	/**
	 * activates the account of that user by setting the state to "a" and then
	 * renders a message
	 * 
	 * @author Mostafa Ali , Mai Magdy
	 * 
	 * @story C1S10,C1S11
	 * 
	 * @params userId long Id of the user that his account ll be activated
	 * 
	 * 
	 */
	public static void activate(long userId) {
		User user = User.findById(userId);
		if(user.state.equals("a"))
		{
			return;
		}
		user.state = "a";
		user._save();

		Invitation invite = Invitation.find("byEmail", user.email).first();

		if (invite != null)
			Invitations.view();
		else
			render();
	}

}