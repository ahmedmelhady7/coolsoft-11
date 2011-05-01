package controllers;

import java.util.List;

import models.NotificationProfile;
import models.User;
import play.mvc.Controller;

/**
 * This class is the notification profile's controller.
 * 
 * @author Ahmed Maged
 *
 */

public class NotificationProfiles extends Controller {
	
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
		List<User> ul = User.findAll();
		User u = ul.get(0);
		List<NotificationProfile> npList = u.openNotificationProfile();
		// String x = u.username;
		render(npList);
	}
	
	/**
	 * This method changes the preferences of the user according to the list
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

	public static void changePreferences(long[] a) {
		for (int i = 0; i < a.length; i++) {
			NotificationProfile np = NotificationProfile.findById(a[i]);
			np.disableNotification();
		}
	}
}
