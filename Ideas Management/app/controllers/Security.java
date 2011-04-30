package controllers;

import models.User;


public class Security extends Secure.Security {

	/**
	 * This method is used to get the user how is already connected on the session.
	 * 
	 * 
	 * @return User
	 * 			
	 */
	public static User getConnected()
	{
		String id = session.get("user_id");
		return User.findById(Long.parseLong(id == null ? "0" : id));	
	}
	
	/**
	 * This method is needed to check that the logining user has access to the website
	 * 
	 *@param username
	 * 
	 *@param password
	 *
	 * @return boolean
	 * 			true if the user is authorised
	 * 			false otherwise
	 */
	
	public static boolean authenticate( String username, String password ) {
		User u = User.find("select u from User u where (u.username=? and u.password = ?)", username,password).first();
		session.put("user_id", u.id);
		if(u != null) {
			return true;
		}
		return false;
	}
	
//	public static void onAuthenticated() {
//		String usr = (isConnected() ? connected() : "");
//		User user =  User.find( "select u from User u where u.email=? or u.name=?", usr.toLowerCase(), usr ).first();
//		session.put("user_id", user.id);
//		System.out.println(user.id);
//		String url = flash.get("url");
//        if(url == null) {
//            url = "/";
//        }
//        redirect(url);
//	}
}
