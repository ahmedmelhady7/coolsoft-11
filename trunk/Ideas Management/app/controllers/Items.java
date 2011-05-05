package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.With;

import models.Item;
import models.User;

@With(Secure.class)
public class Items extends CRUD {

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
	public static void startItem(String id) {
		User user = Security.getConnected();
		long itemId = Long.parseLong(id);
		Item item = Item.findById(itemId);
		List<User> list = item.plan.topic.getOrganizer();
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i) != user)
				list.add(item.assignees.get(i));
		}
		if (item.status == 0) {
			System.out.println("it is started");
			item.status = 1;
			item.save();
			String s = "Work has started on the following item " + item.summary
					+ ".";
			for (User userToNotify : list) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", s);
			}
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
	public static void toggleItem(String id) {
		User user = Security.getConnected();
		long itemId = Long.parseLong(id);
		Item item = Item.findById(itemId);
		List<User> list = item.plan.topic.organizers;
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i) != user)
				list.add(item.assignees.get(i));
		}
		switch (item.status) {
		case 1:
			item.status = 2;
			String s = item.summary + ": Item done!";
			for (User userToNotify : list) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", s);
			}
			break;
		case 2:
			item.status = 1;
			String s2 = item.summary + ": Item now in progress.";
			for (User userToNotify : list) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", s2);
			}
			break;
		default:
			break;
		}
		item.save();
	}
}
