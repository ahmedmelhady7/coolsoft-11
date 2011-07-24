package controllers;

import models.Comment;
import models.Idea;
import models.Item;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;
import models.User;
import models.VolunteerRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.Router;
import play.mvc.With;
import controllers.CoolCRUD.ObjectType;
import controllers.Tags;

@With(Secure.class)
public class Plans extends CoolCRUD {

	/**
	 * This method renders the page for viewing the plan as a to-do list.
	 * 
	 * @story C5S7
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param planId
	 *            ID of the plan to be viewed as a to-do list.
	 */
	public static void viewAsList(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		List<Comment> comments = plan.commentsList;
		List<Item> itemsList = plan.items;
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		boolean canView = true;
		boolean isOrganizer = false;
		boolean canDelete = false;
		String listOfTags = "";
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();

		boolean notBlockedFromUsing = Users.isPermitted(user, "use",
				plan.topic.id, "topic");
		boolean checkNotRated;
		List<User> allUsers = User.findAll();
		List<String> userNames = new ArrayList<String>();
		String s;
		for (int i = 0; i < allUsers.size(); i++) {
			s = allUsers.get(i).username;
			userNames.add(s);
		}
		Collections.sort(userNames);
		userNames.remove(user.username);
		if (plan.usersRated.contains(user))
			checkNotRated = false;
		else
			checkNotRated = true;

		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}

			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}

			for (int i = 0; i < globalListOfTags.size(); i++) {
				if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
						|| plan.topic.entity.organization
								.equals(globalListOfTags.get(i).createdInOrganization)) {
					listOfTags += globalListOfTags.get(i) + "|";
				}
			}

			// faruki
			List<MainEntity> entitiesList = new ArrayList<MainEntity>();
			int iii = 1;
			while (iii < plan.topic.entity.organization.entitiesList.size()) {
				entitiesList.add(plan.topic.entity.organization.entitiesList
						.get(iii));
				iii++;
			}
			// faruki
			render(plan, itemsList, user, canAssign, canEdit, canView,
					canDelete, isOrganizer, canIdea, comments, entitiesList,
					listOfTags, notBlockedFromUsing, userNames);
		} else {
			canView = false;
			// render(p, itemsList, user, canAssign, canEdit, canView,
			// canDelete,
			// isOrganizer, canIdea, userNames);
			BannedUsers.unauthorized();
		}

	}

	/**
	 * This Method directly assigns the logged in user to the item, notify the
	 * other users assigned to the item and the topic organizers and add to the
	 * logs of the user, item, item's plan, entity, organization given the item
	 * id
	 * 
	 * @story C5S10
	 * 
	 * @author Salma Osama
	 * 
	 * @param itemId
	 *            The ID of the item that the user will be assigned to
	 */
	public static void workOnItem(long itemId) {
		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		user.itemsAssigned.add(item);
		user.save();
		item.assignees.add(user);
		item.save();
		List<User> otherUsers = new ArrayList<User>();
		otherUsers.addAll(item.getAssignees());
		for (User user1 : item.plan.topic.getOrganizer()) {
			if (!otherUsers.contains(user1)) {
				otherUsers.add(user1);
			}
		}
		User userToBeNotified;
		String notificationMsg = "User " + user.username
				+ " is now working on the item " + item.summary
				+ " in the plan " + item.plan.title;
		for (int i = 0; i < otherUsers.size(); i++) {
			userToBeNotified = otherUsers.get(i);
			if (userToBeNotified.id.compareTo(user.id) != 0) {
				Notifications.sendNotification(userToBeNotified.id,
						item.plan.id, "plan", notificationMsg);
			}
		}
		String logDescription = "User "
				+ "<a href=\"/users/viewprofile?userId=" + user.id + "\">"
				+ user.username + "</a>" + " is now working on the item "
				+ item.summary + " in the plan "
				+ "<a href=\"/plans/viewaslist?planId=" + item.plan.id + "\">"
				+ item.plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + item.plan.topic.id
				+ "\">" + item.plan.topic.title + "</a>";

		Log.addUserLog(logDescription, user, item, item.plan, item.plan.topic,
				item.plan.topic.entity, item.plan.topic.entity.organization);

	}

	/**
	 * This Method renders the page addIdea, after checking the permissions
	 * given to the user, where the user selects the topic ideas that will be
	 * promoted to execution in the plan given the plan id
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            The id of the plan that the ideas will be associated to
	 */
	public static void addIdea(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		Topic topic = Topic.findById(plan.topic.id);
		notFoundIfNull(topic);

		List<Idea> ideas = new ArrayList<Idea>();
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		boolean canView = false;
		boolean isOrganizer = false;
		boolean canDelete = false;

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
				if (Users
						.isPermitted(
								user,
								"accept/Reject user request to volunteer to work on action item in a plan",
								plan.topic.id, "topic")) {
					isOrganizer = true;
				}
				if (Users.isPermitted(user, "edit an action plan",
						plan.topic.id, "topic")) {

					canEdit = 1;
				}

				if (Users.isPermitted(user, "delete an action plan",
						plan.topic.id, "topic")) {

					canDelete = true;
				}
				if (Users.isPermitted(user,
						"assign one or many users to a to-do item in a plan",
						plan.topic.id, "topic")) {

					canAssign = 1;
				}
				for (Idea idea : topic.ideas) {
					if (idea.plan == null) {
						ideas.add(idea);
					}
				}
				render(ideas, topic, plan, user, canEdit, canView, isOrganizer,
						canIdea, canDelete, canAssign);
			} else {
				BannedUsers.unauthorized();
			}
		}
	}

	/**
	 * This Method associates the list of selected ideas to the plan, sends
	 * notifications to the authors of the selected ideas , add the logs,
	 * increments the community contribution counter of the authors of these
	 * plans and then calls the planView method that renders the list of ideas
	 * promoted to execution
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param checkedIdeas
	 *            The list of idea ids selected to be associated to the plan
	 * @param planId
	 *            The id of the plan that the selected ideas will be associated
	 *            to
	 */
	public static void selectedIdeas(long[] checkedIdeas, long planId) {
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		Idea idea;
		String notificationContent = "";

		for (int i = 0; i < checkedIdeas.length; i++) {
			idea = Idea.findById(checkedIdeas[i]);
			idea.plan = plan;
			plan.ideas.add(idea);
			idea.save();
			idea.author.communityContributionCounter = idea.author.communityContributionCounter + 13;
			idea.author.save();
			plan.save();
			notificationContent = "Congratulations!! your idea: " + idea.title
					+ "have been promoted to execution in the following plan "
					+ plan.title + " of the topic: " + plan.topic.title;
			Notifications.sendNotification(idea.author.id, plan.id, "plan",
					notificationContent);
			String logDescription = "Idea " + "<a href=\"/topics/show?ideaId="
					+ idea.id + "\">" + idea.title + "</a>"
					+ " that belongs to "
					+ "<a href=\"/users/viewprofile?userId=" + idea.author.id
					+ "\">" + idea.author.username + "</a>"
					+ " has been promoted to exection in the plan "
					+ "<a href=\"/plans/viewaslist?planId=" + plan.id + "\">"
					+ plan.title + "</a>" + " of the topic "
					+ "<a href=\"/topics/show?topicId=" + plan.topic.id + "\">"
					+ plan.topic.title + "</a>";

			Log.addUserLog(logDescription, idea.author, plan, idea, plan.topic,
					plan.topic.entity, plan.topic.entity.organization);

		}

		planView(planId);

	}

	/**
	 * This method renders the view containing the list of ideas associated to
	 * the plan after checking if the user is allowed to view the plan given the
	 * plan id
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            the id of the plan whose list of ideas will be viewed
	 */
	public static void planView(long planId) {
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);

		User user = Security.getConnected();
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		boolean canView = false;
		boolean isOrganizer = false;
		boolean canDelete = false;

		plan.incrmentViewed();
		plan.save();

		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}

			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}
			List<Idea> ideas = plan.ideas;

			render(ideas, plan, user, canEdit, canView, isOrganizer, canIdea,
					canDelete, canAssign);
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * This method removes an idea from the the list of ideas associated to the
	 * plan, sends notifications to the idea author, decrements its community
	 * contribution counter and add the logs given the idea id and the plan id
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param ideaId
	 *            the id of the idea to be removed from the list of ideas of the
	 *            plan
	 * @param planId
	 *            the id of the plan that the idea will be removed from
	 */
	public static void unassociateIdea(long ideaId, long planId) {
		Idea idea = Idea.findById(ideaId);
		notFoundIfNull(idea);
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);

		idea.plan = null;
		idea.author.communityContributionCounter = idea.author.communityContributionCounter - 13;
		idea.author.save();
		plan.ideas.remove(idea);
		idea.save();
		plan.save();
		String notificationMsg = "Sorry!! your idea " + idea.title
				+ " has been removed from the following plan" + plan.title
				+ " of the topic: " + plan.topic.title;
		Notifications.sendNotification(idea.author.id, plan.id, "plan",
				notificationMsg);

		String logDescription = "Idea " + "<a href=\"/topics/show?ideaId="
				+ idea.id + "\">" + idea.title + "</a>" + " that belongs to "
				+ "<a href=\"/users/viewprofile?userId=" + idea.author.id
				+ "\">" + idea.author.username + "</a>"
				+ " has been removed from the plan "
				+ "<a href=\"/plans/viewaslist?planId=" + plan.id + "\">"
				+ plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + plan.topic.id + "\">"
				+ plan.topic.title + "</a>";

		Log.addUserLog(logDescription, idea.author, plan, idea, plan.topic,
				plan.topic.entity, plan.topic.entity.organization);

	}

	/**
	 * This method returns the list of plans that belongs to a certain
	 * organization or entity which is determined by the parameter type give the
	 * id of the organization or entity
	 * 
	 * @story C5S18
	 * 
	 * @author Salma Osama
	 * 
	 * @param type
	 *            the type of the place from which the method gets the list of
	 *            plans it could be either organization or entity
	 * @param id
	 *            the id of the organization or entity that the methods returns
	 *            the list of plans that belongs to it
	 * 
	 * @return List<Plan>
	 */
	public static List<Plan> planList(String type, long id) {
		User user = Security.getConnected();
		List<Plan> plans = new ArrayList<Plan>();
		if (type == "entity") {
			MainEntity entity = MainEntity.findById(id);
			notFoundIfNull(entity);
			List<Topic> topics = entity.topicList;
			for (int i = 0; i < topics.size(); i++) {
				if (topics.get(i).plan != null
						&& Users.isPermitted(user, "view", topics.get(i).id,
								"topic")) {
					plans.add(topics.get(i).plan);
				}
			}
		} else {
			Organization organization = Organization.findById(id);
			notFoundIfNull(organization);
			List<MainEntity> entities = organization.entitiesList;
			MainEntity entity;
			for (int i = 0; i < entities.size(); i++) {
				entity = entities.get(i);
				List<Topic> topics = entity.topicList;
				for (int j = 0; j < topics.size(); j++) {
					if (topics.get(j).plan != null
							&& Users.isPermitted(user, "view",
									topics.get(j).id, "topic")) {
						plans.add(topics.get(j).plan);
					}
				}
			}
		}
		return (plans);
	}

	/**
	 * 
	 * This Method creates an instance of VolunteerRequest if the user is
	 * allowed to volunteer and adds it to the list of sent volunteer requests
	 * of the user and to the list of volunteer requests in the item given the
	 * the item id and the required justification string
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param itemId
	 *            : the id of the item the user wishes to volunteer to work on
	 * @param justification
	 *            : the reason why the user would like to volunteer in this item
	 */
	public static void sendVolunteerRequest(long itemId, String justification) {
		User sender = Security.getConnected();
		Item destination = Item.findById(itemId);
		notFoundIfNull(destination);
		if (sender.canVolunteer(itemId)) {
			if (!(destination.status == 2) && !destination.endDatePassed()) {
				VolunteerRequest volunteerRequest = new VolunteerRequest(
						sender, destination, justification).save();
				destination.addVolunteerRequest(volunteerRequest);
				destination.save();
				sender.addVolunteerRequest(volunteerRequest);
				sender.save();
				String description = sender.username
						+ " has requested to volunteer to work on the following item "
						+ destination.summary + "in the plan "
						+ destination.plan.title + "of the topic"
						+ destination.plan.topic.title;
				List<User> notificationDestination = destination.plan.topic
						.getOrganizer();
				for (int i = 0; i < notificationDestination.size(); i++) {
					Notifications.sendNotification(
							notificationDestination.get(i).id,
							destination.plan.id, "plan", description);
				}

				String logDescription = "<a href=\"/users/viewprofile?userId="
						+ sender.id + "\">" + sender.username + "</a>"
						+ " is requesting to volunteer to work on the item "
						+ destination.summary + " in the plan "
						+ "<a href=\"/plans/viewaslist?planId="
						+ destination.plan.id + "\">" + destination.plan.title
						+ "</a>" + " of the topic "
						+ "<a href=\"/topics/show?topicId="
						+ destination.plan.topic.id + "\">"
						+ destination.plan.topic.title + "</a>";

				Log.addUserLog(logDescription, sender, destination.plan,
						destination.plan.topic.entity, destination,
						destination.plan.topic.entity.organization,
						destination.plan.topic);
				// Plans.viewAsList(dest.plan.id);
				// justify(itemId, dest.plan.id, 2);
			}
		} else {
			// justify(itemId, dest.plan.id, 1);
		}
	}

	/**
	 * 
	 * This Method deletes a volunteer request to work on an item that has been
	 * sent by the user given the item id
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param itemId
	 *            : the id of the item the user wishes to volunteer to work on
	 */
	public static void cancelVolunteerRequest(long itemId) {
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		User user = Security.getConnected();
		for (VolunteerRequest volunteerRequest : user.volunteerRequests) {
			if (volunteerRequest.destination.id == itemId) {
				volunteerRequest.delete();
				String description = user.username
						+ " has cancelled his/her volunteer request to work on the following item "
						+ item.summary + "in the plan " + item.plan.title
						+ "of the topic" + item.plan.topic.title;
				List<User> notificationDestination = item.plan.topic
						.getOrganizer();
				for (int i = 0; i < notificationDestination.size(); i++) {
					Notifications.sendNotification(
							notificationDestination.get(i).id, item.plan.id,
							"plan", description);
				}
				String logDescription = "<a href=\"/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ "</a>"
						+ " canceled his/her volunteer request to work on the item "
						+ item.summary + " in the plan "
						+ "<a href=\"/plans/viewaslist?planId=" + item.plan.id
						+ "\">" + item.plan.title + "</a>" + " of the topic "
						+ "<a href=\"/topics/show?topicId="
						+ item.plan.topic.id + "\">" + item.plan.topic.title
						+ "</a>";

				Log.addUserLog(logDescription, user, item.plan, item,
						item.plan.topic.entity, item.plan.topic,
						item.plan.topic.entity.organization);
			}
		}
	}

	/**
	 * This Method renders the page for the plan creation
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param topicId
	 *            The ID of the topic that this action plan is based upon
	 * 
	 */
	public static void planCreate(long topicId) {
		User user = Security.getConnected();
		render(topicId, user);
	}

	/**
	 * This method generates the average of the user-entered rating and the
	 * rating of the specified plan
	 * 
	 * @author Ibrahim Safwat
	 * 
	 * @story C4S09
	 * 
	 * @param planId
	 *            : the id of the plan the user wants to rate
	 * 
	 * @param rating
	 *            : user-entered rating
	 * 
	 * @return : void
	 */

	public static void rate(long planId, int rating) {
		Plan planInUse = Plan.findById(planId);
		User userLoggedIn = Security.getConnected();

		planInUse.usersRated.add(userLoggedIn);
		if (planInUse.rating.equals("Not yet rated"))
			planInUse.rating = Integer.toString(rating);
		else {
			int oldRating = Integer.parseInt(planInUse.rating);
			int newRating;
			newRating = (oldRating + rating) / 2;
			planInUse.rating = Integer.toString(newRating);
		}
		planInUse.save();
		redirect("/plans/viewaslist?planId=" + planId);

	}

	/**
	 * This method takes the parameters from the web page of the plan creation
	 * to instantiate a plan object, it also sends a notification to the
	 * organizers of the topic, updates the log and then calls a method to view
	 * the plan as a list.
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param title
	 *            The title of the plan
	 * @param startDate
	 *            The date when the plan will start
	 * @param endDate
	 *            The date when the plan will end
	 * @param description
	 *            The description of the plan
	 * @param topicId
	 *            The id of the topic which this plan is based upon
	 * @param requirement
	 *            The requirements needed for executing this plan
	 */

	public static void myCreate(String title, String startDate, String endDate,
			String description, long topicId, String requirement) {

		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Date sd = new Date(startDate);
		Date ed = new Date(endDate);
		Plan plan = new Plan(title, user, sd, ed, description, topic,
				requirement);
		plan.save();
		List<User> topicOrganizers = plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, plan.id,
					"plan", "A new plan: " + plan.title
							+ " has been created in topic: " + topic.title);
		}
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " created the plan " + "<a href =\"/plans/viewaslist?planId="
				+ plan.id + "\">" + plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + topic.id + "\">"
				+ topic.title + "</a>";
		Log.addUserLog(logDescription, user, plan, plan.topic.entity,
				plan.topic.entity.organization);
		viewAsList(plan.id);

	}

	/**
	 * This method allows users to share plans through notifications
	 * 
	 * @author Ibrahim Safwat
	 * 
	 * @story C4S08
	 * 
	 * @param userName
	 *            : username of the user that the notification will be sent to
	 * 
	 * @param planId
	 *            : the ID of the plan that the notification will be sent from
	 * 
	 * @return void
	 */
	public static void sharePlan(String userName, long planId) {
		User userChosen = User.find("byUsername", userName).first();
		User userLoggedIn = Security.getConnected();
		String desc = userLoggedIn.firstName + " " + userLoggedIn.lastName
				+ " shared a plan with you";
		Plan plan = Plan.findById(planId);
		if (userChosen.state.equals("a")) {
			Notifications.sendNotification(userChosen.id, planId, "Plan", desc);
			String logDescription = "<a href=\"/users/viewprofile?userId="
					+ Security.getConnected().id + "\">"
					+ Security.getConnected().username + "</a>"
					+ " shared a plan  "
					+ "<a href=\"/plans/viewaslist?planId=" + planId + "\">"
					+ plan.title + "</a>" + " with "
					+ "<a href=\"/users/viewprofile?userId=" + userChosen.id
					+ "\">" + userChosen.username + "</a>";

			Log.addUserLog(logDescription, Security.getConnected(), userChosen,
					plan.topic, plan.topic.entity,
					plan.topic.entity.organization);

		}
	}

	/**
	 * This Method renders the page for adding items, it checks if for user
	 * permissions before taking the user to the designated page.
	 * 
	 * @story C5S16
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param planId
	 *            The ID of the plan where the items will be added
	 * 
	 */

	public static void addItem(long planId) {
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		User user = Security.getConnected();
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		boolean canView = false;
		boolean isOrganizer = false;
		boolean canDelete = false;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}

			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}
		}
		if (canView) {
			render(plan, user, canEdit, canView, isOrganizer, canIdea,
					canAssign, canDelete);
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * This method takes the parameters of the item from the web page to
	 * instantiate an item and add it to the list of items of a plan, it also
	 * sends users notifications and updates the log.
	 * 
	 * @story C5S16
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDate
	 *            The date on which the item start
	 * @param endDate
	 *            The date on which the item ends
	 * @param descriprtion
	 *            The description of the item
	 * @param planId
	 *            The id of the plan that contains this item in its items list
	 * @param summary
	 *            The summary of the description of the item (for vewing
	 *            purposes)
	 * @param check
	 *            The checkbox value that checks whether the user wants to add
	 *            another item or not
	 */
	public static void add(String startDate, String endDate,
			String description, long planId, String summary, String check) {
		User user = Security.getConnected();
		Date sd = new Date(startDate);
		Date ed = new Date(endDate);
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		plan.addItem(sd, ed, description, summary);
		List<User> topicOrganizers = plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, plan.id,
					"plan", "A new plan: " + plan.title
							+ "has been created in the topic: "
							+ plan.topic.title);
		}
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " added an item in  "
				+ "<a href =\"/plans/viewaslist?planId=" + plan.id + "\">"
				+ plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + plan.topic.id + "\">"
				+ plan.topic.title + "</a>";
		Log.addUserLog(logDescription, user, plan, plan.topic.entity,
				plan.topic.entity.organization);
		if (check != null && check.equals("checked")) {
			addItem(plan.id);
		} else {
			viewAsList(plan.id);
		}
	}

	/**
	 * This method renders the page for editing a plan, it checks if the user
	 * can edit the plan or not.
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param planId
	 *            The ID of the plan that will be edited
	 * 
	 */
	public static void editPlan(long planId) {
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		User user = Security.getConnected();
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		boolean canView = false;
		boolean isOrganizer = false;
		boolean canDelete = false;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}

			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}
		}
		if (canView == true) {
			render(plan, user, canEdit, canView, isOrganizer, canIdea,
					canDelete, canAssign);
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * This method renders the page for editing an item, it checks if the user
	 * can edit the item or not, before taking the user to the page.
	 * 
	 * @story C5S16
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param itemId
	 *            The ID of the item that will be edited
	 * 
	 */

	public static void editItem(long itemId) {
		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		boolean canView = false;
		boolean isOrganizer = false;
		int canEdit = 0;
		int canIdea = 0;
		int canAssign = 0;
		boolean canDelete = false;
		Plan plan = item.plan;

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;

			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
				if (Users
						.isPermitted(
								user,
								"accept/Reject user request to volunteer to work on action item in a plan",
								plan.topic.id, "topic")) {
					isOrganizer = true;
				}

				if (Users.isPermitted(user, "delete an action plan",
						plan.topic.id, "topic")) {

					canDelete = true;
				}
				if (Users.isPermitted(user,
						"assign one or many users to a to-do item in a plan",
						plan.topic.id, "topic")) {

					canAssign = 1;
				}
				if (Users
						.isPermitted(
								user,
								"associate an idea or more to an already existing plan",
								plan.topic.id, "topic")) {

					canIdea = 1;
				}

				render(plan, item, user, canEdit, canView, isOrganizer,
						canIdea, canDelete, canAssign);

			} else {
				BannedUsers.unauthorized();

			}
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * This method takes the parameters from the web page of the plan editing to
	 * edit a certain plan, it sends notifications to the organizers of the
	 * topic, updates the log and then goes to the plan of viewing a plan
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param title
	 *            The title of the plan
	 * @param startDate
	 *            The date on which the plan starts
	 * @param endDate
	 *            The date on which the plan ends
	 * @param description
	 *            The description of the plan
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param planId
	 *            The id of the plan being edit
	 */
	public static void edit(String title, String startDate, String endDate,
			String description, String requirement, long planId) {
		User user = Security.getConnected();
		Date sd = new Date(startDate);
		Date ed = new Date(endDate);
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		plan.title = title;
		plan.startDate = sd;
		plan.endDate = ed;
		plan.description = description;
		plan.requirement = requirement;
		plan.save();

		List<User> topicOrganizers = plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, plan.id,
					"plan", "The action plan: " + plan.title
							+ " has been edited" + "in the topic: "
							+ plan.topic.title);
		}
		List<User> assignees = new ArrayList<User>();
		for (int i = 0; i < plan.items.size(); i++) {
			assignees = plan.items.get(i).assignees;
			for (int j = 0; j < assignees.size(); j++) {
				Notifications.sendNotification(assignees.get(j).id, plan.id,
						"plan", "This action plan has been edited");
			}
		}
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " edited the plan " + "<a href =\"/plans/viewaslist?planId="
				+ plan.id + "\">" + plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + plan.topic.id + "\">"
				+ plan.topic.title + "</a>";
		Log.addUserLog(logDescription, user, plan, plan.topic.entity,
				plan.topic.entity.organization);
		viewAsList(plan.id);
	}

	/**
	 * This method takes the parameters from the web page of the item editing to
	 * edit in a certain item, it also sends notifications to the users working
	 * on the item and the organizer that the plan has been edited, and then
	 * updates the log.
	 * 
	 * @story C5S16
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDate
	 *            The date on which the item starts
	 * @param endDate
	 *            The date on which the item ends
	 * @param description
	 *            The description of the item
	 * @param planId
	 *            The id of the plan that will be viewed
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param itemId
	 *            The id of the item being edit
	 */

	public static void edit2(String startDate, String endDate,
			String description, long planId, String summary, long itemId) {
		User user = Security.getConnected();
		Date sd = new Date(startDate);
		Date ed = new Date(endDate);
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		item.startDate = sd;
		item.endDate = ed;
		item.description = description;
		item.summary = summary;
		item.save();

		List<User> topicOrganizers = item.plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id,
					item.plan.id, "plan", "The item: " + item.summary
							+ " has been edited in plan: " + item.plan.title);
		}

		List<User> assignees = item.assignees;
		for (int j = 0; j < assignees.size(); j++) {
			Notifications.sendNotification(assignees.get(j).id, item.plan.id,
					"plan", "The item: " + item.summary
							+ " has been edited in plan: " + item.plan.title);
		}
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " edited an item in  "
				+ "<a href =\"/plans/viewaslist?planId=" + item.plan.id + "\">"
				+ item.plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + item.plan.topic.id
				+ "\">" + item.plan.topic.title + "</a>";
		Log.addUserLog(logDescription, user, item.plan, item.plan.topic.entity,
				item.plan.topic.entity.organization);
		viewAsList(item.plan.id);
	}

	/**
	 * This methods deletes an item, removes it from the item list of the plan,
	 * delete all its volunteer requests and assign requests and add to the logs
	 * of the plan, plan's topic, entity and organization given the item id and
	 * the variable that to indicate whether to notify or not
	 * 
	 * @story C5S19
	 * 
	 * @author Salma Osama
	 * 
	 * @param itemId
	 *            The id of the item being deleted
	 * @param notify
	 *            variable to indicate whether to send notifications to the
	 *            assigned users and topic organizers or not
	 * 
	 * @return boolean
	 */
	public static boolean deleteItem(long itemId, int notify) {
		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		Plan plan = Plan.findById(item.plan.id);
		plan.items.remove(item);
		plan.save();
		for (int i = 0; i < item.volunteerRequests.size(); i++) {
			item.volunteerRequests.get(i).delete();
		}
		for (int i = 0; i < item.assignRequests.size(); i++) {
			item.assignRequests.get(i).delete();
		}
		List<Item> items;
		User userAssigned;
		String notificationMsg;
		for (int i = 0; i < item.assignees.size(); i++) {
			userAssigned = item.assignees.get(i);
			items = userAssigned.itemsAssigned;
			items.remove(item);
			userAssigned.save();
			if (notify == 1) {
				notificationMsg = "The item " + item.summary
						+ " has been deleted from the plan";
				Notifications.sendNotification(userAssigned.id, plan.id,
						"plan", notificationMsg);
			}
		}
		Tag tag;
		for (int i = 0; i < item.tags.size(); i++) {
			tag = item.tags.get(i);
			items = tag.taggedItems;
			items.remove(item);
			tag.save();
		}
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " deleted the item " + item.summary + " in the plan "
				+ "<a href=\"/plans/viewaslist?planId=" + plan.id + "\">"
				+ plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + plan.topic.id + "\">"
				+ plan.topic.title + "</a>";

		Log.addUserLog(logDescription, user, plan, plan.topic.entity,
				plan.topic, plan.topic.entity.organization);
		item.logs.clear();
		item.delete();

		return true;
	}

	/**
	 * This methods deletes a plan with all its items and comments of the plan,
	 * sends notifications to all the users working on items in the plan and the
	 * plan's topic organizers and add to the logs of the plan's topic, entity
	 * and organization given the plan id
	 * 
	 * @story C5S2
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            The id of the plan that will be deleted
	 * 
	 * @return boolean
	 */
	public static boolean deletePlan(long planId) {
		User user = Security.getConnected();
		List<User> toBeNotified = new ArrayList<User>();
		Plan plan = Plan.findById(planId);
		String notificationMsg = "The plan " + plan.title + " has been deleted";
		for (Item item : plan.items) {
			for (User userAssigned : item.getAssignees()) {
				if (!toBeNotified.contains(userAssigned)
						&& user.id != userAssigned.id) {
					toBeNotified.add(userAssigned);
					Notifications.sendNotification(userAssigned.id,
							plan.topic.id, "topic", notificationMsg);
				}
			}
		}
		for (User organizer : plan.topic.getOrganizer()) {
			if (!toBeNotified.contains(organizer) && user.id != organizer.id) {
				toBeNotified.add(organizer);
				Notifications.sendNotification(organizer.id, plan.topic.id,
						"topic", notificationMsg);
			}
		}

		while (!plan.items.isEmpty()) {
			deleteItem(plan.items.get(0).id, 0);
			plan.save();
		}

		Idea idea;
		while (!plan.ideas.isEmpty()) {
			idea = plan.ideas.remove(0);
			idea.plan = null;
			idea.save();
			plan.save();
		}
		Comment comment;
		while (!plan.commentsList.isEmpty()) {
			comment = plan.commentsList.remove(0);
			plan.save();
			comment.commentedPlan = null;
			comment.save();
		}
		User userRated;
		while (!plan.usersRated.isEmpty()) {
			userRated = plan.usersRated.remove(0);
			userRated.ratedPlans.remove(plan);
			userRated.save();
			plan.save();

		}
		User creator = plan.madeBy;
		creator.planscreated.remove(plan);

		creator.save();
		plan.madeBy = null;
		plan.save();

		Topic topic = plan.topic;
		topic.plan = null;
		topic.save();
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>"
				+ " deleted the plan " + plan.title + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + topic.id + "\">"
				+ topic.title + "</a>";

		Log.addUserLog(logDescription, user, topic, topic.entity,
				topic.entity.organization);
		plan.logs.clear();
		plan.save();
		plan.delete();
		return true;
	}

	/**
	 * Ovverides CRUD's delete and deletes a plan
	 * 
	 * @author Alia El Bolock
	 * 
	 * @param id
	 *            : the id of the plan to be deleted
	 * 
	 */
	public static void delete(String id) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		long planId = Long.parseLong(id);
		try {
			deletePlan(planId);
		} catch (Exception e) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		redirect(request.controller + ".list");
	}

	/**
	 * The method Renders the Timeline view of a plan, after checking that the
	 * user is permitted to view the plan.
	 * 
	 * @author Alaa Samer
	 * 
	 * @story C5S8
	 * 
	 * @param planId
	 *            The id of the plan needed to be viewed
	 * 
	 * 
	 * @throws java.text.ParseException
	 */
	public static void viewasTimeline(long planId) throws IOException,
			ParseException, java.text.ParseException {

		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);
		List<Item> itemsList = plan.items;

		boolean canView = false;
		boolean isOrganizer = false;
		int canEdit = 0;
		int canIdea = 0;
		int canAssign = 0;
		boolean canDelete = false;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}
			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}

			FileWriter fstream;
			fstream = new FileWriter("public/xml/out.xml");
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.write("\n");
			out.write("<data>");
			out.write("\n");

			out.write("<event start=\"");
			// out.write(itemsList.get(i).startDate.toGMTString());
			String inputDate = (plan.startDate.toGMTString());
			Date date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz")
					.parse(inputDate);

			out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z")
					.format(date));

			out.write("\"");

			out.write(" ");
			out.write("title=\"");
			out.write("START");
			out.write("\"");
			out.write(" ");
			out.write("icon=\"/public/rer7.png\"");
			out.write(">");
			out.write(plan.description);
			out.write("</event>");
			out.write("\n");

			out.write("<event start=\"");
			// out.write(itemsList.get(i).startDate.toGMTString());
			inputDate = (plan.endDate.toGMTString());
			date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz")
					.parse(inputDate);

			out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z")
					.format(date));

			out.write("\"");
			out.write(" ");

			out.write("title=\"");
			out.write("END");
			out.write("\"");
			out.write(" ");
			out.write("icon=\"/public/rer7.png\"");
			out.write(">");

			out.write("</event>");
			out.write("\n");

			for (int i = 0; i < itemsList.size(); i++) {
				out.write("<event start=\"");
				// out.write(itemsList.get(i).startDate.toGMTString());
				inputDate = (itemsList.get(i).startDate.toGMTString());
				date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz")
						.parse(inputDate);

				out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z")
						.format(date));

				out.write("\"");
				out.write(" ");
				out.write("end=\"");

				// out.write(itemsList.get(i).endDate.toGMTString());
				inputDate = (itemsList.get(i).endDate.toGMTString());
				date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz")
						.parse(inputDate);
				out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z")
						.format(date));

				out.write("\"");
				out.write(" ");
				out.write("title=\"");
				out.write(itemsList.get(i).summary);
				out.write("\"");
				out.write(">");
				out.write(itemsList.get(i).description);
				out.write("</event>");
				out.write("\n");

			}

			out.write("</data>");
			out.flush();
			out.close();

			boolean timeline = true;
			render(plan, itemsList, user, canEdit, canView, isOrganizer,
					canIdea, canDelete, canAssign, timeline);
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * 
	 * This method checks first if the user is an organizer then renders the a
	 * plan and list of items in that plan to the calendar view
	 * 
	 * @author Yasmine Elsayed
	 * 
	 *         C5S9
	 * 
	 * @param planId
	 *            the id of the plan
	 * 
	 * @throws NotFoundIFNull
	 * 
	 **/

	public static void viewAsCalendar(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);

		List<Item> itemsList = plan.items;

		boolean canView = false;
		boolean isOrganizer = false;
		int canEdit = 0;
		int canIdea = 0;
		int canAssign = 0;
		boolean canDelete = false;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", plan.topic.id, "topic")) {
			canView = true;
			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
					"topic")) {

				canEdit = 1;
			}

			if (Users.isPermitted(user, "delete an action plan", plan.topic.id,
					"topic")) {

				canDelete = true;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					plan.topic.id, "topic")) {

				canAssign = 1;
			}
			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					plan.topic.id, "topic")) {

				canIdea = 1;
			}

			render(plan, itemsList, user, canEdit, canView, isOrganizer,
					canIdea, canDelete, canAssign);
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * 
	 * This method relates a given item to the given entity if the item is not
	 * related to any entity and logs this action.
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S17
	 * 
	 * @param itemId
	 *            the id of the item
	 * 
	 * @param entityId
	 *            the id of the entity
	 */
	public static void relateToEntity(long itemId, long entityId) {
		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		MainEntity entity = MainEntity.findById(entityId);
		notFoundIfNull(entity);
		if (!entity.relatedItems.contains(item)) {
			if (item.relatedEntity == null) {
				entity.relatedItems.add(item);
				item.relatedEntity = entity;
				entity.save();
				item.save();
			}
		}
		Log.addUserLog("<a href=\"/users/viewprofile?userId=" + user.id + "\">"
				+ user.username
				+ "</a> has related item: <a href=\"/plans/viewaslist?planId="
				+ item.plan.id + "\">" + item.summary
				+ "</a> to entity: <a href=\"/mainentitys/viewentity?id="
				+ item.relatedEntity.id + "\">" + item.relatedEntity.name
				+ "</a>", user, item, item.plan, item.plan.topic,
				item.plan.topic.entity, item.plan.topic.entity.organization);
		long id = item.relatedEntity.id;
		String name = item.relatedEntity.name;
		JsonObject json = new JsonObject();
		json.addProperty("name", name + "");
		json.addProperty("id", id + "");

		renderJSON(json.toString());

	}

	/**
	 * 
	 * This method removes the relation between an item and the entity and logs
	 * this action.
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S17
	 * 
	 * @param itemId
	 *            the id of the item
	 */
	public static void removeItemEntityRelation(long itemId) {
		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		Log.addUserLog(
				"<a href=\"/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ "</a> removed relation between item: <a href=\"/plans/viewaslist?planId="
						+ item.plan.id
						+ "\">"
						+ item.summary
						+ "</a> and entity: <a href=\"/mainentitys/viewentity?id="
						+ item.relatedEntity.id + "\">"
						+ item.relatedEntity.name + "</a>", user, item,
				item.plan, item.plan.topic, item.plan.topic.entity,
				item.plan.topic.entity.organization);
		item.relatedEntity.relatedItems.remove(item);
		item.relatedEntity.save();
		item.relatedEntity = null;
		item.save();
	}

	/**
	 * This method first checks if the user is allowed to edit in the plan so he
	 * can tag the item, searches for the tag in the global list of tags, if
	 * found it checks if the item had the same tag already or add the new one
	 * to the list if not it creates a new tag, save it and add it to the list,
	 * it updates the log .
	 * 
	 * @author Yasmine Elsayed
	 * 
	 * @story C5S15
	 * 
	 * @param itemID
	 *            : the id of the item that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */
	public static void tagItem(long itemId, String tag) {

		boolean tagAlreadyExists = false;
		boolean newTag = false;
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();

		User user = Security.getConnected();
		Item item = Item.findById(itemId);
		notFoundIfNull(item);
		long planId = item.plan.id;
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);

		Tag tagTemp = null;

		tagTemp = Tags.createTag(tag, user, plan.topic.entity.organization.id);
		if (tagTemp == null) {
			newTag = false;
			for (int i = 0; i < globalListOfTags.size(); i++) {
				if (globalListOfTags.get(i).name.equals(tag)) {
					tagTemp = globalListOfTags.get(i);
				}
			}

		} else {
			newTag = true;
		}

		if (item.tags.contains(tagTemp)) {
			tagAlreadyExists = true;

		} else {
			item.tags.add(tagTemp);
			item.save();
			tagTemp.taggedItems.add(item);
			tagTemp.save();
		}

		item.save();
		String logDescription = "<a href=\"/users/viewprofile?userId="
				+ user.id + "\">" + user.username + "</a>" + " has tagged "
				+ item.summary;

		JsonObject json = new JsonObject();
		if (!tagAlreadyExists) {
			json.addProperty("name", tagTemp.name);
			json.addProperty("id", tagTemp.id + "");

			if (newTag) {
				json.addProperty("success", "1");
				logDescription += "with a new Tag ";

			} else {
				json.addProperty("success", "2");
				logDescription += "with an already existing Tag";
			}
			logDescription += "<a href=\"/tags/mainpage?tagId=" + tagTemp.id
					+ "\">" + tagTemp.name + "</a>";

		}

		else {
			json.addProperty("success", "0");
		}
		logDescription += " in the plan "
				+ "<a href=\"/plans/viewaslist?planId=" + item.plan.id + "\">"
				+ item.plan.title + "</a>" + " of the topic "
				+ "<a href=\"/topics/show?topicId=" + item.plan.topic.id
				+ "\">" + item.plan.topic.title + "</a>";

		Log.addUserLog(logDescription, user, item, item.plan, item.plan.topic,
				item.plan.topic.entity, item.plan.topic.entity.organization,
				tagTemp);

		renderJSON(json.toString());

		// viewAsList(plan.id);
	}

	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param planID
	 *            ID of the plan that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the plan adds a
	 *            comment to a plan
	 */
	public static void addCommentToPlan(String comment, long planID) {
		Plan plan = Plan.findById(planID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, plan, user).save();
		plan.commentsList.add(c);
		plan.save();
		c.save();

		String logDescription = "User <a href=\"/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "</a> has commented on the following plan: <a href=\"/ideas/show?ideaId="
				+ plan.id + "\">" + plan.title + "</a>.";
		Log.addUserLog(logDescription, user, plan, plan.topic, plan.topic.entity,
				plan.topic.entity.organization);

		JsonObject json = new JsonObject();
		json.addProperty("commentMsg", comment);
		json.addProperty("commentUser", user.username);
		json.addProperty("commentDate", c.commentDate + "");

		renderJSON(json.toString());

	}

}
