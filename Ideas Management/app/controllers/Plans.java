package controllers;

import models.Item;
import models.Plan;
import models.User;

import java.util.List;

public class Plans extends CRUD {

	public static void viewAsList(long id) {
		long userid = 0;
		User user = User.findById(userid);
		Plan p = Plan.findById(id);
		List<Item> itemsList = p.items;

		render(p, itemsList, user);
	}

//	public static void volunteer(long itemid) {
//		long userid = 0;
//		User user = User.findById(userid);
//		Item item = Item.findById(itemid);
//		int y = 0;
//		if (item.assignees.contains(user)) {
//			y = 1;
//			render();
//		} 
//		
//
//	}
//
//	public static void assign(long itemid) {
//
//	}

}
