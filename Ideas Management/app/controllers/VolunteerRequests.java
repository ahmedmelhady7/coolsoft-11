package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.validation.MinSize;
import play.data.validation.Required;
import models.Item;
import models.Plan;
import models.User;
import models.VolunteerRequest;

public class VolunteerRequests extends CRUD {

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

	public static void sendVolunteerRequest(long itemId,
			@Required String justification) {
		User sender = Security.getConnected();
		Item dest = Item.findById(itemId);
		Date d = new Date();
		System.out.println("ana ItemId" + itemId);
		if (sender.canVolunteer(itemId)) {
			if (!(dest.status == 2) && dest.endDate.compareTo(d) > 0) {
				VolunteerRequest volunteerRequest = new VolunteerRequest(
						sender, dest, justification).save();
				dest.addVolunteerRequest(volunteerRequest);
				sender.addVolunteerRequest(volunteerRequest);
				String description = sender.username
						+ " has requested to volunteer to work on the following item "
						+ dest.summary + "in the plan " + dest.plan.title
						+ "of the topic" + dest.plan.topic.title;
				// List<User> notificationDestination =
				// dest.plan.topic.getOrganizer();
				// Notifications.sendNotification(notificationDestination,
				// dest.plan.id, "plan", description);
				Plans.viewAsList(dest.plan.id);
			}
		} else {
			justify(itemId, dest.plan.id, 1);
		}
	}

	/**
	 * 
	 * This Method renders the view where the user enters the justification of
	 * his volunteer request given the item id, the plan id and the success
	 * indicator
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param planId
	 *            : the id of the plan having the item that the user wants to
	 *            work on
	 * @param itemId
	 *            : the id of the item the user wishes to volunteer to work on
	 * @param success
	 *            : an integer that controls how the justify will be rendered if
	 *            it's 0 then the user is asked to enter his justification to
	 *            send the volunteer request, if it's 1 then sending the
	 *            volunteer request has failed and the user gets an error
	 *            message
	 * 
	 */

	public static void justify(long itemId, long planId, int success) {
		Item item = Item.findById(itemId);
		render(item, planId, success);
	}

	/*
	 * Extra method. Unused in the mean time.
	 * 
	 * public static void viewMyRequests(long userId) {
	 * 
	 * User user = User.findById(userId); List<VolunteerRequest>
	 * myVolunteerRequests = user.volunteerRequests; render(user,
	 * myVolunteerRequests);}
	 */

	/**
	 * This method renders the page for allowing the organizer to view the
	 * volunteer requests to work on items in a certain plan.
	 * 
	 * @story C5S6
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param planId
	 *            : ID of the plan whose items' volunteer requests are to be
	 *            viewed.
	 */
	public static void viewVolunteerRequests(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		List<VolunteerRequest> planVolunteerRequests = new ArrayList<VolunteerRequest>();
		boolean error = false;
		// insert here if condition to check if user is permitted
		for (int i = 0; i < plan.items.size(); i++) {
			planVolunteerRequests.addAll(plan.items.get(i).volunteerRequests);
		}
		if (planVolunteerRequests.size() > 0) {
			for (int i = 0; i < planVolunteerRequests.size(); i++) {
				User sender = planVolunteerRequests.get(i).sender;
				Date d = new Date();
				if (planVolunteerRequests.get(i).destination.endDate
						.compareTo(d) < 0) {
					Item destination = planVolunteerRequests.get(i).destination;
					user.volunteerRequests.remove(planVolunteerRequests.get(i));
					destination.volunteerRequests.remove(planVolunteerRequests
							.get(i));
					planVolunteerRequests.remove(i);
					user.save();
					destination.save();
				} else {
					if (planVolunteerRequests.get(i).destination.assignees
							.contains(sender)
							|| !Topics.searchByTopic(plan.topic.id).contains(
									user)) {
						VolunteerRequest request = VolunteerRequest
								.findById(planVolunteerRequests.get(i).id);
						planVolunteerRequests.remove(request);
					}
				}
			}
		}
		render(user, plan, planVolunteerRequests, error);
	}

	/**
	 * This method accepts the volunteer request with the given id.
	 * 
	 * @story C5S6
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param requestId
	 *            : ID of the volunteer request to be accepted.
	 */
	public static void accept(String requestId) {
		long reqId = Long.parseLong(requestId);
		VolunteerRequest request = VolunteerRequest.findById(reqId);
		User org = Security.getConnected();
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		List<User> list = new ArrayList<User>();
		for (int i = 0; i < item.assignees.size(); i++)
			list.add(item.assignees.get(i));
		item.volunteerRequests.remove(request);
		user.itemsAssigned.add(item);
		item.assignees.add(user);
		user.save();
		item.save();
		request.delete();
		String s = "Your request to volunteer on item: " + item.summary
				+ " has been accepted.";
		list.add(user);
		for (int i = 0; i < item.plan.topic.getOrganizer().size(); i++) {
			if (org != item.plan.topic.getOrganizer().get(i))
				list.add(item.plan.topic.getOrganizer().get(i));
		}
		Notifications.sendNotification(list, item.plan.id, "plan", s);
	}

	/**
	 * This method rejects the volunteer request with the given id.
	 * 
	 * @story C5S6
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param requestId
	 *            : ID of the volunteer request to be rejected.
	 */
	public static void reject(String requestId) {
		long reqId = Long.parseLong(requestId);
		VolunteerRequest request = VolunteerRequest.findById(reqId);
		User org = Security.getConnected();
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		item.volunteerRequests.remove(request);
		user.save();
		item.save();
		request.delete();
		String s = "Your request to volunteer on item: " + item.summary
				+ " has been rejected.";
		List<User> list = new ArrayList<User>();
		list.add(user);
		for (int i = 0; i < item.plan.topic.getOrganizer().size(); i++) {
			if (org != item.plan.topic.getOrganizer().get(i))
				list.add(item.plan.topic.getOrganizer().get(i));
		}
		Notifications.sendNotification(list, item.plan.id, "plan", s);
	}
}
