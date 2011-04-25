package controllers;

import models.MainEntity;
import models.Organization;
import models.User;

public class Organizations extends CRUD{
	
	/**
	 * This Method removes a user from the list of followers in an organization
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param   org     : the organization the user is following
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	
	public static void unfollow(Organization org, User user) {
		org.unfollow(user);
	}
}
