package controllers;

import java.util.Date;

import javax.persistence.PersistenceException;

import com.google.gson.JsonObject;

import play.mvc.Controller;
import models.User;
import notifiers.Mail;

public class Accounts extends Controller {

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
	 * adds an unregistered to the database and sends a generated activation key after checking that
	 * this user's info(attributes) are valid
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S10
	 * 
	 * @param email
	 *            :String , the user's email
	 * 
	 * @param firstName
	 *            : String ,the user's first name
	 * 
	 * @param lastName
	 *            : String ,the user's last name
	 * 
	 * @param username
	 *            : String ,the user's username
	 * 
	 * @param password
	 *            : String ,the user's password
	 * 
	 * @param profession
	 *            : String ,the user's profession
	 * 
	 * @param communityContributionCounter
	 *            : the user's community contribution counter
	 * 
	 * @param dateofBirth
	 *            : String ,the user's date of birth
	 * 
	 * @param country
	 *            : String ,the user's country
	 * 
	 * @param securityQuestion 
	 * 				String , security question of the user
	 * 
	 * @param answer
	 * 				String , the security answer of the user
	 */

	public static void addUser(String email, String username, String password,
			String firstName, String lastName, String securityQuestion,
			String answer, int communityContributionCounter, String dateofBirth,
			String country, String profession) {
		email = email.trim().toLowerCase();
		username = username.trim().toLowerCase();
		firstName = firstName.trim();
		String message = "";
		boolean validUserFlag = false;
		if (email.equals("")) {
			message = "A User must have an email";
			validUserFlag = true;
			System.out.println(message);
		} else if (!(User.find("byEmail", email).fetch().isEmpty())) {
			message = "This Email already exists !";
		} else if (!(validation.email(email).ok)) {
			message = "Please enter a valid email address";
			validUserFlag = true;
			System.out.println(message);
		}else if (username.equals("")) {
			message = "A User must have a username";
			validUserFlag = true;
			System.out.println(message);
		}else if (!(User.find("byUsername", username).fetch().isEmpty())) {
			message = "This Username already exists !";
		} else if (password.equals("")) {
			message = "A User must have a password";
			validUserFlag = true;
			System.out.println(message);
		} else if (firstName.equals("")) {
			message = "A User must have a first name";
			validUserFlag = true;
			System.out.println(message);
		} else if (username.length() >= 20) {
			validUserFlag = true;
			message = "Username cannot exceed 20 characters";
		} else if (username.length() < 3) {
			validUserFlag = true;
			message = "Username cannot be less than 3 characters";
		} else if (password.length() < 4) {
			message = "Password cannot be less than 4 characters";
			validUserFlag = true;
		} else if (password.length() >= 25) {
			message = "First name cannot exceed 25 characters";
			validUserFlag = true;
		} else if (securityQuestion.equals("")) {
			message = "A User must have a security Question";
			validUserFlag = true;
			System.out.println(message);
		} else if (answer.trim().equals("")) {
			message = "A User must have a security answer";
			validUserFlag = true;
			System.out.println(message);
		}

		if (validUserFlag) {
			render(request.controller.replace(".", "/") + "/register.html",
					message);
		}
		User user = new User(email, username, password, firstName, lastName,
				securityQuestion, answer, 0, dateofBirth, country, profession);
		user.state = "w";
		user.activationKey = Application.randomHash(10);
		Mail.activation(user, user.activationKey);
		try
		{
			user._save();

		}
		catch(PersistenceException e)
		{
			
			render(request.controller.replace(".", "/") + "/register.html",
					message);
		}
		render(email, username);
	}

	/**
	 * activates the account of that user by setting the state to "a" and then
	 * renders a message
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S10
	 * 
	 * @params userId long Id of the user that his account will be activated
	 * 
	 * 
	 */
	public static void activate(long userId) {
		User user = User.findById(userId);
		if (user.state.equals("a")) {
			return;
		}
		user.state = "a";
		user._save();
	}

	/**
	 * checks if the given email already exists
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9 , C1S10
	 * 
	 * @param email
	 *            String email , the email to check
	 * 
	 * 
	 * */
	public static boolean checkEmail(String email) {
		User existingUser = User.find("byEmail" + email).first();
		return (existingUser != null);
	}

	/**
	 * checks if the given username already exists
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9 , C1S10
	 * 
	 * @param email
	 *            String email , the email to check
	 * 
	 * 
	 * */
	public static void checkUsername(String username) {

		/*
		 * User existingUser = User.find( "username like '" + username
		 * ).first(); if( existingUser != null ) { flash.error(
		 * "User already exists!" + "\t" + "Please choose another username " );
		 * }
		 */
		JsonObject json = new JsonObject();
		User existingUser = User.find("byUsername" + username).first();
		json.addProperty("existingUser", (existingUser != null));
		renderJSON(json.toString());

	}

}
