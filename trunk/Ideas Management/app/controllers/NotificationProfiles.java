package controllers;

import java.util.List;

import models.NotificationProfile;
import models.User;
import play.mvc.Controller;

public class NotificationProfiles extends Controller {

	public static void alterPreferences() {
		List<User> ul = User.findAll();
		User u = ul.get(0);
		List<NotificationProfile> npList = u.openNotificationProfile();
		// String x = u.username;
		render(npList);
	}

	public static void changePreferences(int[] a) {
		for (int i = 0; i < a.length; i++) {
			NotificationProfile np = NotificationProfile.findById(a[i]);
			np.disableNotification();
		}
	}

	public static void disablePreference(long nId) {
		NotificationProfile np = NotificationProfile.findById(nId);
		np.disableNotification();
	}
}
