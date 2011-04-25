package controllers;

import models.MainEntity;
import models.User;

public class MainEntitys extends CRUD {

	/**
	 * This Method removes a user from the list of followers in an entity
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param   entity  : the entity the user is following
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	
	public static void unfollow(MainEntity entity, User user) {
		entity.unfollow(user);
	}
}
