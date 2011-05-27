package TestNotification;

import models.Notification;
import models.User;

import org.junit.*;

import play.test.*;

public class TestNotificationConstuctor extends UnitTest {
	
	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test(timeout = 1000)
	public void constructorTest() {
		User user = new User("aymaestro@gmail.com", "majic", "1234", "Ahmed",
				"Maged", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
		Notification notification = new Notification(1, "Test source", "Test title", user, "Test message");
		assertNotNull("A notification cannot be intialized",notification);
	}

}
