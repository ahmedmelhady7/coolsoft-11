package controllers;

import models.Idea;
import models.Item;
import models.Plan;
import models.Topic;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class Plans extends CRUD {
	public static void viewAsList(long planId, String y) {
		long userid = 0;
		User user = User.findById(userid);
		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;

		render(p, itemsList, user, y);
	}

	public static void associateIdeaToPlan(long planId, long ideaId) {
		Idea idea = Idea.findById(ideaId);
		Plan plan = Plan.findById(planId);
		plan.addIdea(idea);
		idea.plan = plan;
	}

	public static void planCreate(long topicId, long id) {

	}

	public static void volunteer(long itemId, long planId) {
		long userid = 0;
		User user = User.findById(userid);
		Item item = Item.findById(itemId);
		String y = "";
		if (item.assignees.contains(user)) {

			y = "You are already assigned to this item";
		} else {

			for (int i = 0; i < item.volunteerRequests.size(); i = i + 1) {
				if (user.id == item.volunteerRequests.get(i).sender.id) {
					y = "You already sent a volunteer request to work on this item";
				}
			}

			for (int i = 0; i < item.assignRequests.size(); i = i + 1) {
				if (user.id == item.assignRequests.get(i).destination.id) {
					y = "You already received an assignment request to work on this item";
				}
			}
		}
		if (y == "") {
			VolunteerRequests.justify(itemId, planId);
		} else {
			viewAsList(planId, y);
		}
	}

	//
	// public static void assign(long itemid) {
	//
	// }

	public static void viewAsTimeline(long planid) {
		// Plan p = Plan.findById(planid);
		// List<Item> itemsList = p.items;
		// render(p, itemsList);
		render();
	}
	/*
	 * @author yassmeen.hussein
	 * 
	 * this method renders the Calendar view of a plan
	 * 
	 * @param planId 
	 *       the id of the plan
	 * 
	 * @return void
	 * 
	 * */
	
	public static void viewAsCalendar(long planId) {

		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;
		long userid = 0;
		User user = User.findById(userid);
		render(p, itemsList, user);
	}


}
