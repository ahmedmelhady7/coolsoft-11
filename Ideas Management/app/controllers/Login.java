package controllers;

import java.util.Date;

import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This is just a controller class for the login page and it still requires a
 * lot of modifications
 */

@With(Secure.class)
public class Login extends Controller {

	/**
	 * This method is executed only once when running the project using play
	 * we can use this method to insert dummy data in the database for testing.
	 * When terminating the project the inserted data will be removed form the database.
	 * Just uncomment the @Before line
	 * 
	 * @author Ahmed Maged
	 * 
	 * @reurn void
	 */
	@Before
	public static void createUser() {
		
		// u.save();
	}
	
	/**
	 * This is method renders the login page, first the user has to login using his user name
	 * and password and then his profile page will be rendered
	 * 
	 * @author Ahmed Maged
	 * 
	 * @return void
	 */

	public static void index() {
		User u = Security.getConnected();
		render(u);

	}
}
