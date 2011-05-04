package controllers;

import java.util.List;

import play.data.validation.Required;
import models.MainEntity;
import models.Organization;
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
	 */

	public static void followEntity(long entityId) {
		User user = Security.getConnected();
		MainEntity e = MainEntity.findById(entityId);
		if (Users.isPermitted(user, "follow", entityId, "entity")) {
			e.followers.add(user);
			user.followingEntities.add(e);
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

	public static void createEntity(@Required String name,
			@Required String desc, Organization org) {
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render(name, desc);
		}
		MainEntity existing = MainEntity.find("name like '" + name + "'")
				.first();
		if (existing != null) {
			flash.error("Entity already exists!" + "\n\t\t"
					+ "Please choose another organization name.");
			render(name, desc);
		}
		MainEntity e = new MainEntity(name, desc, org);
		e.save();
		flash.success("Your entity has been created.");
	}

	/**
	 * This method adds entity2 to the list of entities in entity
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param entity
	 *            : first entity to be related
	 * 
	 * @param entity2
	 *            : second entity to be related
	 */
	public static void relateEntity(MainEntity entity, MainEntity entity2) {
		entity.relatedEntities.add(entity2);
	}

	// public static void createEntity(@Required String n,
	// @Required String d, Organization org) {
	// MainEntity entity = new MainEntity(n, d, org);
	// entity.save();
	// flash.success("Done =)");
	//		
	// }

	public static void viewEntity(long id) {
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(id);
		Organization org = entity.organization;
		List<MainEntity> subentities = entity.subentities;
		render(user, org, entity, subentities);
	}
}
