package controllers;

import java.util.List;

import models.Notification;
import models.User;
import play.mvc.Controller;

public class Notifications extends Controller {

	public static void notificationIndex() {
		render();
	}
	
	public static void sendNotification(List<User> list, int notId, String type, String desc) {
		for(int i = 0; i < list.size(); i++) {
			User r = User.findById(list.get(i).id);
			// First check his notification Profile
			for(int j = 0; j < r.notificationProfiles.size(); j++) {
				if(r.notificationProfiles.get(j).notifiableId == notId 
						&& r.notificationProfiles.get(j).notifiableType.equals(type)) {
					if(r.notificationProfiles.get(j).enabled) {
						//profile enabled
						Notification n = new Notification(type, r, desc);
						n.save();						
						break;
					}
				}
			}
		}
	}	
}
