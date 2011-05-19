package controllers;

import models.Comment;
import models.Idea;
import models.Item;
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
import java.util.Date;
import java.util.List;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import controllers.CRUD.ObjectType;

@With(Secure.class)
public class Plans extends CRUD {

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
		boolean canView = true;
		boolean isOrganizer = false;
		Plan p = Plan.findById(planId);
		List<Comment> comments = p.commentsList;
		List<Item> itemsList = p.items;
		int canAssign = 0;
		int canEdit = 0;
		int canIdea = 0;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						p.topic.id, "topic")) {
			isOrganizer = true;
		}

		if (Users.isPermitted(user, "view", p.topic.id, "topic")) {
			if (Users.isPermitted(user, "edit an action plan", p.topic.id,
					"topic")) {

				canEdit = 1;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					p.topic.id, "topic")) {

				canAssign = 1;
			}

			if (Users.isPermitted(user,
					"associate an idea or more to an already existing plan",
					p.topic.id, "topic")) {

				canIdea = 1;
			}
			List<MainEntity> entitiesList = p.topic.entity.organization.entitiesList;
			render(p, itemsList, user, canAssign, canEdit, canView, isOrganizer, canIdea, comments, entitiesList);
		} else {
			canView = false;
			render(canView, isOrganizer);
		}

	}

	/**
	 * This Method directly assigns the logged in user to the item given the
	 * item id
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
		System.out.println("ana hena");
		user.itemsAssigned.add(item);
		user.save();
		item.assignees.add(user);
		item.save();
		List<User> otherUsers = new ArrayList<User>();
		otherUsers.addAll(item.getAssignees());
		otherUsers.addAll(item.plan.topic.getOrganizer());
		User userToBeNotified;
		String notificationMsg = "User " + user.username
				+ " is now working on the item " + item.summary
				+ " in the plan ";
		for (int i = 0; i < otherUsers.size(); i++) {
			userToBeNotified = otherUsers.get(i);
			if (userToBeNotified.id.compareTo(user.id) != 0) {
				Notifications.sendNotification(userToBeNotified.id,
						item.plan.id, "plan", notificationMsg);
			}
		}

		// viewAsList(item.plan.id);
	}

	/**
	 * This Method renders the page addItem where the user selects the ideas
	 * that will be promoted to execution in the plan
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            The ID of the topic that this action plan is based upon
	 */
	public static void addIdea(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		Topic topic = Topic.findById(plan.topic.id);
		List<Idea> ideas = new ArrayList<Idea>();

		for (Idea idea : topic.ideas) {
			if (idea.plan == null) {
				ideas.add(idea);
			}
		}

		render(ideas, topic, plan);
	}

	/**
	 * This Method associates the list of selected ideas to the plan and
	 * increments the community contribution counter of the authors of these
	 * plans
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param checkedIdeas
	 *            The list of ideas selected to be associated to the plan
	 * @param planId
	 *            The ID of the topic that this action plan is based upon
	 */
	public static void selectedIdeas(long[] checkedIdeas, long planId) {
		Plan plan = Plan.findById(planId);
		Topic topic = Topic.findById(plan.topic.id);
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
			notificationContent = "Congratulayions!! your idea: " + idea.title
					+ "have been promoted to execution in the following plan "
					+ plan.title + " of the topic: " + topic.title;
			Notifications.sendNotification(idea.author.id, plan.id, "plan",
					notificationContent);

		}
		planView(planId);

	}

	public static void planList(String type, long id) {
		User user = Security.getConnected();
		List<Plan> plans = new ArrayList<Plan>();
		if (type == "entity") {
			MainEntity entity = MainEntity.findById(id);
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
		render(plans);
	}

	/**
	 * This method renders the view containing the list of ideas associated to
	 * the plan given the plan id
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            the id of the plan whose list of ideas will be viewed
	 */
	public static void planView(long planId) {
		Plan p = Plan.findById(planId);
		User user = Security.getConnected();

		List<Idea> ideas = p.ideas;
		int canIdea = 0;
		if (Users.isPermitted(user,
				"associate an idea or more to an already existing plan",
				p.topic.id, "topic")) {
			canIdea = 1;
		}
		render(ideas, p, canIdea);
	}

	/**
	 * This method removes an idea from the the list of ideas associated to the
	 * plan given the idea id and the plan id
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
		Plan plan = Plan.findById(planId);
		idea.plan = null;
		plan.ideas.remove(idea);
		idea.save();
		plan.save();
		String notificationMsg = "Sorry!! your idea " + idea.title
				+ "has been removed from the following plan" + plan.title
				+ " of the topic: " + plan.topic.title;
		Notifications.sendNotification(idea.author.id, plan.id, "plan",
				notificationMsg);

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
		Item dest = Item.findById(itemId);

		if (sender.canVolunteer(itemId)) {
			if (!(dest.status == 2) && !dest.endDatePassed()) {
				VolunteerRequest volunteerRequest = new VolunteerRequest(
						sender, dest, justification).save();
				dest.addVolunteerRequest(volunteerRequest);
				dest.save();
				sender.addVolunteerRequest(volunteerRequest);
				sender.save();
				String description = sender.username
						+ " has requested to volunteer to work on the following item "
						+ dest.summary + "in the plan " + dest.plan.title
						+ "of the topic" + dest.plan.topic.title;
				List<User> notificationDestination = dest.plan.topic
						.getOrganizer();
				for (int i = 0; i < notificationDestination.size(); i++) {
					Notifications.sendNotification(
							notificationDestination.get(i).id, dest.plan.id,
							"plan", description);
				}

				// Plans.viewAsList(dest.plan.id);
				// justify(itemId, dest.plan.id, 2);
			}
		} else {
			// justify(itemId, dest.plan.id, 1);
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

		render(topicId);
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rat
	 *            the user given rating for the specified plan
	 * @param planID
	 *            ID of the plan wished to rate rates a given plan
	 */

	public static void rate(long planId, int rat) {
		planId++;
		Plan p = Plan.findById(planId);
		User user = Security.getConnected();
		if(p.usersRated.contains(user))
			System.out.print("user already rated");
		else
		{
			System.out.println("user didn't rate yet");
			p.usersRated.add(user);
			if(p.rating.equals("Not yet rated"))
				p.rating = Integer.toString(rat);
			else
			{
				int oldRating = Integer.parseInt(p.rating);
				int newRating;
				newRating = (oldRating + rat) / 2;
				p.rating = Integer.toString(newRating);
			}
			p.save();
			redirect("/plans/viewaslist?planId="+planId);
		}
	

	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param userToCheck
	 *            User to be checked if he/she is in the list usersRated
	 * @return checks if a user has rated
	 */
	// public static boolean checkRated(User userToCheck, long planID) {
	// Plan p = Plan.findById(planID);
	// if(p.usersRated.size()==0)
	// {
	// return false;
	// }
	// else
	// {
	// for (int i = 0; i < p.usersRated.size(); i++)
	// {
	// if (userToCheck == p.usersRated.get(i))
	// return true;
	// }
	// }
	// return false;
	// }

	/**
	 * This method takes the parameters from the web page of the plan creation
	 * to instantiate a plan object
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param title
	 *            The title of the plan
	 * @param user
	 *            The user creating the plan
	 * @param startDay
	 *            The day when the plan will start
	 * @param startMonth
	 *            The month when the plan will start
	 * @param startYear
	 *            The year when the plan will start
	 * @param endDay
	 *            The day when the plan will end
	 * @param endMonth
	 *            The month when the plan will end
	 * @param endYear
	 *            The year when the plan will end
	 * @param description
	 *            The description of the plan
	 * @param topicId
	 *            The id of the topic which this plan is based upon
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param istartDay
	 *            The start day of the first item added
	 * @param istartMonth
	 *            The start month of the first item added
	 * @param istartYear
	 *            The start year of the the first item added
	 * @param iendDay
	 *            The end day of the first item added
	 * @param iendMonth
	 *            The end month of the first item added
	 * @param iendYear
	 *            The end year of the first item added
	 * @param idescription
	 *            The description of the first item added
	 * @param isummary
	 *            The summary of the first item added
	 * @param check
	 *            The value of the checkbox that indicates whether the user
	 *            wants to add more items or not
	 */

	public static void myCreate(String title, int startDay, int startMonth,
			int startYear, int endDay, int endMonth, int endYear,
			String description, long topicId, String requirement,
			int istartDay, int istartMonth, int istartYear, int iendDay,
			int iendMonth, int iendYear, String idescription, String isummary,
			String check) {

		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Plan p = new Plan(title, user, new Date(startYear - 1900, startMonth,
				startDay), new Date(endYear - 1900, endMonth, endDay),
				description, topic, requirement);
		System.out.println("creation of the plan");
		p.save();
		p.addItem(new Date(istartYear - 1900, istartMonth, istartDay),
				new Date(iendYear - 1900, iendMonth, iendDay), idescription,
				isummary);
		p.save();

		List<User> topicOrganizers = p.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, p.id,
					"plan", "A new plan has been created");
		}

		if (check != null && check.equals("checked")) {
			addItem(p.id);
		} else {
			viewAsList(p.id);
		}

	}

	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param userId
	 *            User that wants to share the plan
	 * @param planID
	 *            ID of the plan to be shared
	 * 
	 *            shares a plan with a given user
	 */
	public static void sharePlan(String userName, long planID) {
		User U = User.find("byUsername", userName).first();
		planID++;
		Plan p = Plan.findById(planID);
		String type = "Plan";
		User user = Security.getConnected();
		String desc = user.firstName + " " + user.lastName
				+ " shared a plan with you" + "\n" + "Copy this link into your address bar to view the shared Idea : http://localhost:9008/plans/viewaslist?planId="+p.id;
		long notId = planID;
		long userId = U.id;
		Notifications.sendNotification(userId, notId, type, desc);
		redirect("/plans/viewaslist?planId="+p.id);
	}

	/**
	 * This Method renders the page for adding items
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param planId
	 *            The ID of the plan where the items will be added
	 * 
	 */

	public static void addItem(long planId) {
		Plan p = Plan.findById(planId);
		render(p);
	}

	/**
	 * This method takes the parameters of the item from the web page to
	 * instantiate an item and add it to the list of items of a plan
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDay
	 *            The day on which the item start
	 * @param startMonth
	 *            The month when the item starts
	 * @param startYear
	 *            The year when the item starts
	 * @param endDay
	 *            The day on which the item ends
	 * @param endMonth
	 *            The month when the item ends
	 * @param endYear
	 *            The year when the item ends
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
	public static void add(int startDay, int startMonth, int startYear,
			int endDay, int endMonth, int endYear, String description,
			long planId, String summary, String check) {
		Plan plan = Plan.findById(planId);
		plan.addItem(new Date(startYear - 1900, startMonth, startDay),
				new Date(endYear - 1900, endMonth, endDay), description,
				summary);
		if (check != null && check.equals("checked")) {
			addItem(plan.id);
		} else {
			viewAsList(plan.id);
		}
		List<User> topicOrganizers = plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, plan.id,
					"plan", "A new plan has been created");
		}
	}

	/**
	 * This method renders the page for editing a plan
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
		Plan p = Plan.findById(planId);
		render(p);
	}

	/**
	 * This method renders the page for editing a plan
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param itemId
	 *            The ID of the item that will be edited
	 * 
	 */

	public static void editItem(long itemId) {
		Item item = Item.findById(itemId);
		render(item);
	}

	/**
	 * This method takes the parameters from the web page of the plan editing to
	 * edit in a certain plan
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param title
	 *            The title of the plan
	 * @param startDay
	 *            The day on which the plan starts
	 * @param startMonth
	 *            The month when the plan starts
	 * @param startYear
	 *            The year when the plan starts
	 * @param endDay
	 *            The day on which the plan ends
	 * @param endMonth
	 *            The month when the plan ends
	 * @param endYear
	 *            The year when the plan ends
	 * @param description
	 *            The description of the plan
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param planId
	 *            The id of the plan being edit
	 */
	public static void edit(String title, int startDay, int startMonth,
			int startYear, int endDay, int endMonth, int endYear,
			String description, String requirement, long planId) {
		Plan p = Plan.findById(planId);
		p.title = title;
		p.startDate = new Date(startYear - 1900, startMonth, startDay);
		p.endDate = new Date(endYear - 1900, startMonth, startDay);
		p.description = description;
		p.requirement = requirement;
		p.save();

		List<User> topicOrganizers = p.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id, p.id,
					"plan", "A new plan has been created");
		}
		List<User> assignees = new ArrayList<User>();
		for (int i = 0; i < p.items.size(); i++) {
			assignees = p.items.get(i).assignees;
			for (int j = 0; j < assignees.size(); j++) {
				Notifications.sendNotification(assignees.get(j).id, p.id,
						"plan", "This action plan has been edited");
			}
		}
		viewAsList(p.id);
	}

	/**
	 * This method takes the parameters from the web page of the item editing to
	 * edit in a certain item
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDay
	 *            The day on which the item starts
	 * @param startMonth
	 *            The month when the item starts
	 * @param startYear
	 *            The year when the item starts
	 * @param endDay
	 *            The day on which the item ends
	 * @param endMonth
	 *            The month when the item ends
	 * @param endYear
	 *            The year when the item ends
	 * @param description
	 *            The description of the item
	 * @param planId
	 *            The id of the plan that will be viewed
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param itemId
	 *            The id of the item being edit
	 */

	public static void edit2(int startDay, int startMonth, int startYear,
			int endDay, int endMonth, int endYear, String description,
			long planId, String summary, long itemId) {
		Item item = Item.findById(itemId);
		item.startDate = new Date(startYear - 1900, startMonth, startDay);
		item.endDate = new Date(endYear - 1900, endMonth, endDay);
		item.description = description;
		item.summary = summary;
		item.save();

		List<User> topicOrganizers = item.plan.topic.getOrganizer();
		for (int i = 0; i < topicOrganizers.size(); i++) {
			Notifications.sendNotification(topicOrganizers.get(i).id,
					item.plan.id, "plan", "This item has been edited");
		}

		List<User> assignees = item.assignees;
		for (int j = 0; j < assignees.size(); j++) {
			Notifications.sendNotification(assignees.get(j).id, item.plan.id,
					"plan", "This item has been edited");
		}

		viewAsList(item.plan.id);
	}

	/**
	 * This methods deletes an item from the item list of a plan
	 * 
	 * @story C5S3
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param planId
	 *            The id of the plan that contains the item
	 * 
	 * @param itemId
	 *            The id of the item being deleted
	 */
	public static boolean deleteItem(long itemId, int notify) {
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
			if(notify ==1) {
			notificationMsg = "The item " + item.summary
					+ " has been deleted from the plan";
			Notifications.sendNotification(userAssigned.id, plan.id, "plan",
					notificationMsg);
			}
		}
		Tag tag;
		for (int i = 0; i < item.tags.size(); i++) {
			tag = item.tags.get(i);
			items = tag.taggedItems;
			items.remove(item);
			tag.save();
		}
		item.delete();
		return true;
		// viewAsList(planId);
	}

	/**
	 * This methods deletes a plan given the plan id
	 * 
	 * @story C5S2
	 * 
	 * @author Salma Osama
	 * 
	 * @param planId
	 *            The id of the plan that will be deleted
	 *            
	 */
	public static void deletePlan(long planId) {
		List <User> toBeNotified = new ArrayList<User>();
		Plan plan = Plan.findById(planId);
		String notificationMsg = "The plan " + plan.title + " has been deleted";
		for(Item item:plan.items) {
			for(User user: item.assignees) {
				if(!toBeNotified.contains(user)) {
					toBeNotified.add(user);
					Notifications.sendNotification(user.id, plan.topic.id, "topic",
							notificationMsg);
			}
			}
		}
		for(User organizer:plan.topic.getOrganizer()) {
			if(!toBeNotified.contains(organizer)) {
				toBeNotified.add(organizer);
				Notifications.sendNotification(organizer.id, plan.topic.id, "topic",
						notificationMsg);
		}
		}
		
		while (!plan.items.isEmpty()) {
			deleteItem(plan.items.get(0).id, 0);
			System.out.println("items " + plan.items.size());
			plan.save();
		}
		Idea idea;
		while (!plan.ideas.isEmpty()) {
			idea = plan.ideas.remove(0);
			idea.plan = null;
			System.out.println("ideas " + plan.items.size());
			idea.save();
			plan.save();
		}
		Comment comment;
		while (!plan.commentsList.isEmpty()) {
			comment = plan.commentsList.remove(0);
			System.out.println("comments " + plan.items.size());
			plan.save();
			comment.delete();
		}
		
		User userRated;
		while(!plan.usersRated.isEmpty()) {
			userRated = plan.usersRated.remove(0);
			userRated.ratedPlans.remove(plan);
			System.out.println("rated " + plan.usersRated.size());
			userRated.save();
			plan.save();
			
		}
		User creator = plan.madeBy;
		creator.planscreated.remove(plan);
		creator.save();
		plan.madeBy = null;
		plan.save();
		
		Topic topic = plan.topic;
		topic.plan=null;
		topic.save();
		plan.delete();
		Topics.show("" + topic.id);
	}

	/**
	 * This methods This Method renders the Timeline view of a plan
	 * 
	 * @story C5S8
	 * 
	 * @author Alaa Samer
	 * 
	 * @param planId
	 *            The id of the plan
	 * @throws java.text.ParseException 
	 */
