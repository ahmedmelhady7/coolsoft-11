package controllers;

import models.Idea;
import models.Item;
import models.Organization;
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
		boolean error = false;
		boolean org = false;
		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;
		int canAssign = 0;
		int canEdit = 0;

		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						p.topic.id, "topic")) {
			org = true;
		}


		if (Users.isPermitted(user, "view an action plan", p.topic.id, "topic")) {


			if (Users.isPermitted(user, "edit an action plan", p.topic.id,
					"topic")) {
				canEdit = 1;
			}
			if (Users.isPermitted(user,
					"assign one or many users to a to-do item in a plan",
					p.topic.id, "topic")) {

				canAssign = 1;
			}

			render(p, itemsList, user, canAssign, canEdit, error, org);
		} else {
			error = true;
			render(error, org);
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

            user.itemsAssigned.add(item);
            user.save();
            item.assignees.add(user);
            item.save();
            List<User> otherUsers = item.getAssignees();
            User userToBeNotified;
            String notificationMsg = "User " + user.username
                            + "has been assigned to the item " + item.summary
                            + "in the plan " + item.plan.title;
            for (int i = 0; i < otherUsers.size(); i++) {
                    userToBeNotified = otherUsers.get(i);
                    if (userToBeNotified.id != user.id) {
                            Notifications.sendNotification(userToBeNotified.id,
                                            item.plan.id, "plan", notificationMsg);
                    }
            }
            viewAsList(item.plan.id);
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

	// /**
	// * This Method passes the selected items to the planCreate page so that
	// they
	// * will be associated to the plan once it's created
	// *
	// * @story C5S4
	// *
	// * @author Salma Osama
	// *
	// * @param topicId
	// * The ID of the topic that this action plan is based upon
	// * @param checkedIdeas
	// * The list of ideas selected to be associated to the plan
	// */
	// public static void selectedIdeas(long[] checkedIdeas, long topicId) {
	// String s = "";
	// for (int i = 0; i < checkedIdeas.length; i++) {
	// s = s + checkedIdeas + "a";
	// }
	// //planCreate(topicId, s);
	//
	// }

	/**
	 * This Method renders the page for the plan creation
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param topicId
	 *            The ID of the topic that this action plan is based upon
	 * @param checkedIdeas
	 *            The list of ideas ids selected to be associated to the plan
	 * 
	 */
	public static void planCreate(long[] checkedIdeas, long topicId) {
		String ideas = "";
		for (int i = 0; i < checkedIdeas.length; i++) {
			ideas = ideas + checkedIdeas[i] + ",";
		}
		render(topicId, ideas);
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rat
	 *            the user given rating for the specified plan
	 * @param planID
	 *            ID of the plan wished to rate
	 */

	public static void rate(long planId, int rat) {
//		User user = Security.getConnected();
//		if (!checkRated(user, planId)) {
			planId++;
			Plan p = Plan.findById(planId);
			int oldRating = p.rating;
			int newRating = (oldRating + rat) / 2;
			p.rating = newRating;
			p.save();
//		}

	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param userToCheck
	 *            User to be checked if he/she is in the list usersRated
	 * @return
	 */
//	public static boolean checkRated(User userToCheck, long planID) {
//		Plan p = Plan.findById(planID);
//		if(p.usersRated.size()==0)
//		{
//			return false;
//		}
//		else
//		{
//			for (int i = 0; i < p.usersRated.size(); i++) 
//			{
//			if (userToCheck == p.usersRated.get(i))
//				return true;
//			}
//		}
//		return false;
//	}

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
	 * @param istartdate
	 *            The start date of the first item added
	 * @param ienddate
	 *            The end date of the first item added
	 * @param idescription
	 *            The description of the first item added
	 * @param isummary
	 *            The summary of the first item added
	 * @param check
	 *            The value of the checkbox that indicates whether the user
	 *            wants to add more items or not
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
		System.out.println("creation of the plan");
		p.save();
		p.addItem(istartdate, ienddate, idescription, isummary);
		p.save();
		String[] list2 = ideaString.split(",");
		long[] list = new long[list2.length];
		for (int i = 0; i < list2.length; i++) {
			list[i] = Long.parseLong(list2[i]);
		}
		Idea idea;
		String notificationContent = "";

		for (int i = 0; i < list.length; i++) {
			idea = Idea.findById(list[i]);
			idea.plan = p;
			p.ideas.add(idea);
			System.out.println(idea.author.username);
			notificationContent = "your idea: " + idea.title
					+ "have been promoted to execution in the following plan "
					+ p.title + " in the topic: " + topic.title;
			Notifications.sendNotification(idea.author.id, p.id, "plan",
					notificationContent);

		}
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
	 */
	public static void sharePlan(String userName, long planID) {
		User U = User.find("byUsername",userName).first();
		planID++;
		Plan p = Plan.findById(planID);
		String type = "Plan";
		User user = Security.getConnected();
		String desc = user.firstName +" "+ user.lastName + " shared a plan with you : " + p.title;
		long notId = planID;
		long userId = U.id;
		Notifications.sendNotification(userId, notId, type, desc);
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
		Plan plan = Plan.findById(planId);
		render(plan);
	}

	/**
	 * This method takes the parameters of the item from the web page to
	 * instantiate an item and add it to the list of items of a plan
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDate
	 *            The date when the item(task) should be started
	 * @param endDate
	 *            The date when the item(task) should be done
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
	public static void add(Date startDate, Date endDate, String description,
			long planId, String summary, String check) {
		Plan plan = Plan.findById(planId);
		plan.addItem(startDate, endDate, description, summary);
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
		Plan plan = Plan.findById(planId);
		render(plan);
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
	 * @param startDate
	 *            The date when the plan will start
	 * @param endDate
	 *            The date when the plan will end
	 * @param description
	 *            The description of the plan
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param planId
	 *            The id of the plan being edit
	 */
	public static void edit(String title, Date startDate, Date endDate,
			String description, String requirement, long planId) {
		Plan p = Plan.findById(planId);
		p.title = title;
		p.startDate = startDate;
		p.endDate = endDate;
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
	 * @param startDate
	 *            The start date of an item
	 * @param endDate
	 *            The end date of an item
	 * @param description
	 *            The description of the item
	 * @param planId
	 *            The id of the plan that will be viewed
	 * @param requirement
	 *            The requirements needed for executing this plan
	 * @param itemId
	 *            The id of the item being edit
	 */

	public static void edit2(Date startDate, Date endDate, String description,
			long planId, String summary, long itemId) {
		Item item = Item.findById(itemId);
		item.startDate = startDate;
		item.endDate = endDate;
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
	public static void deleteItem(long planId, long itemId) {
		Plan plan = Plan.findById(planId);
		Item item = Item.findById(itemId);
		plan.items.remove(item);
		item.delete();
		viewAsList(planId);
	}

	public static void viewAsTimeline(long planid) {
		// Plan p = Plan.findById(planid);
		// List<Item> itemsList = p.items;
		// render(p, itemsList);
		render();
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

	// <!--
	// #{if p.topic.getOrganizer().size() > 1}
	// <ul>
	// #{list items:${p.topics.getOrganizer}, as:'organizer'}
	// <li>
	// <a href="#">${organizer.username}</a>
	// </li>
	// #{/list}
	// </ul>
	// #{/if}
	// #{else}
	// ${p.topics.getOrganizer.get(0).username}
	// #{/else}
	// -->
	// <!--#{if user.canVolunteer(item.id) && !(item.status == 2)}
	// <a href="@{VolunteerRequests.justify(item.id, p.id, 0)}">Volunteer to
	// work on this item</a>
	// <br/>
	// #{/if} -->

	// #{if canAssign == 1 && !(item.status == 2)}
	// <a href="@{AssignRequests.assign(item.id, p.id)}">Assign a user to this
	// item</a>
	// #{/if}
	// #{if canEdit == 1}
	// <a href="@{Plans.editItem(item.id)}">Edit this item</a>
	// #{/if}

}
