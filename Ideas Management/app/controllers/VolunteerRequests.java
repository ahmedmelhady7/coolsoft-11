package controllers;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.MinSize;
import play.data.validation.Required;
import models.Item;
import models.Plan;
import models.User;
import models.VolunteerRequest;

public class VolunteerRequests extends CRUD {

	 /**
     * 
     * This Method creates an instance of VolunteerRequest and adds it to the list of sent volunteer requests of the user
     * and to the list of volunteer requests in the item given the user id, the item id and the required justification string
     * 
     * @author      salma.qayed
     * 
     * @story       C5S10
     * 
     * @param       senderId        : the id of the user sending the volunteer request
     * @param       itemId  : the id of the item the user wishes to volunteer to work on
     * @param   justification: the reason why the user would like to volunteer in this item
     * 
     * @return      void
     */
    
    public static void sendVolunteerRequest(long senderId, long itemId,
                    @Required String justification) {
                    User sender = User.findById(senderId);
                    Item dest = Item.findById(itemId);
                    VolunteerRequest volunteerRequest = new VolunteerRequest(sender,
                                    dest, justification).save();
                    dest.addVolunteerRequest(volunteerRequest);
                    sender.addVolunteerRequest(volunteerRequest);
                    renderText("Request sent successfully");
            }

    /**
     * 
     * This Method renders the view where the user enters the justification of his volunteer request given the item id and the plan id
     * 
     * @author      salma.qayed
     * 
     * @story       C5S10
     * 
     * @param       planId  : the id of the plan having the item that the user wants to work on
     * @param       itemId  : the id of the item the user wishes to volunteer to work on
     * 
     * @return      void
     */
    
    public static void justify(long itemId, long planId) {
            Item item = Item.findById(itemId);
            
            render(item, planId);
    }

	public static void viewMyRequests(long userId) {
		User user = User.findById(userId);
		List<VolunteerRequest> myVolunteerRequests = user.volunteerRequests;
		render(user, myVolunteerRequests);
	}

	public static void viewVolunteerRequests(long userId, long planId){
		User user = User.findById(userId);
		Plan plan = Plan.findById(planId);
		List<List<VolunteerRequest>> planVolunteerRequests = null;		
		for(int i = 0; i < plan.items.size(); i++){
			planVolunteerRequests.add(plan.items.get(i).volunteerRequests);
		}
		render(user, plan, planVolunteerRequests);
	}
	
	public static void accept(long requestId){
		VolunteerRequest request = VolunteerRequest.findById(requestId);
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		item.volunteerRequests.remove(request);
		user.itemsAssigned.add(item);
		item.assignees.add(user);
	}
	
	public static void reject(long requestId){
		VolunteerRequest request = VolunteerRequest.findById(requestId);
		User user = request.sender;
		user.volunteerRequests.remove(request);
		Item item = request.destination;
		item.volunteerRequests.remove(request);
	}
}
