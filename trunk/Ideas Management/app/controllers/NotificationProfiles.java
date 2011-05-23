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
public class NotificationProfiles extends CoolCRUD {
	
	/**
	 * Renders the view were the user can change his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 */

	public static void alterPreferences() {		
		User user = Security.getConnected();
		List<NotificationProfile> npList = user.notificationProfiles;		
		render(user, npList);
	}
	
	/**
	 * Enables the preferences of the user according to the list
	 * provided as input to the method.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param b 
	 * 		long[] The list of notification sources to be disabled.	
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
	 * 		long[] The list of notification sources to be disabled.
	 * 
	 */
	public static void disablePreferences(long[] a) {
		for(int i = 0; i < a.length; i++) {
			NotificationProfile notificationProfile = NotificationProfile.findById(a[i]);
			notificationProfile.enabled = false;
			notificationProfile.save();
		}
	}
}
