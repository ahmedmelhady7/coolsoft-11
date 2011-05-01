package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Item;
import models.User;

public class Items extends CRUD {

	/**
	 * 
	 * This Method renders the view of the list of items the user is assigned
	 * to, given the user id
	 * 
	 * @author salma.qayed
	 * 
	 * @story C5S11
	 * 
	 * @param userid
	 *            : the id of the user whose list of assigned items will be
	 *            viewed
	 * 
	 * @return void
	 */

	public static void viewAssignedItems(long userId) {
		User user = User.findById(userId);
		List<Item> assignedItems = user.itemsAssigned;
		render(assignedItems, userId);
	}

	/**
	 * 
	 * This method changes the item with the given id's status from "New" (0) to
	 * "In progress" (1).
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
		List<User> list = item.plan.topic.organizers;
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i) != user)
				list.add(item.assignees.get(i));
		}
		if (item.status == 0) {
			item.status = 1;
			String s = "Work has started on the following item " + item.summary
					+ ".";
			Notifications.sendNotification(list, item.id, "item", s);
		}
	}

	/**
	 * 
	 * This method changes the item with the given id's status from
	 * "In progress" (1) to "Done" (2) and vice versa.
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
		List<User> list = item.plan.topic.organizers;
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i) != user)
				list.add(item.assignees.get(i));
		}
		switch (item.status) {
		case 1:
			item.status = 2;
			String s = item.summary + ": Item done!";
			Notifications.sendNotification(list, item.id,
					"item", s);
			break;
		case 2:
			item.status = 1;
			String s2 = item.summary + ": Item now in progress.";
			Notifications.sendNotification(list, item.id,
					"item", s2);
			break;
		default:
			break;
		}
	}
}
