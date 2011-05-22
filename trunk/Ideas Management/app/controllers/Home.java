package controllers;

import java.util.Date;

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
	
	/**
	 * renders the page responsible for the registration
	 * 
	 * @story C1S10
	 * 
	 * @author Mostafa Ali
	 * 
	 */
	public static void register() {
		render();
	}
	
	/**
	 * 
	 * This method is responsible for adding an unregistered user
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param email
	 * 				: the user's email
	 * 
	 * @param firstName
	 * 				: the user's first name
	 * 
	 * @param lastName
	 * 				: the user's last name
	 * 
	 * @param username
	 *              : the user's username
	 * 
	 * @param password
	 * 				: the user's password
	 * 
	 * @param profession
	 * 				: the user's profession
	 * 
	 * @param communityContributionCounter
	 * 	            : the user's community contribution counter
	 * 
	 * @param dateofBirth
	 * 				: the user's date of birth
	 * 
	 * @param country
	 * 				: the user's country
	 * 
	 */
	
	public static void addUser(String email, String password, String firstName,
			String lastName, String username, int communityContributionCounter,
			Date dateofBirth, String country, String profession)
	{
			User u = new User(email,username, password, firstName, lastName," "," ",  communityContributionCounter, dateofBirth, country, profession);
			u._save();
		
		//render(email,username,password);
	}


}
