package controllers;

import java.util.Date;

import notifiers.Mail;

import models.Invitation;
import models.User;
import play.mvc.Controller;

/**
 * Creates the home page of the web site
 * 
 * @author Ahmed Maged
 *
 */

public class Home extends Controller {
	
	/**
	 * Renders the welcome page of the web site
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 */
	
	public static void index() {
		User user = Security.getConnected();
		if(user != null) {
			Login.homePage();
		} else {
			render();
		}
	}
	
	/**
	 * Renders the about view
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 */
	
	public static void about() {
		render();
	}
	
	/**
	 * Renders the contact view
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 */
	
	public static void contact() {
		render();
	}
	
	
}
