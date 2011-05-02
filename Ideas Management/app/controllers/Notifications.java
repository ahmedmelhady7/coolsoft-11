package controllers;

/**
 * This class is the Notification's Controller
 * 
 * @author Ahmed Maged
 * 
 */

import java.util.List;

import models.Notification;
import models.User;
import play.mvc.Controller;

public class Notifications extends CRUD {
	
	/**
	 * This method renders the view sending a notification to the user
	 * 
	 *  @author Ahmed Maged
	 *  
	 *  @story C1S14
	 *  
	 *  @return void
	 */

	public static void notificationIndex() {
		render();
	}
	
	/**
	 * This method sends a notification to many users first it checks that the user is
	 * enabling his preferences to receive a notification from this source.
	 * 
	 *@author Ahmed Maged
	 *
	 * 
	 * @param list
	 * 			The list of users to be notified.
	 * @param notId
	 * 			The ID of the source of the notification.
	 * @param type
	 * 			The type of the source whether an Idea, Topic, Organisation, Entity or Plan.
	 * @param desc
	 * 			The description message sent as the body of the notification.
	 * 
	 * @return void
	 */

	public static void sendNotification(List<User> list, long notId,
			String type, String desc) {
		for (int i = 0; i < list.size(); i++) {
			User r = User.findById(list.get(i).id);
			// First check his notification Profile
			for (int j = 0; j < r.notificationProfiles.size(); j++) {
				if (r.notificationProfiles.get(j).notifiableId == notId
						&& r.notificationProfiles.get(j).notifiableType
								.equals(type)) {
					if (r.notificationProfiles.get(j).enabled) {
						// profile enabled
						Notification n = new Notification(type, r, desc);
						n.save();
						break;
					}
				}
			}
		}
	}
}
