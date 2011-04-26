package controllers;

import models.Item;
import models.Plan;
import java.util.List;

public class Plans extends CRUD {

	public static void viewAsList(long id) {
		Plan p = Plan.findById(id);
		List<Item> itemsList = p.items;
		render(p, itemsList);
	}

	public static void volunteer(long itemid) {

	}

	public static void assign(long itemid) {

	}

}