public static void viewasTimeline(long planId) throws IOException, ParseException, java.text.ParseException {

		
		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;

		 FileWriter fstream;
		fstream =  new FileWriter("Ideas Management/public/xml/out.xml");	
		
			BufferedWriter out = new BufferedWriter(fstream);
					
		         out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");out.write("\n");
		         out.write("<data>");
		         out.write("\n");
		
		         


		         
		         out.write("<event start=\"");
				  //       out.write(itemsList.get(i).startDate.toGMTString());
				        String inputDate = (p.startDate.toGMTString());
				         Date date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz").parse(inputDate);
				        
				         
				         
				         
				         out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z").format(date));
			         
						 out.write("\"");
								 
						
						 out.write(" ");		 
						 out.write("title=\"");
						 out.write("START");
						 out.write("\"");out.write(" ");
						 out.write("icon=\"/public/rer7.png\"");
						 out.write(">" );
						  
						 out.write("</event>");
						 out.write("\n");
				
		         
		         
		         
		        
						 
						 
						 out.write("<event start=\"");
						  //       out.write(itemsList.get(i).startDate.toGMTString());
						         inputDate = (p.endDate.toGMTString());
						         date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz").parse(inputDate);
						        
						         
						         
						         
						         out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z").format(date));
					         
								 out.write("\"");
								 out.write(" ");		 
									 
								 out.write("title=\"");
								 out.write("END");
								 out.write("\"");out.write(" ");
								 out.write("icon=\"/public/rer7.png\"");
								 out.write(">" );
								  
								 out.write("</event>");
								 out.write("\n");
						
		         
			        
		         
		 for(int i=0;i<itemsList.size();i++){
			 	 out.write("<event start=\"");
		  //       out.write(itemsList.get(i).startDate.toGMTString());
		         inputDate = (itemsList.get(i).startDate.toGMTString());
		          date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz").parse(inputDate);
		        
		         
		         
		         
		         out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z").format(date));
	         
				 out.write("\"");
				 out.write(" ");		 
				 out.write("end=\"");
				 
				 //out.write(itemsList.get(i).endDate.toGMTString());
				 inputDate = (itemsList.get(i).endDate.toGMTString());
		         date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz").parse(inputDate);
		         out.write(new SimpleDateFormat("MMM dd yyyy HH:mm:ss Z").format(date));
		         
				 out.write("\"");
				 out.write(" ");		 
				 out.write("title=\"");
				 out.write(itemsList.get(i).summary);
				 out.write("\"");
				 out.write(">" );
				 out.write(itemsList.get(i).description); 
				 out.write("</event>");
				 out.write("\n");
		
	
	
		
		 }
		 
		 out.write("</data>");
		 out.flush();
		 out.close();
		
render(p, itemsList);
		
		
		
		
	}
	



	/**
	 * @author Yasmine Elsayed
	 * 
	 *         this method renders the Calendar view of a plan
	 * 
	 * @param planId
	 *            the id of the plan
	 * 
	 * 
	 **/

	public static void viewAsCalendar(long planId) {

		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;

		render(itemsList);
	}
	
	public static void relateToEntity(long itemId, long entityId) {
		// User user = Security.getConnected();
		Item item = Item.findById(itemId);
		MainEntity entity = MainEntity.findById(entityId);
		System.out.println("Testeeeeeeeeeeeeeeeeeeee");
		if (!entity.relatedItems.contains(item)) {
			if (item.relatedEntity == null) {
				entity.relatedItems.add(item);
				item.relatedEntity = entity;
				entity.save();
				item.save();
			}
		}
	}
	
	public static void removeItemEntityRelation(long itemId) {
		// User user = Security.getConnected();
		Item item = Item.findById(itemId);
		System.out.println("Test removing");
		item.relatedEntity.relatedItems.remove(item);
		item.relatedEntity.save();
		item.relatedEntity = null;
		item.save();
		
	}

}
