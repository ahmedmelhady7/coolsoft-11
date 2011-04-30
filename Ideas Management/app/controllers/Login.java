package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This is just a controller class for the login page 
 * and it still requires a lot of modifications
 */

@With(Secure.class)
public class Login extends Controller {

	
	//@Before
	public static void createUser() {
		//User u = new User("ahmed", "1234", "email@hotmail.com");
		//u.save();
	}
	
	public static void index() {
		User u = Security.getConnected();
		render(u);
		
	}
}
