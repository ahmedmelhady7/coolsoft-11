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
         * user id, the item id and the required justification string
         * 
         * @author Salma Osama
         * 
         * @story C5S10
         * 
         * @param senderId
         *            : the id of the user sending the volunteer request
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
                if (sender.canVolunteer(itemId)) {
                        if(!(dest.status == 2) && dest.endDate.compareTo(d)>0) {
                        VolunteerRequest volunteerRequest = new VolunteerRequest(sender,
                                        dest, justification).save();
                        dest.addVolunteerRequest(volunteerRequest);
                        sender.addVolunteerRequest(volunteerRequest);
                        String description = sender.username
                                        + " has requested to volunteer to work on the following item "
                                        + dest.summary + "in the plan " + dest.plan.title
                                        + "of the topic" + dest.plan.topic.title;
                        Notifications.sendNotification(dest.plan.topic.getOrganizer(),
                                        dest.plan.id, "plan", description);
                        Plans.viewAsList(dest.plan.id);
                        }
                } else {
                        justify(itemId, dest.plan.id, 1);
                }
        }

        /**
         * 
         * This Method renders the view where the user enters the justification of
         * his volunteer request given the item id and the plan id
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
         */

        public static void justify(long itemId, long planId, int success) {
                Item item = Item.findById(itemId);
                render(item, planId, success);
        }

	public static void viewMyRequests(long userId) {
		User user = User.findById(userId);
		List<VolunteerRequest> myVolunteerRequests = user.volunteerRequests;
		render(user, myVolunteerRequests);
	}

	/**
	 * This method renders the page for allowing the organizer to view the
	 * volunteer requests to work on items in a certain plan.
	 * 
	 * @story C5S6
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param userId
	 *            : ID of the organizer viewing the requests.
	 * 
	 * @param planId
	 *            : ID of the plan whose items' volunteer requests are to be
	 *            viewed.
	 */
	public static void viewVolunteerRequests(long planId) {
		User user = Security.getConnected();
		Plan plan = Plan.findById(planId);
		List<List<VolunteerRequest>> planVolunteerRequests = null;
		boolean error = false;
		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			for (int i = 0; i < plan.items.size(); i++) {
				planVolunteerRequests.add(plan.items.get(i).volunteerRequests);
			}
			if (planVolunteerRequests != null) {
				for (int i = 0; i < planVolunteerRequests.size(); i++) {
					for (int j = 0; i < planVolunteerRequests.get(i).size(); j++) {
						User sender = planVolunteerRequests.get(i).get(j).sender;
						for (int k = 0; k < planVolunteerRequests.get(i).get(j).destination.assignees
								.size(); k++) {
							if (sender == planVolunteerRequests.get(i).get(j).destination.assignees
									.get(k)) {
								VolunteerRequest request = VolunteerRequest
										.findById(planVolunteerRequests.get(i)
												.get(j).id);
								sender.volunteerRequests.remove(request);
								Item item = request.destination;
								item.volunteerRequests.remove(request);
							}
						}
					}
				}
			}
			render(user, plan, planVolunteerRequests, error);
		} else
			render(error);
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
	public static void accept(long requestId) {
		VolunteerRequest request = VolunteerRequest.findById(requestId);
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
		String s = "Your request to volunteer on item: " + item.summary
				+ " has been accepted.";
		list.add(user);
		for (int i = 0; i < item.plan.topic.organizers.size(); i++) {
			if (org != item.plan.topic.organizers.get(i))
				list.add(item.plan.topic.organizers.get(i));
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
	public static void reject(long requestId) {
		VolunteerRequest request = VolunteerRequest.findById(requestId);
		User org = Security.getConnected();
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		item.volunteerRequests.remove(request);
		String s = "Your request to volunteer on item: " + item.summary
				+ " has been rejected.";
		List<User> list = new ArrayList<User>();
		list.add(user);
		for (int i = 0; i < item.plan.topic.organizers.size(); i++) {
			if (org != item.plan.topic.organizers.get(i))
				list.add(item.plan.topic.organizers.get(i));
		}
		Notifications.sendNotification(list, item.plan.id, "plan", s);
	}
}
