package controllers;

// list of organizers in entity 

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
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


@With(Secure.class)

public class Users extends CRUD {

	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
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
	 * @description This method renders the list of users
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
	 * User view method
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param userId
	 *            : id of the user we want to show
	 * 
	 * @description This method renders the form for viewing a user
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

		try {
			System.out.println("view() done, about to render");
			render(type, object, username, name, communityContributionCounter,
					profession, birthDate, userId);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/users/view.html");
		}
	}

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param userId
	 *            : id of the user we want to show
	 * 
	 * @description This method renders the form for editing and viewing a user
	 * 
	 * @throws Exception
	 * 
	 */
	public static void show(String userId) {
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
		try {
			System.out.println("show() done, about to render");
			render(type, object, username, name, communityContributionCounter,
					profession, birthDate, userId);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("show() done with exception, rendering to CRUD/show.html");
			render("CRUD/show.html", type, object);
		}
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @description This method renders the form for creating a user
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
		try {
			render(type, object);
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
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param topicId
	 *            : the topic id that the user is following
	 * 
	 * @param userId
	 *            : the user id who follows
	 * 
	 * @return void
	 */

	public static void unfollowTopic(long topicId, long userId) {
		Topic topic = Topic.findById(topicId);
		User user = User.findById(userId);
		topic.unfollow(user);
		user.unfollow(topic);
	}

	/**
	 * This Method removes a user from the list of followers in a given Tag
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param tagId
	 *            : the tag id that the user is following
	 * 
	 * @param userId
	 *            : the user id who follows
	 * 
	 * @return void
	 */
	public static void unfollowTag(long tagId, long userId) {
		Tag tag = Tag.findById(tagId);
		User user = User.findById(userId);
		tag.unfollow(user);
		user.unfollow(tag);
	}

	/**
	 * This Method removes a user from the list of followers in an organization
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param orgId
	 *            : the organization id the user is following
	 * 
	 * @param userId
	 *            : the user id who follows
	 * 
	 * @return void
	 */

	public static void unfollowOrganization(long orgId, long userId) {
		Organization org = Organization.findById(orgId);
		User user = User.findById(userId);
		org.unfollow(user);
		user.unfollow(org);
	}

	/**
	 * This Method removes a user from the list of followers in an entity
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param entity
	 *            : the entity the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public static void unfollowEntity(long entityId, long userId) {
		MainEntity entity = MainEntity.findById(entityId);
		User user = User.findById(userId);
		entity.unfollow(user);
		user.unfollow(entity);
	}

	/**
	 * This Method renders a page of all objects followed by a user
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param userId
	 *            : the user id who follows
	 * 
	 * @return void
	 */

	public static void listFollows(long userId) {
		User user = User.findById(userId);
		try {
			List<Organization> organizations = user.followingOrganizations;
			List<Tag> tags = user.followingTags;
			List<MainEntity> entities = user.followingEntities;
			List<Topic> topics = user.topicsIFollow;
			render(organizations, tags, entities, topics, user);
		} catch (Exception e) {
		}
	}

	/**
	 * this Method is responsible for reporting an idea as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param idea
	 *            : the idea to be reported
	 * 
	 * @param reporter
	 *            : the user who wants to report the idea
	 * 
	 */
	public static void reportIdeaAsSpam(Idea idea) {
		boolean alreadyReported = false;
		User reporter = Security.getConnected();
		for (int i = 0; i < reporter.ideasReported.size(); i++) {
			if (idea == reporter.ideasReported) {
				alreadyReported = true;
			}
		}
		if (!alreadyReported) {
			idea.spamCounter++;
			reporter.ideasReported.add(idea);
		}
		for (int j = 0; j < idea.belongsToTopic.getOrganizer().size(); j++) {
			Mail.ReportAsSpamMail(idea.belongsToTopic.getOrganizer().get(j),
					reporter, idea);
		}
		ideaSpamView(idea);

	}

	/**
	 * this Method is responsible for the view of reporting the idea as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 */

	public static void ideaSpamView(Idea idea) {
		int alreadyReported = -1;
		User reporter = Security.getConnected();
		for (int i = 0; i < idea.reporters.size(); i++) {
			if (reporter == idea.reporters.get(i))
				alreadyReported = 1;
			else
				alreadyReported = 0;
		}
		render(alreadyReported);

	}

	public static List<User> getBannedUser(Organization o, String action,
			long sourceID, String type) {
		List<User> user = (List<User>) BannedUser
				.find("select bu.bannedUser from BannedUser where bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ? ",
						o, action, type, sourceID);
		return (user);
	}


	/**
	 * 
	 * This method is for a user to create a Topic in an Entity he manages
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S24
	 * 
	 * @param entityId
	 *            : the id of the entity desired to create topic in
	 * 
	 * @param title
	 *            : the title of the topic being created
	 * 
	 * @param description
	 *            : the description/content of the topic being created
	 * 
	 * @param privacyLevel
	 *            : the privacy level of the topic being created
	 * 
	 * @param user
	 *            : the user trying to create the topic
	 */

	public void createTopic(long entityId, String title, String description,
			short privacyLevel, User user) {
		Topic topic = new Topic(title, description, privacyLevel, user);
		MainEntity entity = MainEntity.findById(entityId);

		if (Users.isPermitted(user, "create topic", entityId, "entity")
				&& user.entitiesIOrganize.contains(entity)) {
			entity.topicList.add(topic);
			user.topicsCreated.add(topic);
			user.topicsIOrganize.add(topic);
		} else {
			System.out.println("action cannot be performed in this entity");
		}
	}

	/**
	 * 
	 * This method is responsible for searching for users using specific
	 * criteria
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param keyword
	 *            : the keyword the user enters for searching
	 * 
	 */
	public static ArrayList<User> searchUser(String keyword) {

		List<User> searchResultByName = new ArrayList<User>();
		List<User> searchResultByProfession = new ArrayList<User>();
		List<User> searchResultByEmail = new ArrayList<User>();

		if (keyword != null) {
			searchResultByName = User.find("byUsernameLike","%" + keyword + "%"). <User>fetch();
			searchResultByProfession = User.find("byProfessionLike", "%" + keyword+ "%")
					.<User>fetch();
			searchResultByEmail = User.find("byEmailLike", "%" + keyword + "%").<User>fetch();
		}

		int nameSize = searchResultByName.size();
		int professionSize = searchResultByProfession.size();
		int emailSize = searchResultByEmail.size();
		for (int i = 0; i < nameSize; i++) {
			if (searchResultByName.get(i).state == "d"
					|| searchResultByName.get(i).state == "n") {
				searchResultByName.remove(i);
			}
		}
		for (int i = 0; i < professionSize; i++) {
			if (searchResultByProfession.get(i).state == "d"
					|| searchResultByProfession.get(i).state == "n") {
				searchResultByProfession.remove(i);
			}
		}

		for (int i = 0; i < emailSize; i++) {
			if (searchResultByEmail.get(i).state == "d"
					|| searchResultByEmail.get(i).state == "n") {
				searchResultByEmail.remove(i);
			}
		}
		ArrayList<User> search = new ArrayList<User>();
		search.addAll(searchResultByEmail);
		search.addAll(searchResultByName);
		search.addAll(searchResultByProfession);

		// searchResultByName.addAll(searchResultByProfession);
		// searchResultByName.addAll(searchResultByEmail);
		// render(searchResultByName, searchResultByProfession,
		// searchResultByEmail);
		return search;

	}

	/**
	 * 
	 * This method is responsible for searching for organizers in a certain
	 * organization
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
			if (user.get(i).state == "d" || user.get(i).state == "n") {
				user.remove(i);
			}
		}
		return user;
	}

	/**
	 * 
	 * This method is responsible for telling whether a user is allowed to do a
	 * specific action in an organization/entity/topic
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S15
	 * 
	 * @param user
	 *            : the user who is going to perform the action
	 * 
	 * @param action
	 *            :the action performed
	 * 
	 * @param placeId
	 *            : the id of the organization/ entity/ topic
	 * 
	 * @param placeType
	 *            : the type whether an organization/ entity/ topic
	 * 
	 * @return boolean
	 */
	public static boolean isPermitted(User user, String action, long placeId,
			String placeType) {
		// User banned =
		// BannedUser.find("select b.bannedUser from BannedUser b where b.bannedUser = ? and b.resourceID = ? and b.resourceType = ? and b.action =  ",
		// user);
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
			List<String> r = Roles.getRoleActions("organizer");
			if (r.contains(action)) {
				return true;
			} else {
				if (Roles.getRoleActions("idea developer").contains(action)) {
					return true;
				} else {
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
				if (allowed == null) {
					return false;
				} else {
					if (Roles.getRoleActions("idea developer").contains(action)) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (Roles.getRoleActions("idea developer").contains(action)) {
					return true;
				} else {
					return false;
				}
			}

		}
		if (placeType.equalsIgnoreCase("topic")) {
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
			if (topic.privacyLevel == 0 || topic.privacyLevel == 1) {
				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndEntityTopicIDAndType", user,
								topic.id, "topic").fetch();
				if (allowed == null) {
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
					if (allowed == null) {
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
					if (Roles.getRoleActions("idea developer").contains(action)) {
						return true;
					} else {
						return false;
					}
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
				if (allowed == null) {
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
	 * gets the list of organizers of a certain topic
	 * 
	 * @author: Nada Ossama
	 * 
	 * @param e
	 *            : is the topic that i want to retrieve all the organizers that
	 *            are enrolled in it
	 * 
	 * @return List of Organizers in that topic
	 */
	// /to be modefied
	public List<User> getTopicOrganizers(Topic t) {
		List<User> enrolled = new ArrayList<User>();
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		if (t != null) {
			Organization o = t.entity.organization;
			organizers = (List<UserRoleInOrganization>) UserRoleInOrganization


					.find("select uro from UserRoleInOrganization uro  where uro.organization = ? and uro.entityTopicID = ? and uro.type like ?",
							o, t.getId(), "topic");
			for (int i = 0; i < organizers.size(); i++) {
				if (!(organizers.get(i).role.roleName.equals("organizer"))) {
					// =======
					// .find("select uro from UserRoleInOrganization uro "
					// + "where uro.organization = ? and"
					// + " uro.entityTopicID = ? " + "and uro.type like ?",
					// o, t.getId(), "topic");
					// for (int i = 0; i < organizers.size(); i++) {
					// if
					// (!(organizers.get(i).role.roleName.equals("organizer")))
					// {
					// >>>>>>> .r797

					organizers.remove(i);
				} else {
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
	 * @param e
	 *            : is the entity that i want to retrieve all the organizers
	 *            that are enrolled in it
	 * 
	 * @return List of Organizers in that entity
	 */
	public static List<User> getEntityOrganizers(MainEntity e) {
		List<User> enrolled = new ArrayList<User>();
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		if (e != null) {
			Organization o = e.organization;

			long eID = e.getId();
			organizers = UserRoleInOrganization
					.find("select uro from UserRoleInOrganization uro where uro.organization = ? and uro.entityTopicID = ? and uro.type like ?",
							o, eID, "entity").fetch();
			for (int i = 0; i < organizers.size(); i++) {
				if (!((organizers.get(i).role.roleName).equals("organizer"))) {
					// =======
					// organizers = (List<UserRoleInOrganization>)
					// UserRoleInOrganization
					// .find("select uro from UserRoleInOrganization uro"
					// + "where uro.organization = ? "
					// + " and uro.entityTopicID = ? "
					// + "and uro.type like ?", o, e.getId(), "entity");
					// for (int i = 0; i < organizers.size(); i++) {
					// if (!((organizers.get(i).role).equals("organizer"))) {
					// >>>>>>> .r797

					organizers.remove(i);
				} else {
					enrolled.add(organizers.get(i).enrolled);
				}
			}

		}
		return enrolled;
	}

	/**
	 * gets all the users enrolled in an organization these users are: 1- the
	 * Organization lead 2- the organizers (even if blocked) 3- Idea Developers
	 * in secret or private topics
	 * 
	 * @author : Nada Ossama
	 * 
	 * @param: the organization that the users are enrolled in
	 * 
	 * @return :List of enrolled users
	 */

	public static List<User> getEnrolledUsers(Organization o) {
		List<User> enrolled = null;
		if (o != null) {

			enrolled = (List<User>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = ? ",
							o);

		}
		return enrolled;
	}

	/**
	 * 
	 <<<<<<< .mine
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            : the user's id
	 * 
	 * @return void
	 * 
	 * */
	public static void delete(long id) {
		User user = User.findById(id);
		String x = "";
		try {

			if (user.state.equals("n")) {
				user.state = "d";
				x = "deletion successful";
			} else {
				x = "You can not delete a user who's deactivated his account !";
			}
			render(x);
		} catch (NullPointerException e) {
			x = "No such User !!";
			render(x);
		}

	}

	/**
	 * 
	 */
	public static List<MainEntity> getEntitiesOfOrganizer(Organization org,
			User user) {
		List<MainEntity> entities = new ArrayList<MainEntity>();

		List<UserRoleInOrganization> uro = UserRoleInOrganization.find(
				"byOrganizationAndEnrolled", org, user).fetch();
		for (int i = 0; i < uro.size(); i++) {
			if (uro.get(i).role.roleName.equals("organizer")) {
				entities.add((MainEntity) MainEntity.findById(uro.get(i).entityTopicID));
			}
		}
		return entities;
	}

	/**
	 * 
	 ======= >>>>>>> .r797 This method returns list of entities within a
	 * specific organization that a user is enrolled in as organizer or
	 * organization lead (or admin)
	 * 
	 * @author {Mai Magdy}
	 * 
	 * @param org
	 *            the organization that we want to search within its entities
	 * 
	 * 
	 * @return list<MainEntity>
	 */

	public static List<MainEntity> getOrganizerEntities(Organization org,
			User user) {

		List<MainEntity> list = new ArrayList<MainEntity>();
		if (org.creator.equals(user) || user.isAdmin) {
			return org.entitiesList;
		}

		else {
			for (int i = 0; i < org.entitiesList.size(); i++) {
				if (UserRoleInOrganizations.isOrganizer(user,
						org.entitiesList.get(i).id, "entity")
						&& isPermitted(user, "invite",
								org.entitiesList.get(i).id, "entity"))
					list.add(org.entitiesList.get(i));

			}
		}

		return list;
	}

	/**
	 * 
	 * This method returns list of organizations that a user is enrolled in as
	 * organizer or organization lead (or admin)
	 * 
	 * @author {Mai Magdy}
	 * 
	 * @param user
	 * 
	 * 
	 * @return list<Organization>
	 */

	public static List<Organization> getOrganizerOrganization(User user) {

		List<Organization> list = new ArrayList<Organization>();
		List<Organization> org = Organization.findAll();

		for (int i = 0; i < org.size(); i++) {
			List<User> organizer = new ArrayList<User>();

			if (org.get(i).creator.equals(user) || user.isAdmin)
				list.add(org.get(i));
			else {
				organizer = searchOrganizer(org.get(i));

				for (int j = 0; j < organizer.size(); j++) {
					if (organizer.get(j).equals(user)
							&& isPermitted(user, "invite", org.get(i).id,
									"organization"))
						list.add(org.get(i));
				}
			}
		}

		return list;
	}

	/**
	 * This method overrides the CRUD create method that is used to create a new
	 * user and make sure that this user is valid, and then it renders a message
	 * mentioning whether the operation was successful or not.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * 
	 * @return void
	 */

	public static void create() throws Exception {
		// if(Security.getConnected().isAdmin);
		ObjectType type = ObjectType.get(Users.class);
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = type.entityClass.newInstance();
		User user=(User) object;
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		System.out.println(object.toString());
		String errorMessage="";
		try{
			if(user.username.length()>=20)
			{
				errorMessage = "Username cannot exceed 20 characters";
			}
			if(user.find("ByUsername", user.username)!=null)
			{
				errorMessage += "This username already exists !";
			}
		}
		catch(NullPointerException e)
		{
			errorMessage = "Username field is required, u must have a username !";
		}
		
		try{
			if(user.password.length()>=20)
			{
				errorMessage += "Password cannot exceed 20 characters";
			}
			
		}
		catch(NullPointerException e)
		{
			errorMessage += "Password field is required, u must have a username !";
		}
		
		try{
			if(user.password.length()>=20)
			{
				errorMessage += "First name cannot exceed 20 characters";
			}
			
		}
		catch(NullPointerException e)
		{
			errorMessage += "First name field is required, u must have a username !";
		}
		
		try{
			
			if(user.find("ByEmail", user.email)!=null)
			{
				errorMessage += "This mail already exists !";
			}
			
		}
		catch(NullPointerException e)
		{
			errorMessage += "Email field is required, u must have a username !";
		}
			
		/*if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				System.out.println(object.toString() + "!1try");
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				System.out.println(object.toString() + "catch");
				render("CRUD/blank.html", type, object);
			}
		}*/
		
		try {
			render(request.controller.replace(".", "/") + "/blank.html",
					user.email, user.username, user.password, user.firstName,
					user.lastName, user.communityContributionCounter, user.dateofBirth,user.country,user.profession ,errorMessage);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type);
		}
		System.out.println(object.toString() + "before the save");
		object._save();
		flash.success(Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			System.out.println(object.toString() + "save condition");
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	/**
	 * This method is used to submit the edit, to make sure that the edits are
	 * acceptable, and then it renders a message mentioning whether the
	 * operation was successful or not.
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            : the user's id
	 * 
	 * @return void
	 */

	public static void save(String id) throws Exception {
		// Security.check(Security.getConnected().isAdmin||);
		ObjectType type = ObjectType.get(Users.class);
		notFoundIfNull(type);
		// Model object = type.findById(id);
		long userId = Long.parseLong(id);
		User object = User.findById(userId);
		Binder.bind(object, "object", params.all());
		System.out.println(object.toString() + "begin");
		validation.valid(object);
		/*if (validation.hasErrors()) {
			System.out.println(object.toString() + "first if");
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			System.out.println(object.toString() + "t7t first if");
			try {
				System.out.println(object.toString() + "try");
				render(request.controller.replace(".", "/") + "/show.html",
						type, object);
				System.out.println("m3mlsh render");
			} catch (TemplateNotFoundException e) {
				System.out.println(object.toString() + "catch");
				render("CRUD/show.html", type, object);
			}
		}*/
		System.out.println(object.toString() + "before save");
		object._save();
		System.out.println(object.toString() + "after the save");
		flash.success(Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	/**
	 * This method is responsible for deleting a user by the system admin after
	 * specifying that user's id, and then it renders a message confirming
	 * whether the delete was successful or not
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            : the user's id
	 * 
	 * @return void
	 * 
	 * */
	public static void delete(String id) {
		long userId = Long.parseLong(id);
		User user = User.findById(userId);
		String x = "";
		try {

			if (!(user.state.equals("n"))) {
				user.state = "d";
				user._save();
				x = "deletion successful";
				System.out.println(x+"  first if");
			} else {
				x = "You can not delete a user who's deactivated his account !";
				System.out.println(x+"else");
			}
			render(request.controller.replace(".", "/") + "/index.html",x);
		} catch (NullPointerException e) {
			x = "No such User !!";
			System.out.println(x+"catch");
			// render(x);
			render(request.controller.replace(".", "/") + "/index.html");

		}

	}

	/**
	 * This method renders the list of notifications of the user, to the view to
	 * display the notifications.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @return void
	 */

	public static void viewNotifications() {
		User u = Security.getConnected();
		List<Notification> nList = u.notifications;
		render(nList);
	}

	/**
	 * This method renders the list of notification profiles for the user to
	 * view and edit his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param userId
	 *            the ID of the user to view his/her profile
	 * 
	 * @return void
	 */

	public static void viewNotificationProfile() {
		User u = Security.getConnected();
		List<NotificationProfile> npList = u.notificationProfiles;
		render(npList);
	}

	/**
	 * This method renders the list of invitations to join an organization for a
	 * user.
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S16
	 * 
	 * @param userId
	 *            the id of the user
	 * 
	 * @return void
	 */

	public static void ViewOrgInv(long userId) {
		User u = User.findById(userId);
		// User u = (User) User.findAll().get(1);
		List<Invitation> invitations = new ArrayList<Invitation>();
		List<Invitation> invs1 = Invitation.findAll();
		for (int j = 0; j < invs1.size(); j++) {
			if (invs1.get(j).email.equalsIgnoreCase(u.email)
					&& invs1.get(j).organization != null) {
				invitations.add(invs1.get(j));
			}

		}
		render(invitations, userId);
	}

	/**
	 * This method accepts or rejects an invitation to a user to join an
	 * organization.
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S16
	 * 
	 * @param invId
	 *            the id of the invitation
	 * 
	 * @param userId
	 *            the id of the user
	 * 
	 * @param r
	 *            a boolean to indicate acceptance (true for accepted)
	 * 
	 * @return void
	 */

	public static void acceptToJoinOrg(long invId, long userId, boolean r) {
		Invitation inv = Invitation.findById(invId);
		Organization org = inv.organization;
		User user = User.findById(userId);
		if (r) {
			// System.out.println("@@@@@");
			// Role role = Role.find("byRoleName", "Idea Developer").first();
			// if (role == null) {
			// // role ???
			// role = new Role("Idea Developer", "view");
			// role._save();
			// }
			Role role = Roles.getRoleByName("idea developer");
			UserRoleInOrganization roleInOrg = new UserRoleInOrganization(user,
					org, role);
			roleInOrg._save();
			user.userRolesInOrganization.add(roleInOrg);
			user.save();
			User orgLead = org.creator;
			Notifications.sendNotification(user.id, org.id, "Organization",
					user.username + " has accepted the invitation to join the "
							+ org.name);
			Notifications.sendNotification(orgLead.id, org.id, "Organization",
					user.username + " has accepted the invitation to join the "
							+ org.name);
			// Notification n1 = new Notification("Organization",
			// inv.sender, user.username + " accepted the invitation.");
			// n1._save();
			// if (orgLead.id != inv.sender.id) {
			// Notification n2 = new Notification("Invitation accepted",
			// orgLead, user.username + " accepted th invitation");
			// n2._save();
			// }
			// any other insertions?
		}
		org.invitation.remove(inv);
		org._save();
		user.invitation.remove(inv);
		user._save();
		inv._delete();
	}
	
	/**
	 * This method ends the session of the current user 
	 * and logs out
	 * 
	 * @author Ahmed Maged
	 * 
	 * @return void
	 */
	
	public static void logout() {
		try {			
			session.remove("user_id");
			Secure.logout();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}