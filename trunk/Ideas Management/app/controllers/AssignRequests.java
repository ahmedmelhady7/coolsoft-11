package controllers;

import java.util.ArrayList;
import java.util.List;

import models.AssignRequest;
import models.Item;
import models.Plan;
import models.User;

public class AssignRequests extends CRUD {

	/**
	 * 
	 * This Method creates an instance of AssignRequest and adds it to the list
	 * of sent assign requests of the sender and to the list of received assign
	 * requests of the receiver and to the list of volunteer requests in the
	 * item given the user id, the item id and the required justification string
	 * 
	 * @author salma.qayed
	 * 
	 * @story C5S10
	 * 
	 * @param senderId
	 *            : the id of the user sending the assign request
	 * @param itemId
	 *            : the id of the item the user is requested to be assigned to
	 *            work on
	 * @param destId
	 *            : the id of the user the assign request is sent to
	 * 
	 * @return void
	 */
	public static void sendAssignRequest(long senderId, long itemId, long destId) {

		User sender = User.findById(senderId);
		User destination = User.findById(destId);
		Item source = Item.findById(itemId);
		String content = "";
		AssignRequest assignRequest = new AssignRequest(source, destination,
				sender, content).save();
		source.addAssignRequest(assignRequest);
		sender.addSentAssignRequest(assignRequest);
		destination.addReceivedAssignRequest(assignRequest);
	}
	
	public static void assign(long itemId, long planId) {
		
		Plan plan = Plan.findById(planId);
		List<User> nonBlockedUsers = Topics.searchByTopic(plan.topic.id);
		viewUsers(nonBlockedUsers, itemId, planId);
		
	}
	
	public static void search (String keyword, String item, String plan) {
		int planId = Integer.parseInt(plan);
		int itemId = Integer.parseInt(item);
		Plan plann = Plan.findById((long)planId);
		List<User> nonBlockedUsers = Topics.searchByTopic(plann.topic.id);
		List<User> searchResult = Users.searchUser(keyword);
		List<User> finalResult = new ArrayList<User> ();
		for(int i = 0; i < nonBlockedUsers.size(); i++) {
			if(searchResult.contains(nonBlockedUsers.get(i))) {
				finalResult.add(nonBlockedUsers.get(i));
			}
		}
		viewUsers(finalResult, (long) itemId, (long) planId);
	}
	
	public static void viewUsers(List<User> users, long itemId, long planID) {
		render(users);
	}
	
	

	public static void view(long userId) {
		User user = User.findById(userId);
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		render(user, assignRequests);
	}

	public static void accept(long userId, long id) {
		User user = User.findById(userId);
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		AssignRequest request = AssignRequest.findById(id);
		assignRequests.remove(request);
		Item item = request.source;
		user.itemsAssigned.add(item);
		item.assignees.add(user);
	}

	public static void reject(long userId, long id) {
		User user = User.findById(userId);
		List<AssignRequest> assignRequests = user.receivedAssignRequests;
		AssignRequest request = AssignRequest.findById(id);
		assignRequests.remove(request);
	}

}
