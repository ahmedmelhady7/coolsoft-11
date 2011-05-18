package controllers;

import models.User;
import play.mvc.Controller;

public class Home extends Controller {
	
	public static void index() {
		User user = Security.getConnected();
		if(user != null) {
			Login.index();
		} else {
			render();
		}
	}
	
	public static void about() {
		render();
	}
	
	public static void contact() {
		render();
	}

}
