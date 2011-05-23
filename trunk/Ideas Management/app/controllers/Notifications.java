package controllers;

/**
 * This class is the Notification's Controller
 * 
 * @author Ahmed Maged
 * 
 */

import java.util.List;

import models.Idea;
import models.MainEntity;
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
public class Notifications extends CoolCRUD {
	
	/**
	 * Renders the view sending a notification to the user
	 * Tests the notification API
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
	 * Sends a notification to one user first it checks that the user is
	 * enabling his preferences to receive a notification from this source.
	 * 
	 *@author Ahmed Maged
	 *
	 * 
	 * @param userId long
	 * 			The ID of the user to be notified.
	 * @param notificationId long
	 * 			The ID of the source of the notification.
	 * @param type String
	 * 			The type of the source whether an Idea, Topic, Organisation, Entity or Plan.
	 * @param description String
	 * 			The description message sent as the body of the notification.
	 * 
	 */
	
	public static boolean sendNotification(long userId, long notificationId, String type, String description) {
		User user = User.findById(userId);		
		if (user == null) {
			return false;
		}
		if (user.state.equals("d")) {
			return false;
		}
		if (user.state.equals("n")) {
			return false;
		}
		/**
		 * If the source type is a user then send the notification
		 */
		if (type.equalsIgnoreCase("User")) {
			User sender = User.findById(notificationId);
			Notification notification = new Notification(notificationId, "User", sender.username, user, description);
			notification.save();
			user.notificationsNumber++;
			return true;
		}
		List<NotificationProfile> nProfiles = user.notificationProfiles;
		boolean contains = false; // contains the profile
		boolean isenabled = true; // preference enabled
		String title = "No title!";
		if (type.equalsIgnoreCase("Idea")) {
			Idea idea = Idea.findById(notificationId);
			if(idea != null) {				
				title = idea.toString();
			}
				
		} else {
			if (type.equalsIgnoreCase("Organization") || type.equalsIgnoreCase("Organisation")) {
				Organization organization = Organization.findById(notificationId);
				if (organization != null) {					
					title = organization.toString();
				}
					
			} else {
				if(type.equalsIgnoreCase("Topic")) {
					Topic topic = Topic.findById(notificationId);
					if(topic != null) {						
						title = topic.toString();
					}
						
				} else {
					if(type.equalsIgnoreCase("Plan")) {
						Plan plan = Plan.findById(notificationId);
						if(plan != null) {							
							title = plan.toString();
						}
							
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
							} else {
								if(type.equalsIgnoreCase("Entity")) {
									MainEntity mainEntity = MainEntity.findById(notificationId);
									if(mainEntity != null) {										
										title = mainEntity.toString();
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
		 * if not then add it, if present then check if enabled or not.
		 */
		for (int i = 0; i < nProfiles.size(); i++) {
			if(nProfiles.get(i).notifiableId == notificationId 
					&& nProfiles.get(i).notifiableType.equalsIgnoreCase(type)) {
				isenabled = nProfiles.get(i).enabled;
				contains = true;
				break;
			}
		}		
		// Adding the NP if not there
		if (!contains) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			NotificationProfile notificationProfile = new NotificationProfile(notificationId, type, title, user);
			notificationProfile.save();
			isenabled = true;
		}
		// Send the notification if enabled
		if (isenabled) {
			Notification notification = new Notification(notificationId, type, title, user, description);
			notification.save();
			user.notificationsNumber++;
			return true;
		}
		return false;
	}
	
	/**
	 * Just for testing that the sendNotification
	 * method works and it will be removed as soon as everybody
	 * confirms that the method is working.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param directed String
	 * 				the reciever
	 * @param notId long
	 * 			the ID of the source
	 * @param type String
	 * 			the type of the source
	 * @param description String
	 * 			the message in the notification
	 * 
	 */
	
	public static void sendNotify(String directed, long notId,
			String type, String description) {
		User user = User.find("byUsername", directed).first();
		if (user == null) {
			String result = "Not Sent";
			render(result);
		}
		if (user.state.equals("d")) {
			String result = "Not Sent";
			render(result);
		}
		if (user.state.equals("n")) {
			String result = "Not Sent";
			render(result);
		}
		/**
		 * If the source type is a user then send the notification
		 */
		if (type.equalsIgnoreCase("User")) {			
			User sender = User.findById(notId);
			Notification notification = new Notification(notId, "User", sender.username, user, description);			
			notification.save();
			user.notificationsNumber++;
			String result = "Sent Successfully";
			render(result);
		}
		List<NotificationProfile> nProfiles = user.notificationProfiles;
		boolean contains = false; // contains the profile
		boolean isenabled = true; // preference enabled
		String title = "No title!";
		if (type.equalsIgnoreCase("Idea")) {
			Idea idea = Idea.findById(notId);
			if(idea != null) {				
				title = idea.toString();
			}
				
		} else {
			if (type.equalsIgnoreCase("Organization") || type.equalsIgnoreCase("Organisation")) {
				Organization organization = Organization.findById(notId);
				if (organization != null) {					
					title = organization.toString();
				}
					
			} else {
				if(type.equalsIgnoreCase("Topic")) {
					Topic topic = Topic.findById(notId);
					if(topic != null) {						
						title = topic.toString();
					}
						
				} else {
					if(type.equalsIgnoreCase("Plan")) {
						Plan plan = Plan.findById(notId);
						if(plan != null) {							
							title = plan.toString();
						}
							
					} else {
						if(type.equalsIgnoreCase("Tag")) {
							Tag tag = Tag.findById(notId);
							if(tag != null) {								
								title = tag.toString();
							}
						} else {
							if(type.equalsIgnoreCase("User")) {
								User user2 = User.findById(notId);
								if(user2 != null) {
									title = user2.toString();
								}
							} else {
								if(type.equalsIgnoreCase("Entity")) {
									MainEntity mainEntity = MainEntity.findById(notId);
									if(mainEntity != null) {										
										title = mainEntity.toString();
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
		 * if not then add it, if present then check if enabled or not.
		 */
		for (int i = 0; i < nProfiles.size(); i++) {
			if(nProfiles.get(i).notifiableId == notId 
					&& nProfiles.get(i).notifiableType.equalsIgnoreCase(type)) {
				isenabled = nProfiles.get(i).enabled;
				contains = true;
				break;
			}
		}		
		// Adding the NP if not there
		if (!contains) {
			type = (type.charAt(0) + "").toUpperCase() + type.substring(1).toLowerCase();
			NotificationProfile notificationProfile = new NotificationProfile(notId, type, title, user);
			notificationProfile.save();
			isenabled = true;
		}
		// Send the notification if enabled
		if (isenabled) {
			Notification notification = new Notification(notId, type, title, user, description);
			notification.save();
			user.notificationsNumber++;
			String result = "Sent Successfully";
			render(result);
		}
		String result = "Sent Successfully";
		render(result);
	}
}
