package controllers;

import java.util.ArrayList;
import java.util.List;

import controllers.CRUD.ObjectType;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import play.mvc.With;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.RenameEndRelationshipRequest;
import models.Topic;
import models.User;

@With(Secure.class)
public class MainEntitys extends CRUD {

	/**
	 * renders the related entity and the list of other entities in the
	 * organization to the view
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param entityId
	 *            : id of the entity to be related
	 * 
	 * @param organizationId
	 *            : id of the organization the entity belongs to
	 */
	public static void createRelation(long entityId, long organizationId) {

		MainEntity entity = MainEntity.findById(entityId);
		Organization organization = Organization.findById(organizationId);
		List<MainEntity> entityList = new ArrayList<MainEntity>();

		if (organization.entitiesList != null) {
			entityList = organization.entitiesList;
			entityList.remove(entity);
		}

		for (int i = 0; i < entityList.size(); i++) {
			if (!entityList.get(i).createRelationship)
				entityList.remove(entityList.get(i));
		}
		render(entity, entityList);
	}

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
		else {
			entity.followers.add(user);
			entity.save();
			user.followingEntities.add(entity);
			user.save();
			redirect(request.controller + ".viewEntity", entity.id,
					"You are now a follower");
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
	 * This method allows the user to create an entity in an organization
	 * 
	 * @author Noha Khater, Omar Faruki
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
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 */
	public static void createEntity(String name, String description,
			long orgId, boolean createRelationship) {
		boolean canCreate = true;
		System.out.println(createRelationship);
		Organization org = Organization.findById(orgId);
		for (int i = 0; i < org.entitiesList.size(); i++) {
			if (org.entitiesList.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, org,
					createRelationship);
			entity.save();
		}
		redirect("Organizations.viewProfile", org.id, "Entity created");
	}

	/**
	 * This method allows the user to create a subentity in an entity
	 * 
	 * @author Noha Khater, Omar Faruki
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
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 */
	public static void createSubEntity(String name, String description,
			long parentId, long orgId, boolean createRelationship) {
		boolean canCreate = true;
		MainEntity parent = MainEntity.findById(parentId);
		Organization org = Organization.findById(orgId);
		for (int i = 0; i < parent.subentities.size(); i++) {
			if (parent.subentities.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, parent, org,
					createRelationship);
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
		// int canView = 0;
		int canRequest = 0;
		int canRequestRelationship = 0;
		int canRestrict = 0;
		boolean entityIsLocked = entity.createRelationship;
		List<User> allowed = Users.getEntityOrganizers(entity);
		if (org.creator.equals(user) || user.isAdmin) {
			canRestrict = 1;
		}
		if (org.creator.equals(user) || allowed.contains(user) || user.isAdmin)
			canEdit = 1;
		if (!Users.isPermitted(user, "post topics", entity.id, "entity"))
			permission = 0;
		// if (Users.isPermitted(user, "view", entity.id, "entity"))
		// canView = 1;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			canRequest = 1;
		if (Users
				.isPermitted(
						user,
						"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						entity.id, "entity"))
			invite = 1;

		int check = 0;
		if (Users.isPermitted(user,
				"block a user from viewing or using a certain entity",
				entity.id, "entity"))
			check = 1;
		int check1 = 0;
		if (Users.isPermitted(user,
				"view",
				entity.id, "entity"))
			check1 = 1;
		int check2 = 0;
		if (Users.isPermitted(user,
				"use",
				entity.id, "entity"))
			check2 = 1;
		if (Users.getEntityOrganizers(entity).contains(user)) {
			canRequestRelationship = 1;
		}
		boolean follower = user.followingEntities.contains(entity);
		boolean canCreateRelationship = EntityRelationships.isAllowedTo(id);
		List<Plan> plans = Plans.planList("entity", entity.id);
		render(user, org, entity, subentities, topicList, permission, invite,
				canEdit, canCreateEntity, follower, canCreateRelationship,
				/* canView, */canRequest, canRequestRelationship, check,
				canRestrict, entityIsLocked, plans,check1,check2);
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
	 * @author Noha Khater, Omar Faruki
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
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 * 
	 */
	public static void editEntity(long entityId, String name,
			String description, boolean createRelationship) {
		MainEntity entity = MainEntity.findById(entityId);
		entity.name = name;
		entity.description = description;
		entity.createRelationship = createRelationship;
		entity.save();
		redirect(request.controller + ".viewEntity", entity.id,
				"Entity created");
	}

	/**
	 * renders the page for requesting relationship creation between entities.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request
	 * 
	 * @param organisationId
	 *            the id of the organisation
	 * 
	 * @param entityId
	 *            the id of the entity from which the request is made
	 * 
	 */
	public static void requestRelationship(long userId, long organisationId,
			long entityId) {
		Organization organisation = Organization.findById(organisationId);
		User user = User.findById(userId);
		MainEntity entity = MainEntity.findById(entityId);
		render(user, organisation, entity);
	}

	/**
	 * creates a request for relationship creation between entities.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request
	 * 
	 * @param source
	 *            the name of the source entity
	 * 
	 * @param destination
	 *            the name of the destination entity
	 * 
	 * @param name
	 *            the name of the relationship
	 * 
	 * @param organisationId
	 *            the id of the organisation
	 * 
	 * @param entityId
	 *            the id of the entity from which the request is made
	 * 
	 */
	
	public static void viewRelationships(long userId, long organisationId,
			long entityId, boolean canRequestRelationship) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		MainEntity entity = MainEntity.findById(entityId);
		render(user, organisation, entity, canRequestRelationship);
	}
	
	public static void deleteRequest(long userId, long relationId, int type) {
		User user = User.findById(userId);
		EntityRelationship relation = EntityRelationship.findById(relationId);
		RenameEndRelationshipRequest deleteRequest = new RenameEndRelationshipRequest(user, relation, type, null);
		deleteRequest.save();
	}
	
	public static void renameRequest(long userId, long relationId, int type, String newName) {
		User user = User.findById(userId);
		EntityRelationship relation = EntityRelationship.findById(relationId);
		RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(user, relation, type, newName);
		renameRequest.save();
	}

}
