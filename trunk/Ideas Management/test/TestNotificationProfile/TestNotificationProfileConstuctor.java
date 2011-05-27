package TestNotificationProfile;

import models.NotificationProfile;
import models.User;

import org.junit.*;

import play.test.*;


public class TestNotificationProfileConstuctor extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test(timeout = 1000)
	public void constructorTest() {
		User user = new User("aymaestro@gmail.com", "majic", "1234", "Ahmed",
				"Maged", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
		NotificationProfile notificationProfile  = new NotificationProfile(1, "Test type", "Test title", user);
		assertNotNull("A notification profile cannot be initalized", notificationProfile);
	}
}
