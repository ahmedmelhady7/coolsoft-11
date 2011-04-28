package controllers;

import models.AssignRequest;
import models.Item;
import models.User;
import models.VolunteerRequest;
import play.data.validation.Required;

public class AssignRequests extends CRUD {

	/**
	 * 
	 * This Method creates an instance of AssignRequest and adds it to the list of sent assign requests of the sender
	 * and to the list of received assign requests of the receiver 
	 * and to the list of volunteer requests in the item given the user id, the item id and the required justification string
	 * 
	 * @author 	salma.qayed
	 * 
	 * @story 	C5S10
	 * 
	 * @param 	senderId 	: the id of the user sending the assign request
	 * @param 	itemId 	: the id of the item the user is requested to be assigned to work on
	 * @param   destId: the id of the user the assign request is sent to 
	 * 
	 * @return	void
	 */
	public static void sendVolunteerRequest(long senderId, long itemId, long destId) {

		User sender = User.findById(senderId);
		User destination = User.findById(destId);
		Item source = Item.findById(itemId);
		String content = "";
		AssignRequest assignRequest = new AssignRequest(source, destination, sender, content).save();
		source.addAssignRequest(assignRequest);
		sender.addSentAssignRequest(assignRequest);
		sender.addReceivedAssignRequest(assignRequest);
	}

}
