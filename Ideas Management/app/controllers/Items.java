package controllers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import play.mvc.With;
import models.Item;
import models.MainEntity;
import models.Plan;
import models.Tag;
import models.User;
import models.*;

@With(Secure.class)
public class Items extends CoolCRUD {

	/**
	 * 
	 * This Method renders the view of the list of assigned items of the logged
	 * in user
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S11
	 * 
	 */

	public static void viewAssignedItems() {
		User user = Security.getConnected();
		long userId = user.id;
		List<Item> assignedItems = user.itemsAssigned;
		render(assignedItems, userId, user);
	}

	/**
	 * 
	 * This method changes the item with the given id's status from "New" (0) to
	 * "In progress" (1). The method then logs this action and sends
	 * notifications to organizers and other assigned users informing them about
	 * this change.
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S13
	 * 
	 * @param id
	 *            : the id of the item to be started
	 */
	public static void startItem(long id) {
		User user = Security.getConnected();
		Item item = Item.findById(id);
		notFoundIfNull(item);
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.addAll(item.plan.topic.getOrganizer());
		if (userToNotifyList.contains(user)) {
			userToNotifyList.remove(user);
		}
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i).id != user.id
					&& !userToNotifyList.contains(item.assignees.get(i)))
				userToNotifyList.add(item.assignees.get(i));
		}
		if (item.status == 0) {
			System.out.println("it is started");
			item.status = 1;
			item.save();
			String description = "Work has started on the following item: "
					+ item.summary + " by user" + user.username + ".";
			String logDescription = "Work has started on the following item: <a href=\"http://localhost:9008/plans/viewaslist?planId="
					+ item.plan.id
					+ "\">"
					+ item.summary
					+ "</a>"
					+ " by user <a href=\"http://localhost:9008/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>.";
			Log.addLog(logDescription, item, item.plan, item.plan.topic,
					item.plan.topic.entity, item.plan.topic.entity.organization);
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", description);
			}
		}
	}

	/**
	 * 
	 * This method changes the item with the given id's status from
	 * "In progress" (1) to "Done" (2) and vice versa. The method then logs this
	 * action and sends notifications to organizers and other assigned users
	 * informing them about this change.
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S13
	 * 
	 * @param id
	 *            : the id of the item to be marked as done or in progress.
	 */
	public static void toggleItem(long id) {
		User user = Security.getConnected();
		Item item = Item.findById(id);
		notFoundIfNull(item);
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.addAll(item.plan.topic.getOrganizer());
		if (userToNotifyList.contains(user)) {
			userToNotifyList.remove(user);
		}
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i).id != user.id
					&& !userToNotifyList.contains(item.assignees.get(i)))
				userToNotifyList.add(item.assignees.get(i));
		}
		switch (item.status) {
		case 1:
			item.status = 2;
			String description = item.summary
					+ ": Item now marked done by user: " + user.username + "!";
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", description);
			}
			String logDescription = "<a href=\"http://localhost:9008/plans/viewaslist?planId="
					+ item.plan.id
					+ "\">"
					+ item.summary
					+ ": Item now marked done by user <a href=\"http://localhost:9008/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>.";
			Log.addLog(logDescription, item, item.plan, item.plan.topic,
					item.plan.topic.entity, item.plan.topic.entity.organization);
			break;
		case 2:
			item.status = 1;
			String descriptionIfInProgress = item.summary
					+ ": Item now marked in progress by user: " + user.username
					+ ".";
			String logDescriptionIfInProgress = "<a href=\"http://localhost:9008/plans/viewaslist?planId="
					+ item.plan.id
					+ "\">"
					+ item.summary
					+ ": Item now marked in progress by user <a href=\"http://localhost:9008/users/viewprofile?userId="
					+ user.id + "\">" + user.username + "</a>.";
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", descriptionIfInProgress);
			}
			Log.addLog(logDescriptionIfInProgress, item, item.plan,
					item.plan.topic, item.plan.topic.entity,
					item.plan.topic.entity.organization);
			break;
		default:
			break;
		}
		item.save();
		JsonObject json = new JsonObject();
		if (item.status == 1) {
			json.addProperty("name",
					"Your item is in progress. Mark it as done");
		} else {
			json.addProperty("name",
					"Your item is done. Mark it as in progress");
		}
		renderJSON(json.toString());
		// return true;
	}

}
