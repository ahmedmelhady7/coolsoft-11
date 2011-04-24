package controllers;

import models.MainEntity;
import models.User;

public class MainEntitys extends CRUD {

	public static void unfollow(MainEntity entity, User user) {
		entity.unfollow(user);
	}
}
