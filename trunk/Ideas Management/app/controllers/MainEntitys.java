package controllers;

import java.util.ArrayList;
import java.util.List;

import controllers.CoolCRUD.ObjectType;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import play.mvc.With;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.Invitation;
import models.Item;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.RenameEndRelationshipRequest;
import models.Tag;
import models.Topic;
import models.User;

@With(Secure.class)
public class MainEntitys extends CoolCRUD {

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
		if (!entity.followers.contains(user)) {
			entity.followers.add(user);
			entity.save();
			user.followingEntities.add(entity);
			user.save();
			Log.addUserLog(
					"<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.firstName
							+ " "
							+ user.lastName
							+ "</a>"
							+ " has followed the entity ("
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?mainentityId="
							+ entity.id + "\">" + entity.name + "</a>" + ")",
					entity.organization);
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
	 * @param flag
	 *            : A string that used to represent a flag
	 * 
	 */
	public static void viewFollowers(long entityId, String flag) {
		MainEntity entity = MainEntity.findById(entityId);
		if (flag.equals("true"))
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
		User user = Security.getConnected();
		Organization organisation = Organization.findById(orgId);
		for (int i = 0; i < organisation.entitiesList.size(); i++) {
			if (organisation.entitiesList.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, organisation,
					createRelationship);
			entity.save();
			Log.addUserLog(
					"<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.firstName
							+ " "
							+ user.lastName
							+ "</a>"
							+ " has created the entity ("
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?mainentityId="
							+ entity.id + "\">" + entity.name + "</a>" + ")",
					organisation);
		}
		redirect("Organizations.viewProfile", organisation.id, "Entity created");
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
		Organization organisation = Organization.findById(orgId);
		User user = Security.getConnected();
		for (int i = 0; i < parent.subentities.size(); i++) {
			if (parent.subentities.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, parent,
					organisation, createRelationship);
			entity.save();
			Log.addUserLog(
					"<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.firstName
							+ " "
							+ user.lastName
							+ "</a>"
							+ " has created the subentity ("
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?mainentityId="
							+ entity.id
							+ "\">"
							+ entity.name
							+ "</a>"
							+ ") for the entity ("
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?mainentityId="
							+ parent.id + "\">" + parent.name + "</a>" + ")",
					organisation);
		}
		redirect("Organizations.viewProfile", organisation.id,
				"SubEntity created");
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
		int canCreateEntity = 0;
		int canCreateSubEntity = 0;
		int canDeleteEntity = 0;
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")) {
			canCreateEntity = 1;
			canDeleteEntity = 1;
		}
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")
				|| Users.isPermitted(user,
						"Create a sub-entity for entity he/she manages",
						entity.id, "entity")) {
			canCreateSubEntity = 1;
		}
		int permission = 1;
		int invite = 0;
		int canEdit = 0;
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
		if (Users.isPermitted(user, "view", entity.id, "entity"))
			check1 = 1;
		int check2 = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			check2 = 1;
		if (UserRoleInOrganizations.isOrganizer(user, entity.id, "entity")) {
			canRequestRelationship = 1;
		}
		boolean follower = user.followingEntities.contains(entity);
		boolean canCreateRelationship = EntityRelationships.isAllowedTo(id);
		List<Plan> plans = Plans.planList("entity", entity.id);
		render(user, org, entity, subentities, topicList, permission, invite,
				canEdit, canCreateEntity, canCreateSubEntity, follower,
				canCreateRelationship, canRequest, canRequestRelationship,
				check, canRestrict, entityIsLocked, plans, check1, check2,
				canDeleteEntity);
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
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(entityId);
		entity.name = name;
		entity.description = description;
		entity.createRelationship = createRelationship;
		entity.save();
		Log.addUserLog(
				"<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.firstName
						+ " "
						+ user.lastName
						+ "</a>"
						+ " has edited the entity ("
						+ "<a href=\"http://localhost:9008/mainentitys/viewentity?mainentityId="
						+ entity.id + "\">" + entity.name + "</a>" + ")",
				entity.organization);
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
	 * renders the page for viewing the relationships for an entity.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user viewing the requests.
	 * 
	 * @param organisationId
	 *            the id of the organisation.
	 * 
	 * @param entityId
	 *            the id of the topic's entity.
	 * 
	 * @param canRequestRelationship
	 *            boolean variable whether this user is allowed to make
	 *            relationship requests or not.
	 * 
	 */
	public static void viewRelationships(long userId, long organisationId,
			long entityId, boolean canRequestRelationship) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		MainEntity entity = MainEntity.findById(entityId);
		render(user, organisation, entity, canRequestRelationship);
	}

	/**
	 * deletes the entity.
	 * 
	 * @author Noha Khater
	 * 
	 * @param entityId
	 *            the id of the entity to be deleted.
	 * 
	 * @return boolean
	 */
	public static boolean deleteEntity(long entityId) {
		MainEntity entity = MainEntity.findById(entityId);
		List<Organization> allOrganizations = Organization.findAll();
		List<MainEntity> allEntities = MainEntity.findAll();
		List<Tag> tags = Tag.findAll();
		List<User> followers = User.findAll();
		int size = allOrganizations.size();
		for (int i = 0; i < size; i++) {
			if (allOrganizations.get(i).entitiesList.contains(entity)) {
				allOrganizations.get(i).entitiesList.remove(entity);
				allOrganizations.get(i).save();
			}
		}
		size = followers.size();
		for (int i = 0; i < size; i++) {
			if (followers.get(i).followingEntities.contains(entity)) {
				followers.get(i).followingEntities.remove(entity);
				followers.get(i).save();
			}
		}
		size = tags.size();
		for (int i = 0; i < size; i++) {
			if (tags.get(i).entities.contains(entity)) {
				tags.get(i).entities.remove(entity);
				tags.get(i).save();
			}
		}
		// size = entity.relationsSource.size();
		// for (int j = 0; j < size; j++) {
		// EntityRelationships.deleteER(entity.relationsSource.get(j).id);
		// }
		// size = entity.relationsDestination.size();
		// for (int j = 0; j < size; j++) {
		// EntityRelationships.deleteER(entity.relationsDestination.get(j).id);
		// }
		// Mohamed Hisham {
		size = entity.relationsSource.size();
		for (int i = 0; i < size; i++) {
			EntityRelationships.delete(entity.relationsSource.get(i).id);
		}
		size = entity.relationsDestination.size();
		for (int i = 0; i < size; i++) {
			EntityRelationships.delete(entity.relationsDestination.get(i).id);
		}
		// }
		size = entity.relationshipRequestsSource.size();
		for (int j = 0; j < size; j++) {
			CreateRelationshipRequests.delete(entity.relationshipRequestsSource
					.get(j).id);
		}
		size = entity.relationshipRequestsDestination.size();
		for (int j = 0; j < size; j++) {
			CreateRelationshipRequests
					.delete(entity.relationshipRequestsDestination.get(j).id);
		}
		// size = entity.topicList.size();
		// ObjectType type = ObjectType.get(getControllerClass());
		// notFoundIfNull(type);
		// for (int j = 0; j < size; j++) {
		// Model object = type.findById(entity.topicList.get(j).id);
		// notFoundIfNull(object);
		// Topics.deleteTopic("" + entity.topicList.get(j).id, "msg");
		// }
		size = allEntities.size();
		for (int i = 0; i < size; i++) {
			if (allEntities.get(i).subentities.contains(entity)) {
				allEntities.get(i).subentities.remove(entity);
				allEntities.get(i).save();
			}
		}
		size = entity.subentities.size();
		for (int i = 0; i < size; i++) {
			MainEntitys.deleteEntity(entity.subentities.get(i).id);
		}
		// Mai Magdy
		List<Invitation> invite = Invitation.find("byEntity", entity).fetch();
		for (int i = 0; i < invite.size(); i++)
			invite.get(i).delete();
		//

		size = entity.topicRequests.size();
		for (int i = 0; i < size; i++) {
			// TopicRequests.delete(entity.topicRequests.get(i).id);
		}
		size = entity.relatedItems.size();
		for (int i = 0; i < size; i++) {
			// Items.delete(entity.relatedItems.get(i).id);
		}
		entity.logs.clear();
		entity.delete();
		return true;
	}

}
