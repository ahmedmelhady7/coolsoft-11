package TestUser;

import java.util.ArrayList;
import java.util.List;

import models.Notification;
import models.User;

import org.junit.*;

import play.test.*;

public class TestNotification extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test(timeout = 1000)
	public void notificationNumberTest() {
		User user = new User("ahmed@gmail.com", "ddd", "1234", "Ahmed",
				"Maged", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
		assertEquals("Initially the notifications number should be zero", 0, user.getNotificationNumber());
		Notification notification = new Notification(1, "Test source", "Test title", user, "Test message");
		user.notifications.add(notification);
		Notification notification2 = new Notification(1, "Test source", "Test title", user, "Test message");
		user.notifications.add(notification2);
		assertEquals("The notification number is not incremented", 2, user.getNotificationNumber());		
		user.notifications.remove(0);
		assertEquals("The notification number should be decremented", 1, user.getNotificationNumber());
		Notification notification3 = new Notification(1, "Test source", "Test title", user, "Test message");
		notification3.seen = true;
		user.notifications.add(notification3);
		assertEquals("A seen notification shouldn't be counted as a notification", 1, user.getNotificationNumber());		
	}
	
	@Test(timeout = 1000)
	public void latestNotificationsTest() {
		User user = new User("ahmed@gmail.com", "ddd", "1234", "Ahmed",
				"Maged", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
		assertTrue("Initially there are no latest notifications", user.getLatest().isEmpty());
		Notification notification = new Notification(1, "Test source", "Test title", user, "Test message");
		user.notifications.add(notification);
		Notification notification2 = new Notification(1, "Test source", "Test title", user, "Test message");
		user.notifications.add(notification2);		
		assertSame("The latest notifications should be added to the list", 2, user.getLatest().size());
		user.notifications.get(0).status = "Old";
		assertEquals("An old notification shouldn't be in the list of the latest notifications", 1, user.getLatest().size());
		for (int i = 0; i < user.notifications.size(); i++) {
			user.notifications.get(i).status = "Old";
		}
		assertTrue("After viewing all the notifications the list should be empty", user.getLatest().isEmpty());
	
	}
}
