package controllers;

import java.util.List;

import models.Item;
import models.User;

public class Items extends CRUD {

	
	
	public static void viewAssignedItems(long userid) {
		User user = User.findById(userid);
		List<Item> assignedItems = user.itemsAssigned;
		render(assignedItems);
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
}
