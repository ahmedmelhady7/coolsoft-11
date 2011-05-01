package controllers;

import models.Idea;
import models.Item;
import models.Plan;
import models.Topic;
import models.User;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import controllers.CRUD.ObjectType;

public class Plans extends CRUD {

	public static void viewAsList(long planId) {
		User user = Security.getConnected();
		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;
		int canAssign = 0;
		int canEdit = 0;
		if (Users.isPermitted(user,
				"edit an action plan",
				p.topic.id, "topic")) {

			canAssign = 1;
		}
		if (Users.isPermitted(user,
				"assign one or many users to a to-do item in a plan",
				p.topic.id, "topic")) {

			canAssign = 1;
		}
		render(p, itemsList, user, canAssign,canEdit);
	}

	/**
	 * This Method renders the page addItem where the user selects the ideas
	 * that will be promoted to execution in the topic's plan
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param topicId
	 *            The ID of the topic that this action plan is based upon
	 */
	public static void addIdea(long topicId) {
		Topic topic = Topic.findById(topicId);
		List<Idea> ideas = topic.ideas;
		render(ideas, topic);
	}

	/**
	 * This Method passes the selected items to the planCreate page so that they
	 * will be associated to the plan once it's created
	 * 
	 * @story C5S4
	 * 
	 * @author Salma Osama
	 * 
	 * @param topicId
	 *            The ID of the topic that this action plan is based upon
	 * @param checkedIdeas
	 *            The list of ideas selected to be associated to the plan
	 */
	public static void selectedIdeas(long[] checkedIdeas, long topicId) {
		String s = "";
		for (int i = 0; i < checkedIdeas.length; i++) {
			s = s + checkedIdeas + ",";
		}
		planCreate(topicId, s);

	}

	/**
	 * This Method renders the page for the plan creation
	 * 
	 * @story C5S1
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param topicId
	 *            The ID of the topic that this action plan is based upon
	 * @param userId
	 *            The ID of the user that is creating the plan
	 * 
	 * @return void
	 */
	public static void planCreate(long topicId, String ideas) {
		render(topicId, ideas);
	}

	/**
	 * This method takes the parameters from the web page of the plan creation
	 * to instantiate a plan object
	 * 
	 * @story C5S1
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param title
	 *            The title of the plan
	 * @param user
	 *            The user creating the plan
	 * @param startDate
	 *            The date when the plan will start
	 * @param endDate
	 *            The date when the plan will end
	 * @param description
	 *            The description of the plan
	 * @param topic
	 *            The topic which this plan is based upon
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param ideaString
	 *            : the String containing all the ideas ids that should be
	 *            associated to the plan
	 */

	public static void myCreate(String title, Date startDate, Date endDate,
			String description, long topicId, String requirement,
			Date istartdate, Date ienddate, String idescription,
			String isummary, String check, String ideaString) {

		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Plan p = new Plan(title, user, startDate, endDate, description, topic,
				requirement);
		p.save();
		p.addItem(istartdate, ienddate, idescription, p, isummary);
		p.save();
		String[] list2 = ideaString.split(",");
		int[] list = new int[list2.length];
		for (int i = 0; i < list2.length; i++) {
			list[i] = Integer.parseInt(list2[i]);
		}
		Idea idea;
		String notificationContent = "";
		ArrayList<User> ideaAuthor = new ArrayList<User>();
		for (int i = 0; i < list.length; i++) {
			idea = Idea.findById((long) list[i]);
			idea.plan = p;
			p.ideas.add(idea);
			ideaAuthor.add(idea.author);
			notificationContent = "your idea: " + idea.title
					+ "have been promoted to execution in the following plan "
					+ p.title + " in the topic: " + topic.title;
			Notifications.sendNotification(ideaAuthor, p.id, "plan",
					notificationContent);
			ideaAuthor.remove(idea.author);

		}
		if (check.equals("checked")) {
			addItem(p.id);
		}else{
			viewAsList(p.id);
		}
		Notifications.sendNotification(p.topic.getOrganizer(), p.id, "plan", "A new plan has been created");

	}

	/**
	 * This Method renders the page for adding items
	 * 
	 * @story C5S1
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param planId
	 *            The ID of the plan where the items will be added
	 * @return void
	 */

	public static void addItem(long planId) {
		Plan plan = Plan.findById(planId);
		render(plan);
	}

