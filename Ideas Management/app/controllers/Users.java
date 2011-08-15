package controllers;

/**
 @author Mostafa Ali
 */
// list of organizers in entity 

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ugot.recaptcha.Recaptcha;

import javax.persistence.Query;

import com.google.gson.JsonObject;

import controllers.CoolCRUD.ObjectType;
import notifiers.Mail;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.GenericModel.JPAQuery;
import play.db.jpa.JPA;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Router;
import play.mvc.With;
import models.*;

import java.util.Arrays;

@With(Secure.class)
public class Users extends CoolCRUD {

	/**
	 * 
	 * overrides the CRUD list method ,renders the list of users if the
	 * connected user is an admin otherwise it redirects him to another page
	 * informing him he's not authorized to view the list
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param page
	 *            : int page of the list we are in
	 * 
	 * @param search
	 *            : String , the keyword the user want to search for
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
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			if (page < 1) {
				page = 1;
			}
			List<Model> objects = type.findPage(page, search, searchFields,
					orderBy, order, (String) request.args.get("where"));
			Long count = type.count(search, searchFields,
					(String) request.args.get("where"));
			Long totalCount = type.count(null, null,
					(String) request.args.get("where"));
			try {
				render(type, objects, count, totalCount, page, orderBy, order);
			} catch (TemplateNotFoundException e) {
				render("CRUD/list.html", type, objects, count, totalCount,
						page, orderBy, order);
			}
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * overrides the CRUD view method and renders the form for viewing / editing
	 * a user if the connected user is a system admin otherwise it redirects him
	 * to another page informing him he's not authorized to view the list
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param userId
	 *            :String id of the user we want to show
	 * 
	 * 
	 * @throws TemplateNotFoundException
	 * 
	 */
	public static void view(String userId) {
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			long userID = Long.parseLong(userId);
			User object = User.findById(userID);
			notFoundIfNull(object);
			int communityContributionCounter = object.communityContributionCounter;
			String name = object.firstName + " " + object.lastName;
			String profession = object.profession;
			String username = object.username;
			String birthDate = "" + object.dateofBirth;

			int adminFlag = 0;
			if (Security.getConnected().isAdmin) {
				adminFlag = 1;
			}
			int admin = 0;
			if (object.isAdmin) {
				admin = 1;
			}
			int isDeleted = 0;
			if (object.state.equals("d")) {
				isDeleted = 1;
			}
			try {

				render(type, object, username, name,
						communityContributionCounter, profession, birthDate,
						userId, adminFlag, isDeleted, admin);

			} catch (TemplateNotFoundException e) {
				render("/users/view.html");
			}
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * overrides the CRUD show method,renders the form for editing and viewing a
	 * user if the connected user is a system admin otherwise it redirects him
	 * to another page informing him he's not authorized to view the list
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param String
	 *            userId : id of the user we want to show
	 * 
	 * 
	 * @throws TemplateNotFoundException
	 * 
	 */

	public static void show(String userId) {
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			Model object = type.findById(userId);
			notFoundIfNull(object);
			User tmp = (User) object;
			int communityContributionCounter = tmp.communityContributionCounter;
			String name = tmp.firstName + " " + tmp.lastName;
			String profession = tmp.profession;
			String username = tmp.username;
			String dateofBirth = "" + tmp.dateofBirth;
			int admin = 0;
			if (tmp.isAdmin) {
				admin = 1;
			}
			int isDeleted = 0;
			if (tmp.state.equals("d")) {
				isDeleted = 1;
			}
			try {
				render(type, object, username, name,
						communityContributionCounter, profession, dateofBirth,
						userId, isDeleted, admin);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		} else {
			BannedUsers.unauthorized();
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
	public static void viewMyProfile(String id) {
		/**
		 * the method was surrounded by a check to make 
		 * sure that user is editing his profile only
		 * 
		 * added by Mostafa Ali
		 */
		long longId = Long.parseLong(id);
		if(Security.getConnected().id==longId)
		{
			long userID = Long.parseLong(id);
			User user = User.findById(userID);

			notFoundIfNull(user);
			

			String firstName = user.firstName;
			String lastName = user.lastName;
			String profession = user.profession;
			String username = user.username;
			String birthDate = "" + user.dateofBirth;
			String email = user.email;

			email = email.trim().toLowerCase();
			username = username.trim().toLowerCase();
			firstName = firstName.trim();
			List<User> userList = User.findAll();
			String emails = "";
			String usernames = "";

			for (int i = 0; i < userList.size(); i++) {
				if (i == userList.size() - 1) {
					usernames = usernames + userList.get(i).username + "";
					emails = emails + userList.get(i).email + "";
				} else {
					usernames = usernames + userList.get(i).username + "|";
					emails = emails + userList.get(i).email + "|";
				}

			}

			int adminFlag = 0;
			if (Security.getConnected().isAdmin) {
				adminFlag = 1;
			}
			try {

				
				render(user, usernames, emails);

			} catch (TemplateNotFoundException e) {
				
				render("/users/view.html");
			}
		}
		else
		{
			BannedUsers.unauthorized();
		}
	}

	/**
	 * passes the user's Id to viewMyProfile method
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S1-2
	 * 
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
		User user = Security.getConnected();
		notFoundIfNull(user);
		boolean flag = true;
		if (user.id == userId) {
			User otherUser = Security.getConnected();
			render(user, otherUser, flag);
		} else {
			User otherUser = User.findById(userId);
			flag = false;
			render(user, otherUser, flag);
		}
	}
	
	public static void viewProfile(long userId, int password) {
		User user = Security.getConnected();
		notFoundIfNull(user);
		boolean flag = true;
		if (user.id == userId) {
			User otherUser = Security.getConnected();
			render(user, otherUser, flag, password);
		} else {
			User otherUser = User.findById(userId);
			flag = false;
			render(user, otherUser, flag, password);
		}
	}

	/**
	 * Overrides the CRUD method blank, renders the form for creating a user if
	 * the connected user is a system admin otherwise it redirects him to
	 * another page informing him he's not authorized to view the list
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
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(Users.class);
			notFoundIfNull(type);
			Constructor<?> constructor = type.entityClass
					.getDeclaredConstructor();
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

		} else {
			BannedUsers.unauthorized();
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
		/**
		 * added by Mostafa Ali to make sure that a deleted user cannot follow a tag
		 */
		if(!(user.state.equals("d")))
		{
			tag.follow(user);
			user.follow(tag);
		}
		
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
	 */
	public static void reportIdeaAsSpam(long ideaId) {
		Idea idea = Idea.findById(ideaId);
		User reporter = Security.getConnected();
		idea.spamCounter++;
		reporter.ideasReported.add(idea);
		idea.reporters.add(reporter);
		// List<User> organizers = Users
		// .getEntityOrganizers(idea.belongsToTopic.entity);
		// for (int j = 0; j < organizers.size(); j++) {
		Mail.reportAsSpamMail(/* organizers.get(j) */idea.belongsToTopic.creator,
				reporter, idea, idea.description, idea.title);
		// }
		
		String logDescription = "<a href=\"/Users/viewProfile?userId="
			+ reporter.id + "\">" 
			+ reporter.username + "</a>" 
			+ " reported the idea  "
			+ "<a href=\"/Ideas/show?ideaId=" + idea.id + "\">" + idea.title + "</a>" + " as spam.";
//	 Log.addLog(logDescription, reporter, idea, 
//			 idea.plan, idea.plan.topic, idea.plan.topic.entity, idea.plan.topic.entity.organization);
		if(idea.plan!=null)
		Log.addLog(logDescription, reporter, idea, 
				 idea.plan, idea.belongsToTopic, idea.belongsToTopic.entity, idea.belongsToTopic.entity.organization);
		else
			Log.addLog(logDescription, reporter, idea, idea.belongsToTopic, idea.belongsToTopic.entity, idea.belongsToTopic.entity.organization);

		idea.save();
		reporter.save();
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
		redirect("/Ideas/show?ideaId=" + idea.getId(), alreadyReported);
		// render(alreadyReported);
	}

	/**
	 * this Method is responsible for reporting a Topic as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param topicId
	 *            : the ID of the topic to be reported
	 * 
	 * 
	 */
	public static void reportTopicAsSpam(long topicId) {
		Topic topic = Topic.findById(topicId);
		User reporter = Security.getConnected();
		if (topic.reporters == null)
			topic.reporters = "";
		topic.reporters += reporter.id + ",";
		List<User> organizers = Users.getEntityOrganizers(topic.entity);
		for (int j = 0; j < organizers.size(); j++) {
			Mail.reportTopicMail(topic.creator, reporter, topic,
					topic.description, topic.title);
		}
		
		String logDescription = "<a href=\"/Users/viewProfile?userId="
			+ reporter.id + "\">" 
			+ reporter.username + "</a>" 
			+ " reported the topic  "
			+ "<a href=\"/Topics/show?topicId=" + topic.id + "\">" + topic.title + "</a>" + " as spam.";
	 Log.addLog(logDescription, reporter, 
			 topic, topic.entity, topic.entity.organization);
		
		
		topic.save();
		reporter.save();
		TopicSpamView(topicId);

	}

	/**
	 * this Method is responsible for the view of reporting the topic as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param topicId
	 *            : the ID of the topic to be reported
	 * 
	 */

	public static void TopicSpamView(long topicId) {
		boolean alreadyReported = false;
		Topic topic = Topic.findById(topicId);
		redirect("/Topics/show?topicId=" + topic.getId(), alreadyReported);
		// render(alreadyReported);
	}

	/**
	 * this Method is responsible for reporting a comment as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param commentId
	 *            : the ID of the comment to be reported
	 * 
	 * 
	 */
	public static void reportCommentAsSpam(long commentId) {
		Comment comment = Comment.findById(commentId);
		User reporter = Security.getConnected();
		if (comment.reporters != null) {
			comment.reporters += reporter.id + ",";
			comment.save();
		}
		if (comment.commentedIdea != null) {
			if (comment.commenter.username.equals(comment.commentedIdea.author.username)) {
				String description1 = "Your comment " + comment.comment
				+ "in the idea " + comment.commentedIdea.toString()
				+ " has been reported as spam";
				String description2 = "A comment " + comment.comment + "in the idea "
				+ comment.commentedIdea.toString()
				+ " has been reported as spam";
		
				Notifications.sendNotification(comment.commenter.id, commentId,
						"comment", description1);
				Notifications.sendNotification(comment.commentedIdea.author.id,
						commentId, "comment", description2);
			} else
				Notifications.sendNotification(comment.commentedIdea.author.id,
						commentId, "comment", "your comment in your idea "
								+ comment.commentedIdea.toString());
		
			String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ reporter.id + "\">" 
				+ reporter.username + "</a>" 
				+ " reported a comment on the idea  "
				+ "<a href=\"/Ideas/show?ideaId=" 
				+ comment.commentedIdea.id + "\">" 
				+ comment.commentedIdea.title + "</a>" + " as spam.";
		 Log.addLog(logDescription, reporter, comment, comment.commentedIdea, 
				 comment.commentedIdea.plan, comment.commentedIdea.plan.topic,
				 comment.commentedIdea.plan.topic.entity, comment.commentedIdea.plan.topic.entity.organization);
			
		
		} else if (comment.commentedPlan != null) {
			Notifications
					.sendNotification(comment.commenter.id,
							commentId, "comment",
							"your comment has been reported as spam "
									+ comment.comment);
			
			String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ reporter.id + "\">" 
				+ reporter.username + "</a>" 
				+ " reported a comment on the plan  "
				+ "<a href=\"/Plans/viewAsList?planId=" + comment.commentedPlan.id + "\">" + comment.commentedPlan.title + "</a>" + " as spam.";
		 Log.addLog(logDescription, reporter, comment, 
				 comment.commentedPlan, comment.commentedPlan.topic, comment.commentedPlan.topic.entity,
				 comment.commentedPlan.topic.entity.organization);
			
		}
		// commentSpamView(commentId);
		
		

	}

	/**
	 * this Method is responsible for the view of reporting the comment as spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param commentId
	 *            : the ID of the topic to be reported
	 * 
	 */

	public static void commentSpamView(long commentId) {
		boolean alreadyReported = false;
		Comment comment = Comment.findById(commentId);
		Idea idea = comment.commentedIdea;
		// for (int i = 0; i < topic.reporters.size(); i++) {
		// if (reporter.username.equals(topic.reporters.get(i).username))
		// alreadyReported = true;}
		redirect("/Ideas/show?ideaId=" + idea.getId(), alreadyReported);
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
	 * @return List<User> is the list of the banned users
	 */

	public static List<User> getBannedUser(Organization organization,
			String action, long sourceID, String type) {
		List<User> users = BannedUser
				.find("select bu.bannedUser from BannedUser where bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ? ",
						organization, action, type, sourceID).fetch();
		List<User> usersReturned = new ArrayList();
		for (int i = 0; i < users.size(); i++) {
			User temp = users.get(i);
			if (temp.state.equals("a") && (!usersReturned.contains(temp))) {
				usersReturned.add(temp);

			}
		}

		return (usersReturned);
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
        System.out.println("name :" +keyword);
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
			searchResultByEmail = User.find("byEmail", keyword).<User> fetch();
		}
		System.out.println("searchResultByName : "+searchResultByName);
		System.out.println("searchResultByProfession : " +searchResultByProfession);
		System.out.println("searchResultByEmail : "+	searchResultByEmail);

		int nameSize = searchResultByName.size();
		int professionSize = searchResultByProfession.size();
		int emailSize = searchResultByEmail.size();
		for (int i = 0; i < nameSize; i++) {
			if (searchResultByName.get(i).state.equals("a")) {
				searchResultByNameActive.add(searchResultByName.get(i));
			}
			System.out.println("state : " +searchResultByName.get(i).state);
			
		}
		System.out.println("searchResultByNameActive : "+searchResultByNameActive);
		for (int i = 0; i < professionSize; i++) {
			if (searchResultByProfession.get(i).state == "a") {
				searchResultByNameActive.add(searchResultByProfession
						.get(i));
				
			}
		}
		System.out.println("searchResultByNameActive : " +searchResultByNameActive);

		for (int i = 0; i < emailSize; i++) {
			if (searchResultByEmail.get(i).state == "a") {
				searchResultByEmailActive.add(searchResultByEmail.get(i));
			}
		}
		System.out.println("searchResultByEmailActive : "+searchResultByEmailActive);
		ArrayList<User> search = new ArrayList<User>();
		search.addAll(searchResultByEmailActive);
		search.addAll(searchResultByNameActive);
		search.addAll(searchResultByProfessionActive);
       System.out.println("search : " +search);
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
			if (user.get(i).state == "a" && (!userActive.contains(user.get(i)))) {
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

			if (org.privacyLevel == 0) {

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
				if( org.privacyLevel == 1 && action.equals("view"))
					return true;
				if (org.privacyLevel == 1) {

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
			if (topic.privacyLevel == 1) {
				List<UserRoleInOrganization> allowed = UserRoleInOrganization
						.find("byEnrolledAndEntityTopicIDAndType", user,
								topic.id, "topic").fetch();
				if (allowed.size() == 0) {
					if (action.equals("view")) {
						return true;
					} else
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
						if (action.equals("view")) {
							return true;
						}
						List<UserRoleInOrganization> allowedTopic = UserRoleInOrganization
								.find("byEnrolledAndEntityTopicIDAndType",
										user, topic.id, "topic").fetch();
						if (allowedTopic.size() == 0) {
							return false;
						} else {
							if (Roles.getRoleActions("idea developer")
									.contains(action)) {
								return true;
							} else {
								return false;
							}
						}

					}
				}
				if (org.privacyLevel == 2) {

					if (action.equals("view")) {
						return true;
					}
					List<UserRoleInOrganization> allowed = UserRoleInOrganization
							.find("byEnrolledAndEntityTopicIDAndType", user,
									topic.id, "topic").fetch();
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
	 *            Topic topic that i want to retrieve all the organizers that
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
						&& organizers.get(i).enrolled.state.equals("a")
						&& (!enrolled.contains(organizers.get(i).enrolled))) {
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
		System.out.println(entity.name);
		List<User> enrolled = new ArrayList<User>();
		List<UserRoleInOrganization> organizers = new ArrayList<UserRoleInOrganization>();
		if (entity != null) {
			//System.out.println("ezay da5alt?!!!");
			Organization organization = entity.organization;

			long entityId = entity.id;
			organizers = UserRoleInOrganization
					.find("select uro from UserRoleInOrganization uro where uro.organization = ? and uro.entityTopicID = ? and uro.type like ?",
							organization, entityId, "entity").fetch();
			for (int i = 0; i < organizers.size(); i++) {
				if (((organizers.get(i).role.roleName).equals("organizer"))
						&& (organizers.get(i).enrolled.state.equals("a"))
						&& (!enrolled.contains(organizers.get(i).enrolled))) {

					enrolled.add(organizers.get(i).enrolled);
				}
			}

		}
		return enrolled;
	}

	/**
	 * checks whether a user is allowed to delete an idea or a comment
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
	 * @param organization
	 *            Organization organization that the users are enrolled in
	 * 
	 * @return List<User> enrolled users
	 */

	public static List<User> getEnrolledUsers(Organization organization) {
		List<User> enrolled = new ArrayList<User>();
		List<User> finalEnrolled = new ArrayList<User>();
		if (organization != null) {

			enrolled = UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = ? ",
							organization).fetch();

			for (int i = 0; i < enrolled.size(); i++) {
				if (enrolled.get(i).state.equals("a")
						&& (!finalEnrolled.contains(enrolled.get(i)))) {
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
	 *            long entityTopicId is the id of the entity or topic
	 * 
	 * @param type
	 *            String type is the parameter that specifies whether to fetch
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
			if (enrolled.get(i).state.equals("a")
					&& (!finalEnrolled.contains(enrolled.get(i)))) {

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
	 * @return list <MainEntities> list of entities that the organizer is
	 *         enrolled in
	 */
	public static List<MainEntity> getEntitiesOfOrganizer(Organization org,
			User user) {
		List<MainEntity> entities = new ArrayList<MainEntity>();

		List<UserRoleInOrganization> userRoleInOrg = UserRoleInOrganization
				.find("byOrganizationAndEnrolled", org, user).fetch();

		for (int i = 0; i < userRoleInOrg.size(); i++) {
			if (userRoleInOrg.get(i).role.roleName.equals("organizer")
					&& (!entities.contains((MainEntity) MainEntity
							.findById(userRoleInOrg.get(i).entityTopicID)))) {
				entities.add((MainEntity) MainEntity.findById(userRoleInOrg
						.get(i).entityTopicID));
			}
		}
		return entities;
	}

	/**
	 * overrides the CRUD create method that is used to create a new user and
	 * make sure that this user is valid, and then it renders a message
	 * mentioning whether the operation was successful or not that is if the
	 * connected user is a system admin otherwise it redirects him to another
	 * page informing him he's not authorized to view the list
	 * 
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @throws TemplateNotFoundException
	 * 
	 */

	public static void create() throws Exception {
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			Constructor<?> constructor = type.entityClass
					.getDeclaredConstructor();
			constructor.setAccessible(true);
			Model object = (Model) constructor.newInstance();
			Binder.bind(object, "object", params.all());
			validation.valid(object);
			String message = "";
			User tmp = (User) object;
			tmp.email = tmp.email.trim().toLowerCase();
			tmp.username = tmp.username.trim().toLowerCase();
			tmp.firstName = tmp.firstName.trim();
			boolean invalidUserFlag = false;
			if (!(User.find("byEmail", tmp.email).fetch().isEmpty())) {
				message = "This Email already exists !";
				invalidUserFlag = true;
			} else if (!(validation.email(tmp.email).ok)) {
				message = "Please enter a valid email address";
				invalidUserFlag = true;
			} else if (!(User.find("byUsername", tmp.username).fetch()
					.isEmpty())) {
				message = "This Username already exists !";
				invalidUserFlag = true;
			} else if (tmp.password.length() < 4) {
				message = "Password cannot be less than 4 characters";
				invalidUserFlag = true;
			}
			if (validation.hasErrors() || invalidUserFlag) {
				if (tmp.email.equals("")) {
					message = "A User must have an email";
				} else if (!(User.find("byEmail", tmp.email).fetch().isEmpty())) {
					message = "This Email already exists !";
				} else if (!(validation.email(tmp.email).ok)) {
					message = "Please enter a valid email address";
				} else if (tmp.username.equals("")) {
					message = "A User must have a username";
				} else if (!(User.find("byUsername", tmp.username).fetch()
						.isEmpty())) {
					message = "This Username already exists !";
				} else if (tmp.password.equals("")) {
					message = "A User must have a password";
				} else if (tmp.username.length() < 3) {
					message = "Username cannot be less than 3 characters";
				} else if (tmp.password.length() < 4) {
					message = "Password cannot be less than 4 characters";
				} else if (tmp.firstName.trim().equals("")) {
					message = "A User must have a first name";
				} else if (tmp.username.length() >= 20) {
					message = "Username cannot exceed 20 characters";
				} else if (tmp.password.length() >= 25) {
					message = "First name cannot exceed 25 characters";
				} else if (tmp.securityQuestion.trim().equals("")) {
					message = "A User must have a security Question";
				} else if (tmp.answer.trim().equals("")) {
					message = "A User must have a security answer";
				}

				try {
					render(request.controller.replace(".", "/") + "/blank.html",
							type, message);
				} catch (TemplateNotFoundException e) {
					render("CRUD/blank.html", type);
				}
			}

			tmp.state = "w";
			tmp.password = Codec.hexMD5(tmp.password);
			tmp.answer=Codec.hexMD5(tmp.answer);
			tmp.activationKey = Application.randomHash(10);
			object._save();
			tmp = (User) object;
			// Mail.welcome(tmp);
			Mail.activation(tmp, tmp.activationKey);
			Log.addUserLog(
					"Admin "
							+ "<a href=\"/Users/viewProfile?userId="
							+ Security.getConnected().id
							+ "\">"
							+ Security.getConnected().username
							+ "</a>"
							+ " "
							+ " has added "
							+ "<a href=\"/Users/viewProfile?userId="
							+ tmp.id + "\">" + tmp.firstName + "</a>"
							+ "'s profile", tmp);
			String message2 = tmp.username + " has been added to users ";
			flash.success(Messages.get("crud.created", type.modelName,
					((User) object).getId()));
			if (params.get("_save") != null) {
				redirect("/Users/show?userId=" + tmp.getId());

			}
			if (params.get("_saveAndAddAnother") != null) {
				redirect(request.controller + ".blank", message2);
			}
			redirect(request.controller + ".view", ((User) object).getId(),
					message2);
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * checks on all the input and make sure that email, username are unique and
	 * in the correct format and then save this changes
	 * 
	 * @author lama Ashraf
	 * 
	 * @story C1S1-2
	 * 
	 * @param id
	 *            long user id
	 * 
	 * @param username
	 *            string new username of the user
	 * 
	 * @param mail
	 *            string new mail of the user
	 * 
	 * @param firstName
	 *            string new first name of the user
	 * 
	 * @param lastName
	 *            string last name of the user
	 * 
	 * @param dateOfBirth
	 *            string the new date of birth of the user
	 * 
	 * @param country
	 *            string the new country of the user
	 * 
	 * @param profession
	 *            string the new profession of the user
	 */

	public static void saveMyProfile(long id, String username, String mail,
			String firstName, String lastName, String dateOfBirth,
			String country, String profession) throws Exception {

		User user = User.findById(id);

		notFoundIfNull(user);

		String usernameOld = user.username;
		String emailOld = user.email;
		emailOld = emailOld.trim().toLowerCase();
		usernameOld = usernameOld.trim().toLowerCase();

		user.username = username;
		user.email = mail;
		user.firstName = firstName;
		user.lastName = lastName;
		user.dateofBirth = dateOfBirth;
		user.country = country;
		user.profession = profession;

		String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "</a>"
				+ " has edited his profile";
		Log.addUserLog(logDescription, user);
		user.save();
		Users.viewProfile(id);

	}

	/**
	 * overrides the CRUD save method ,used to submit the edit, to make sure
	 * that the edits are acceptable, and then it renders a message mentioning
	 * whether the operation was successful or not that is if the connected user
	 * is a system admin otherwise it redirects him to another page informing
	 * him he's not authorized to view the list
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            :String the user's id
	 * 
	 * @throws TemplateNotFoundException
	 */
	public static void save(String id) throws Exception {
		if (Security.getConnected().isAdmin) {
			ObjectType type = ObjectType.get(Users.class);
			notFoundIfNull(type);
			Model object = type.findById(id);
			User oldUser = (User) object;
			String oldEmail = "" + oldUser.email;
			char[] oldemailArray = oldEmail.toCharArray();
			String oldFirstName = "" + oldUser.firstName;
			char[] oldFirstNameArray = oldFirstName.toCharArray();
			String oldLastName = "" + oldUser.lastName;
			char[] oldLastNameArray;
			String oldCountry = "" + oldUser.country;
			char[] oldCountryArray;
			String oldProfession = "" + oldUser.profession;
			char[] oldProfessionArray;
			boolean notEmptyCountry = false;
			boolean notEmptyLastName = false;
			boolean notEmptyProfession = false;

			if (oldCountry.trim() != "") {
				oldCountryArray = oldCountry.toCharArray();
				notEmptyCountry = true;
			} else {
				oldCountryArray = new char[1];
			}
			if (oldLastName != "") {
				oldLastNameArray = oldLastName.toCharArray();
				notEmptyLastName = true;
			} else {
				oldLastNameArray = new char[1];
			}
			if (oldLastName != "") {
				oldProfessionArray = oldProfession.toCharArray();
				notEmptyProfession = true;
			} else {
				oldProfessionArray = new char[1];
			}

			Binder.bind(object, "object", params.all());
			validation.valid(object);
			String editedMessage = "User : " + oldUser.username + "\n";
			String message = "";
			User tmp = (User) object;
			tmp.email = tmp.email.trim();
			tmp.email = tmp.email.toLowerCase();
			tmp.firstName = tmp.firstName.trim();
			boolean validUserFlag = false;
			if ((!oldEmail.equals(tmp.email))
					&& (!(User.find("byEmail", tmp.email).fetch().isEmpty()))) {
				message = "This Email already exists !";
				validUserFlag = true;
			} else if (!(validation.email(tmp.email).ok)) {
				message = "Please enter a valid email address";
				validUserFlag = true;
			}
			if (validation.hasErrors() || validUserFlag) {
				if (tmp.email.equals("")) {
					message = "A User must have an email";
				}
				try {
					render(request.controller.replace(".", "/") + "/view.html",
							object, type, message);

				} catch (TemplateNotFoundException e) {
					render("CRUD/blank.html", type);
				}
			}
			object._save();
			if (!(Arrays.equals(oldemailArray, tmp.email.toCharArray()))) {
				editedMessage += "Email " + oldEmail + " changed to -->  "
						+ tmp.email + "\n";
			}
			if (!(Arrays.equals(oldFirstNameArray, tmp.firstName.toCharArray()))) {
				editedMessage += " First Name " + oldFirstName
						+ " changed to -->  " + tmp.firstName + "\n";
			}
			if (notEmptyLastName) {
				if (tmp.lastName.trim().equals("")) {
					editedMessage += "Last Name " + oldLastName + " removed "
							+ "\n";
				} else {
					if (!(Arrays.equals(oldLastNameArray,
							tmp.lastName.toCharArray()))) {
						editedMessage += "Last Name " + oldLastName
								+ " changed to -->  " + tmp.lastName + "\n";
					}
				}
			} else {
				if (!(tmp.lastName.equals(""))) {
					editedMessage += "Last Name " + oldLastName + " added --> "
							+ tmp.lastName + "\n";
				}
			}
			if (notEmptyProfession) {
				if (tmp.profession.trim().equals("")) {
					editedMessage += "Profession " + oldProfession
							+ " removed " + "\n";
				} else {
					if (!(Arrays.equals(oldProfessionArray,
							tmp.profession.toCharArray()))) {
						editedMessage += "Profession " + oldProfession
								+ " changed to -->  " + tmp.profession + "\n";
					}
				}
			} else {
				if (!(tmp.profession.equals(""))) {
					editedMessage += "Profession " + oldProfession
							+ " added --> " + tmp.profession + "\n";
				}
			}
			if (notEmptyCountry) {
				if (tmp.country.trim().equals("")) {
					editedMessage += "Country " + oldCountry + " removed "
							+ "\n";
				} else {
					if (!(Arrays.equals(oldCountryArray,
							tmp.country.toCharArray()))) {
						editedMessage += "Country " + oldCountry
								+ " changed to -->  " + tmp.country + "\n";
					}
				}
			} else {
				if (!(tmp.country.equals(""))) {
					editedMessage += "Country " + oldCountry + " added --> "
							+ tmp.country + "\n";
				}
			}

			if (Security.getConnected().isAdmin
					&& Security.getConnected().id != tmp.id) {
				Notifications.sendNotification(tmp.id,
						Security.getConnected().id, "User",
						"The admin has edited your profile" + "\n"
								+ editedMessage);
				if(tmp.state.equals("d"))
				{
					Log.addUserLog(
							"Admin "
									+ "<a href=\"/Users/viewProfile?userId="
									+ Security.getConnected().id
									+ "\">"
									+ Security.getConnected().username
									+ "</a>"
									+ " "
									+ " has edited "
									+ tmp.username 
									+ "'s profile" + "\n" + editedMessage, tmp);
				}
				else
				{
					Log.addUserLog(
							"Admin "
									+ "<a href=\"/Users/viewProfile?userId="
									+ Security.getConnected().id
									+ "\">"
									+ Security.getConnected().username
									+ "</a>"
									+ " "
									+ " has edited "
									+ "<a href=\"/Users/viewProfile?userId="
									+ tmp.id + "\">" + tmp.username + "</a>"
									+ "'s profile" + "\n" + editedMessage, tmp);
				}
				
			}
			flash.success(Messages.get("crud.saved", type.modelName));
			if (params.get("_save") != null) {
				if (Security.getConnected().isAdmin) {
					redirect(request.controller + ".list");
				}
			}
			redirect(request.controller + ".show", object._key());

		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * deletes a user after the system admin ( the one who's requesting the
	 * delete) specifies the user's id and the reason for the deletion , then it
	 * sends a mail to the deleted user notifying him of both the event and the
	 * reason if the connected user is a system admin otherwise it redirects him
	 * to another page informing him he's not authorized to view the list
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
		if (Security.getConnected().isAdmin) {
			long userId = Long.parseLong(id);
			User user = User.findById(userId);
			try {

				if (!(user.state.equals("n"))) {
					user.state = "d";
					user._save();
					/*
					 * >>>>> Added by Salma Osama to: delete the
					 * volunteerRequests and the receivedAssignRequests of the
					 * user : remove the user from the list of assignees of the
					 * items he's assigned to
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
					Mail.deletion(user, deletionMessage);
				} else {
				}
				for (int i = 0; i < user.invitation.size(); i++)
					user.invitation.get(i).delete();
				for (int i = 0; i < user.requestsToJoin.size(); i++)
				{
					user.requestsToJoin.get(i).delete();
				}
				Log.addUserLog(
						"Admin "
								+ "<a href=\"/Users/viewProfile?userId="
								+ Security.getConnected().id
								+ "\">"
								+ Security.getConnected().username
								+ "</a>"
								+ " has deleted "
								+ "<a href=\"/Users/viewProfile?userId="
								+ user.id + "\">" + user.username + "</a>",user);
				redirect("/Users/list");
				/**
				 * Added by Ahmed Maged to delete the user's notifications and notification Profile
				 */
				for (int i = 0; i < user.notifications.size(); i++) {
					Notification notification = user.notifications.get(i);
					notification.delete();
				}
				for (int i = 0; i < user.notificationProfiles.size(); i++) {
					NotificationProfile notificationProfile = user.notificationProfiles.get(i);
					notificationProfile.delete();
				}
			} catch (NullPointerException e) {
				render(request.controller.replace(".", "/") + "/index.html");
			}
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * undeletes a user by the system admin, then sends an email to the
	 * undeleted user notifying him of the event that is if the connected user
	 * is a system admin otherwise it redirects him to another page informing
	 * him he's not authorized to view the list
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param id
	 *            :String the user's id
	 * 
	 * 
	 * */
	public static void undelete(String id) {
		if (Security.getConnected().isAdmin) {
			long userId = Long.parseLong(id);
			User user = User.findById(userId);
			String x = "";
			try {

				if (!(user.state.equals("d"))) {
					return;
				}
				user.state = "a";
				user._save();
				Log.addUserLog(
						"Admin "
								+ "<a href=\"/Users/viewProfile?userId="
								+ Security.getConnected().id
								+ "\">"
								+ Security.getConnected().username
								+ "</a>"
								+ " has undeleted "
								+ "<a href=\"/Users/viewProfile?userId="
								+ user.id + "\">" + user.username + "</a>",user);
				Mail.forgiven(user);
				redirect("/Users/list");
			} catch (NullPointerException e) {
				render(request.controller.replace(".", "/") + "/index.html");

			}
		} else {
			BannedUsers.unauthorized();
		}
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
		notFoundIfNull(user);
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

	/**
	 * Deletes the notification of the user
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param notId
	 *            long the notification ID to be deleted
	 * 
	 */

	public static void deleteNotification(long notId) {
		Notification notification = Notification.findById(notId);
		notification.delete();
	}

	/**
	 * Renders the view of the notifications list
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * @param type
	 *            string the type of the notifications to be rendered
	 */

	public static void notificationView(String type) {
		User user = Security.getConnected();
		notFoundIfNull(user);
		List<Notification> notificationList = getNotificationsFrom(type);
		if (type.equals("All")) {
			type = "";
		} else {
			if (type.equals("Organization")) {
				type = "from any Organizations";
			} else {
				if (type.equals("Entity")) {
					type = "from any Entities";
				} else {
					if (type.equals("Topic")) {
						type = "from any Topics";
					} else {
						if (type.equals("Tag")) {
							type = "from any Tags";
						} else {
							if (type.equals("Idea")) {
								type = "from any Ideas";
							} else {
								if (type.equals("User")) {
									type = "from any Users";
								} else {
									if (type.equals("Plan")) {
										type = "from any Plans";
									}
								}
							}
						}
					}
				}
			}
		}
		render(user, notificationList, type);
	}

	/**
	 * returns a list of notifications according to the type
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * @param type
	 *            String the type of the notifications to be selected
	 * 
	 * @return List<Notification> the list of notifications to be rendered
	 */

	public static List<Notification> getNotificationsFrom(String type) {
		User user = Security.getConnected();
		List<Notification> notificationList = new ArrayList<Notification>();
		if (type.equalsIgnoreCase("All")) {
			return user.notifications;
		}
		for (int i = 0; i < user.notifications.size(); i++) {
			if (user.notifications.get(i).type.equalsIgnoreCase(type)) {
				notificationList.add(user.notifications.get(i));
			}
		}
		return notificationList;
	}

	/**
	 * Renders the view of the notification profiles according to the type
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * @param type
	 *            String the type of notifications to be rendered
	 */

	public static void notificationProfileView(String type) {
		User user = Security.getConnected();
		notFoundIfNull(user);
		List<NotificationProfile> notificationProfileList = getNotificationProfilesOf(type);
		String select = "";
		if (type.equals("All")) {
			type = "";
			select = "All";
		} else {
			if (type.equals("Organization")) {
				type = "from any Organizations";
				select = "Organization";
			} else {
				if (type.equals("Entity")) {
					type = "from any Entities";
					select = "Entity";
				} else {
					if (type.equals("Topic")) {
						type = "from any Topics";
						select = "Topic";
					} else {
						if (type.equals("Tag")) {
							type = "from any Tags";
							select = "Tag";
						} else {
							if (type.equals("Idea")) {
								type = "from any Ideas";
								select = "Idea";
							} else {
								if (type.equals("User")) {
									type = "from any Users";
									select = "User";
								} else {
									if (type.equals("Plan")) {
										type = "from any Plans";
										select = "Plan";
									}
								}
							}
						}
					}
				}
			}
		}
		render(user, notificationProfileList, type, select);
	}

	/**
	 * returns a list of notification profiles according to the type
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S20
	 * 
	 * @param type
	 *            String the type of the notification profiles to be selected
	 * 
	 * @return List<NotificationProfile> the list of notification profiles to be
	 *         rendered
	 */

	public static List<NotificationProfile> getNotificationProfilesOf(
			String type) {
		User user = Security.getConnected();
		List<NotificationProfile> notificationProfileList = new ArrayList<NotificationProfile>();
		if (type.equalsIgnoreCase("All")) {
			return user.notificationProfiles;
		}
		for (int i = 0; i < user.notificationProfiles.size(); i++) {
			if (user.notificationProfiles.get(i).notifiableType
					.equalsIgnoreCase(type)) {
				notificationProfileList.add(user.notificationProfiles.get(i));
			}
		}
		return notificationProfileList;
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
		User user = Security.getConnected();
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
	public static void deactivate(String reason) {
		User user = Security.getConnected();
		user.state = "n";
		user.save();
		List<User> admins = User.findAll();
		for (int i = 0; i < admins.size(); i++) {
			if (admins.get(i).isAdmin) {
				Notifications.sendNotification(admins.get(i).id, user.id,
						"user", user.firstName + " " + user.lastName
								+ " has deactivated his account, reason: "
								+ "' " + reason + " '");
			}
		}
		Mail.deactivate();
		logout();
	}

	/**
	 * changes the password of the user after providing his old password and
	 * some validations
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S1-2
	 * 
	 * @param pass
	 *            2 String the new password
	 * 
	 */

	public static void changePassword(@Recaptcha String recaptcha, String pass2) {

		String message = "";

		if (validation.hasErrors()) {
			validation.keep();
			viewProfile(Security.getConnected().id, 1);
		} else {
			if (pass2.length() < 25 || pass2.length() > 3) {
				User user = Security.getConnected();
				user.password = Codec.hexMD5(pass2);
				user.save();
				viewProfile(user.id);
			} else {
				message = "Password cannot exceed 25 characters";
				render(request.controller.replace(".", "/") + "/viewProfile.html",
						message);
			}
		}

	}
	public static void feedbackMail(String feedbackerEmail,String feedback,String browser,String subject){
		Mail.sendFeedback(feedbackerEmail, feedback, browser, subject);
		Notifications.sendNotification(2, 1, "Organization",feedback+" "+feedbackerEmail);
		Notifications.sendNotification(3, 1, "Organization",feedback+" "+feedbackerEmail);
	}

}