package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.mvc.With;

import models.AssignRequest;
import models.Item;
import models.Plan;
import models.User;
@With(Secure.class)
public class AssignRequests extends CRUD {
	public static ArrayList <User> users2 = new ArrayList<User> ();

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
		 
		System.out.println(users2.size() + "A5ER OBBBBAAAAAAAAAA");
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
	 * @param users
	 *            : the list of users that will be displayed so that the
	 *            organizer can choose from to send them assignRequests requests
	 * @param itemId
	 *            : the id of the item the users are requested to be assigned to
	 *            work on
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 */
	public static void viewUsers( long itemId, long planId) {
		System.out.println(itemId + "ITEMID");
		System.out.println(users2.size() + "OFEEEEEEEEEEEEEEEENNN");
		ArrayList<User> users = new ArrayList<User>();
		for(User user:users2){
			users.add(user);
		}
		render(users, itemId, planId);
	}

	/**
	 * 
	 * This Method calls the method sendAssignRequest with each user id in the
	 * given list of userIds and the item the user is being assigned to
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
	 * @param planId
	 *            : the id of the plan containing the item that will be assigned
	 *            to the list of users selected
	 */
	public static void sendRequests(long itemId, long[] userIds) {
		User user;
		Date d = new Date();
		System.out.println("ana da5alt send requests");
		Item item = Item.findById(itemId);
		for (int i = 0; i < userIds.length; i++) {
			user = User.findById(userIds[i]);
			if (filter(itemId,item.plan.id)
					.contains(user)) {
				if (!(item.status == 2) && item.endDate.compareTo(d) > 0) {
					sendAssignRequest(itemId,userIds[i]);
				}
			}
		}

	}

	/**
	 * 
	 * This Method creates an instance of AssignRequest and adds it to the list
	 * of sent assign requests of the sender and to the list of received assign
	 * requests of the receiver and to the list of assign requests in the item
	 * given the item id, the destination id
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5
	 * 
	 * @param itemId
	 *            : the id of the item the user is requested to be assigned to
	 *            work on
	 * @param destId
	 *            : the id of the user the assign request is sent to
	 */
	public static void sendAssignRequest(long itemId, long destId) {

		User sender = Security.getConnected();
		User destination = User.findById(destId);
		Item source = Item.findById(itemId);
		String content = "";
		AssignRequest assignRequest = new AssignRequest(source, destination,
				sender, content).save();
		source.addAssignRequest(assignRequest);
		sender.addSentAssignRequest(assignRequest);
		destination.addReceivedAssignRequest(assignRequest);

		String description = "You have been sent a request to work on this item "
				+ source.summary
				+ "\n "
				+ " In the plan "
				+ source.plan.title
				+ "\n" + "by " + sender.username;
		Notifications.sendNotification(destId, source.plan.id, "plan",
				description);

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
	 * @return List<User>
	 */
	public static ArrayList<User> filter(long itemId, long planId) {
		Plan plan = Plan.findById(planId);
		List<User> nonBlockedUsers = Topics.searchByTopic(plan.topic.id);
		Item item = Item.findById(itemId);
		int size = nonBlockedUsers.size();
		System.out.println(size + "OBBBAAAAAAAAAAA");
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
		System.out.println(finalResult.size() + "OFFFFFFFFFFFFFFFFFFFFF");
		return finalResult;
	}

	/**
	 * 
	 * This Method returns the list of unblocked users from the topic of the
	 * plan who aren't assigned to the given item nor have pending
	 * assignRequests or volunteerRequests to work on this item and who matched
	 * the search result of the entered keyword
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
	 * @return List<User>
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
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		if (assignRequests.size() > 0) {
			for (int i = 0; i < assignRequests.size(); i++) {
				Date d = new Date();
				if (assignRequests.get(i).source.endDate.compareTo(d) < 0
						|| !Topics.searchByTopic(
								assignRequests.get(i).source.plan.topic.id)
								.contains(user)) {
					assignRequests.remove(i);
				} else {
					for (int j = 0; j < assignRequests.get(i).sender.itemsAssigned
							.size(); j++) {
						if (user.itemsAssigned
								.contains(assignRequests.get(i).source)) {
							AssignRequest req = assignRequests.get(i);
							req.destination.receivedAssignRequests.remove(req);
							req.sender.sentAssignRequests.remove(req);
							req.source.assignRequests.remove(req);
							assignRequests.remove(assignRequests.get(i));
							req.destination.save();
							req.sender.save();
							req.source.save();
							req.delete();
						}
					}
				}
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
		assignRequests.remove(request);
		Item item = request.source;
		request.destination.receivedAssignRequests.remove(request);
		request.sender.sentAssignRequests.remove(request);
		item.assignRequests.remove(request);
		List<User> list = new ArrayList<User>();
		list = item.assignees;
		list.addAll(item.plan.topic.getOrganizer());
		user.itemsAssigned.add(item);
		item.assignees.add(user);
		request.destination.save();
		request.sender.save();
		item.save();
		request.delete();
		String s = "User " + user.username
				+ " has accepted the assignment to work on item"
				+ request.source.summary + ".";

		for (User userToNotify : list) {
			Notifications.sendNotification(userToNotify.id, item.plan.id,
					"plan", s);
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
		assignRequests.remove(request);
		request.destination.receivedAssignRequests.remove(request);
		request.sender.sentAssignRequests.remove(request);
		request.source.assignRequests.remove(request);
		request.sender.save();
		request.destination.save();
		request.source.save();
		String s = "User " + user.username
				+ " has rejected the assignment to work on item"
				+ request.source.summary + ".";

		for (User userToNotify : request.source.plan.topic.getOrganizer()) {
			Notifications.sendNotification(userToNotify.id,
					request.source.plan.id, "plan", s);
		}

		request.delete();
	}

}