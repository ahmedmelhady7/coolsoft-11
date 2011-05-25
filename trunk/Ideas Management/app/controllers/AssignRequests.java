package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.mvc.With;

import models.AssignRequest;
import models.Item;
import models.Log;
import models.Plan;
import models.User;

@With(Secure.class)
public class AssignRequests extends CoolCRUD {
	public static ArrayList<User> users2 = new ArrayList<User>();

	/**
	 * 
	 * This Method calls the method viewUsers with the itemId that users will
	 * receive assignRequests to work on and its planId
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param itemId
	 *            : the id of the item the users are requested to be assigned to
	 *            work on
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 */

	public static void assign(long itemId, long planId) {
		users2 = filter(itemId, planId);

		viewUsers(itemId, planId);

	}

	/**
	 * 
	 * This Method renders the viewUsers.html giving it the list of users that
	 * the organizer will choose from, the itemId that users will be assigned to
	 * and its planId
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param itemId
	 *            : the id of the item the users are requested to be assigned to
	 *            work on
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 */
	public static void viewUsers(long itemId, long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		notFoundIfNull(plan);

		if (Users.isPermitted(user,
				"assign one or many users to a to-do item in a plan",
				plan.topic.id, "topic")) {

			ArrayList<User> users = new ArrayList<User>();
			for (User user2 : users2) {
				users.add(user2);
			}

			for (int i = 0; i < users2.size(); i++) {
				System.out.println("user ids" + users2.get(i).id);
				if (users2.get(i).id.compareTo(user.id) == 0) {

					users.remove(i);
				}
			}
			Item item = Item.findById(itemId);
			notFoundIfNull(item);

			render(users, itemId, planId, item, user);
		} else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * 
	 * This Method sends assign requests to the selected users by creating an
	 * instance of AssignRequest and adds it to the list of sent assign requests
	 * of the sender and to the list of received assign requests of the receiver
	 * and to the list of assign requests in the item given list of userIds and
	 * the item the user is being assigned to
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param itemId
	 *            : the id of the item the users are requested to be assigned to
	 *            work on
	 * @param userIds
	 *            : the list of userIds of the users that will receive assign
	 *            requests
	 * @return boolean
	 */
	public static boolean sendRequests(long itemId, long[] userIds) {
		User destination;
		User sender = Security.getConnected();
		Item source = Item.findById(itemId);
		for (int i = 0; i < userIds.length; i++) {
			destination = User.findById(userIds[i]);
			if (filter(itemId, source.plan.id).contains(destination)) {
				if (!(source.status == 2) && !source.endDatePassed()) {

					String description = "You have been sent a request to work on this item"
							+ source.summary
							+ "\n "
							+ " In the plan "
							+ source.plan.title;
					AssignRequest assignRequest = new AssignRequest(source,
							destination, sender, description);

					assignRequest.save();
					source.addAssignRequest(assignRequest);
					sender.addSentAssignRequest(assignRequest);
					destination.addReceivedAssignRequest(assignRequest);

					System.out.println(destination.receivedAssignRequests
							.contains(assignRequest));
					Notifications.sendNotification(userIds[i], source.plan.id,
							"plan", description);

					String logDescription = "User "
							+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ sender.id
							+ "\">"
							+ sender.firstName
							+ " "
							+ sender.lastName
							+ "</a>"
							+ " sent an asssignment request to "
							+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ destination.id
							+ "\">"
							+ destination.firstName
							+ " "
							+ destination.lastName
							+ "</a>"
							+ " to work on the item "
							+ source.summary
							+ " in the plan "
							+ "<a href=\"http://localhost:9008/plans/viewaslist?planId="
							+ source.plan.id
							+ "\">"
							+ source.plan.title
							+ "</a>"
							+ " of the topic "
							+ "<a href=\"http://localhost:9008/topics/show?topicId="
							+ source.plan.topic.id
							+ "\">"
							+ source.plan.topic.title + "</a>";
					Log.addUserLog(logDescription, sender, destination,
							source.plan, source, source.plan.topic,
							source.plan.topic.entity,
							source.plan.topic.entity.organization);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * This Method returns the list of unblocked users from the topic of the
	 * plan who aren't assigned to the given item nor have pending
	 * assignRequests or volunteerRequests to work on this item
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param itemId
	 *            : the id of the item the users will be requested to be
	 *            assigned to work on
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 * 
	 * @return ArrayList<User>
	 */
	public static ArrayList<User> filter(long itemId, long planId) {
		Plan plan = Plan.findById(planId);
		List<User> nonBlockedUsers = Topics.searchByTopic(plan.topic.id);
		Item item = Item.findById(itemId);
		int size = nonBlockedUsers.size();

		ArrayList<User> finalResult = new ArrayList<User>();
		boolean flag = false;
		User user;
		for (int i = 0; i < size; i++) {
			user = nonBlockedUsers.get(i);
			if (item.assignees.contains(user)) {
				flag = true;
			} else {

				for (int j = 0; j < item.volunteerRequests.size(); j++) {
					if (user.id == item.volunteerRequests.get(j).sender.id) {
						flag = true;
						break;
					}
				}

				for (int k = 0; k < item.assignRequests.size(); k++) {
					if (user.id == item.assignRequests.get(k).destination.id) {
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				finalResult.add(user);
			}
			flag = false;
		}

		return finalResult;
	}

	/**
	 * 
	 * This Method assigns the list of unblocked users from the topic of the
	 * plan who aren't assigned to the given item nor have pending
	 * assignRequests or volunteerRequests to work on this item and who matched
	 * the search result of the entered keyword to the global list of users
	 * users2 and calls method viewUsers given the keyword, item id and plan id
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param keyword
	 *            : the keyword the user enters for searching
	 * @param itemId
	 *            : the id of the item the users will be requested to be
	 *            assigned to work on
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 * 
	 */

	public static void search(String keyword, long itemId, long planId) {

		List<User> nonBlockedUsers = filter(itemId, planId);
		List<User> searchResult = Users.searchUser(keyword);
		ArrayList<User> finalResult = new ArrayList<User>();
		for (int i = 0; i < nonBlockedUsers.size(); i++) {
			if (searchResult.contains(nonBlockedUsers.get(i))) {
				finalResult.add(nonBlockedUsers.get(i));
			}
		}
		users2 = finalResult;
		viewUsers(itemId, planId);

	}

	/**
	 * This method renders the page for allowing the user to view his assign
	 * requests to work on items in a certain plan.
	 * 
	 * @story C5S12
	 * 
	 * @author Mohamed Mohie
	 */
	public static void view() {
		User user = Security.getConnected();
		List<AssignRequest> assignRequests = new ArrayList();

		for (int i = 0; i < user.receivedAssignRequests.size(); i++) {
			AssignRequest currentRequest = user.receivedAssignRequests.get(i);
			if (!currentRequest.source.endDatePassed()
					&& Topics
							.searchByTopic(currentRequest.source.plan.topic.id)
							.contains(user)
					&& currentRequest.source.status != 2
					&& Users.isPermitted(user, "use",
							currentRequest.source.plan.topic.id, "topic")
					&& !user.itemsAssigned.contains(currentRequest.source)) {
				assignRequests.add(currentRequest);
			}
		}
		render(user, assignRequests);
	}

	/**
	 * This method accepts the assign request with the given id.
	 * 
	 * @story C5S12
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param id
	 *            : ID of the volunteer request to be accepted.
	 */
	public static void accept(String id) {
		User user = Security.getConnected();
		long reqId = Long.parseLong(id);
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		AssignRequest request = AssignRequest.findById(reqId);
		notFoundIfNull(request);
		assignRequests.remove(request);
		Item item = request.source;
		String logDescription = "User <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "</a> has accepted the assignment to work on item <a href=\"http://localhost:9008/plans/viewaslist?planId="
				+ item.plan.id + "\">" + item.summary + "</a>.";
		Log.addLog(logDescription, request.source, request.source.plan,
				request.source.plan.topic, request.source.plan.topic.entity,
				request.source.plan.topic.entity.organization);
		request.destination.receivedAssignRequests.remove(request);
		request.sender.sentAssignRequests.remove(request);
		item.assignRequests.remove(request);
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.addAll(item.assignees);
		for (User organizer : item.plan.topic.getOrganizer()) {
			if (!item.plan.topic.getOrganizer().contains(organizer))
				userToNotifyList.add(organizer);
		}
		user.itemsAssigned.add(item);
		item.assignees.add(user);
		request.destination.save();
		request.sender.save();
		item.save();
		request.delete();
		String description = "User " + user.username
				+ " has accepted the assignment to work on item: "
				+ item.summary + ".";
		for (User userToNotify : userToNotifyList) {
			Notifications.sendNotification(userToNotify.id, item.plan.id,
					"plan", description);
		}

	}

	/**
	 * This method rejects the assign request with the given id.
	 * 
	 * @story C5S12
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param id
	 *            : ID of the assign request to be rejected.
	 */
	public static void reject(String id) {
		User user = Security.getConnected();
		long reqId = Long.parseLong(id);
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		AssignRequest request = AssignRequest.findById(reqId);
		notFoundIfNull(request);
		String logDescription = "User <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "</a> has rejected the assignment to work on item <a href=\"http://localhost:9008/plans/viewaslist?planId="
				+ request.source.plan.id
				+ "\">"
				+ request.source.summary
				+ "</a>.";
		Log.addLog(logDescription, request.source, request.source.plan,
				request.source.plan.topic, request.source.plan.topic.entity,
				request.source.plan.topic.entity.organization);
		assignRequests.remove(request);
		request.destination.receivedAssignRequests.remove(request);
		request.sender.sentAssignRequests.remove(request);
		request.source.assignRequests.remove(request);
		request.sender.save();
		request.destination.save();
		request.source.save();
		String description = "User " + user.username
				+ " has rejected the assignment to work on item: "
				+ request.source.summary + ".";
		for (User userToNotify : request.source.plan.topic.getOrganizer()) {
			Notifications.sendNotification(userToNotify.id,
					request.source.plan.id, "plan", description);
		}

		request.delete();
	}

}