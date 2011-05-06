package controllers;

import play.mvc.With;
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
	 * Needed to check that the loging user has access to the
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
		User u = User.find(
				"select u from User u where (u.username=? and u.password = ?)",
				username, password).first();
		if (u != null) {
			session.put("user_id", u.id);
			return true;
		}
		return false;
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