	/**
	 * This method takes the parameters of the item from the web page to
	 * instantiate an item and add it to the list of items of a plan
	 * 
	 * @story C5S1
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param startDate
	 *            The date when the item(task) should be started
	 * @param endDate
	 *            The date when the item(task) should be done
	 * @param descriprtion
	 *            The description of the item
	 * @param plan
	 *            The plan that contains this item in its items list
	 * @param summary
	 *            The summary of the description of the item (for vewing
	 *            purposes)
	 * @return void
	 */
	public static void add(Date startDate, Date endDate, String description,
			long planId, String summary, String check) {
		Plan plan = Plan.findById(planId);
		plan.addItem(startDate, endDate, description, plan, summary);
		if(check.equals("checked")){
			addItem(plan.id);
		}else{
			viewAsList(plan.id);
		}
		Notifications.sendNotification(plan.topic.getOrganizer(), plan.id, "plan", "A new item has been added");
	}

	/**
	 * This method renders the page for editing a plan
	 * 
	 * @story C5S3
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param planId
	 *            The ID of the plan that will be edited
	 * @return void
	 */
	public static void editPlan(long planId) {
		Plan plan = Plan.findById(planId);
		render(plan);
	}
	public static void editItem(long itemId){
		Item item = Item.findById(itemId);
		render(item);
	}

	/**
	 * This method takes the parameters from the web page of the plan editing to
	 * edit in a certain plan
	 * 
	 * @story C5S1
	 * 
	 * @author hassan.ziko1
	 * 
	 * @param title
	 *            The title of the plan
	 * @param startDate
	 *            The date when the plan will start
	 * @param endDate
	 *            The date when the plan will end
	 * @param description
	 *            The description of the plan
	 * @param topic
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @return void
	 */
	public static void edit(String title, Date startDate,
			Date endDate, String description, String requirement, long planId) {
		Plan p = Plan.findById(planId);
		p.title = title;
		p.startDate = startDate;
		p.endDate = endDate;
		p.description = description;
		p.requirement = requirement;
		p.save();
		Notifications.sendNotification(p.topic.getOrganizer(), p.id, "plan", "This action plan has been edited");
		
		for(int i = 0; i < p.items.size()-1;i++){
			
			Notifications.sendNotification(p.items.get(i).assignees, p.id, "plan", "This action plan has been edited");
		}
		viewAsList(p.id);
	}
	public static void edit2(Date startDate, Date endDate, String description,
			long planId, String summary, long itemId){
		Item i = Item.findById(itemId);
		i.startDate = startDate;
		i.endDate = endDate;
		i.description = description;
		i.summary = summary;
		i.save();
		Notifications.sendNotification(i.plan.topic.getOrganizer(), i.plan.id, "plan", "This item has been edited");
		Notifications.sendNotification(i.assignees, i.plan.id, "plan", "This item has been edited");
		viewAsList(i.plan.id);
	}

	public static void viewAsTimeline(long planid) {
		// Plan p = Plan.findById(planid);
		// List<Item> itemsList = p.items;
		// render(p, itemsList);
		render();
	}

	/*
	 * @author Yasmine Elsayed
	 * 
	 * this method renders the Calendar view of a plan
	 * 
	 * @param planId the id of the plan
	 * 
	 *  Omit @return 
	 */

	public static void viewAsCalendar(long planId) {

		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;
		
		render(p, itemsList);
	}

	//
	// public static void viewAsList(long planId, String y) {
	// long userid = 0;
	// User user = User.findById(userid);
	// Plan p = Plan.findById(planId);
	// List<Item> itemsList = p.items;
	//
	// render(p, itemsList, user, y);
	// }
	//
	//
	// public static void volunteer(long itemId, long planId) {
	// long userid = 0;
	// User user = User.findById(userid);
	// Item item = Item.findById(itemId);
	// String y = "";
	// if (item.assignees.contains(user)) {
	//
	// y = "You are already assigned to this item";
	// } else {
	//
	// for (int i = 0; i < item.volunteerRequests.size(); i = i + 1) {
	// if (user.id == item.volunteerRequests.get(i).sender.id) {
	// y = "You already sent a volunteer request to work on this item";
	// }
	// }
	//
	// for (int i = 0; i < item.assignRequests.size(); i = i + 1) {
	// if (user.id == item.assignRequests.get(i).destination.id) {
	// y = "You already received an assignment request to work on this item";
	// }
	// }
	// }
	// if (y == "") {
	// VolunteerRequests.justify(itemId, planId);
	// } else {
	// viewAsList(planId, y);
	// }
	// }

}
