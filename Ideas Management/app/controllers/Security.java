package controllers;

import java.util.LinkedList;

import notifiers.Mail;

import play.data.validation.Required;
import play.mvc.With;
import models.Log;
import models.User;

/**
 * Manages the login actions, and the password recovery
 * 
 * @author Ahmed Maged
 *
 */

public class Security extends Secure.Security {

	/**
	 * Used to get the user who is already connected on the
	 * session.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 * 
	 * @return User
	 * 
	 */
	public static User getConnected() {
		String id = session.get("user_id");
		return User.findById(Long.parseLong(id == null ? "0" : id));
	}

	/**
	 * Needed to check that the logging user has access to the
	 * web site
	 * 
	 * @author Ahmed Maged
	 * 
	 * @stroy C1S18
	 * 
	 * @param username 
	 * 			String the username of the logging user
	 * 
	 * @param password 
	 * 			String the password of the user
	 * 
	 * @return boolean 
	 * 			true if the user is authorised false otherwise
	 */

	public static boolean authenticate(String username, String password) {
		String hashedPassword = Application.hash(password);
		/*User user = User.find(
				"select u from User u where (u.username=? and u.password = ?)",
				username, password).first();*/
		User user = User.find(
				"select u from User u where (u.username=? and u.password = ?)",
				username, hashedPassword).first();
		if (user != null) {
			if(user.state.equals("d")) {
				flash.error("Your account has been deleted");
				return false;
			}
			if(user.state.equals("w"))
			{
				flash.error("You must activate your account first ");
				return false;
			}
			session.put("user_id", user.id);
			return true;
		}
		flash.error("Incorrect username or password");
		return false;		
	}
	
	/**
	 * renders the forgot password view.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 
	 */
	
	public static void forgotPassword() {
		render();
	}
	
	/**
	 * checks that the entered username or e-mail belongs to an existing
	 * user, and if so displays the user's security question.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 
	 * @param username 
	 *			String the username or the e-mail of the user who forgot his password 
	 *
	 */
	
	public static void checkUsername( @Required String username) {
		if( validation.hasErrors() )
		{
			flash.error( "Please enter a valid username/Email" );
			Security.forgotPassword();
		}
		User user = User.find( "select u from User u where u.email=? or u.username=?", username.toLowerCase(), username ).first();
		if(user == null || user.state.equals("d") || user.state.equals("n")) {
			flash.error( "This username/Email does not exist" );
			Security.forgotPassword();
		} else {
			render(user);
		}
	}
	
	/**
	 * recovers the password of the user by sending it to his e-mail address,
	 * if he answered the security question correctly.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 
	 * @param username 
	 * 			String the username or the e-mail of the user who forgot his password
	 * @param answer
	 * 			String the answer of the security question
	 * 	
	 */
	
	public static void recoverPassword(String username, @Required String answer) {
		//User user = User.find("ByUsername", username).first();
		if( validation.hasErrors() )
		{
			flash.error( "You must answer the question!" );
			Security.checkUsername(username);
		}
		User user = User.find("select u from User u where u.username=?", 
				username).first();
		if(answer == null) {
			flash.error("You must answer the security question!");
			Security.checkUsername(username);
		}
		String userAnswer = user.answer;
		if(answer.equalsIgnoreCase(userAnswer)) {
			// should send a generated password by mail
			String newPassword = generatePassword();
			user.password = newPassword;
			user.save();
			Mail.recoverPassword(user.username, user.email, newPassword);
			flash.success("An email is sent to " + user.email 
					+ " with your new password \n");
			Security.checkUsername(username);
		} else {
			flash.error("Incorrect answer");
			Security.checkUsername(username);
		}
	}
	
	/**
	 * Generates a random password of length 7
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 	 
	 * @return String
	 * 			the new generated password
	 */
	
	public static String generatePassword() {
		LinkedList<String> chars = new LinkedList<String>();
		char current = 'a';
		int number = 0;
		// add all the characters in a list of characters
		for(int i = 0; i < 26; i++) {
			chars.add(current + "");
			String tmp = ("" + current).toUpperCase();
			chars.add(tmp);
			chars.add(number + "");
			current++;
			number = (number + 1) % 10;
		}
		// generate a random password of length 7
		String password = "";
		for(int i = 0; i < 7; i++) {
			int x = (int) (Math.random() * (chars.size() - 1));
			password += chars.get(x);
		}
		return password;
	}
}
