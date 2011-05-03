package controllers;

// list of organizers in entity 

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

import notifiers.Mail;

import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.GenericModel.JPAQuery;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

import models.MainEntity;
import models.Notification;
import models.NotificationProfile;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import models.*;

public class Users extends CRUD {

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
	public static void reportIdeaAsSpam(Idea idea, User reporter) {
		boolean alreadyReported = false;
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
		ideaSpamView(idea, reporter);

	}

	/**
	 * this Method is responsible for the view of reporting the idea as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 */

	public static void ideaSpamView(Idea idea, User reporter) {
		int alreadyReported = -1;
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

	public void postTopic(String name, String description, short privacyLevel,
			User creator, MainEntity entity) {

		if (entity.organizers.contains(creator)) {
			Topic newTopic = new Topic(name, description, privacyLevel,
					creator, entity).save();
			creator.topicsCreated.add(newTopic);

			// List usr = getEntityOrganizers(entity);
			// UserRoleInOrganization.addEnrolledUser(creator,
			// newTopic.entity.organization,
			// Roles.getRoleByName("Organizer"), newTopic.getId(), "topic");
			List usr = getEntityOrganizers(entity);
			// UserRoleInOrganization.addEnrolledUser(creator,
			// newTopic.entity.organization,
			// Role.getRoleByName("Organizer"), newTopic.getId(), "topic");
			creator.topicsIOrganize.add(newTopic);
			for (int i = 0; i < entity.organizers.size(); i++) {
				entity.organizers.get(i).topicsIOrganize.add(newTopic);
			}
		}
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
	      searchResultByName = User.find("username like ? ", keyword)
	        .fetch();
	      searchResultByProfession = User.find("profession like ? ",
	        keyword).fetch();
	      searchResultByEmail = User.find("email like ? ", keyword).fetch();
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
	     
	     //searchResultByName.addAll(searchResultByProfession);
	     //searchResultByName.addAll(searchResultByEmail);
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
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization> ();
		List<User> user = new ArrayList<User> ();
		if (o != null) {
//			organizers = (List<UserRoleInOrganization>) UserRoleInOrganization
//					.find("select uro.enrolled from UserRoleInOrganization uro,Role r where  uro.Role = r and uro.organization = ? and r.roleName like ? ",
//							o, "organizer");
			organizers = UserRoleInOrganization.find("byOrganization", o).fetch();
			for(int i= 0; i <organizers.size(); i++) {
				if((organizers.get(i).role.roleName).equals("organizer")){
					user.add(organizers.get(i).enrolled);
				}
			}

		}
//		List<User> finalOrganizers = new ArrayList<User>();
//		for (int i = 0; i < organizers.size(); i++) {
//			finalOrganizers.add((organizers.get(i)).enrolled);
//		}
		int size = organizers.size();
		for (int i = 0; i < size; i++) {
			if(user.get(i).state == "d" || user.get(i).state == "n"){
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
		JPAQuery banned = BannedUser.find(
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

		if (placeType == "organization") {
			Organization org = Organization.findById(placeId);
			// List<UserRoleInOrganization> l =
			// UserRoleInOrganization.find("byOrganizationAnd")
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organization lead").contains(action)) {
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
						.find("byEnrolledAndbyOrganization", user, org).fetch();
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
		if (placeType == "topic") {
			Topic topic = Topic.findById(placeId);
			MainEntity m = topic.entity;
			Organization org = m.organization;
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organization lead").contains(action)) {
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
						.find("byEnrolledAndbyEntityTopicIDAndType", user,
								topic, "topic").fetch();
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
							.find("byEnrolledAndbyOrganization", user, org)
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
			}
		}

		if (placeType == "entity") {
			MainEntity entity = MainEntity.findById(placeId);
			Organization org = entity.organization;
			if (user.equals(org.creator)) {
				if (Roles.getRoleActions("organization lead").contains(action)) {
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
						.find("byEnrolledAndbyOrganization", user, org).fetch();
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
	public List<User> getTopicOrganizers(Topic t) {

		List<User> organizers = null;
		if (t != null) {
			Organization o = t.entity.organization;
			organizers = (List<User>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro and Role r "
							+ "where uro.organization = ? and"
							+ " uro.Role = r and r.roleName like ? and uro.entityTopicID = ? "
							+ "and uro.type like ?", o, "organizer", t.getId(),
							"topic");

		}
		return organizers;
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
	public static List getEntityOrganizers(MainEntity e) {

		List<User> organizers = null;
		if (e != null) {
			Organization o = e.organization;
			organizers = (List<User>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro and Role r "
							+ "where uro.organization = ? and"
							+ " uro.Role = r and r.roleName like ? and uro.entityTopicID = ? "
							+ "and uro.type like ?", o, "organizer", e.getId(),
							"entity");

		}
		return organizers;
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

			enrolled = (List<User>) UserRoleInOrganization.find(
					"select uro.enrolled from UserRoleInOrganization uro "
							+ "where uro.organization = ? ", o);

		}
		return enrolled;
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
	 * This method returns list of entities within a specific organization that
	 * a user is enrolled in as organizer or organization lead (or admin)
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
		if (org.creator.equals(user) || user.isAdmin)
			return org.entitiesList;

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
		// Security.check(Security.getConnected().isAdmin);
		ObjectType type = ObjectType.get(Users.class);
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = type.entityClass.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		object._save();
		flash.success(Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
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
		User object = User.findById(id);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		object._save();
		flash.success(Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	/**
	 * This method renders the list of notifications of the user, to the view to
	 * display the notifications.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param userId
	 *            the ID of the user required to get his notifications
	 * 
	 * @return void
	 */

	public static void viewNotifications(long userId) {
		User u = User.findById(userId);
		List<Notification> nList = u.openNotifications();
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

	public static void viewNotificationProfile(long userId) {
		User u = User.findById(userId);
		List<NotificationProfile> npList = u.openNotificationProfile();
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
			Notification n1 = new Notification("Invitation accepted",
					inv.sender, user.username + " accepted the invitation.");
			n1._save();
			User orgLead = org.creator;
			if (orgLead.id != inv.sender.id) {
				Notification n2 = new Notification("Invitation accepted",
						orgLead, user.username + " accepted th invitation");
				n2._save();
			}
			// any other insertions?
		}

		org.invitation.remove(inv);
		org._save();
		user.invitation.remove(inv);
		user._save();
		inv._delete();
	}

}