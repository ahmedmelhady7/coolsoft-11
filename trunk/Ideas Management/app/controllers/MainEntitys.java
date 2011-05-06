package controllers;

import java.util.List;

import controllers.CRUD.ObjectType;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

@With(Secure.class)
public class MainEntitys extends CRUD {

	/**
	 * The method that allows a user to follow a certain entity and also checks
	 * first that the user is permitted to do so.
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
		MainEntity entity = MainEntity.findById(entityId);
		if (entity.followers.contains(user)) 
			System.out.println("You are already a follower");
		else if (Users.isPermitted(user,
				"can follow organization/entities/topics", entityId, "entity")) {
			entity.followers.add(user);
			entity.save();
			user.followingEntities.add(entity);
			user.save();
			redirect(request.controller + ".viewEntity", entity.id,
					"You are now a follower");
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

	/**
	 * The method renders the page for viewing the followers
	 * 
	 * @author Noha Khater
	 * 
	 * @story C2S10
	 * 
	 * @param entityId
	 *            : The id of the entity whose followers will be viewed
	 * 
	 * @param f
	 *            : A string that used to represent a flag
	 * 
	 */
	public static void viewFollowers(long entityId, String f) {
		MainEntity entity = MainEntity.findById(entityId);
		if (f.equals("true")) 
			followEntity(entityId);
		render(entity);
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
		// entity.relatedEntities.add(entity2);
	}

	/**
	 * This method allows the user to create an entity in an organization
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2
	 * 
	 * @param name
	 *            : The name of the entity to be created
	 * 
	 * @param description
	 *            : The description of the entity to be created
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 */
	public static void createEntity(String name, String description, long orgId) {
		boolean canCreate = true;
		Organization org = Organization.findById(orgId);
		for (int i = 0; i < org.entitiesList.size(); i++) {
			if (org.entitiesList.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, org);
			entity.save();
		}
		redirect("Organizations.viewProfile", org.id, "Entity created");
	}

	/**
	 * This method allows the user to create a subentity in an entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S20
	 * 
	 * @param name
	 *            : The name of the entity to be created
	 * 
	 * @param description
	 *            : The description of the entity to be created
	 * 
	 * @param parentId
	 *            : The id of the parent entity
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 */
	public static void createSubEntity(String name, String description,
			long parentId, long orgId) {
		boolean canCreate = true;
		MainEntity parent = MainEntity.findById(parentId);
		Organization org = Organization.findById(orgId);
		for (int i = 0; i < parent.subentities.size(); i++) {
			if (parent.subentities.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, parent, org);
			entity.save();
		}
		redirect("Organizations.viewProfile", org.id, "SubEntity created");
	}

	/**
	 * This method that renders the page for creating an entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 */
	public static void goToCreateEntity(long orgid) {
		Organization org = Organization.findById(orgid);
		render(org);
	}

	/**
	 * This method that renders the page for creating a subentity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S20
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 * @param pId
	 *            : The id of the parent entity
	 * 
	 */
	public static void goToCreateSubEntity(long orgid, long pId) {
		Organization org = Organization.findById(orgid);
		MainEntity parent = MainEntity.findById(pId);
		render(org, parent);
	}

	/**
	 * This method that renders the page for viewing any entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param id
	 *            : The id of the entity that will be viewed
	 * 
	 */
	public static void viewEntity(long id) {
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(id);
		Organization org = entity.organization;
		List<MainEntity> subentities = entity.subentities;
		List<Topic> topicList = entity.topicList;
		List<User> organizers = Users.getEntityOrganizers(entity);
		int canCreateEntity = 0;
		if (user.isAdmin || org.creator.equals(user)
				|| organizers.contains(user)) 
			canCreateEntity = 1;
		int permission = 1;
		int invite = 0;
		int canEdit = 0;
		List<User> allowed = Users.getEntityOrganizers(entity);
		if (org.creator.equals(user) || allowed.contains(user) || user.isAdmin) 
			canEdit = 1;
		if (!Users.isPermitted(user, "post topics", entity.id, "entity")) 
			permission = 0;
		if (Users
				.isPermitted(
						user,
						"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						entity.id, "entity")) 
			invite = 1;
		render(user, org, entity, subentities, topicList, permission, invite,
				canEdit, canCreateEntity);
	}

	/**
	 * This method that renders the page for editing any entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param entityId
	 *            : The id of the entity that will be edited
	 * 
	 */
	public static void editEntityPage(long entityId) {
		MainEntity entity = MainEntity.findById(entityId);
		render(entity);
	}

	/**
	 * The method that allows editing any entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param entityId
	 *            : The id of the entity that will be edited
	 * 
	 * @param name
	 *            : The new name of the entity
	 * 
	 * @param description
	 *            : The new description of the entity
	 * 
	 */
	public static void editEntity(long entityId, String name, String description) {
		MainEntity entity = MainEntity.findById(entityId);
		entity.name = name;
		entity.description = description;
		entity.save();
		redirect(request.controller + ".viewEntity", entity.id, "Entity created");
	}
}
