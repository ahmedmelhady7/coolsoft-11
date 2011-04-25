package controllers;

import models.Item;
import models.User;
import models.VolunteerRequest;

public class VolunteerRequests extends CRUD{

	
	public static void sendVolunteerRequest(long senderid, long itemid, String justification) {
	User sender = User.findById(senderid);
	Item dest = Item.findById(itemid);
	VolunteerRequest volunteerRequest = new VolunteerRequest(sender, dest, justification).save();
	}
}
