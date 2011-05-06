package controllers;

/**
 * This class is the Notification's Controller
 * 
 * @author Ahmed Maged
 * 
 */

import java.util.List;

import models.Idea;
import models.Notification;
import models.NotificationProfile;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Notifications extends CRUD {
	
	/**
	 * This method renders the view sending a notification to the user
	 * This method is only there for testing the notification API
	 * 
	 *  @author Ahmed Maged
	 *  
	 *  @story C1S14
	 *  
	 */

	public static void notificationIndex() {
		render();
	}
	
	/**
	 * This method sends a notification to one user first it checks that the user is
	 * enabling his preferences to receive a notification from this source.
	 * 
	 *@author Ahmed Maged
	 *
	 * 
	 * @param uId
	 * 			The ID of the user to be notified.
	 * @param notId
	 * 			The ID of the source of the notification.
	 * @param type
	 * 			The type of the source whether an Idea, Topic, Organisation, Entity or Plan.
	 * @param desc
	 * 			The description message sent as the body of the notification.
	 * 
	 */
	
	public static boolean sendNotification(long userId, long notificationId, String type, String description) {
		User user = User.findById(userId);
		List<NotificationProfile> nProfiles = user.notificationProfiles;
		boolean contains = false;
		boolean isenabled = true;
		String title = "No title!";
		if(type.equalsIgnoreCase("Idea")) {
			Idea idea = Idea.findById(notificationId);
			if(idea != null)
				title = idea.toString();
		} else {
			if(type.equalsIgnoreCase("Organization") || type.equalsIgnoreCase("Organisation")) {
				Organization organization = Organization.findById(notificationId);
				if(organization != null)
					title = organization.toString();
			} else {
				if(type.equalsIgnoreCase("Topic")) {
					Topic topic = Topic.findById(notificationId);
					if(topic != null)
						title = topic.toString();
				} else {
					if(type.equalsIgnoreCase("Plan")) {
						Plan plan = Plan.findById(notificationId);
						if(plan != null)
							title = plan.toString();
					} else {
						if(type.equalsIgnoreCase("Tag")) {
							Tag tag = Tag.findById(notificationId);
							if(tag != null) {
								title = tag.toString();
							}
						} else {
							if(type.equalsIgnoreCase("User")) {
								User user2 = User.findById(notificationId);
								if(user2 != null) {
									title = user2.toString();
								}
							}
						}
					}
				}
			}
		}
		/**
		 * check that the notification profile has the sending source
		 * if not then add it, if present the check if enabled or not.
		 */
		if(type.equalsIgnoreCase("User")) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			Notification n = new Notification(notificationId, type, title, user, description);
			n.save();
			return true;
		}
		for (int i = 0; i < nProfiles.size(); i++) {
			if(nProfiles.get(i).notifiableId == notificationId 
					&& nProfiles.get(i).notifiableType.equals(type)) {
				isenabled = nProfiles.get(i).enabled;
				break;
			}
		}		
		// Adding the NP if not there
		if(!contains) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			NotificationProfile notificationProfile = new NotificationProfile(notificationId, type, title, user);
			notificationProfile.save();			
			isenabled = true;
		}
		// Send the notification if enabled
		if(isenabled) {
			Notification notification = new Notification(notificationId, type, title, user, description);
			notification.save();
			return true;
		}
		return false;
	}
	
	/**
	 * This method is just for testing that the sendNotification
	 * method works and it will be removed as soon as everybody
	 * confirms that the method is working.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param directed
	 * @param notId
	 * @param type
	 * @param desc
	 * 
	 * @return void
	 */
	
	public static void sendNotify(String directed, long notId,
			String type, String desc) {
		User u = User.find("byUsername", directed).first();
		List<NotificationProfile> np = u.notificationProfiles;
		String title = "No title!";
		if(type.equals("Idea")) {
			Idea idea = Idea.findById(notId);
			if(idea != null)
				title = idea.toString();
		} else {
			if(type.equals("Organisation")) {
				Organization o = Organization.findById(notId);
				if(o != null)
					title = o.toString();
			} else {
				if(type.equals("Topic")) {
					Topic topic = Topic.findById(notId);
					if(topic != null)
						title = topic.toString();
				} else {
					if(type.equals("Plan")) {
						Plan p = Plan.findById(notId);
						if(p != null)
							title = p.toString();
					} else {
						if(type.equals("Tag")) {
							Tag tag = Tag.findById(notId);
							if(tag != null) {
								title = tag.toString();
							} else {
								if(type.equalsIgnoreCase("User")) {
									User user2 = User.findById(notId);
									if(user2 != null) {
										title = user2.toString();
									}
								}
							}
						}
					}
				}
			}
		}
		/**
		 * check that the notification profile has the sending source
		 * if not then add it, if present the check if enabled or not.
		 */
		if(type.equalsIgnoreCase("User")) {
			Notification n = new Notification(notId, type, title, u, desc);
			n.save();
			return;
		}
		boolean contains = false;
		boolean isenabled = true;
		/**
		 * check that the notification profile has the sending source
		 * if not then add it, if present the check if enabled or not.
		 */
		for (int i = 0; i < np.size(); i++) {
			if(np.get(i).notifiableId == notId && np.get(i).notifiableType.equals(type)) {
				isenabled = np.get(i).enabled;
				contains = true;
				break;
			}
		}
		// Adding the NP if not there
		if(!contains) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			NotificationProfile np2 = new NotificationProfile(notId, type, title, u);
			np2.save();
			//u.notificationProfiles.add(np2);
			isenabled = true;
		}
		// Send the notification if enabled
		if(isenabled) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			Notification n = new Notification(notId, type, title, u, desc);
			n.save();
		}
		render();
	}
}
