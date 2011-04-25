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
}
