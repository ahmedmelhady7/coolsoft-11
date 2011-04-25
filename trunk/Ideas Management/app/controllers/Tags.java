package controllers;

import models.MainEntity;
import models.Tag;
import models.User;

public class Tags {
	
	/**
	 * This Method removes a user from the list of followers in a given Tag
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param 	tag 	: the tag that the user is following
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	public static void unfollow(Tag tag, User user) {
		tag.unfollow(user);
	}
}
