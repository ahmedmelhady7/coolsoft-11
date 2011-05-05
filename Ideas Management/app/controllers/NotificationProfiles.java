package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.NotificationProfile;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This class is the notification profile's controller.
 * 
 * @author Ahmed Maged
 *
 */

@With(Secure.class)
public class NotificationProfiles extends CRUD {
	
	/**
	 * This method renders the view were the user can change his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @return void
	 * 
	 */

	public static void alterPreferences() {
		//List<User> ul = User.findAll();
		//User u = ul.get(0);
		User u = Security.getConnected();
		List<NotificationProfile> npList = u.openNotificationProfile();
		List<String> info = new ArrayList<String>();
		for(int i = 0; i < npList.size(); i++) {
			String t = npList.get(i).notifiableType;
			if(t.equals("Idea")) {
				Idea idea = Idea.findById(npList.get(i).notifiableId);
				if(idea != null)
					info.add(idea.toString());
			} else {
				if(t.equals("Organisation")) {
					Organization o = Organization.findById(npList.get(i).notifiableId);
					if(o != null)
						info.add(o.toString());
				} else {
					if(t.equals("Topic")) {
						Topic topic = Topic.findById(npList.get(i).notifiableId);
						if(topic != null)
							info.add(topic.toString());
					} else {
						if(t.equals("Plan")) {
							Plan p = Plan.findById(npList.get(i).notifiableId);
							if(p != null)
								info.add(p.toString());
						} else {
							if(t.equals("Tag")) {
								Tag tag = Tag.findById(npList.get(i).notifiableId);
								if(tag != null) {
									info.add(tag.toString());
								}
							}
						}
					}
				}
			}
		}
		// String x = u.username;
		render(npList, info);
	}
	
	/**
	 * This method enables the preferences of the user according to the list
	 * provided as input to the method.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param a
	 * 		The list of notification sources to be disabled.
	 * 
	 * @return void
	 * 
	 */
	public static void enablePreferences(long[] b) {
		for(int i = 0; i < b.length; i++) {
			NotificationProfile np = NotificationProfile.findById(b[i]);
			np.enabled = true;
			np.save();
		}
	}
	
	/**
	 * This method disables the preferences of the user according to the list
	 * provided as input to the method.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param a
	 * 		The list of notification sources to be disabled.
	 * 
	 * @return void
	 * 
	 */
	public static void disablePreferences(long[] a) {
		for(int i = 0; i < a.length; i++) {
			NotificationProfile np = NotificationProfile.findById(a[i]);
			np.enabled = false;
			np.save();
		}
	}
	
	public static void changePreferences(long[] a, long[] b, int y) {
		System.out.println("I am here");
		for(int i = 0; i < a.length; i++) {
			NotificationProfile np = NotificationProfile.findById(a[i]);
			np.enabled = false;
			np.save();
		}
		for(int i = 0; i < b.length; i++) {
			NotificationProfile np = NotificationProfile.findById(b[i]);
			np.enabled = true;
			np.save();
		}
	}
	public static void goHome() {
		alterPreferences();
	}
}
