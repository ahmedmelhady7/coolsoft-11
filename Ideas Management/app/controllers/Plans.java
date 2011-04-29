package controllers;

import models.Idea;
import models.Item;
import models.Plan;
import models.Topic;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class Plans extends CRUD {

	public static void viewAsList(long planId) {
		long userid = 0;
		User user = User.findById(userid);
		Plan p = Plan.findById(planId);
		List<Item> itemsList = p.items;

		render(p, itemsList, user);
	}

	
	public static void associateIdeaToPlan(long planId, long ideaId) {
		Idea idea = Idea.findById(ideaId);
		Plan plan = Plan.findById(planId);
		plan.addIdea(idea);
		idea.plan = plan;
	}

	public static void planCreate(Topic t, long id){
		Idea i  = Idea.findById(id);
		long userid = 0;
		User user = User.findById(userid);
		Plan p = new Plan();
		p.topic = t;
		p.items = new ArrayList<Item>();
		p.ideas = new ArrayList<Idea>();
		p.ideas.add(i);
		p.requirements = new ArrayList<String>();
		p.madeBy = user;
		p.status = "new";
		
		render(p, user);
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
	
	public static void viewAsTimeline(long planid ){
		Plan p = Plan.findById(planid);
		List<Item> itemsList = p.items;
		render(p, itemsList);
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
