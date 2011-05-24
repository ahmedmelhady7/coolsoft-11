package controllers;

import java.util.Date;

import play.mvc.Controller;
import models.User;
import notifiers.Mail;

public class Accounts extends Controller{
	
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
	
	public static void addUser(String email, String username, String password,
			String firstName, String lastName, String securityQuestion, String answer,
			int communityContributionCounter, Date dateofBirth, String country,
			String profession)
	{
		
		//String hashedPassword =Application.hash(password);
		User user = new User(email,username,password , firstName, lastName,securityQuestion, answer, 0, dateofBirth, country, profession);
		user.state = "w";
		Mail.activation(user);
		user._save();
		
		render(email,username);
	}
	
	/**
	 * activates the account of that user by setting the state to "a" and then
	 * renders a message
	 * 
	 * @author Mostafa Ali 
	 * 
	 * @story C1S10
	 * 
	 * @params userId 
	 * 				long Id of the user that his account ll be activated
	 * 
	 * 
	 */
	public static void activate(long userId) {
		User user = User.findById(userId);
		if(user.state.equals("a"))
		{
			return;
		}
		user.state = "a";
		user._save();
		render(user.state);
	}

}
