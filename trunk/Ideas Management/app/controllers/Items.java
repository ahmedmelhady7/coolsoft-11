package controllers;

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

	/*
	public static void viewAssignedItems(long userId) {
		User user = User.findById(userId);
		List<Item> assignedItems = user.itemsAssigned;
		render(assignedItems, userId);
	}

	public static void startItem(long id) {
		Item item = Item.findById(id);
		if (item.status == 0) {
			item.status = 1;
		}
	}

	public static void toggleItem(long id) {
		Item item = Item.findById(id);
		switch (item.status) {
		case 1:
			item.status = 2;
			break;
		case 2:
			item.status = 1;
			break;
		default:
			break;
		}
	}
	*/
}
