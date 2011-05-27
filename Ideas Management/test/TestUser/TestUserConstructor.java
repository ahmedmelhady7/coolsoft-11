package TestUser;

import models.User;

import org.junit.*;

import play.test.*;

public class TestUserConstructor extends UnitTest {
	
	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test(timeout = 1000)
	public void constructorTest() {
		User user = new User("mostafa@gmail.com", "mostafa", "1234", "Mostafa",
				"Ali", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Hacker");
		assertNotNull("A user should be intialized",user);
	}

}
