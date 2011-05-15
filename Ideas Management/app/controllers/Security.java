package controllers;

import play.data.validation.Required;
import play.mvc.With;
import models.Log;
import models.User;

public class Security extends Secure.Security {

	/**
	 * Used to get the user how is already connected on the
	 * session.
	 * 
	 * @author C1
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
	 * website
	 * 
	 * @author C1
	 * 
	 * @param username String
	 * 			the username of the logging user
	 * 
	 * @param password String
	 * 			the password of the user
	 * 
	 * @return boolean 
	 * 			true if the user is authorised false otherwise
	 */

	public static boolean authenticate(String username, String password) {
		User user = User.find(
				"select u from User u where (u.username=? and u.password = ?)",
				username, password).first();
		if (user != null) {
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
	 * @story C1S18
	 * 
	 */
	
	public static void forgotPassword()
	{
		render();
	}
	
	/**
	 * chedcks that the entered username or e-mail belongs to an existing
	 * user, and if so dispalys the user's security question.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 * 
	 * @param username String
	 *			the username or the e-mail of the user who forgot his password 
	 *
	 */
	public static void checkUsername( @Required String username ) {
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
			//flash.success("User exists");
			//Security.forgotPassword();
			render(user);
		}
	}
	
	/**
	 * recovers the password of the user by sending it to his e-mail address,
	 * if he answered the security question correctly.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 * 
	 * @param username String 
	 * 			the username or the e-mail of the user who forgot his password
	 * @param answer String
	 * 			the answer of the security question
	 * 	
	 */
	
	public static void recoverPassword(String username, @Required String answer) {
		//User user = User.find("ByUsername", username).first();
		if( validation.hasErrors() )
		{
			flash.error( "You must answer the question!" );
			Security.checkUsername(username);
		}
		User u = User.find("select u from User u where u.username=?", 
				username).first();
//		if(user == null) {
//			flash.error("NULL");
//			Security.checkUsername();
//		}
		if(answer == null) {
			flash.error("You must answer the security question!");
			Security.checkUsername(username);
		}
		if(answer.equalsIgnoreCase("rogerfederer")) {
			// should send a generated password by mail
			flash.success("your passord is " + u.password);
			Security.checkUsername(username);
		} else {
			flash.error("Incorrect answer");
			Security.checkUsername(username);
		}
	}

	// public static void onAuthenticated() {
	// String usr = (isConnected() ? connected() : "");
	// User user = User.find(
	// "select u from User u where u.email=? or u.name=?", usr.toLowerCase(),
	// usr ).first();
	// session.put("user_id", user.id);
	// System.out.println(user.id);
	// String url = flash.get("url");
	// if(url == null) {
	// url = "/";
	// }
	// redirect(url);
	// }
}
