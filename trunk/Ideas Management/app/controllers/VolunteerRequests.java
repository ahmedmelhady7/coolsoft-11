package controllers;

import play.data.validation.MinSize;
import play.data.validation.Required;
import models.Item;
import models.User;
import models.VolunteerRequest;

public class VolunteerRequests extends CRUD {

	public static void sendVolunteerRequest(long senderId, long itemId,
			@Required String justification) {

			User sender = User.findById(senderId);
			Item dest = Item.findById(itemId);
			VolunteerRequest volunteerRequest = new VolunteerRequest(sender,
					dest, justification).save();
			dest.addVolunteerRequest(volunteerRequest);
			sender.addVolunteerRequest(volunteerRequest);
		}

	public static void justify(long itemId, long planId) {
		Item item = Item.findById(itemId);
		
		render(item, planId);
	}
}
