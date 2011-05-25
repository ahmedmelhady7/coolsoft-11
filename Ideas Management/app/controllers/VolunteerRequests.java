package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.With;
import models.Item;
import models.Log;
import models.Plan;
import models.User;
import models.VolunteerRequest;

@With(Secure.class)
public class VolunteerRequests extends CoolCRUD {



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
		notFoundIfNull(plan);
		List<VolunteerRequest> planVolunteerRequests = new ArrayList<VolunteerRequest>();

		if (Users
				.isPermitted(
						user,
						"accept/Reject user request to volunteer to work on action item in a plan",
						plan.topic.id, "topic")) {
			List<VolunteerRequest> itemRequests = new ArrayList<VolunteerRequest>();
			for (int i = 0; i < plan.items.size(); i++) {
				itemRequests = plan.items.get(i).volunteerRequests;
				VolunteerRequest currentRequest;
				for (int j = 0; j < itemRequests.size(); j++) {
					currentRequest = itemRequests.get(j);
					if (!currentRequest.destination.endDatePassed()
							&& !currentRequest.destination.assignees
									.contains(currentRequest.sender)
							&& Topics.searchByTopic(plan.topic.id).contains(
									user)
							&& currentRequest.destination.status != 2
							&& currentRequest.sender.state == "a"
							&& Users.isPermitted(currentRequest.sender, "use",
									currentRequest.destination.plan.topic.id,
									"topic")) {
						planVolunteerRequests.add(currentRequest);
					}
				}
			}
			render(user, plan, planVolunteerRequests);
		} else {
			BannedUsers.unauthorized();
		}
	}

	/**
	 * This method accepts the volunteer request with the given id and sends
	 * notification to the volunteers and organizers informing them about this.
	 * 
	 * @story C5S6
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @param requestId
	 *            : ID of the volunteer request to be accepted.
	 */
	public static void accept(long requestId) {
		// long reqId = Long.parseLong(requestId);
		VolunteerRequest request = VolunteerRequest.findById(requestId);
		User organizer = Security.getConnected();
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		List<User> userToNotifyList = new ArrayList<User>();
		for (int i = 0; i < item.assignees.size(); i++)
			userToNotifyList.add(item.assignees.get(i));
		String logDescription = "Organizer <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ organizer.id
				+ "\">"
				+ organizer.username
				+ "</a> has accepted user <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "'s </a> volunteer request on item <a href=\"http://localhost:9008/plans/viewaslist?planId="
				+ item.plan.id + "\">" + item.summary + "</a>.";
		Log.addLog(logDescription, item, item.plan, item.plan.topic,
				item.plan.topic.entity, item.plan.topic.entity.organization);
		item.volunteerRequests.remove(request);
		user.itemsAssigned.add(item);
		item.assignees.add(user);
		user.save();
		item.save();
		request.logs.clear();
		request.delete();
		String descriptionToNewVolunteer = "Your request to volunteer on item: "
				+ item.summary + " has been accepted.";
		String descriptionToOldVolunteer = "User: " + user.username
				+ " is now working with you on item: " + item.summary + " .";
		Notifications.sendNotification(user.id, item.plan.id, "plan",
				descriptionToNewVolunteer);
		for (int i = 0; i < item.plan.topic.getOrganizer().size(); i++) {
			if (organizer.id != item.plan.topic.getOrganizer().get(i).id) {
				if (!userToNotifyList.contains(item.plan.topic.getOrganizer()
						.get(i)))
					userToNotifyList.add(item.plan.topic.getOrganizer().get(i));
			}
		}

		for (User userToNotify : userToNotifyList) {
			Notifications.sendNotification(userToNotify.id, item.plan.id,
					"plan", descriptionToOldVolunteer);
		}

	}

	/**
	 * This method rejects the volunteer request with the given id and sends
	 * notification to the volunteers and organizers informing them about this.
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
		User organizer = Security.getConnected();
		User user = request.sender;
		Item item = request.destination;
		String logDescription = "Organizer <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ organizer.id
				+ "\">"
				+ organizer.username
				+ "</a> has rejected user <a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.username
				+ "'s </a> volunteer request on item <a href=\"http://localhost:9008/plans/viewaslist?planId="
				+ item.plan.id + "\">" + item.summary + "</a>.";
		Log.addLog(logDescription, request, item, item.plan, item.plan.topic,
				item.plan.topic.entity, item.plan.topic.entity.organization);
		user.volunteerRequests.remove(request);
		item.volunteerRequests.remove(request);
		user.save();
		item.save();
		request.delete();
		String description = "Your request to volunteer on item: "
				+ item.summary + " has been rejected.";
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.add(user);
		for (int i = 0; i < item.plan.topic.getOrganizer().size(); i++) {
			if (organizer.id != item.plan.topic.getOrganizer().get(i).id) {
				if (!userToNotifyList.contains(item.plan.topic.getOrganizer()
						.get(i)))
					userToNotifyList.add(item.plan.topic.getOrganizer().get(i));
			}
		}

		for (User userToNotify : userToNotifyList) {
			Notifications.sendNotification(userToNotify.id, item.plan.id,
					"plan", description);
		}

	}
}
