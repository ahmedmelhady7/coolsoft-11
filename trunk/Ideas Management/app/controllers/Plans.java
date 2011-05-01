package controllers;

import models.Idea;
import models.Item;
import models.Plan;
import models.Topic;
import models.User;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import controllers.CRUD.ObjectType;

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

	/**
	 * This Method renders the page for the plan creation
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param topicId
	 * 			The ID of the topic that this action plan is based upon
	 * @param userId
	 * 			The ID of the user that is creating the plan
	 * 
	 * @return	void
	 */
	public static void planCreate(long topicId, long userId){
		User user = User.findById(userId);
		Topic topic = Topic.findById(topicId);
		render(user,topic);
	}
	/**
	 * This method takes the parameters from the web page of the plan creation to instantiate a plan object
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param title
	 * 			The title of the plan
	 * @param user
	 * 			The user creating the plan
	 * @param startDate
	 * 			The date when the plan will start
	 * @param endDate
	 * 			The date when the plan will end
	 * @param description
	 * 			The description of the plan
	 * @param topic
	 * 			The topic which this plan is based upon
	 * @param requirement
	 * 			The requirements needed for executing this plan
	 * @return	void
	 */
	public static  void create(String title, User user,Date startDate,
			Date endDate, String description, Topic topic, String requirement){
		Plan p = new Plan(title, user, startDate,
				endDate, description, topic, requirement);
		p.save();
		
	}
	/**
	 * This Method renders the page for adding items
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param planId
	 * 			The ID of the plan where the items will be added
	 * @return	void
	 */
	public static void addItem(long planId){
		Plan p = Plan.findById(planId);
		render(p);
	}
	/**
	 * This method takes the parameters of the item from the web page to instantiate an item and add it
	 * to the list of items of a plan 
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param startDate
	 * 			The date when the item(task) should be started
	 * @param endDate 
	 * 			The date when the item(task) should be done
	 * @param descriprtion
	 * 			The description of the item
	 * @param plan
	 * 			The plan that contains this item in its items list
	 * @param summary
	 * 			The summary of the description of the item (for vewing purposes)
	 * @return	void
	 */
	public static void add(Date startDate, Date endDate,
			String description, Plan plan, String summary){
		plan.addItem(startDate, endDate,description, plan, summary);
	}
	/**
	 * This method renders the page for editing a plan 
	 * 
	 * @story 	C5S3  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param planId
	 * 			The ID of the plan that will be edited
	 * @return	void
	 */
	public static void editPlan(long planId){
		Plan plan = Plan.findById(planId);
		render(plan);
	}
	/**
	 * This method takes the parameters from the web page of the plan editing to edit in a certain plan
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param title
	 * 			The title of the plan
	 * @param startDate
	 * 			The date when the plan will start
	 * @param endDate
	 * 			The date when the plan will end
	 * @param description
	 * 			The description of the plan
	 * @param topic
	 * @param requirement
	 * 			The requirements needed for executing this plan
	 * @return	void
	 */
	public static void edit(String title, User user,Date startDate,
			Date endDate, String description, String requirement, long planId){
		Plan p = Plan.findById(planId);
		p.title = title;
		p.startDate = startDate;
		p.endDate = endDate;
		p.description = description;
		p.requirement = requirement;
		p.save();
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
  
//	public static void volunteer(long itemid) {
//	long userid = 0;
//	User user = User.findById(userid);
//	Item item = Item.findById(itemid);
//	int y = 0;
//	if (item.assignees.contains(user)) {
//		y = 1;
//		render();
//	} 
//	
//
//}
//
//public static void assign(long itemid) {
//
//}
}
