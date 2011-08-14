package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Games extends Controller{
	
	public static void doodle()
	{
		User user = Security.getConnected();
		render(user);
	}

}
