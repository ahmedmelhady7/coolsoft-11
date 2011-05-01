package controllers;

import models.MainEntity;
import models.Topic;
import models.User;

public class MainEntitys extends CRUD {

	/**
	 * The method that allows a user to follow a certain entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param entityId
	 *            : The id of the entity that the user wants to follow
	 * 
	 * @param user
	 *            : The user who wants to follow an entity
	 */

	public static void followEntity(long entityId, User user) {
		MainEntity e = MainEntity.findById(entityId);
		if (Users.isPermitted(user, "follow", entityId, "entity")) {
			e.followers.add(user);
			user.followingEntities.add(e);
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

}
